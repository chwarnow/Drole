package com.madsim.engine.optik;


import processing.opengl.PGraphicsOpenGL;

import com.madsim.engine.Engine;

public abstract class Optik {

	protected Engine e;
	protected PGraphicsOpenGL g;
	
	public Optik(Engine e) {
		this.e = e;
		setG(e.g);
	}
	
	public void setG(PGraphicsOpenGL g) {
		this.g = g;
	}
	
	// Is called just before set
	public abstract void calculate();
	
	// Is called to set the optik of a scene
	public abstract void set();
	
}
