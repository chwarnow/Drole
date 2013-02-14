package drole.engine.shader;

import processing.core.PApplet;
import codeanticode.glgraphics.GLSLShader;

public abstract class Shader {
	
	protected GLSLShader s;
	
	public Shader(PApplet p) {
		
	}
	
	public abstract void start();
	
	public void stop() {
		s.stop();
	}
	
	public GLSLShader glsl() {
		return s;
	}
	
}
