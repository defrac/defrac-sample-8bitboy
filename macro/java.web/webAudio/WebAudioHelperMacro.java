package webAudio;

import defrac.compiler.Context;
import defrac.compiler.macro.Macro;
import defrac.compiler.macro.MethodBody;
import defrac.compiler.macro.Parameter;

import javax.annotation.Nonnull;

/**
 * @author Andre Michelle
 */
public final class WebAudioHelperMacro extends Macro
{
	public WebAudioHelperMacro( @Nonnull final Context context )
	{
		super( context );
	}

	@Nonnull
	public MethodBody writeAudio(
			@Nonnull final Parameter target0,
			@Nonnull final Parameter target1,
			@Nonnull final Parameter source0,
			@Nonnull final Parameter source1,
			@Nonnull final Parameter length )
	{
		return MethodBody(
				Local( "i", IntTypeReference(), Int( 0 ) ),
				Local( "n", IntTypeReference(), LocalGet( length ) ),
				While( LT( LocalGet( "i" ), LocalGet( "n" ) ),
						Block(
								Untyped( VoidTypeReference(), "${0}[${2}]=${1}[${2}]", LocalGet( target0 ), LocalGet( source0 ), LocalGet( "i" ) ),
								Untyped( VoidTypeReference(), "${0}[${2}]=${1}[${2}]", LocalGet( target1 ), LocalGet( source1 ), LocalGet( "i" ) ),
								LocalSet( "i", ADD( LocalGet( "i" ), Int( 1 ) ) ) ) ) );
	}

	@Nonnull
	public MethodBody writeFrequency(
			@Nonnull final Parameter target,
			@Nonnull final Parameter source,
			@Nonnull final Parameter length )
	{
		return MethodBody(
				Local( "i", IntTypeReference(), Int( 0 ) ),
				Local( "n", IntTypeReference(), LocalGet( length ) ),
				While( LT( LocalGet( "i" ), LocalGet( "n" ) ),
						Block(
								Untyped( VoidTypeReference(), "${0}[${2}]=${1}[${2}]", LocalGet( target ), LocalGet( source ), LocalGet( "i" ) ),
								LocalSet( "i", ADD( LocalGet( "i" ), Int( 1 ) ) ) ) ) );
	}
}
