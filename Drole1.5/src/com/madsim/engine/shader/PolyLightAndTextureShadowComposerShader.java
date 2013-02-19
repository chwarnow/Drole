package com.madsim.engine.shader;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class PolyLightAndTextureShadowComposerShader extends Shader {

	private PApplet p;
	
	public PolyLightAndTextureShadowComposerShader(PApplet p) {
		super(p);
		this.p = p;
		s = new GLSLShader(p, "shader/passes/shadowmap/PolyLightAndTextureShadowComposerVert.glsl", "shader/passes/shadowmap/PolyLightAndTextureShadowComposerFrag.glsl");
	}
	
	@Override
	public void start() {
		GLGraphics renderer = (GLGraphics) p.g;
		GL gl = renderer.gl;
		
		s.start();
		
		s.setTexUniform("permTexture", GL.GL_TEXTURE0+0);
	}

	@Override
	public void stop() {
		s.stop();
	}
	
}
