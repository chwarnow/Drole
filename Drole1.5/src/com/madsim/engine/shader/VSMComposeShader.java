package com.madsim.engine.shader;

import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class VSMComposeShader extends Shader {

	public VSMComposeShader(PApplet p) {
		super(p);
		
		s = new GLSLShader(p, "shader/passes/vsmshadow/VertexShader.glsl", "shader/passes/vsmshadow/FragmentShader.glsl");
	}

	@Override
	public void start() {
		s.start();
	}

}
