package com.audiotool.bitboy.dsp;

import com.audiotool.bitboy.format.Format;
import com.audiotool.bitboy.format.Waveform;
import com.audiotool.bitboy.format.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a channel's tick state.
 *
 * This probably the most magic part and also buggy.
 *
 * @author Andre Michelle
 */
final class ChannelState
{
	private static final double NTSC = 7159090.5 * 0.5; // NTSC machine clock (Magic Number)

	private final PlayerState playerState;

	private Step currentStep;

	/* PITCH */
	private int tone;
	private int period;

	/* EFFECT */
	private int effect;
	private int effectParam;

	double[] waveForm;
	double wavePhase;
	int waveStart;
	int waveLength;

	private boolean firstRun;
	private int volume;

	private int volumeSlide;
	private int portamentoSpeed;
	private int tonePortamentoSpeed = 0;
	private int tonePortamentoPeriod;
	private int vibratoSpeed;
	private int vibratoDepth;
	private int vibratoPosition;
	private int vibratoOffset;
	private int arpeggio1;
	private int arpeggio2;

	//-- EXT EFFECT
	private boolean patternFirstRun;
	private int patternFirstRunCount;
	private int patternFirstRunPosition;

	ChannelState( @Nonnull final PlayerState playerState )
	{
		this.playerState = playerState;
	}

	/**
	 * Invokes by Channel when a waveform has been processed
	 */
	void nextCycle()
	{
		if( firstRun )
		{
			firstRun = false;

			final Waveform waveform = getWaveform( currentStep );

			if( null == waveform || 0 == waveform.repeatLength ) // no repeat > stop
			{
				waveForm = null;
				return;
			}

			waveStart = waveform.repeatStart;
			waveLength = waveform.repeatLength;
		}

		wavePhase %= waveLength;
	}

	/**
	 * Invokes by PlayerState when a tick has been processed.
	 *
	 * @param tick index
	 */
	void nextTick( final int tick )
	{
		switch( effect )
		{
			case Effects.Arpeggio:

				updateArpeggio( tick % 3 );
				break;

			case Effects.PortamentoUp:
			case Effects.PortamentoDown:

				updatePortamento();
				break;

			case Effects.TonePortamento:

				updateTonePortamento();
				break;

			case Effects.TonePortamentoVolumeSlide:

				updateTonePortamento();
				updateVolumeSlide();
				break;

			case Effects.VolumeSlide:

				updateVolumeSlide();
				break;

			case Effects.Vibrato:

				updateVibrato();
				break;

			case Effects.VibratoVolumeSlide:

				updateVibrato();
				updateVolumeSlide();
				break;

			case Effects.ExtendedEffect:

				final int extEffect = effectParam >> 4;
				final int extParam = effectParam & 0xf;

				switch( extEffect )
				{
					case 0x9: //-- retrigger note
						if( tick % extParam == 0 )
							wavePhase = 0.0;
						break;

					case 0xc: //-- cut note
						waveForm = null;
						break;
				}

				break;
		}
	}

	/**
	 * Invokes by PlayerState when a row has been processed.
	 *
	 * @param step The row data
	 */
	void nextStep( @Nonnull final Step step )
	{
		currentStep = step;

		updateWave();

		if( step.effect == Effects.TonePortamento )
		{
			initTonePortamento();
		}
		else if( step.period > 0 )
		{
			period = step.period;
			tone = Tables.ToneIndices.get( period );
			tonePortamentoPeriod = period; // fix for 'delicate.mod'
		}

		initEffect();
	}

	/**
	 * Resets the ChannelState for another mod to kick it
	 */
	void reset()
	{
		waveForm = null;
		wavePhase = 0.0;

		firstRun = false;
		volume = 0;
		currentStep = null;

		patternFirstRun = false;
		patternFirstRunCount = 0;
		patternFirstRunPosition = 0;

		volumeSlide = 0;
		portamentoSpeed = 0;
		tonePortamentoSpeed = 0;
		tonePortamentoPeriod = 0;
		vibratoSpeed = 0;
		vibratoDepth = 0;
		vibratoPosition = 0;
		vibratoOffset = 0;

		effect = 0;
		effectParam = 0;
	}

	double phaseVelocity()
	{
		return NTSC / ( period + ( double ) vibratoOffset );
	}

	double gain()
	{
		return ( double ) volume / 128.0;
	}

