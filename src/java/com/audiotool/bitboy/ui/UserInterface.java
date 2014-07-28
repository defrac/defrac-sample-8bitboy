package com.audiotool.bitboy.ui;

import com.audiotool.bitboy.dsp.Player;
import com.audiotool.bitboy.format.Format;
import com.audiotool.bitboy.model.Model;
import com.audiotool.bitboy.playlist.Playlist;
import defrac.event.EventListener;
import defrac.lang.Procedure;
import defrac.util.Timer;

import javax.annotation.Nonnull;

/**
 * @author Andre Michelle
 */
public final class UserInterface
{
	public static void glue( @Nonnull final Design design, @Nonnull final Player player, @Nonnull final Playlist playlist )
	{
		//
		// Design Events
		//
		design.toggleButtonPlay.onClick( new Procedure<ToggleButton>()
		{
			@Override
			public void apply( @Nonnull final ToggleButton toggleButton )
			{
				player.pause.setValue( toggleButton.isActive() );
			}
		} );
		design.toggleButtonLoop.onClick( new Procedure<ToggleButton>()
		{
			@Override
			public void apply( @Nonnull final ToggleButton toggleButton )
			{
				player.loopMode.setValue( toggleButton.isActive() );
			}
		} );
		design.toggleButtonMute.onClick( new Procedure<ToggleButton>()
		{
			@Override
			public void apply( @Nonnull final ToggleButton toggleButton )
			{
				player.mute.setValue( toggleButton.isActive() );
			}
		} );
		design.toggleButtonShuffle.onClick( new Procedure<ToggleButton>()
		{
			@Override
			public void apply( @Nonnull final ToggleButton toggleButton )
			{
				playlist.shuffle.setValue( toggleButton.isActive() );
			}
		} );
		design.sliderVolume.onChange( new Procedure<Slider>()
		{
			@Override
			public void apply( @Nonnull final Slider slider )
			{
				player.volume.setValue( ( double ) slider.value() );
			}
		} );
		design.buttonNext.click( new Procedure<Button>()
		{
			@Override
			public void apply( @Nonnull final Button ignore )
			{
				player.applyFormat( playlist.next() );
			}
		} );
		design.buttonPrev.click( new Procedure<Button>()
		{
			@Override
			public void apply( @Nonnull final Button ignore )
			{
				player.applyFormat( playlist.prev() );
			}
		} );
		design.buttonStop.click( new Procedure<Button>()
		{
			@Override
			public void apply( @Nonnull final Button ignore )
			{
				player.pause.setValue( true );
				player.rewind();
			}
		} );

		playlist.shuffle.setDispatch( new EventListener<Model<Boolean>>()
		{
			@Override
			public void onEvent( final Model<Boolean> booleanModel )
			{
				design.toggleButtonShuffle.isActive( booleanModel.getValue() );
			}
		} );

		//
		// Player Settings
		//
		player.mute.setDispatch( new EventListener<Model<Boolean>>()
		{
			@Override
			public void onEvent( @Nonnull final Model<Boolean> booleanModel )
			{
				design.toggleButtonMute.isActive( booleanModel.getValue() );
			}
		} );

		player.volume.setDispatch( new EventListener<Model<Double>>()
		{
			@Override
			public void onEvent( final Model<Double> doubleModel )
			{
				final Double value = doubleModel.getValue();

				design.sliderVolume.value( value.floatValue() );
				design.labelVolume.text( Integer.toString( ( int ) Math.round( value * 100.0 ) ) );
			}
		} );

		player.pause.setDispatch( new EventListener<Model<Boolean>>()
		{
			@Override
			public void onEvent( @Nonnull final Model<Boolean> booleanModel )
			{
				design.toggleButtonPlay.isActive( booleanModel.getValue() );
			}
		} );

		player.loopMode.setDispatch( new EventListener<Model<Boolean>>()
		{
			@Override
			public void onEvent( @Nonnull final Model<Boolean> booleanModel )
			{
				design.toggleButtonLoop.isActive( booleanModel.getValue() );
			}
		} );

		//
		// Player Events
		//
		player.onModLoad.add( new EventListener<Format>()
		{
			Format current;

			int creditIndex = 0;

			final Timer timer = new Timer( 2000, 0 ).listener( new Timer.SimpleListener()
			{
				@Override
				public void onTimerTick( @Nonnull final Timer timer )
				{
					final String[] credits = current.credits;

					final String output;

					if( creditIndex < credits.length )
					{
						output = credits[ creditIndex++ ];
					}
					else
					{
						output = current.title;

						creditIndex = 0;
					}

					design.labelInfo.text( output );
				}
			} );

			@Override
			public void onEvent( @Nonnull final Format format )
			{
				current = format;

				creditIndex = 0;

				design.labelInfo.text( current.title );

				timer.resetAndStart();
			}
		} );

		player.onModComplete.add( new EventListener<Player>()
		{
			@Override
			public void onEvent( @Nonnull final Player player )
			{
				player.applyFormat( playlist.next() );
			}
		} );
	}
}