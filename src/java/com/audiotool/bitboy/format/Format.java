package com.audiotool.bitboy.format;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author Joa Ebert
 */
public final class Format {
	public final String title;
	public final Waveform[] waveforms;
	public final Step[][][] patterns;
	public final String[] credits;
	public final int[] sequence;
	public final int numPatterns;
	public final int type;

	Format(
			@Nonnull final String title,
			@Nonnull final Waveform[] waveforms,
			@Nonnull final Step[][][] patterns,
			@Nonnull final String[] credits,
			@Nonnull final int[] sequence,
			final int numPatterns,
			final int type ) {
		this.credits = credits;
		this.title = title;
		this.waveforms = waveforms;
		this.patterns = patterns;
		this.sequence = sequence;
		this.numPatterns = numPatterns;
		this.type = type;
	}

	@Nonnull
	public Step getStepAt( final int patternIndex, final int rowIndex, final int channelIndex ) {
		return patterns[ patternIndex ][ rowIndex ][ channelIndex ];
	}

	public int getSequenceAt( final int sequenceIndex ) {
		return sequence[ sequenceIndex ];
	}

	public int getPatternLength( final int patternIndex ) {
		return patterns[ patternIndex ].length;
	}

	@Override
	public String toString() {
		return "[Format" +
				" title: '" + title + '\'' +
				", type: '" + type + '\'' +
				", waveforms: " + Arrays.toString( waveforms ) +
				", credits: " + Arrays.toString( credits ) +
				", sequence: " + Arrays.toString( sequence ) +
				", numPatterns: " + numPatterns +
				"]";
	}
}