package drole.engine.shader;

import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class JustColorShader extends Shader {

	public JustColorShader(PApplet p) {
		super(p);
		s = new GLSLShader(p, "shader/std/JustColorVert.glsl", "shader/std/JustColorFrag.glsl");
	}
	
	@Override
	public void start() {
		s.start();
	}

	
	
}
