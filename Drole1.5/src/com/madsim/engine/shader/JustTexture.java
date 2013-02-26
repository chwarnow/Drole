package com.madsim.engine.shader;

import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class JustTexture extends Shader {

	public JustTexture(PApplet p) {
		super(p);
		s = new GLSLShader(p, "shader/std/JustTextureVert.glsl", "shader/std/JustTextureFrag.glsl");
	}
	
	@Override
	public void start() {
		s.start();
	}

	
	
}
