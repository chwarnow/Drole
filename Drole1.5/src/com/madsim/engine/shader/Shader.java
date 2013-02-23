package com.madsim.engine.shader;

import processing.core.PApplet;
import codeanticode.glgraphics.GLSLShader;

public abstract class Shader {
	
	protected GLSLShader s;
	
	public static short NO_TEXTURES;
	public static short USE_TEXTURES;
	
	private short textureHint = NO_TEXTURES;
	
	public Shader(PApplet p) {
		
	}
	
	public short textureHint() {
		return textureHint;
	}
	
	public abstract void start();
	
	public void stop() {
		s.stop();
	}
	
	public GLSLShader glsl() {
		return s;
	}
	
}
