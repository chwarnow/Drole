package com.madsim.engine.optik;


import processing.core.PVector;

import codeanticode.glgraphics.GLGraphics;

import com.madsim.engine.Engine;

public abstract class Optik {

	protected Engine e;
	protected GLGraphics g;
	
	protected PVector stdPOV;

	public Optik(Engine e, float x, float y, float z) {
		this(e, new PVector(x, y, z));
	}
	
	public Optik(Engine e, PVector stdPOV) {
		this.e 		= e;
		this.stdPOV = stdPOV;
		this.g = e.g;
	}
	
	// Is called just before set
	public void calculate() {
		calculate(stdPOV);
	}
	
	public void calculate(PVector pov) {
		calculate(pov.x, pov.y, pov.z);
	}
	
	public abstract void calculate(float povX, float povY, float povZ);
	
	// Is called to set the optik of a scene
	public abstract void set();
	
}
