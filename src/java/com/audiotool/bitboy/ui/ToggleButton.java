package com.audiotool.bitboy.ui;

import defrac.display.Image;
import defrac.display.Texture;
import defrac.display.event.UIEvent;
import defrac.display.event.UIEventType;
import defrac.display.event.UIProcessHook;
import defrac.lang.Procedure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andre Michelle
 */
final class ToggleButton extends Image implements UIProcessHook {
	private final Texture[] normal;
	private final Texture[] active;

	@Nullable
	private Procedure<ToggleButton> click;

	private boolean isActive;
	private boolean over;

	ToggleButton( @Nonnull final Texture normal, @Nonnull final Texture active ) {
		this( new Texture[]{ normal }, new Texture[]{ active } );
	}

	ToggleButton( @Nonnull final Texture[] normal, @Nonnull final Texture[] active ) {
		this.normal = normal;
		this.active = active;

		update();
	}

	@Override
	public void processEvent( @Nonnull final UIEvent event ) {
		if( event.type == UIEventType.MOUSE_IN ) {
			over = true;
			update();
		}
		else if( event.type == UIEventType.MOUSE_OUT ) {
			over = false;
			update();
		}
		else if( event.type == UIEventType.ACTION_SINGLE ) {
			if( null != click ) {
				isActive( !isActive );
				click.apply( this );
			}
		}
	}

	@Nonnull
	ToggleButton onClick( @Nullable final Procedure<ToggleButton> callback ) {
		click = callback;
		return this;
	}

	void isActive( final boolean value ) {
		if( isActive == value )
			return;
		isActive = value;
		update();
	}

	boolean isActive() {
		return isActive;
	}

	private void update() {
		final Texture[] texture = isActive ? active : normal;
		final int index = over ? texture.length - 1 : 0;
		texture( texture[ index ] );
	}
}