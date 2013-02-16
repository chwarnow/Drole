package com.madsim.engine.optik;

import processing.core.PApplet;

public abstract class Optik {

	@SuppressWarnings("unused")
	protected PApplet p;
	
	public Optik(PApplet p) {
		this.p = p;
	}
	
	// Is called just before set
	public abstract void calculate();
	
	// Is called to set the optik of a scene
	public abstract void set();
	
}
