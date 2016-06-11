package com.audiotool.bitboy.format;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * @author Joa Ebert
 */
public final class FormatParser {
	private static final int TypeMK = 0x2e4d;

	private static final int P_FORMAT = 0x438;
	private static final int P_LENGTH = 0x3b6;
	private static final int P_SEQUENCE = 0x3b8;
	private static final int P_PATTERNS = 0x43c;

	public static Format parse( @Nonnull final byte[] bytes ) {
		return parse( ByteBuffer.wrap( bytes ) );
	}

	public static Format parse( @Nonnull final ByteBuffer buffer ) {
		buffer.order( ByteOrder.LITTLE_ENDIAN );

		final int type = readFormat( buffer );

		if( TypeMK != type )
			throw new RuntimeException( "Unsupported MOD format: " + type );

		final String title = readTitle( buffer );
		final int[] sequence = new int[ readSequenceLength( buffer ) ];
		final Waveform[] waveforms = readWaveforms( buffer );
		final int numPatterns = readSequence( buffer, sequence );

		final Step[][][] patterns = readPatterns( buffer, numPatterns );
		final ArrayList<String> credits = new ArrayList<String>();

		for( int i = 0 ; i < 31 ; ++i ) {
			final Waveform waveform = waveforms[ i ];

			waveform.loadWaveform( buffer );

			if( !waveform.title.isEmpty() )
				credits.add( waveform.title );
		}

		return new Format(
				title,
				type,
				waveforms,
				patterns,
				credits.toArray( new String[ credits.size() ] ),
				sequence,
				numPatterns );
	}

	@Nonnull
	private static Step[][][] readPatterns( @Nonnull final ByteBuffer buffer, final int numPatterns ) {
		final Step[][][] patterns = new Step[ numPatterns + 1 ][][];

		for( int i = 0 ; i <= numPatterns ; ++i ) {
			buffer.position( P_PATTERNS + i * 0x400 ); // 4bytes * 4channels * 64rows = 0x400bytes

			final Step[][] rows = new Step[ 64 ][];

			for( int j = 0 ; j < 64 ; ++j ) {
				final Step[] channels = new Step[ 4 ];

				for( int k = 0 ; k < 4 ; ++k ) {
					channels[ k ] = Step.parse( buffer );
				}

				rows[ j ] = channels;
			}

			patterns[ i ] = rows;
		}

		return patterns;
	}

	private static int readFormat( @Nonnull final ByteBuffer buffer ) {
		return buffer.getShort( P_FORMAT ) & 0xFFFF;
	}

	@Nonnull
	private static String readTitle( @Nonnull final ByteBuffer buffer ) {
		buffer.position( 0 );

		final StringBuilder s = new StringBuilder();

		for( int i = 0 ; i < 20 ; ++i ) {
			final int c = buffer.get();

			if( 0 == c )
				break;

			s.append( Character.toChars( c ) );
		}

		return s.toString();
	}

	private static int readSequenceLength( @Nonnull final ByteBuffer buffer ) {
		buffer.position( P_LENGTH );

		return buffer.get() & 0xFF;
	}

	@Nonnull
	private static Waveform[] readWaveforms( @Nonnull final ByteBuffer buffer ) {
		final Waveform[] waveforms = new Waveform[ 31 ];

		for( int i = 0 ; i < 31 ; ++i ) {
			final int position = i * 0x1e + 0x14;

			waveforms[ i ] = Waveform.parse( ByteBuffer.allocate( 30 ).put( buffer.array(), position, 30 ) );
		}

		return waveforms;
	}

	private static int readSequence( @Nonnull final ByteBuffer buffer, @Nonnull final int[] sequence ) {
		buffer.position( P_SEQUENCE );

		int patternNum = 0;

		final int n = sequence.length;

		for( int i = 0 ; i < n ; ++i ) {
			sequence[ i ] = buffer.get() & 0xFF;

			patternNum = Math.max( patternNum, sequence[ i ] );
		}

		return patternNum;
	}
}