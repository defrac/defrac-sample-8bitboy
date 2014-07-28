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
class Button extends Image implements UIProcessHook
{
	private final Texture[] textures;

	@Nullable
	private Procedure<Button> click;

	private boolean over;

	Button( @Nonnull final Texture[] textures )
	{
		this.textures = textures;

		update();
	}

	@Nonnull
	Button click( @Nullable final Procedure<Button> callback )
	{
		click = callback;

		return this;
	}

	@Override
	public void processEvent( @Nonnull final UIEvent event )
	{
		if( event.type == UIEventType.MOUSE_IN )
		{
			over = true;

			update();
		}
		else if( event.type == UIEventType.MOUSE_OUT )
		{
			over = false;

			update();
		}
		else if( event.type == UIEventType.ACTION_SINGLE )
		{
			if( null != click )
				click.apply( this );
		}
	}

	private void update()
	{
		texture( textures[ over ? 1 : 0 ] );
	}
}