package drole.tests.vbo;

import javax.media.opengl.GL2;

import processing.core.PApplet;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;

public class VBOTest2 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	private PShape particles;
	
	private float spawnDim = 3000;
	
	private GL2 gl; 
	private PGraphicsOpenGL pgl;
	
	private double[] vertices = new double[30000];
	
	public void setup() {
		size(1200, 720, OPENGL);
		
		//hint(DISABLE_OPENGL_2X_SMOOTH);
		//hint(ENABLE_OPENGL_4X_SMOOTH);
		
		// Begin creation
		print("Begin creation ... ");
		particles = createShape();

		particles.beginShape(POINTS);
			particles.noStroke();
			
			particles.stroke(random(255), 0, random(255));
			particles.strokeWeight(10);
			
			for(int i = 0; i < 10000; i++) {
				vertices[i] = random(-spawnDim, spawnDim);
				//particles.vertex(random(-spawnDim, spawnDim), random(-spawnDim, spawnDim), random(-spawnDim, spawnDim));
			}
			
		particles.endShape();
		println("done!");
		
		
		
		lights();
	}
	
	public void draw() {
		background(20);
		
		translate(width/2, height/2, -8000);
		rotateY(frameCount/100f);
		rotateX(frameCount/100f);
		rotateZ(frameCount/100f);
			
			shape(particles);
			
			pointLight(0, 200, 200, 100, 100, 100);
			
		if(frameCount%100 == 0) println(frameRate);
	}

}
