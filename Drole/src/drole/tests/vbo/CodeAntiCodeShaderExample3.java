package drole.tests.vbo;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.madsim.p5.opengl.PGLUtil;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class CodeAntiCodeShaderExample3 extends PApplet {

	private static final long serialVersionUID = 1L;

	private GL2 gl;
	private PGraphicsOpenGL pgl;

	PShader flatShader;

	int vertLoc;
	int colorLoc;
	
	FloatBuffer vertData;
	FloatBuffer colorData;
	
	public void setup() {
		size(1000, 1000, OPENGL);
		
		pgl = (PGraphicsOpenGL) g; // g may change
		gl = pgl.beginPGL().gl.getGL2();

		// Get the default shader that Processing uses to
		// render flat geometry (w/out textures and lights).
		flatShader = loadShader("../shader/processing/PolyNoTexShaderFrag.glsl");
		
		vertData = PGLUtil.allocateDirectFloatBuffer(12);
		colorData = PGLUtil.allocateDirectFloatBuffer(12);
		
		for(int i = 0; i < 12; i++) {
			vertData.put(random(-1000, 1000));
			colorData.put((float)Math.random());
		}
		
		colorData.rewind();
		vertData.rewind();
	}

	public void draw() {
		pgl = (PGraphicsOpenGL) g; // g may change
		gl = pgl.beginPGL().gl.getGL2();
		
		background(0);

		// The geometric transformations will be automatically passed
		// to the shader.
		translate(width/2, height/2, -1000);
		rotate(frameCount * 0.01f, width, height, 0);

		// Update the geometry hold by the buffer objects
		flatShader.bind();

		vertLoc = gl.glGetAttribLocation(flatShader.glProgram, "inVertex");
		colorLoc = gl.glGetAttribLocation(flatShader.glProgram, "inColor");

		gl.glEnableVertexAttribArray(vertLoc);
		gl.glEnableVertexAttribArray(colorLoc);

		gl.glVertexAttribPointer(vertLoc, 4, GL2.GL_FLOAT, false, 0, vertData);
		gl.glVertexAttribPointer(colorLoc, 4, GL2.GL_FLOAT, false, 0, colorData);

		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);

		gl.glDisableVertexAttribArray(vertLoc);
		gl.glDisableVertexAttribArray(colorLoc);

		flatShader.unbind();
		pgl.endPGL();
	}

}