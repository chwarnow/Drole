package drole.tests.menu;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.tracking.kinect.targeting.PositionTargetListener;

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

public class AssoziierenTest extends EngineApplet implements PositionTargetListener, MouseWheelListener {

	BildweltAssoziationPensee penseeA;
	float sphereConstraintRadius = 160.0f;
	
	Engine engine;
	
	public void setup() {
		size(1200, 720, GLConstants.GLGRAPHICS);
	
		engine = new Engine(this);
		
		// init ribbon sculpture
		penseeA = new BildweltAssoziationPensee(engine, "data/images/associationA.png", sphereConstraintRadius, 1.0f, new PVector(), new PVector());
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
		penseeA.draw();
		renderer.endGL();
		
		popMatrix();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.AssoziierenTest"
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
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
