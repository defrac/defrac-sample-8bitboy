package com.audiotool.bitboy.ui;

import defrac.display.Quad;
import defrac.display.event.UIActionEvent;
import defrac.display.event.UIEvent;
import defrac.display.event.UIEventTarget;
import defrac.display.event.UIEventType;
import defrac.geom.Point;
import defrac.geom.Rectangle;
import defrac.lang.Procedure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andre Michelle
 */
final class Slider extends Quad
{
	static final int Width = 45;
	static final int Height = 5;

	private final Point local = new Point();
	private final Rectangle bounds = new Rectangle( 0, 0, Width, Height );

	private Procedure<Slider> procedure;
	private boolean processing;
	private float value = 0.0f;

	Slider()
	{
		super( Width, Height, 0xFFFFFFFF );
	}

	void value( final float value )
	{
		if( this.value == value )
			return;

		width( Math.round( value * bounds.width ) );

		this.value = value;

		if( null != procedure )
			procedure.apply( this );
	}

	float value()
	{
		return value;
	}

	void onChange( @Nullable final Procedure<Slider> procedure )
	{
		this.procedure = procedure;
	}

	@Nullable
	@Override
	public UIEventTarget captureEventTarget( @Nonnull final Point point )
	{
		globalToLocal( point, local );

		if( bounds.contains( local ) )
			return this;

		return null;
	}

	@Override
	protected void processEvent( @Nonnull final UIEvent event )
	{
		if( event.type == UIEventType.ACTION_BEGIN )
		{
			processing = true;

			translate( ( ( UIActionEvent ) event ).pos );
		}
		else if( event.type == UIEventType.ACTION_MOVE )
		{
			if( !processing )
				return;

			translate( ( ( UIActionEvent ) event ).pos );
		}
		else if( event.type == UIEventType.ACTION_END )
		{
			processing = false;
		}
	}

	private void translate( @Nonnull final Point global )
	{
		globalToLocal( global, local );

		value( Math.min( 1.0f, Math.max( 0.0f, local.x / bounds.width ) ) );
	}
}
