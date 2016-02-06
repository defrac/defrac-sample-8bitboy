import com.audiotool.bitboy.dsp.Player;
import com.audiotool.bitboy.playlist.Playlist;
import com.audiotool.bitboy.ui.Design;
import com.audiotool.bitboy.ui.TextureAtlas;
import com.audiotool.bitboy.ui.UserInterface;
import defrac.display.*;
import defrac.display.event.raw.EnterFrameEvent;
import defrac.event.EventListener;
import defrac.resource.TextureDataResource;
import defrac.text.BitmapFont;
import defrac.ui.ContentScreen;
import defrac.ui.DisplayList;
import defrac.ui.FrameBuilder;

import javax.annotation.Nonnull;

/**
 * @author Andre Michelle
 */
public final class Boot extends ContentScreen
{
	public static void main( final String[] arguments )
	{
		FrameBuilder.
				forScreen( new Boot() ).
				containerById( "screen" ).
				show();
	}

	private Stage stage;

	private Boot() {
		final DisplayList displayList = new DisplayList();
		displayList.onStageReady(this::onStart, this::onCreationFailure);

		rootView(displayList);
	}

	private void onStart(Stage stage) {
		this.stage = stage;
		stage.backgroundColor(0xFFFFFF);
		load();
	}

	private void onCreationFailure( @Nonnull final Throwable reason )
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
				Playlist.fromResources(playlist -> BitmapFont.fromFnt( "fonts/bitboy.fnt", "fonts/bitboy.png" ).
            onSuccess(bitmapFont -> run( playlist, textureData, bitmapFont )).
            onFailure(Throwable::printStackTrace),
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

		webAudio.connect((left, right, length) -> player.render( left, right, 0, length ));

		stage.globalEvents().onEnterFrame.add( new EventListener<EnterFrameEvent>()
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

		stage.addChild( design );

		UserInterface.glue(design, player, playlist );

		player.applyFormat( playlist.current() );
	}
}
