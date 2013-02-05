package fhp.fassade.spielraum.scene;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import fhp.fassade.spielraum.TextFile;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.video.Movie;

public class Scene implements MouseMotionListener, MouseListener {

	public static final short PAUSED 		= 10;
	public static final short DRAWING 		= 20;
	public static final short FADING_OUT 	= 30;
	public static final short FADING_IN 	= 40;
	
	private PApplet parent;
	
	private ArrayList<SceneContent> content = new ArrayList<SceneContent>();
	
	public String name;
	private String setup;
	
	private Movie myMovie;
	
	private int tick = 0, transitionTime;
	
	public short state = PAUSED;
	
	public Scene(PApplet parent, String name) throws ScenenDoesNotExistsException {
		this.parent = parent;
		this.name	= name;
		if(!loadScene(name)) throw new ScenenDoesNotExistsException();
		
		parseSetup();
	}
	
	private boolean loadScene(String name) {
		File f = new File("data/scenes/"+name+"/"+name+".txt");
		if(!f.exists()) return false;
		
		setup = TextFile.getContents(f);
		
		return true;
	}
	
	protected void parseSetup() {
		String[] sMain = setup.split("\n");
		for(String sLine : sMain) {
			String[] sAttrs = sLine.split("=");
			if(sAttrs.length <= 1) return;
			
			String[] sA = sAttrs[1].split(",");
			
			if(sAttrs[0].toString().equals("image")) {
				SceneContent ic = new SceneImage(parent, "data/scenes/"+name+"/content/", sA);
				content.add(ic);
			}
			if(sAttrs[0].toString().equals("video")) {
				SceneContent vi = new SceneVideo(parent, "data/scenes/"+name+"/content/", sA);
				content.add(vi);
			}
			if(sAttrs[0].toString().equals("parallax")) {
				SceneContent vi = new SceneParallax(parent, "data/scenes/"+name+"/content/", sA);
				content.add(vi);
			}
		}
	}
	
	public SceneContent getElement(String name) {
		for(SceneContent sc : content) if(sc.name.equals(name)) return sc;
		return null;
	}
	
	public void startEditMode() {
		for(SceneContent si : content) si.startEditMode();
	}
	
	public void endEditMode() {
		for(SceneContent si : content) si.endEditMode();
	}
	
	public void save(String path) {
		String c = "";
		for(SceneContent si : content) c = c+si.getConfigString();
		try {
			TextFile.setContents(new File(path), c);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fadeOut(int fadeOutTime) {
		tick = 0;
		transitionTime = fadeOutTime;
		state = FADING_OUT;
	}

	public void fadeIn(int fadeInTime) {
		tick = 0;
		transitionTime = fadeInTime;
		state = FADING_IN;
	}
	
	private void checkAnimation() {
		if(state == FADING_OUT && tick == transitionTime) state = PAUSED;
		if(state == FADING_IN && tick == transitionTime) state = DRAWING;
		
		if(state == FADING_IN) {
			for(SceneContent i : content) i.alpha = PApplet.map(tick, 0, transitionTime, 0, 255);
			tick++;
		} else if(state == FADING_OUT) {
			for(SceneContent i : content) i.alpha = PApplet.map(tick, 0, transitionTime, 255, 0);
			tick++;			
		} 		
	}
	
	public void draw(PGraphics g) {
		checkAnimation();
		
		if(state == PAUSED) return;
		
		g.noStroke();
		for(SceneContent i : content) i.draw(g);
		
		if(myMovie != null) {
			if(myMovie.isPlaying()) myMovie.read();
			g.image(myMovie, 100, 100, 200, 200);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		for(SceneContent si : content) si.mouseDragged(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for(SceneContent si : content) si.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(SceneContent si : content) si.mouseReleased(e);
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
