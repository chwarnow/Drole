package com.madsim.ui.kinetics.gestures;

import com.madsim.ui.kinetics.PositionalMovementInput;

public abstract class PositionalGestureInterpreter {
	
	protected PositionalMovementInput input;
	
	public PositionalGestureInterpreter(PositionalMovementInput input) {
		this.input = input;
	}
	
	/* Return result */
	public abstract float[] get();
	
	public long millis() {
		return System.currentTimeMillis();
	}
	
}
