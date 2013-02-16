package com.madsim.engine.optik;

import com.madsim.engine.Engine;

import processing.core.PApplet;

public class StdOptik extends Optik {

	public float cameraZ;
	
	public StdOptik(Engine e) {
		super(e);
	}

	@Override
	public void calculate() {
		cameraZ = ((g.height/2.0f) / PApplet.tan(PApplet.PI*60.0f/360.0f));
	}

	@Override
	public void set() {
		g.perspective(PApplet.PI/3.0f, g.width/g.height, cameraZ/10.0f, cameraZ*10.0f);
//		p.ortho();
	}

}
