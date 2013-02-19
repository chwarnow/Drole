package com.madsim.engine.optik;


import javax.media.opengl.GL;

import com.madsim.engine.Engine;

import processing.core.PVector;

public class LookAt extends Optik {

	private float lx, ly, lz, angle;
	
	public LookAt(Engine e) {
		super(e, 0, 0, 0);
	}
	
	@Override
	public void calculate(float povX, float povY, float povZ) {}
	
	public void calculate(float povX, float povY, float povZ, float lx, float ly, float lz, float angle) {
		stdPOV = new PVector(povX, povY, povZ);
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
		this.angle = angle;
	}
	
	@Override
	public void set() {
		  e.g.gl.glMatrixMode(GL.GL_PROJECTION);
		  e.g.gl.glLoadIdentity();
		  e.g.glu.gluPerspective(angle, g.width/(float)g.height, 0.1f, 100000);
		  e.g.gl.glMatrixMode(GL.GL_MODELVIEW);
		  e.g.gl.glLoadIdentity();
		  e.g.glu.gluLookAt(stdPOV.x, stdPOV.y, stdPOV.z, lx, ly, lz, 0, 1, 0);
	}

}
