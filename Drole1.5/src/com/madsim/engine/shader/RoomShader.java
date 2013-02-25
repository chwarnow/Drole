package com.madsim.engine.shader;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class RoomShader extends Shader {

	private PApplet p;
	
	public RoomShader(PApplet p) {
		super(p);
		this.p = p;
		s = new GLSLShader(p, "shader/std/RoomShaderVert.glsl", "shader/std/RoomShaderFrag.glsl");
		
		textureHint = Shader.USE_TEXTURES;
		lightHint = Shader.USE_LIGHTS;
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
