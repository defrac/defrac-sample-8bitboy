package com.audiotool.bitboy.ui;

import defrac.display.Texture;
import defrac.display.TextureData;

import javax.annotation.Nonnull;

/**
 * Converted from TexturePacker JSON-Array
 *
 * @author André Michelle
 */
public final class TextureAtlas {
	private final TextureData textureData;

	public TextureAtlas( @Nonnull final TextureData textureData ) {
		this.textureData = textureData;
	}

	Texture Background() {
		return new Texture( textureData, 1, 1, 150, 45, 0, 0, 0, 150, 45 );
	}

	Texture[] buttonLinear() {
		return new Texture[]{
				new Texture( textureData, 219, 11, 21, 9, 0, 0, 0, 21, 9 ),
				new Texture( textureData, 197, 11, 21, 9, 0, 0, 0, 21, 9 )
		};
	}

	Texture[] buttonLoop() {
		return new Texture[]{
				new Texture( textureData, 175, 17, 21, 9, 0, 0, 0, 21, 9 ),
				new Texture( textureData, 219, 1, 21, 9, 0, 0, 0, 21, 9 )
		};
	}

	Texture buttonMute() {
		return new Texture( textureData, 241, 9, 8, 7, 0, 0, 0, 8, 7 );
	}

	Texture buttonUnMute() {
		return new Texture( textureData, 241, 1, 8, 7, 0, 0, 0, 8, 7 );
	}

	Texture[] buttonNext() {
		return new Texture[]{
				new Texture( textureData, 197, 1, 21, 9, 0, 0, 0, 21, 9 ),
				new Texture( textureData, 175, 1, 21, 9, 0, 0, 0, 21, 9 )
		};
	}

	Texture[] buttonNormal() {
		return new Texture[]{
				new Texture( textureData, 152, 31, 22, 9, 0, 0, 0, 22, 9 ),
				new Texture( textureData, 152, 21, 22, 9, 0, 0, 0, 22, 9 )
		};
	}

	Texture[] buttonPause() {
		return new Texture[]{
				new Texture( textureData, 115, 47, 37, 9, 0, 0, 0, 37, 9 ),
				new Texture( textureData, 77, 47, 37, 9, 0, 0, 0, 37, 9 )
		};
	}

	Texture[] buttonPlay() {
		return new Texture[]{
				new Texture( textureData, 39, 47, 37, 9, 0, 0, 0, 37, 9 ),
				new Texture( textureData, 1, 47, 37, 9, 0, 0, 0, 37, 9 )
		};
	}

	Texture[] buttonPrev() {
		return new Texture[]{
				new Texture( textureData, 175, 37, 20, 9, 0, 0, 0, 20, 9 ),
				new Texture( textureData, 175, 27, 20, 9, 0, 0, 0, 20, 9 )
		};
	}

	Texture[] buttonShuffle() {
		return new Texture[]{
				new Texture( textureData, 152, 11, 22, 9, 0, 0, 0, 22, 9 ),
				new Texture( textureData, 152, 1, 22, 9, 0, 0, 0, 22, 9 )
		};
	}

	Texture[] buttonStop() {
		return new Texture[]{
				new Texture( textureData, 153, 51, 21, 9, 0, 0, 0, 21, 9 ),
				new Texture( textureData, 153, 41, 21, 9, 0, 0, 0, 21, 9 )
		};
	}
}