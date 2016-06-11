package com.audiotool.bitboy.playlist;

import com.audiotool.bitboy.format.Format;
import com.audiotool.bitboy.format.FormatParser;
import com.audiotool.bitboy.model.Model;
import defrac.lang.Procedure;
import defrac.resource.BinaryResource;
import defrac.resource.ResourceGroup;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Andre Michelle
 */
public final class Playlist {
	@Nonnull
	public static void fromResources( @Nonnull final Procedure<Playlist> onComplete, @Nonnull final String... paths ) {
		final int n = paths.length;

		final ArrayList<Format> list = new ArrayList<Format>( n );

		final ResourceGroup<byte[]> group = new ResourceGroup<byte[]>( n );

		for( int i = 0 ; i < n ; ++i )
			group.add( BinaryResource.from( paths[ i ] ) );

		group.listener( new ResourceGroup.SimpleListener<byte[]>() {
			@Override
			public void onResourceGroupError( @Nonnull final ResourceGroup<byte[]> resourceGroup, @Nonnull final Throwable reason ) {
				reason.printStackTrace();
			}

			@Override
			public void onResourceGroupComplete( @Nonnull final ResourceGroup<byte[]> resourceGroup, @Nonnull final List<byte[]> content ) {
				for( final byte[] bytes : content ) {
					try {
						list.add( FormatParser.parse( bytes ) );
					}
					catch( Throwable t ) {
						t.printStackTrace();
					}
				}

				onComplete.apply( new Playlist( list ) );
			}
		} );

		group.load();
	}

	public final Model<Boolean> shuffle = Model.create( false );

	private final Random random = new Random( 0xFFF );

	private final ArrayList<Format> list;

	private int index = 0;

	private Playlist( @Nonnull final ArrayList<Format> list ) {
		this.list = list;
	}

	@Nonnull
	public Format current() {
		return list.get( index );
	}

	@Nonnull
	public Format prev() {
		if( shuffle.getValue() )
			shuffleIndex();
		else
			decrementIndex();

		return current();
	}

	@Nonnull
	public Format next() {
		if( shuffle.getValue() )
			shuffleIndex();
		else
			incrementIndex();

		return current();
	}

	private void decrementIndex() {
		index = 0 == index ? list.size() - 1 : index - 1;
	}

	private void incrementIndex() {
		index = list.size() - 1 == index ? 0 : index + 1;
	}

	private void shuffleIndex() {
		index = random.nextInt( list.size() );
	}
}