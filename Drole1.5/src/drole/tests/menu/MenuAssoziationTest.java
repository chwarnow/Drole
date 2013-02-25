package drole.tests.menu;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.tracking.kinect.PositionTargetListener;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLTexture;
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

public class MenuAssoziationTest extends EngineApplet implements PositionTargetListener, MouseWheelListener {

	BildweltAssoziationPensee penseeA, penseeB, penseeC, penseeD, penseeE;
	float sphereConstraintRadius = 300.0f;
	GLTexture bg;
	
	int swarmsAmount = 20;
	RibbonGroupTest[] swarms;
	
	boolean isSave = false;
	boolean isAssociations = true;
	
	Engine engine;
	
	public void setup() {
		size(1080, 1080, GLConstants.GLGRAPHICS);
	
		engine = new Engine(this);
		
		// TODO: send wandering length from here
		if(isAssociations) {
		// init ribbon sculpture
		float randomRadius = 150;
		penseeA = new BildweltAssoziationPensee(engine, "data/images/menuAssoziationA.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeB = new BildweltAssoziationPensee(engine, "data/images/menuAssoziationB.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeC = new BildweltAssoziationPensee(engine, "data/images/menuAssoziationC.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeD = new BildweltAssoziationPensee(engine, "data/images/menuAssoziationD.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		penseeE = new BildweltAssoziationPensee(engine, "data/images/menuAssoziationE.png", sphereConstraintRadius, .75f, new PVector(random(-randomRadius, randomRadius),random(-randomRadius, randomRadius),random(-randomRadius, randomRadius)), new PVector());
		
		penseeB.currPosition += 90;
		penseeC.currPosition += 150;
		}
		
		// background dtexture
		bg = new GLTexture(this, "data/images/light_box_two_spots_02.png");
		
		// a swarm
		swarms = new RibbonGroupTest[swarmsAmount];
		for(int i=0;i<swarmsAmount;i++) {
			swarms[i] = new RibbonGroupTest(engine, sphereConstraintRadius, 50 + (int)random(150), 3, 10 + (int)random(10));
		}
	}
	
	public void draw() {
		// update globe swarm
		for(RibbonGroupTest swarm:swarms) swarm.update();
		
		if(isAssociations) {
		// update sculpture
		penseeA.update();
		
		penseeB.update();
		penseeC.update();
		if(frameCount > 200) penseeD.update();
		if(frameCount > 150) penseeE.update();
		}
		
		background(55);
		
		// draw bg
		hint(DISABLE_DEPTH_TEST);
		image(bg, 0, 0, width, height);
		hint(ENABLE_DEPTH_TEST);
		
		pushMatrix();
		
		translate(width/2, height/2, 0);
		rotateY(radians(mouseX));
		
		// draw sculpture
		GLGraphics renderer = (GLGraphics)g;
		renderer.beginGL();
		
		if(isAssociations) {
		pushMatrix();
		translate(0, cos(frameCount*.05f)*10, 0);
		penseeA.draw();
		popMatrix();
		
		pushMatrix();
		translate(0, cos((frameCount+30)*.05f)*10, 0);
		penseeB.draw();
		popMatrix();
		
		pushMatrix();
		translate(0, cos((frameCount+60)*.05f)*10, 0);
		penseeC.draw();
		popMatrix();
		
		pushMatrix();
		translate(0, cos((frameCount+90)*.05f)*10, 0);
		penseeD.draw();
		popMatrix();
		
		pushMatrix();
		translate(0, cos((frameCount+30)*.05f)*10, 0);
		penseeE.draw();
		popMatrix();
		}
		
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

	@Override
	public void jointEnteredTarget(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jointLeftTarget(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
