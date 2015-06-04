package com.audiotool.bitboy.dsp;

import com.audiotool.bitboy.format.Format;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * Represents the current PlayerState
 *
 * @author Andre Michelle
 */
final class PlayerState
{
	private final Player player;

	final ChannelState[] channelStates;

	final LinkedList<String> warnings = new LinkedList<>();

	boolean warningsEnabled;

	@Nullable
	private Format format;

	private double bpm;
	private int speed;

	private int tickIndex;
	private int stepIndex;
	private int patternIndex;
	private boolean incrementPatternIndex;

	private boolean complete;
	private boolean lastRow;
	private boolean idle;
	private boolean loopDetected;

	PlayerState( @Nonnull final Player player )
	{
		this.player = player;

		channelStates = new ChannelState[]{
				new ChannelState( this ),
				new ChannelState( this ),
				new ChannelState( this ),
				new ChannelState( this )
		};
	}

	void format( Format value )
	{
		if( format == value )
			return;

		format = value;
	}

	@Nullable
	Format format()
	{
		return format;
	}

	void nextTick()
	{
		if( --tickIndex <= 0 )
		{
			if( lastRow )
				complete = true;
			else
				nextStep();
		}
		else
		{
			for( final ChannelState channel : channelStates )
				channel.nextTick( tickIndex );
		}
	}

	void nextStep()
	{
		assert null != format;

		final int rowIndex = stepIndex++;

		incrementPatternIndex = false;

		for( int index = 0 ; index < 4 ; ++index )
		{
			channelStates[ index ].nextStep(
					format.getStepAt(
							format.getSequenceAt( patternIndex ), rowIndex, index ) );
		}

		if( incrementPatternIndex )
		{
			nextPattern();
		}
		else if( stepIndex == format.getPatternLength( format.getSequenceAt( patternIndex ) ) )
		{
			stepIndex = 0;
			nextPattern();
		}

		tickIndex = speed;
	}

	void reset()
	{
		bpm = Player.DefaultBpm;
		speed = Player.DefaultSpeed;

		tickIndex = 0;
		stepIndex = 0;
		patternIndex = 0;

		complete = false;
		lastRow = false;
		idle = false;
		loopDetected = false;
		incrementPatternIndex = false;

		for( final ChannelState channel : channelStates )
			channel.reset();
	}

	boolean running()
	{
		if( complete )
			idle = true;

		return !idle;
	}

	void patternJump( final int index )
	{
		assert null != format;

		if( index <= patternIndex )
		{
			loopDetected = true;

			if( player.loopMode.getValue() )
				patternIndex = index;
			else
				lastRow = true;
		}
		else
		{
			patternIndex = index;
		}

		stepIndex = 0;
	}

	void patternBreak( final int value )
	{
		stepIndex = value;

		incrementPatternIndex = true;
	}

	int stepIndex()
	{
		return stepIndex;
	}

	void stepIndex( final int value )
	{
		stepIndex = value;
	}

	void bpm( final double value )
	{
		bpm = value;
	}

	double bpm()
	{
		return bpm;
	}

	void speed( final int value )
	{
		speed = value;
	}

	@Nonnull
	ChannelState[] channelStates()
	{
		return channelStates;
	}

	boolean lastRow()
	{
		return lastRow;
	}

	boolean loopDetected()
	{
		return loopDetected;
	}

	int speed()
	{
		return speed;
	}

	boolean complete()
	{
		return complete;
	}

	void warning( @Nonnull final String message )
	{
		if( warningsEnabled && !warnings.contains( message ) )
			warnings.add( message );
	}

	private void nextPattern()
	{
		assert null != format;

		if( ++patternIndex == format.sequence.length )
		{
			if( player.loopMode.getValue() )
				patternIndex = 0;
			else
				lastRow = true;
		}
	}
}
