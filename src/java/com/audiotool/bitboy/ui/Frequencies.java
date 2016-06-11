package com.audiotool.bitboy.ui;

import defrac.display.Image;
import defrac.display.TextureData;
import defrac.display.TextureDataBuffer;
import defrac.display.TextureDataFormat;
import defrac.display.TextureDataRepeat;
import defrac.display.TextureDataSmoothing;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author Andre Michelle
 */
public final class Frequencies extends Image {
	private static final int Width = 142;
	private static final int Height = 13;

	public static Frequencies create() {
		final byte[] pixels = new byte[ Width * Height * 3 ];
		return new Frequencies( TextureData.Persistent.fromData(
				pixels, Width, Height,
				TextureDataFormat.RGB,
				TextureDataRepeat.NO_REPEAT,
				TextureDataSmoothing.NO_SMOOTHING_WITHOUT_MIPMAP ), pixels );
	}

	private final TextureDataBuffer textureBuffer;

	private final byte[] pixels;

	private Frequencies( @Nonnull final TextureData textureData, @Nonnull final byte[] pixels ) {
		super( textureData );

		this.pixels = pixels;

		textureBuffer = textureData.loadPixels( TextureDataBuffer.MemoryHint.WRITE_ONLY );
	}

	public void render( @Nonnull final double[] bands ) {
		assert 16 == bands.length;

		Arrays.fill( pixels, ( byte ) 0 );

		final int yMax = Height - 1;
		for( int i = 0 ; i < 16 ; ++i ) {
			final int x = i * 9;
			final int h = yMax - ( int ) Math.ceil( bands[ i ] * yMax );
			int y = yMax;
			do {
				drawRow( x, y );
				y -= 2;
			}
			while( y >= h );
		}

		textureBuffer.setPixels( 0, 0, Width, Height, pixels, 0 );
		textureBuffer.storePixels();
	}

	private void drawRow( final int x, final int y ) {
		setPixel( x, y, 0x0000ff );
		setPixel( x + 2, y, 0x0000ff );
		setPixel( x + 4, y, 0x0000ff );
		setPixel( x + 6, y, 0x0000ff );
	}

	private void setPixel( final int x, final int y, final int color ) {
		int index = ( x + y * Width ) * 3;
		pixels[ index++ ] = ( byte ) ( ( color >> 16 ) & 0xFF );
		pixels[ index++ ] = ( byte ) ( ( color >> 8 ) & 0xFF );
		pixels[ index ] = ( byte ) ( color & 0xFF );
	}
}