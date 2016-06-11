package com.audiotool.bitboy.ui;

import defrac.display.Image;
import defrac.display.Label;
import defrac.display.Layer;
import defrac.filter.ColorMatrixFilter;
import defrac.text.BitmapFont;

import javax.annotation.Nonnull;

/**
 * @author Andre Michelle
 */
public final class Design extends Layer {
	public final Button buttonStop;
	public final Button buttonPrev;
	public final Button buttonNext;
	public final ToggleButton toggleButtonMute;
	public final ToggleButton toggleButtonPlay;
	public final ToggleButton toggleButtonShuffle;
	public final ToggleButton toggleButtonLoop;
	public final Slider sliderVolume;
	public final Label labelVolume;
	public final Frequencies frequencies;
	public final Label labelInfo;

	public Design( @Nonnull final TextureAtlas textureAtlas, @Nonnull final BitmapFont bitmapFont ) {
		addChild( new Image( textureAtlas.Background() ) );
		buttonStop = new Button( textureAtlas.buttonStop() );
		addChild( buttonStop.moveTo( 39, 48 ) );
		buttonPrev = new Button( textureAtlas.buttonPrev() );
		addChild( buttonPrev.moveTo( 62, 48 ) );
		buttonNext = new Button( textureAtlas.buttonNext() );
		addChild( buttonNext.moveTo( 82, 48 ) );
		toggleButtonMute = new ToggleButton( textureAtlas.buttonMute(), textureAtlas.buttonUnMute() );
		addChild( toggleButtonMute.moveTo( 134, 4 ) );
		toggleButtonMute.isActive( true );
		toggleButtonPlay = new ToggleButton( textureAtlas.buttonPause(), textureAtlas.buttonPlay() );
		addChild( toggleButtonPlay.moveTo( 0, 48 ) );
		toggleButtonShuffle = new ToggleButton( textureAtlas.buttonNormal(), textureAtlas.buttonShuffle() );
		addChild( toggleButtonShuffle.moveTo( 128, 48 ) );
		toggleButtonLoop = new ToggleButton( textureAtlas.buttonLinear(), textureAtlas.buttonLoop() );
		addChild( toggleButtonLoop.moveTo( 105, 48 ) );
		sliderVolume = new Slider();
		addChild( sliderVolume.moveTo( 61, 5 ) );
		labelVolume = new Label().font( bitmapFont ).color( 0xFF0000FF ).autoSize( Label.AutoSize.AUTO );
		addChild( labelVolume.moveTo( 115, 4 ) );
		frequencies = Frequencies.create();
		addChild( frequencies.moveTo( 4, 29 ) );
		labelInfo = new Label().font( bitmapFont ).color( 0xFF0000FF ).width( 138 );
		addChild( labelInfo.moveTo( 6, 16 ) );
		filter( createColorTransform( 0xFF000000, 0x00FFFFFF ) );
	}

	@Nonnull
	private static ColorMatrixFilter createColorTransform( final int background, final int foreground ) {
		final ColorMatrixFilter colorMatrixFilter = new ColorMatrixFilter();
		final float a0 = ( background >> 24 & 0xff ) / 255.0f;
		final float r0 = ( background >> 16 & 0xff ) / 255.0f;
		final float g0 = ( background >> 8 & 0xff ) / 255.0f;
		final float b0 = ( background & 0xff ) / 255.0f;
		final float a1 = ( foreground >> 24 & 0xff ) / 255.0f;
		final float r1 = ( foreground >> 16 & 0xff ) / 255.0f;
		final float g1 = ( foreground >> 8 & 0xff ) / 255.0f;
		final float b1 = ( foreground & 0xff ) / 255.0f;
		final float[] values = colorMatrixFilter.matrix().values;
		values[ 0 ] = 0.0f;
		values[ 1 ] = r0;
		values[ 2 ] = r1;
		values[ 3 ] = 0.0f;
		values[ 4 ] = 0.0f;
		values[ 5 ] = 0.0f;
		values[ 6 ] = g0;
		values[ 7 ] = g1;
		values[ 8 ] = 0.0f;
		values[ 9 ] = 0.0f;
		values[ 10 ] = 0.0f;
		values[ 11 ] = b0;
		values[ 12 ] = b1;
		values[ 13 ] = 0.0f;
		values[ 14 ] = 0.0f;
		values[ 15 ] = 0.0f;
		values[ 16 ] = a0 - 1.0f;
		values[ 17 ] = a1 - 1.0f;
		values[ 18 ] = 1.0f;
		values[ 19 ] = 0.0f;
		return colorMatrixFilter;
	}
}