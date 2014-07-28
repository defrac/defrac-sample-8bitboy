package com.audiotool.bitboy.dsp;

import javax.annotation.Nonnull;

/**
 * Generates Audio for one Channel.
 *
 * @author Andre Michelle
 */
final class Channel
{
	boolean mute;

	private final ChannelState channelState;
	private final double samplingRate;

	private double volume;
	private double panning;

	private double gainL;
	private double gainR;

	private boolean cycleComplete;

	Channel( @Nonnull final ChannelState channelState, final double samplingRate )
	{
		this.channelState = channelState;
		this.samplingRate = samplingRate;

		volume = 1.0;
		panning = 0.0;

		updateStereo();
	}

	double volume()
	{
		return volume;
	}

	void volume( final double value )
	{
		if( volume == value )
			return;

		volume = value;

		updateStereo();
	}

	double panning()
	{
		return panning;
	}

	void panning( final double value )
	{
		if( panning == value )
			return;

		panning = value;

		updateStereo();
	}

	/**
	 * Processes a full audio buffer
	 *
	 * @param l      The left audio buffer
	 * @param r      The right audio buffer
	 * @param offset The offset where to start writing
	 * @param length The number of waveforms to write
	 */
	void processAudio( @Nonnull final double[] l, @Nonnull final double[] r, final int offset, final int length )
	{
		if( null == channelState.waveForm || mute )
			return;

		int position = 0;

		while( position < length )
		{
			position += advanceCycle( l, r, offset + position, length - position );

			if( cycleComplete )
			{
				channelState.nextCycle();

				cycleComplete = false;

				if( null == channelState.waveForm )
					return;
			}
		}
	}

	/**
	 * Processes the current waveform cycle and returns index of completion
	 *
	 * @param l      The left audio buffer
	 * @param r      The right audio buffer
	 * @param offset The offset where to start writing
	 * @param length The number of waveforms to write
	 * @return The actual number of waveforms that has been written
	 */
	private int advanceCycle( @Nonnull final double[] l, @Nonnull final double[] r, final int offset, final int length )
	{
		final double rate = channelState.phaseVelocity() / samplingRate;
		final double[] waveform = channelState.waveForm;
		final int waveStart = channelState.waveStart;
		final int waveLength = channelState.waveLength;

		final double gain = channelState.gain();
		final double multiplierL = gainL * gain;
		final double multiplierR = gainR * gain;

		for( int i = 0 ; i < length ; ++i )
		{
			final double amp = waveform[ ( int ) ( waveStart + channelState.wavePhase ) ];

			final int index = i + offset;

			l[ index ] += amp * multiplierL;
			r[ index ] += amp * multiplierR;

			channelState.wavePhase += rate;

			if( channelState.wavePhase > waveLength )
			{
				cycleComplete = true;
				return i;
			}
		}

		return length;
	}

	/**
	 * Update the left and right gain in a quadratic manner
	 */
	private void updateStereo()
	{
		gainL = Math.sqrt( ( 1.0 - panning ) * 0.5 ) * volume;
		gainR = Math.sqrt( ( panning + 1.0 ) * 0.5 ) * volume;
	}
}