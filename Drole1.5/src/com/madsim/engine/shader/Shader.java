package com.madsim.engine.shader;

import processing.core.PApplet;
import codeanticode.glgraphics.GLSLShader;

public abstract class Shader {
	
	protected GLSLShader s;
	
	public static short NO_TEXTURES 	= 10;
	public static short USE_TEXTURES	= 20;

	protected short textureHint = NO_TEXTURES;

	public static short NO_ENVIRONMENT_MAP		= 10;
	public static short USE_ENVIRONMENT_MAP		= 20;
	
	protected short environmentMapHint = NO_ENVIRONMENT_MAP;
	
	public static short NO_LIGHTS		= 10;
	public static short USE_LIGHTS		= 20;
	
	protected short lightHint = NO_LIGHTS;
	
	public Shader(PApplet p) {
		
	}
	
	public short textureHint() {
		return textureHint;
	}

	public short environmentMapHint() {
		return environmentMapHint;
	}
	
	public short lightHint() {
		return lightHint;
	}
	
	public abstract void start();
	
	public void stop() {
		s.stop();
	}
	
	public GLSLShader glsl() {
		return s;
	}
	
}
