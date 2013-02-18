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

public class MenuAssoziationTest extends PApplet {

	BildweltAssoziationPensee penseeA, penseeB, penseeC, penseeD;
	float sphereConstraintRadius = 150.0f;
	
	public void setup() {
		size(1200, 720, GLConstants.GLGRAPHICS);
	
		// init ribbon sculpture
		float randomRadius = 50;
		penseeA = new BildweltAssoziationPensee(this, "data/images/menuAssoziationA.png", sphereConstraintRadius, .5f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeB = new BildweltAssoziationPensee(this, "data/images/menuAssoziationB.png", sphereConstraintRadius, .5f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeC = new BildweltAssoziationPensee(this, "data/images/menuAssoziationC.png", sphereConstraintRadius, .5f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeD = new BildweltAssoziationPensee(this, "data/images/menuAssoziationD.png", sphereConstraintRadius, .5f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeB.currPosition += 90;
		penseeC.currPosition += 150;
	}
	
	public void draw() {
		// update sculpture
		penseeA.update();
		penseeB.update();
		penseeC.update();
		if(frameCount > 200) penseeD.update();
		
		background(55);
		
		pushMatrix();
		
		translate(width/2, height/2, 300);
		rotateY(radians(mouseX));
		
		// draw sculpture
		GLGraphics renderer = (GLGraphics)g;
		renderer.beginGL();
		penseeA.draw(renderer);
		penseeB.draw(renderer);
		penseeC.draw(renderer);
		penseeD.draw(renderer);
		renderer.endGL();
		
		popMatrix();
		
		println(frameRate);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.MenuAssoziationTest"
		});
	}
	
}
