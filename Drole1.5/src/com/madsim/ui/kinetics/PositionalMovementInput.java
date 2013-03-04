package com.madsim.ui.kinetics;

public interface PositionalMovementInput extends KineticInput {
	
	public static float IGNORED_VALUE = 123456.123456789f;
	
	/* Return the current position */
	public float[] getPosition();
	
}
