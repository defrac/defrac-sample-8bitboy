package com.audiotool.bitboy.model;

import defrac.event.EventDispatcher;
import defrac.event.EventListener;

import javax.annotation.Nonnull;

/**
 * @author Andre Michelle
 */
public final class Model<T> {
	public static <T> Model<T> create( @Nonnull final T value ) {
		return new Model<T>( value );
	}

	private final EventDispatcher<Model<T>> dispatcher;

	private T value;

	private Model( @Nonnull final T value ) {
		this.value = value;

		dispatcher = new EventDispatcher<>();
	}

	public void setValue( @Nonnull final T value ) {
		if( this.value != value ) {
			this.value = value;
			dispatcher.dispatch( this );
		}
	}

	@Nonnull
	public T getValue() {
		return value;
	}

	public void setListenerAndDispatch( @Nonnull final EventListener<Model<T>> listener ) {
		dispatcher.add( listener );
		listener.onEvent( this );
	}
}