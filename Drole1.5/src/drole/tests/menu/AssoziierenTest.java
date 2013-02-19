package drole.tests.menu;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import drole.gfx.assoziation.BildweltAssoziationPensee;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * 
 * show assoziieren ribbons
 * 
 * @author brainsteen
 *
 */

public class AssoziierenTest extends PApplet {

	BildweltAssoziationPensee penseeA;
	float sphereConstraintRadius = 160.0f;
	
	public void setup() {
		size(1200, 720, GLConstants.GLGRAPHICS);
	
		// init ribbon sculpture
		penseeA = new BildweltAssoziationPensee(this, "data/images/associationA.png", sphereConstraintRadius, 1.0f, new PVector(), new PVector());
	}
	
	public void draw() {
		// update sculpture
		penseeA.update();
		
		background(255);
		
		pushMatrix();
		
		translate(width/2, height/2, 300);
		rotateY(radians(mouseX));
		
		// draw sculpture
		GLGraphics renderer = (GLGraphics)g;
		renderer.beginGL();
		penseeA.draw(renderer);
		renderer.endGL();
		
		popMatrix();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.AssoziierenTest"
		});
	}
	
}
