package drole.tests.menu;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLTexture;
import drole.DroleMain;
import drole.RibbonGlobe;
import drole.gfx.assoziation.BildweltAssoziationPensee;
import drole.gfx.ribbon.RibbonGroup;
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

	BildweltAssoziationPensee penseeA, penseeB, penseeC, penseeD, penseeE;
	float sphereConstraintRadius = 300.0f;
	GLTexture bg;
	
	int swarmsAmount = 20;
	RibbonGroupTest[] swarms;
	
	boolean isSave = false;
	
	public void setup() {
		size(1080, 1080, GLConstants.GLGRAPHICS);
	
		// TODO: send wandering length from here
		
		// init ribbon sculpture
		float randomRadius = 150;
		penseeA = new BildweltAssoziationPensee(this, "data/images/menuAssoziationA.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeB = new BildweltAssoziationPensee(this, "data/images/menuAssoziationB.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeC = new BildweltAssoziationPensee(this, "data/images/menuAssoziationC.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeD = new BildweltAssoziationPensee(this, "data/images/menuAssoziationD.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeE = new BildweltAssoziationPensee(this, "data/images/menuAssoziationE.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		
		penseeB.currPosition += 90;
		penseeC.currPosition += 150;
		
		
		// background dtexture
		bg = new GLTexture(this, "data/images/backgroundTest.png");
		
		// a swarm
		swarms = new RibbonGroupTest[swarmsAmount];
		for(int i=0;i<swarmsAmount;i++) {
			swarms[i] = new RibbonGroupTest(this, sphereConstraintRadius, 50 + (int)random(150), 3, 10 + (int)random(10));
		}
	}
	
	public void draw() {
		// update globe swarm
		for(RibbonGroupTest swarm:swarms) swarm.update();
		
		// update sculpture
		penseeA.update();
		
		penseeB.update();
		penseeC.update();
		if(frameCount > 200) penseeD.update();
		if(frameCount > 150) penseeE.update();
		
		background(55);
		
		// draw bg
		hint(DISABLE_DEPTH_TEST);
		image(bg, 0, 0, width, height);
		hint(ENABLE_DEPTH_TEST);
		
		pushMatrix();
		
		translate(width/2, height/2, 300);
		rotateY(frameCount*.01f);//radians(mouseX));
		
		// draw sculpture
		GLGraphics renderer = (GLGraphics)g;
		renderer.beginGL();
		penseeA.draw(renderer);
		penseeB.draw(renderer);
		penseeC.draw(renderer);
		penseeD.draw(renderer);
		penseeE.draw(renderer);
		
		for(RibbonGroupTest swarm:swarms) swarm.draw();
		
		renderer.endGL();
		
		popMatrix();
		
		println(frameRate);
		if(isSave) saveFrame("menutestFrame-####.png");
	}
	
	public void keyPressed() {
		isSave = !isSave;
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.MenuAssoziationTest"
		});
	}
	
}
