package com.madsim.engine.drawable;

final public class FilterSets {

	public static short[] All() {
		return new short[]{ Drawable.CAST_SHADOW, Drawable.RECEIVE_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW };
	}
	
	public static short[] ShadowReceiver() {
		return new short[]{ Drawable.RECEIVE_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW };
	}

	public static short[] ShadowCaster() {
		return new short[]{ Drawable.CAST_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW };
	}
	
}
