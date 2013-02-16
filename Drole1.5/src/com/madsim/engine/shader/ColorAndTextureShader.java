package com.madsim.engine.shader;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class ColorAndTextureShader extends Shader {

	private PApplet p;
	
	public ColorAndTextureShader(PApplet p) {
		super(p);
		this.p = p;
		s = new GLSLShader(p, "shader/std/ColorAndTextureVert.glsl", "shader/std/ColorAndTextureFrag.glsl");
	}
	
	@Override
	public void start() {
		GLGraphics renderer = (GLGraphics) p.g;
		GL gl = renderer.gl;
		
		s.start();
		
		s.setTexUniform("permTexture", 0);
	}

	@Override
	public void stop() {
		s.stop();
	}
	
}
