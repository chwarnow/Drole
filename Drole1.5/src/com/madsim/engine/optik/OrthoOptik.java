package com.madsim.engine.optik;

import com.madsim.engine.Engine;

import processing.core.PApplet;

public class OrthoOptik extends Optik {

	public OrthoOptik(Engine e) {
		super(e);
	}

	@Override
	public void calculate() {
		
	}

	@Override
	public void set() {
		g.ortho(0, g.width, g.height, 0, 10000, -10000);
		// We need to flip the x-axis, dunno y!
		g.rotateX(PApplet.radians(180));
	}

}
