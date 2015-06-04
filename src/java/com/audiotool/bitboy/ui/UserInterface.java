package com.audiotool.bitboy.ui;

import com.audiotool.bitboy.dsp.Player;
import com.audiotool.bitboy.format.Format;
import com.audiotool.bitboy.playlist.Playlist;
import defrac.event.EventListener;
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
		design.toggleButtonPlay.onClick( toggleButton -> player.pause.setValue( toggleButton.isActive() ) );
		design.toggleButtonLoop.onClick( toggleButton -> player.loopMode.setValue( toggleButton.isActive() ) );
		design.toggleButtonMute.onClick( toggleButton -> player.mute.setValue( toggleButton.isActive() ) );
		design.toggleButtonShuffle.onClick( toggleButton -> playlist.shuffle.setValue( toggleButton.isActive() ) );
		design.sliderVolume.onChange( slider -> player.volume.setValue( ( double ) slider.value() ) );
		design.buttonNext.click( ignore -> player.applyFormat( playlist.next() ) );
		design.buttonPrev.click( ignore -> player.applyFormat( playlist.prev() ) );
		design.buttonStop.click( ignore -> {
      player.pause.setValue( true );
      player.rewind();
    });

		playlist.shuffle.setDispatch( booleanModel -> design.toggleButtonShuffle.isActive( booleanModel.getValue() ) );

		//
		// Player Settings
		//
		player.mute.setDispatch( booleanModel -> design.toggleButtonMute.isActive( booleanModel.getValue() ) );

		player.volume.setDispatch( doubleModel -> {
      final Double value = doubleModel.getValue();

      design.sliderVolume.value( value.floatValue() );
      design.labelVolume.text( Integer.toString( ( int ) Math.round( value * 100.0 ) ) );
    } );

		player.pause.setDispatch( booleanModel -> design.toggleButtonPlay.isActive( booleanModel.getValue() ) );

		player.loopMode.setDispatch( booleanModel -> design.toggleButtonLoop.isActive( booleanModel.getValue() ) );

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

		player.onModComplete.add( p -> p.applyFormat(playlist.next()) );
	}
}
