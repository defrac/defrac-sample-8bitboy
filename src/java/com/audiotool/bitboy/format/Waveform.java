package com.audiotool.bitboy.format;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * @author Joa Ebert
 */
public final class Waveform {
	@Nonnull
	static Waveform parse( @Nonnull final ByteBuffer buffer ) {
		buffer.rewind();
		final StringBuilder title = new StringBuilder();
		for( int i = 0 ; i < 22 ; ++i ) {
			final int c = buffer.get();
			if( 0 != c )
				title.append( Character.toChars( c ) );
		}
		final int length = ( buffer.getShort() & 0xFFFF ) << 1;
		final int tone = buffer.get() & 0xFF; // every time 0?
		final int volume = buffer.get() & 0xFF;
		final int repeatStart = ( buffer.getShort() & 0xFFFF ) << 1;
		final int repeatLength = ( buffer.getShort() & 0xFFFF ) << 1;
		return new Waveform( title.toString(), length, tone, volume, repeatStart, repeatLength );
	}

	public final String title;
	public final int length;
	public final int tone;
	public final int volume;
	public final int repeatStart;
	public final int repeatLength;
	public final double[] wave;

	Waveform( @Nonnull final String title,
			  final int length,
			  final int tone,
			  final int volume,
			  final int repeatStart,
			  final int repeatLength ) {
		this.length = length;
		this.title = title;
		this.tone = tone; // unused
		this.volume = volume;
		this.repeatStart = repeatStart;
		this.repeatLength = repeatLength;
		this.wave = new double[ length ];
	}

	void loadWaveform( @Nonnull final ByteBuffer buffer ) {
		if( 0 == length )
			return;

		double min = 1.0;
		double max = -1.0;
		for( int i = 0 ; i < length ; ++i ) {
			final double value = ( ( double ) buffer.get() + 0.5 ) / 127.5;
			if( value < min ) min = value;
			if( value > max ) max = value;
			wave[ i ] = value;
		}

		// Remove DC-Offset
		final double base = ( min + max ) * 0.5;
		for( int i = 0 ; i < length ; ++i )
			wave[ i ] -= base;
	}

	@Override
	public String toString() {
		return "[Waveform" +
				" title: " + title +
				", length: " + length +
				", tone: " + tone +
				", volume: " + volume +
				", repeatStart: " + repeatStart +
				", repeatLength: " + repeatLength +
				", wave(n): " + wave.length +
				"]";
	}
}