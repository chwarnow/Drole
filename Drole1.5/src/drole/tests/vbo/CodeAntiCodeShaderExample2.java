package drole.tests.vbo;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.madsim.p5.opengl.PGLUtil;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class CodeAntiCodeShaderExample2 extends PApplet {

	private static final long serialVersionUID = 1L;

	private GL2 gl;
	private PGraphicsOpenGL pgl;

	PShader flatShader;

	int vertLoc;
	int colorLoc;

	FloatBuffer vertData;
	FloatBuffer colorData;

	private int numVertices = 1000;
	
	private float dim = 1000;
	
	public void setup() {
		size(1000, 1000, OPENGL);

		pgl = (PGraphicsOpenGL) g; // g may change
		gl = pgl.beginPGL().gl.getGL2();

		// Get the default shader that Processing uses to
		// render flat geometry (w/out textures and lights).
		flatShader = loadShader("../shader/processing/PolyNoTexShaderFrag.glsl");

		int vertexBufferSize = numVertices*3;
		int colorBufferSize = numVertices*4;
		
		vertData = PGLUtil.allocateDirectFloatBuffer(vertexBufferSize);
		colorData = PGLUtil.allocateDirectFloatBuffer(colorBufferSize);
		
		for(int i = 0; i < numVertices; i++) {
			vertData.put(random(-dim, dim));
			vertData.put(random(-dim, dim));
			vertData.put(random(-dim, dim));
			colorData.put((float)Math.random());
			colorData.put((float)Math.random());
			colorData.put((float)Math.random());
			colorData.put(1.0f);
		}
		
		colorData.position(0);
		vertData.position(0);
		
		lights();
	}

	public void draw() {
		pgl = (PGraphicsOpenGL) g; // g may change
		gl = pgl.beginPGL().gl.getGL2();
		
		background(0);

		// The geometric transformations will be automatically passed
		// to the shader.
		translate(width/2, height/2, -2000);
		rotate(frameCount * 0.01f, width, height, 0);

		pointLight(200, 0, 0, 0, 0, 1000);
		
		// Update the geometry hold by the buffer objects
		pgl.beginPGL();
			flatShader.bind();
		
			vertLoc = gl.glGetAttribLocation(flatShader.glProgram, "inVertex");
			colorLoc = gl.glGetAttribLocation(flatShader.glProgram, "inColor");
	
			gl.glEnableVertexAttribArray(vertLoc);
			gl.glEnableVertexAttribArray(colorLoc);
	
			gl.glVertexAttribPointer(vertLoc, 3, GL2.GL_FLOAT, false, 0, vertData);
			gl.glVertexAttribPointer(colorLoc, 4, GL2.GL_FLOAT, false, 0, colorData);
	
			gl.glDrawArrays(GL2.GL_TRIANGLES, 0, numVertices);
	
			gl.glDisableVertexAttribArray(vertLoc);
			gl.glDisableVertexAttribArray(colorLoc);
	
			flatShader.unbind();
	
		pgl.endPGL();
	}

}