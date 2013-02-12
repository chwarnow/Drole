package drole.tests.vbo;

import processing.core.PApplet;
import processing.core.PShape;
import processing.opengl.PShader;

public class BOWithShaderTest1 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	private PShape particles;
	
	private PShader displacement;
	
	private float spawnDim = 3000;
	
	public void setup() {
		size(1200, 720, OPENGL);
		
		//hint(DISABLE_OPENGL_2X_SMOOTH);
		//hint(ENABLE_OPENGL_4X_SMOOTH);
		
		// Begin creation
		print("Begin creation ... ");
		particles = createShape();

		particles.beginShape(TRIANGLES);
			particles.noStroke();
			
			for(int i = 0; i < 10000; i++) {
				particles.stroke(random(255), 0, random(255));
				particles.strokeWeight(10);
				particles.vertex(random(-spawnDim, spawnDim), random(-spawnDim, spawnDim), random(-spawnDim, spawnDim));
			}
			
		particles.endShape();
		println("done!");
		
		displacement = loadShader("../shader/frag_allred.glsl", "../shader/vert_random.glsl");
		
		lights();
	}
	
	public void draw() {
		background(20);
		
		translate(width/2, height/2, -8000);
		rotateY(frameCount/100f);
		rotateX(frameCount/100f);
		rotateZ(frameCount/100f);
			
			shader(displacement);
			shape(particles);
			
			pointLight(0, 200, 200, 100, 100, 100);
			
		if(frameCount%100 == 0) println(frameRate);
	}

}
