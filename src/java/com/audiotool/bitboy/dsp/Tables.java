package com.audiotool.bitboy.dsp;

import java.util.HashMap;

/**
 * @author Andre Michelle
 */
final class Tables
{
	static final int[] Tone = new int[]{
			856, 808, 762, 720, 678, 640, 604, 570, 538, 508, 480, 453,
			428, 404, 381, 360, 339, 320, 302, 285, 269, 254, 240, 226,
			214, 202, 190, 180, 170, 160, 151, 143, 135, 127, 120, 113
	};

	static final HashMap<Integer, Integer> ToneIndices = generateToneIndex();

	static final int[] Sine = new int[]{
			0, 24, 49, 74, 97, 120, 141, 161,
			180, 197, 212, 224, 235, 244, 250, 253,
			255, 253, 250, 244, 235, 224, 212, 197,
			180, 161, 141, 120, 97, 74, 49, 24,
			0, -24, -49, -74, -97, -120, -141, -161,
			-180, -197, -212, -224, -235, -244, -250, -253,
			-255, -253, -250, -244, -235, -224, -212, -197,
			-180, -161, -141, -120, -97, -74, -49, -24
	};

	private static HashMap<Integer, Integer> generateToneIndex()
	{
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		final int n = Tone.length;

		for( int i = 0 ; i < n ; ++i )
			map.put( Tone[ i ], i );

		return map;
	}
}
