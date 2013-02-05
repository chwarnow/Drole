package fhp.fassade.spielraum.scene;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;

public class SceneList extends HashMap<String, Scene> implements MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 1L;

	public static final short PAUSED 				= 10;
	public static final short DRAWING 				= 20;
	public static final short IN_TRANSITION_P1 		= 30;
	public static final short IN_TRANSITION_P2 		= 40;
	
	private Scene activeScene;
	
	private int tick = 0;
	
	public short state = PAUSED;
	
	private String nextActive;
	
	private int fadeInTime;
	
	public void add(PApplet parent, String name) throws ScenenDoesNotExistsException {
		Scene s = new Scene(parent, name);
		put(name, s);
	}
	
	public void startEditMode() {
		activeScene.startEditMode();
	}
	
	public void endEditMode() {
		activeScene.endEditMode();
	}

	public void saveScene() {
		activeScene.save("data/scenes/"+activeScene.name+"/"+activeScene.name+".txt");
	}
	
	public void setActiveScene(String name) {
		activeScene = get(name);
	}
	
	public void blendTo(String name, int fadeOutTime, int fadeInTime) {
		if(state == IN_TRANSITION_P1 || state == IN_TRANSITION_P2) return;
		
		nextActive = name;
		this.fadeInTime = fadeInTime;
		tick = 0;
		state = IN_TRANSITION_P1;
		activeScene.fadeOut(fadeOutTime);
	}
	
	public void startDrawing() {
		state = DRAWING;
		activeScene.state = Scene.DRAWING;
	}
	
	public void pauseDrawing() {
		state = PAUSED;
		activeScene.state = Scene.PAUSED;
	}
	
	public void draw(PGraphics g) {
		if(state == PAUSED) return;
		
		if(state == IN_TRANSITION_P1) {
			if(activeScene.state == PAUSED) {
				state = IN_TRANSITION_P2;
				activeScene = get(nextActive);
				activeScene.fadeIn(fadeInTime);
			}
		} else if(state == IN_TRANSITION_P2) {
			if(activeScene.state == DRAWING) state = DRAWING;
		}
		
		activeScene.draw(g);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		for(Map.Entry<String, Scene> sie : entrySet()) sie.getValue().mouseDragged(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for(Map.Entry<String, Scene> sie : entrySet()) sie.getValue().mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(Map.Entry<String, Scene> sie : entrySet()) sie.getValue().mouseReleased(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
