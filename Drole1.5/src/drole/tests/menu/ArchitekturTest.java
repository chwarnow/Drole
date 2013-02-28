package drole.tests.menu;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.tracking.kinect.PositionTargetListener;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import drole.gfx.assoziation.BildweltAssoziationPensee;
import drole.gfx.room.Room;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * 
 * architektur room
 * 
 * @author brainsteen
 *
 */

public class ArchitekturTest extends EngineApplet implements PositionTargetListener, MouseWheelListener {
	
	Engine engine;
	
	Room architekturRoom;
	public void setup() {
		size(1200, 720, GLConstants.GLGRAPHICS);
	
		engine = new Engine(this);
		
		architekturRoom = new Room(engine, "data/room/drolebox3/drolebox-cubemap-cw.jpg");
		
	}
	
	public void draw() {
		g.background(255);
		
		g.pushMatrix();
		
		g.translate(width/2, height/2, -10000);
		g.rotateY(frameCount*.1f);//radians(mouseX));
		
		architekturRoom.draw();
		
		g.popMatrix();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.ArchitekturTest"
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
