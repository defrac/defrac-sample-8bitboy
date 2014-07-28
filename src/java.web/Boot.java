import com.audiotool.bitboy.dsp.Player;
import com.audiotool.bitboy.playlist.Playlist;
import com.audiotool.bitboy.ui.Design;
import com.audiotool.bitboy.ui.TextureAtlas;
import com.audiotool.bitboy.ui.UserInterface;
import defrac.app.Bootstrap;
import defrac.app.GenericApp;
import defrac.display.TextureData;
import defrac.display.TextureDataFormat;
import defrac.display.TextureDataRepeat;
import defrac.display.TextureDataSmoothing;
import defrac.event.EnterFrameEvent;
import defrac.event.EventListener;
import defrac.event.Events;
import defrac.lang.Procedure;
import defrac.resource.TextureDataResource;
import defrac.text.BitmapFont;

import javax.annotation.Nonnull;

/**
 * @author Andre Michelle
 */
public final class Boot extends GenericApp
{
	public static void main( final String[] arguments )
	{
		Bootstrap.configure( new Boot() ).
				run();
	}

	@Override
	protected void onStart()
	{
		backgroundColor( 0xFFFFFF );

		load();
	}

	@Override
	protected void onCreationFailure( @Nonnull final Throwable reason )
	{
		reason.printStackTrace();
	}

	private WebAudio webAudio = null;
	private Player player = null;
	private Design design = null;

	private void load()
	{
		TextureDataResource.from( "texture/TextureAtlas.png", TextureDataFormat.RGBA, TextureDataRepeat.NO_REPEAT, TextureDataSmoothing.NO_SMOOTHING ).listener( new TextureDataResource.SimpleListener()
		{
			@Override
			public void onResourceComplete( @Nonnull final TextureDataResource resource, @Nonnull final TextureData textureData )
			{
				Playlist.fromResources( new Procedure<Playlist>()
				{
					@Override
					public void apply( @Nonnull final Playlist playlist )
					{
						BitmapFont.fromFnt( "fonts/bitboy.fnt", "fonts/bitboy.png" ).
								onSuccess( new Procedure<BitmapFont>()
								{
									@Override
									public void apply( @Nonnull final BitmapFont bitmapFont )
									{
										run( playlist, textureData, bitmapFont );
									}
								} ).
								onFailure( new Procedure<Throwable>()
								{
									@Override
									public void apply( final Throwable throwable )
									{
										throwable.printStackTrace();
									}
								} );
					}
				},
						"mod/class11.mod",
						"mod/-super_mario_land.mod",
						"mod/agnostic.mod",
						"mod/complex.mod",
						"mod/delicate.mod",
						"mod/goto8o-dansa_i_neon.mod",
						"mod/rainfore.mod",
						"mod/sac06.mod",
						"mod/sac14.mod",
						"mod/wotw-commodore_rulez.mod"
				);
			}
		} ).load();
	}

	private void run(
			@Nonnull final Playlist playlist,
			@Nonnull final TextureData textureData,
			@Nonnull final BitmapFont bitmapFont )
	{
		webAudio = new WebAudio();

		player = new Player( webAudio.sampleRate() );

		webAudio.connect( new WebAudio.Source()
		{
			@Override
			public void render( @Nonnull final double[] left, @Nonnull final double[] right, final int length )
			{
				player.render( left, right, 0, length );
			}
		} );

		Events.onEnterFrame.add( new EventListener<EnterFrameEvent>()
		{
			final double[] bands = new double[ 16 ];

			@Override
			public void onEvent( final EnterFrameEvent ignore )
			{
				webAudio.getFrequencyBands( bands );

				design.frequencies.render( bands );
			}
		} );

		design = new Design( new TextureAtlas( textureData ), bitmapFont );

		UserInterface.glue(
				stage().addChild(
						design ), player, playlist );

		player.applyFormat( playlist.current() );
	}
}