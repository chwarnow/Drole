package drole.tests.menu;

import processing.core.PApplet;
import processing.core.PShape;

public class BoxHatchTest extends PApplet {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PShape testSphere;
	public void setup() {
		size(1200, 720, P3D);
		
		noStroke();
		fill(0);
		testSphere = createShape(SPHERE, 150);
	}
	
	public void draw() {
		background(255);
	
		shape(testSphere);
	}
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.BoxHatchTest"
		});
	}
}