	private void initEffect()
	{
		effect = currentStep.effect;
		effectParam = currentStep.effectParam;

		if( effect != Effects.Vibrato && effect != Effects.VibratoVolumeSlide )
			vibratoOffset = 0;

		switch( effect )
		{
			case Effects.Arpeggio:

				if( effectParam > 0 )
					initArpeggio();
				else
					volumeSlide = 0; // no effect here, reset some values
				break;

			case Effects.PortamentoUp:

				initPortamento( -effectParam );
				break;

			case Effects.PortamentoDown:

				initPortamento( effectParam );
				break;

			case Effects.TonePortamento:
				break;

			case Effects.Vibrato:
				final Waveform waveform = getWaveform( currentStep );
				if( waveform != null )
					volume = waveform.volume;
				initVibrato();
				break;

			case Effects.VibratoVolumeSlide:

				/*This is a combination of Vibrato (4xy), and volume slide (Axy).
				The parameter does not affect the vibrato, only the volume.
				If no parameter use the vibrato parameters used for that channel.*/
				initVolumeSlide();
				break;

			case Effects.ExtendedEffect:

				final int extEffect = effectParam >> 4;
				final int extParam = effectParam & 0xf;

				switch( extEffect )
				{
					case 0x6: //-- pattern firstRun

						if( extParam == 0 )
							patternFirstRunPosition = playerState.stepIndex() - 1;
						else
						{
							if( !patternFirstRun )
							{
								patternFirstRunCount = extParam;
								patternFirstRun = true;
							}

							if( --patternFirstRunCount >= 0 )
								playerState.stepIndex( patternFirstRunPosition );
							else
								patternFirstRun = false;
						}
						break;

					case 0x9: //-- retrigger note

						wavePhase = 0.0;
						break;

					case 0xc: //-- cut note

						if( extParam == 0 )
							waveForm = null;
						break;

					default:

						playerState.warning( "extended effect: " + extEffect + " is not defined." );
						break;
				}

				break;

			case Effects.TonePortamentoVolumeSlide:
			case Effects.VolumeSlide:

				initVolumeSlide();
				break;

			case Effects.SetVolume:

				volumeSlide = 0;
				volume = effectParam;
				break;

			case Effects.PositionJump:

				playerState.patternJump( effectParam );
				break;

			case Effects.PatternBreak:

				playerState.patternBreak( Integer.parseInt( Integer.toString( effectParam, 16 ), 10 ) );
				break;

			case Effects.SetSpeed:

				if( effectParam > 32 )
					playerState.bpm( effectParam );
				else
					playerState.speed( effectParam );
				break;

			default:
				playerState.warning( "effect: " + effect + " is not defined." );
				break;
		}
	}

	private void updateWave()
	{
		final Waveform waveform = getWaveform( currentStep );

		if( waveform == null || currentStep.period <= 0 )
			return;

		volume = waveform.volume;
		waveForm = waveform.wave;
		waveStart = 0;
		waveLength = waveForm.length;
		wavePhase = 0.0;
		firstRun = true;
	}

	private void initArpeggio()
	{
		arpeggio1 = Tables.Tone[ tone + ( effectParam >> 4 ) ];
		arpeggio2 = Tables.Tone[ tone + ( effectParam & 0xf ) ];
	}

	private void updateArpeggio( final int index )
	{
		if( 0 == effectParam )
			return;

		if( index == 1 )
			period = arpeggio2;
		else if( index == 2 )
			period = arpeggio1;
	}

	private void initVolumeSlide()
	{
		final Waveform waveform = getWaveform( currentStep );

		if( null != waveform )
			volume = waveform.volume;

		volumeSlide = effectParam >> 4;
		volumeSlide -= effectParam & 0xf;
	}

	private void updateVolumeSlide()
	{
		final int value = volume + volumeSlide;
		volume = 0 > value ? 0 : 64 < value ? 64 : value;
	}

	private void initTonePortamento()
	{
		if( currentStep.effectParam > 0 )
		{
			tonePortamentoSpeed = currentStep.effectParam;

			if( currentStep.period > 0 )
				tonePortamentoPeriod = currentStep.period;
		}
	}

	private void updateTonePortamento()
	{
		if( period > tonePortamentoPeriod )
		{
			period -= tonePortamentoSpeed;
			if( period < tonePortamentoPeriod )
				period = tonePortamentoPeriod;
		}
		else if( period < tonePortamentoPeriod )
		{
			period += tonePortamentoSpeed;
			if( period > tonePortamentoPeriod )
				period = tonePortamentoPeriod;
		}
	}

	private void initPortamento( final int value )
	{
		portamentoSpeed = value;
	}

	private void updatePortamento()
	{
		period += portamentoSpeed;
	}

	private void initVibrato()
	{
		if( 0 == effectParam )
			return;

		vibratoSpeed = effectParam >> 4;
		vibratoDepth = effectParam & 0xf;
		vibratoPosition = 0;
	}

	private void updateVibrato()
	{
		vibratoPosition += vibratoSpeed;
		vibratoOffset = Tables.Sine[ vibratoPosition % Tables.Sine.length ] * vibratoDepth / 128;
	}

	@Nullable
	private Waveform getWaveform( @Nonnull final Step step )
	{
		if( -1 == step.waveformIndex )
			return null;

		final Format format = playerState.format();

		assert null != format;

		return format.waveforms[ step.waveformIndex ];
	}
}
