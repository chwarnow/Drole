package com.madsim.engine.shader;

import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class VSMBlurShader extends Shader {

	public VSMBlurShader(PApplet p) {
		super(p);
		
		s = new GLSLShader(p, "shader/passes/vsmshadow/BlurVertexShader.glsl", "shader/passes/vsmshadow/BlurFragmentShader.glsl");
	}

	@Override
	public void start() {
		s.start();
	}

}
