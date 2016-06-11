package com.audiotool.bitboy.ui;

import defrac.display.DisplayObjectContainer;
import defrac.display.Image;
import defrac.display.Label;
import defrac.filter.ColorMatrixFilter;
import defrac.text.BitmapFont;

import javax.annotation.Nonnull;

/**
 * @author Andre Michelle
 */
public final class Design {
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

	public Design( @Nonnull final DisplayObjectContainer container,
				   @Nonnull final TextureAtlas textureAtlas,
				   @Nonnull final BitmapFont bitmapFont ) {
		container.addChild( new Image( textureAtlas.Background() ) );
		buttonStop = new Button( textureAtlas.buttonStop() );
		container.addChild( buttonStop.moveTo( 39, 48 ) );
		buttonPrev = new Button( textureAtlas.buttonPrev() );
		container.addChild( buttonPrev.moveTo( 62, 48 ) );
		buttonNext = new Button( textureAtlas.buttonNext() );
		container.addChild( buttonNext.moveTo( 82, 48 ) );
		toggleButtonMute = new ToggleButton( textureAtlas.buttonMute(), textureAtlas.buttonUnMute() );
		container.addChild( toggleButtonMute.moveTo( 134, 4 ) );
		toggleButtonMute.isActive( true );
		toggleButtonPlay = new ToggleButton( textureAtlas.buttonPause(), textureAtlas.buttonPlay() );
		container.addChild( toggleButtonPlay.moveTo( 0, 48 ) );
		toggleButtonShuffle = new ToggleButton( textureAtlas.buttonNormal(), textureAtlas.buttonShuffle() );
		container.addChild( toggleButtonShuffle.moveTo( 128, 48 ) );
		toggleButtonLoop = new ToggleButton( textureAtlas.buttonLinear(), textureAtlas.buttonLoop() );
		container.addChild( toggleButtonLoop.moveTo( 105, 48 ) );
		sliderVolume = new Slider();
		container.addChild( sliderVolume.moveTo( 61, 5 ) );
		labelVolume = new Label().font( bitmapFont ).color( 0xFF0000FF ).autoSize( Label.AutoSize.AUTO );
		container.addChild( labelVolume.moveTo( 115, 4 ) );
		frequencies = Frequencies.create();
		container.addChild( frequencies.moveTo( 4, 29 ) );
		labelInfo = new Label().font( bitmapFont ).color( 0xFF0000FF ).width( 138 );
		container.addChild( labelInfo.moveTo( 6, 16 ) );
		container.filter( createColorTransform( 0xFF000000, 0x00FFFFFF ) );
	}

	@Nonnull
	private static ColorMatrixFilter createColorTransform( final int background,
														   final int foreground ) {
		final float a0 = ( background >> 24 & 0xff ) / 255f;
		final float r0 = ( background >> 16 & 0xff ) / 255f;
		final float g0 = ( background >> 8 & 0xff ) / 255f;
		final float b0 = ( background & 0xff ) / 255f;
		final float a1 = ( foreground >> 24 & 0xff ) / 255f;
		final float r1 = ( foreground >> 16 & 0xff ) / 255f;
		final float g1 = ( foreground >> 8 & 0xff ) / 255f;
		final float b1 = ( foreground & 0xff ) / 255f;
		final float[] matrix = {
				0f, r0, r1, 0f, 0f,
				0f, g0, g1, 0f, 0f,
				0f, b0, b1, 0f, 0f,
				0f, a0 - 1f, a1 - 1f, 1f, 0f
		};
		return new ColorMatrixFilter( matrix );
	}
}