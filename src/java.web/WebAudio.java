import defrac.audio.WebAudioAPI;
import defrac.lang.Bridge;
import defrac.web.AnalyserNode;
import defrac.web.AudioContext;
import defrac.web.AudioProcessingEvent;
import defrac.web.EventListener;
import defrac.web.Float32Array;
import defrac.web.ScriptProcessorNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple entry point to the web audio api.
 *
 * @author Andre Michelle
 */
public final class WebAudio {
	public interface Source {
		void render(
				@Nonnull final double[] left,
				@Nonnull final double[] right,
				final int length );
	}

	private static final int BufferSize = 8192;
	private static final int NumberOfInputChannels = 0;
	private static final int NumberOfOutputChannels = 2;

	private final AudioContext context;

	/**
	 * We need to hold a reference here.
	 * Otherwise Chrome is killing it in the GC.
	 */
	private ScriptProcessorNode scriptProcessor;

	@Nullable
	private AnalyserNode analyser;

	@Nullable
	private Float32Array frequencyBands = null;

	public WebAudio() {
		context = WebAudioAPI.createContext();
	}

	public float sampleRate() {
		return context.sampleRate;
	}

	public void connect( @Nonnull final Source source ) {
		analyser = context.createAnalyser();
		analyser.fftSize = 32; // We need 16 bands (fftSize/2)
		analyser.smoothingTimeConstant = 0.2;

		analyser.minDecibels = -100.0;
		analyser.maxDecibels = -30.0;

		scriptProcessor = context.createScriptProcessor( BufferSize, NumberOfInputChannels, NumberOfOutputChannels );

		final int bufferSize = scriptProcessor.bufferSize;

		final double[] sourceBuffer0 = new double[ bufferSize ];
		final double[] sourceBuffer1 = new double[ bufferSize ];

		scriptProcessor.onaudioprocess = new EventListener<AudioProcessingEvent>() {
			@Override
			public void onEvent( @Nonnull final AudioProcessingEvent event ) {
				final Float32Array channelData0 = event.outputBuffer.getChannelData( 0 );
				final Float32Array channelData1 = event.outputBuffer.getChannelData( 1 );
				source.render( sourceBuffer0, sourceBuffer1, bufferSize );

				for( int i = 0 ; i < bufferSize ; ++i ) {
					channelData0.set( i, ( float ) sourceBuffer0[ i ] );
					channelData1.set( i, ( float ) sourceBuffer1[ i ] );
				}
			}
		};

		scriptProcessor.connect( analyser );

		analyser.connect( context.destination );
	}

	public void getFrequencyBands( @Nonnull final double[] bands ) {
		if( null == analyser )
			return;

		final int numBands = bands.length;
		if( null == frequencyBands || numBands != frequencyBands.length )
			frequencyBands = Bridge.toFloat32Array( new float[ numBands ] );

		analyser.getFloatFrequencyData( frequencyBands );

		for( int i = 0 ; i < numBands ; ++i ) {
			bands[ i ] = frequencyBands.get( i );
		}

		final double minDecibels = analyser.minDecibels;
		final double maxDecibels = analyser.maxDecibels;

		for( int i = 0 ; i < numBands ; ++i ) {
			// Clamp value between min and max decibel
			final double band = Math.max( Math.min( bands[ i ], maxDecibels ), minDecibels );

			// Scale it into normalized space
			final double scaled = ( band - minDecibels ) / ( maxDecibels - minDecibels );

			// Make peaks stronger
			bands[ i ] = scaled * scaled * scaled;
		}
	}
}