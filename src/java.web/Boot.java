import com.audiotool.bitboy.dsp.Player;
import com.audiotool.bitboy.playlist.Playlist;
import com.audiotool.bitboy.ui.Design;
import com.audiotool.bitboy.ui.TextureAtlas;
import com.audiotool.bitboy.ui.UserInteraction;
import defrac.concurrent.Dispatchers;
import defrac.display.TextureData;
import defrac.resource.TextureDataResource;
import defrac.text.BitmapFont;
import defrac.ui.ContentScreen;
import defrac.ui.DisplayList;
import defrac.ui.FrameBuilder;
import defrac.ui.FrameLayout;

import javax.annotation.Nonnull;

import static defrac.display.TextureDataFormat.RGBA;
import static defrac.display.TextureDataRepeat.NO_REPEAT;
import static defrac.display.TextureDataSmoothing.NO_SMOOTHING;
import static defrac.ui.Gravity.CENTER;

/**
 * @author Andre Michelle
 */
public final class Boot extends ContentScreen {
	public static void main( final String[] arguments ) {
		FrameBuilder.
				forScreen( new Boot() ).
				title( "8Bitboy" ).
				show();
	}

	private static final WebAudio webAudio = new WebAudio();

	@Override
	protected void onStart() {
		super.onStart();

		TextureDataResource.from( "texture/TextureAtlas.png", RGBA, NO_REPEAT, NO_SMOOTHING ).
				listener( new TextureDataResource.SimpleListener() {
					@Override
					public void onResourceComplete( @Nonnull final TextureDataResource resource,
													@Nonnull final TextureData textureData ) {
						Playlist.fromResources( playlist -> BitmapFont.fromFnt( "fonts/bitboy.fnt", "fonts/bitboy.png" ).
										onSuccess( bitmapFont -> start( playlist, new TextureAtlas( textureData ), bitmapFont ) ),
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

	private void start(
			@Nonnull final Playlist playlist,
			@Nonnull final TextureAtlas textureAtlas,
			@Nonnull final BitmapFont bitmapFont ) {

		final Player player = new Player( webAudio.sampleRate() );
		player.applyFormat( playlist.current() );

		webAudio.connect( ( left, right, length ) -> player.render( left, right, 0, length ) );

		final FrameLayout layout = new FrameLayout();
		final DisplayList displayList = new DisplayList();
		displayList.
				layoutConstraints( new FrameLayout.LayoutConstraints( 150, 57 ).
				gravity( CENTER ) );
		displayList.root().onSuccess( stage -> {
			final Design design	 = new Design( stage, textureAtlas, bitmapFont );
			UserInteraction.glue( design, player, playlist );
			Dispatchers.FOREGROUND.exec( new Runnable() {
				final double[] bands = new double[ 16 ];

				@Override
				public void run() {
					webAudio.getFrequencyBands( bands );
					design.frequencies.render( bands );
					Dispatchers.FOREGROUND.exec( this );
				}
			} );
		} );

		layout.addView( displayList );
		rootView( layout );
	}
}