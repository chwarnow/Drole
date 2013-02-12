package drole.tests.vbo;

import javax.media.opengl.GL2;

import com.madsim.p5.opengl.DGShape;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class DGShapeWithBOTest1 extends PApplet {

	private static final long serialVersionUID = 1L;

	private PGraphicsOpenGL pgl;
	private GL2 gl;
	
	private DGShape shape;
	
	public void setup() {
		size(1000, 1000, OPENGL);
		
		shape = new DGShape(this, 1000, "../shader/std/frag_color.glsl", "../shader/std/vert_poly.glsl");
/*
		float[] vertices = new float[3000];
		for(int i = 0; i < 3000; i++) vertices[i] = random(-20, 20);
*/
		

		float[] vertices = new float[4000];
		for(int i = 0; i < 4000; i+=4) {
			vertices[i] = random(-100, 100);
			vertices[i+1] = random(-100, 100);
			vertices[i+2] = random(-100, 100);
			vertices[i+3] = 1;
		}

		shape.setVertices(vertices);
		
		shape.setRandomColors();
//		shape.setColor(0, 1, 0, 1);
/*		
		float[] bov = new float[3000];
		for(int i = 0; i < 3000; i++) bov[i] = random(-1000, 1000);
*/
/*
		float[] bov = new float[3000];
		for(int i = 0; i < 3000; i+=3) bov[i] = 1;
	
		shape.createAttributeVBO("inBO", 3000);
		shape.setAttributeVBOData("inBO", bov);
*/
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
		
			shape.draw(gl, GL2.GL_TRIANGLES);
			
		pgl.endPGL();
	}
	
}
