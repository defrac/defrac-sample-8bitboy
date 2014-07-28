package com.audiotool.bitboy.format;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * @author Joa Ebert
 */
public final class Step
{
	static Step parse( @Nonnull final ByteBuffer buffer )
	{
		/*
			Byte 0    Byte 1   Byte 2   Byte 3
			aaaaBBBB CCCCCCCCC DDDDeeee FFFFFFFFF

			aaaaDDDD     = sample number
			BBBBCCCCCCCC = sample period value
			eeee         = effect number
			FFFFFFFF     = effect parameters
		*/

		final int b0 = buffer.get();
		final int b1 = buffer.get() & 0xFF;
		final int b2 = buffer.get() & 0xFF;
		final int b3 = buffer.get() & 0xFF;

		final int sampleIndex = ( b0 & 0xf0 ) | ( b2 >> 4 );
		final int period = ( ( b0 & 0x0f ) << 8 ) | b1;
		final int effect = b2 & 0x0F;
		final int effectParam = b3;

		return new Step( effect, effectParam, period, sampleIndex - 1 );
	}

	public final int effect;
	public final int effectParam;
	public final int period;
	public final int sampleIndex;

	Step( final int effect, final int effectParam, final int period, final int sampleIndex )
	{
		this.effect = effect;
		this.effectParam = effectParam;
		this.period = period;
		this.sampleIndex = sampleIndex;
	}

	@Override
	public String toString()
	{
		return "[Step" +
				" effect: " + effect +
				", effectParam: " + effectParam +
				", period: " + period +
				", sampleIndex: " + sampleIndex +
				"]";
	}
}