package drole.tests.vbo;

import javax.media.opengl.GL2;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class VBOTest2 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	private float spawnDim = 500;
	
	private GL2 gl; 
	private PGraphicsOpenGL pgl;
	
	private float[][] vertices = new float[20][100000];
	
	private VBO vbo;
	
	public void setup() {
		size(1200, 720, OPENGL);
		
		pgl = (PGraphicsOpenGL) g;  // g may change
		gl = pgl.beginPGL().gl.getGL2(); 
		
		// Begin creation
		print("Begin creation ... ");
		
		for(int j = 0; j < 20; j++) {
			for(int i = 0; i < 100000; i++) {
				vertices[j][i] = random(-spawnDim, spawnDim);
				//particles.vertex(random(-spawnDim, spawnDim), random(-spawnDim, spawnDim), random(-spawnDim, spawnDim));
			}
		}
		
		println("done!");
		
		vbo = new VBO(100000, gl);
		vbo.updateVertices(vertices[(int)random(19)]);
		
		lights();
	}
	
	public void draw() {
		background(20);
		
		gl.glPushMatrix();
			gl.glTranslatef(width/2f, height/2f, -1000);
			gl.glRotatef(frameCount/2f, 1, 1, 1);
			
			vbo.updateVertices(vertices[(int)random(19)]);
			vbo.render(GL2.GL_QUADS);
		
		gl.glPopMatrix();
		
		if(frameCount%100 == 0) println(frameRate);
	}

}
