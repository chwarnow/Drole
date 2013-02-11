package drole.tests.vbo;

import processing.core.PApplet;
import processing.core.PShape;

public class VBOTest1 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	private PShape particles;
	
	public void setup() {
		size(1200, 720, OPENGL);
		
		particles = createShape(POINTS);
		
		for(int i = 0; i < 1000000; i++) {
			particles.fill(random(0, 255), random(0, 255), random(0, 255));
			particles.vertex(random(-5000, 5000), random(-5000, 5000), random(-5000, 5000));
		}
	}
	
	public void draw() {
		background(0);
		shape(particles);
	}

}
