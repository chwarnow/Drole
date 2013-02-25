package com.madsim.engine.optik;

import javax.media.opengl.GL;

import com.madsim.engine.Engine;

public class OrthoOptik extends Optik {

	public OrthoOptik(Engine e) {
		super(e, 0, 0, 0);
	}

	@Override
	public void calculate() {}
	
	@Override
	public void calculate(float povX, float povY, float povZ) { calculate(); }

	@Override
	public void set() {
		g.gl.glMatrixMode(GL.GL_PROJECTION);
		g.gl.glLoadIdentity();
		
		g.gl.glOrtho(0, g.width, g.height, 0, 10, -10);
		
		g.gl.glMatrixMode(GL.GL_MODELVIEW);
		g.gl.glLoadIdentity();
		
		g.gl.glTranslatef(g.width/2, g.height/2, 0);	
	}

}
