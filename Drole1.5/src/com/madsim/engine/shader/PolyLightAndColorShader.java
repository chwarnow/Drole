package com.madsim.engine.shader;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class PolyLightAndColorShader extends Shader {

	private PApplet p;
	
	public PolyLightAndColorShader(PApplet p) {
		super(p);
		this.p = p;
		s = new GLSLShader(p, "shader/std/PolyLightAndColorVert.glsl", "shader/std/PolyLightAndColorFrag.glsl");
	}
	
	@Override
	public void start() {
		GLGraphics renderer = (GLGraphics) p.g;
		GL gl = renderer.gl;
		
		s.start();
	}

	@Override
	public void stop() {
		s.stop();
	}
	
}
