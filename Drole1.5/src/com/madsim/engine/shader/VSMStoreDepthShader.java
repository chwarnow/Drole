package com.madsim.engine.shader;

import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class VSMStoreDepthShader extends Shader {

	public VSMStoreDepthShader(PApplet p) {
		super(p);
		
		s = new GLSLShader(p, "shader/passes/vsmshadow/StoreDepthVertexShader.glsl", "shader/passes/vsmshadow/StoreDepthFragmentShader.glsl");
	}

	@Override
	public void start() {
		s.start();
	}

}
