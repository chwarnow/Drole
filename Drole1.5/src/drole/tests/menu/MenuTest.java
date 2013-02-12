package drole.tests.menu;

import com.christopherwarnow.bildwelten.SpherePrimitive;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class MenuTest extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ------ cube sphere ------
	PImage cubeTex;
	SpherePrimitive cubeEnvironment;

	public void setup() {
		size(1200, 720, P3D);

		// cube environment
		cubeTex = loadImage("data/images/panorama02.jpg");
		cubeEnvironment = new SpherePrimitive(this, new PVector(), 1500, cubeTex, 32);
		
	}

	public void draw() {
		background(255);
		noStroke();
		pushMatrix();
		
		translate(width/2, height/2, 0);
		rotateY(radians(mouseX));
		rotateX(radians(mouseY));

		cubeEnvironment.draw();
		
		popMatrix();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.MenuTest"
		});
	}
}
