package com.audiotool.bitboy.dsp;

import com.audiotool.bitboy.format.Format;
import com.audiotool.bitboy.model.Model;
import defrac.event.EventDispatcher;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * The 8Bitboy Player
 *
 * @author Andre Michelle
 */
public final class Player {
	public static final double DefaultBpm = 125.0;
	public static final int DefaultSpeed = 6;
	public static final double BpmRatio = 2.5;

	// Settings
	//
	public final Model<Boolean> pause = Model.create( false );
	public final Model<Double> volume = Model.create( 0.75 );
	public final Model<Boolean> loopMode = Model.create( false );
	public final Model<Boolean> mute = Model.create( false );

	public final EventDispatcher<Format> onModLoad;
	public final EventDispatcher<Player> onModComplete;

	private final double samplingRate;
	private final PlayerState playerState;
	private final Channel[] channels;

	private double speed;
	private boolean isLoop;
	private double tickSampleIndex;

	public Player( final double samplingRate ) {
		this.samplingRate = samplingRate;

		onModLoad = new EventDispatcher<Format>();
		onModComplete = new EventDispatcher<Player>();

		playerState = new PlayerState( this );

		channels = new Channel[ 4 ];
		channels[ 0 ] = new Channel( playerState.channelStates[ 0 ], samplingRate );
		channels[ 1 ] = new Channel( playerState.channelStates[ 1 ], samplingRate );
		channels[ 2 ] = new Channel( playerState.channelStates[ 2 ], samplingRate );
		channels[ 3 ] = new Channel( playerState.channelStates[ 3 ], samplingRate );

		// Original Amiga Panning
		channels[ 0 ].panning( -1.0 );
		channels[ 1 ].panning( 1.0 );
		channels[ 2 ].panning( 1.0 );
		channels[ 3 ].panning( -1.0 );

		speed = 1.0 / 1.0; // default
	}

	@Nonnull
	public Player applyFormat( @Nonnull final Format format ) {
		tickSampleIndex = 0.0;

		final boolean wasPause = pause.getValue();
		final boolean wasLoopMode = loopMode.getValue();

		playerState.reset();
		playerState.format( format );

		pause.setValue( false );
		loopMode.setValue( false );

		playerState.warningsEnabled = true;
		final double seconds = analyse();
		playerState.warningsEnabled = false;

		System.out.println( "*-------*" );
		System.out.println( "|8Bitboy|" );
		System.out.println( "*-------*" );
		System.out.println( "Title: " + format.title );
		System.out.println( "SampleRate: " + samplingRate );
		System.out.println( "Duration: " + ( ( isLoop ? "loop " : "" ) + Math.round( seconds ) + "s" ) );
		System.out.println( "Warnings: " + Arrays.toString( playerState.warnings.toArray( new String[ playerState.warnings.size() ] ) ) );
		System.out.println( "Credits: " + Arrays.toString( format.credits ) );

		playerState.reset();
		pause.setValue( wasPause );
		loopMode.setValue( wasLoopMode );

		onModLoad.dispatch( format );

		return this;
	}

	public void rewind() {
		playerState.reset();
	}

	public void render( @Nonnull final double[] l, @Nonnull final double[] r, final int offset, final int length ) {
		Arrays.fill( l, offset, offset + length, 0.0 );
		Arrays.fill( r, offset, offset + length, 0.0 );

		if( !playerState.running() || null == playerState.format() || pause.getValue() )
			return;

		int sampleIndex = 0;

		while( sampleIndex < length ) {
			final double samplesPerTick = ( samplingRate * BpmRatio * speed ) / playerState.bpm();

			final int process = Math.min( length - sampleIndex, ( int ) Math.ceil( samplesPerTick - tickSampleIndex ) );

			assert 0 < process;

			for( int i = 0 ; i < 4 ; ++i )
				channels[ i ].processAudio( l, r, sampleIndex + offset, process );

			sampleIndex += process;
			tickSampleIndex += process;

			if( tickSampleIndex >= samplesPerTick ) {
				tickSampleIndex -= samplesPerTick;

				playerState.nextTick();
			}

			if( !playerState.running() ) {
				playerState.reset();

				onModComplete.dispatch( this );
			}
		}

		applyVolume( l, r, offset, length, mute.getValue() ? 0.0 : volume.getValue() );
	}

	private double analyse() {
		double seconds = 0.0;

		isLoop = false;

		while( true ) {
			playerState.nextStep();

			if( playerState.loopDetected() ) {
				isLoop = true;
				return seconds;
			}

			seconds += ( Player.BpmRatio / playerState.bpm() ) * playerState.speed();

			if( playerState.lastRow() )
				return seconds;
		}
	}

	private void applyVolume( final double[] l, final double[] r, final int offset, final int length, final double gain ) {
		for( int i = 0 ; i < length ; ++i ) {
			final int io = i + offset;

			l[ io ] *= gain;
			r[ io ] *= gain;
		}
	}
}