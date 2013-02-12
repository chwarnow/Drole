package drole.tests.vbo;

import javax.media.opengl.GL2;

import com.madsim.p5.opengl.DGShape;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class DGShapeTest1 extends PApplet {

	private static final long serialVersionUID = 1L;

	private PGraphicsOpenGL pgl;
	private GL2 gl;
	
	private DGShape shape;
	
	public void setup() {
		size(1000, 1000, OPENGL);
		
		shape = new DGShape(this, 1000);
		
		shape.setRandomVertices(1000);
		shape.setRandomColors();
	}
	
	@SuppressWarnings("static-access")
	private void refreshGL() {
		pgl = (PGraphicsOpenGL) g; // g may change
		gl = pgl.beginPGL().gl.getGL2();
	}
	
	public void draw() {
		refreshGL();
		
		pgl.beginPGL();
		
		background(0);
		translate(width/2, height/2, -5000);
		rotateX(frameCount * 0.01f);
		rotateY(frameCount * 0.01f);
		rotateZ(frameCount * 0.01f);
		
			shape.draw(gl, GL2.GL_TRIANGLE_STRIP);
			
		pgl.endPGL();
	}
	
}
