package xx.codeflower.spielraum.motion.scene;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;

import xx.codeflower.spielraum.motion.TextFile;

public class EditableScene extends Scene implements MouseListener, MouseMotionListener, KeyListener {

	private String name, setup;
	
	protected ArrayList<SceneContent> content = new ArrayList<SceneContent>();
	
	private int editI = 0;
	
	private boolean isEditing = false;
	
	public EditableScene(PApplet p, String name, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
		
		p.addMouseListener(this);
		p.addMouseMotionListener(this);
		p.addKeyListener(this);
		
		this.name = name;
			
		if(loadScene(name)) {
			parseSetup();	
		} else {
			System.out.println("Couldn't load data of EditableScene '"+name+"'");
		}
	}
	
	private boolean loadScene(String name) {
		File f = new File("data/scenes/"+name+"/"+name+".txt");
		if(!f.exists()) return false;
		
		setup = TextFile.getContents(f);
		
		return true;
	}
	
	protected void parseSetup() {
		log("Start parsing setup file ...");
		
		String[] sMain = setup.split("\n");
		for(String sLine : sMain) {
			String[] sAttrs = sLine.split("=");
			if(sAttrs.length <= 1) return;
			
			String[] sA = sAttrs[1].split(",");
			
			if(sAttrs[0].toString().equals("image")) {
				SceneContent ic = new SceneImage(p, "data/scenes/"+name+"/content/", sA);
				content.add(ic);
				log("- adding image "+ic.name);
			}
			if(sAttrs[0].toString().equals("video")) {
				SceneContent vi = new SceneVideo(p, "data/scenes/"+name+"/content/", sA);
				content.add(vi);
				log("- adding video "+vi.name);
			}
			if(sAttrs[0].toString().equals("parallax")) {
				SceneContent vi = new SceneParallax(p, "data/scenes/"+name+"/content/", sA);
				content.add(vi);
				log("- adding parallax "+vi.name);
			}
		}
		
		log("done!");
	}
	
	public SceneContent getElement(String name) {
		for(SceneContent sc : content) if(sc.name.equals(name)) return sc;
		return null;
	}
	
	public boolean isEditing() { return isEditing; }
	
	public void startEditMode() {
		content.get(editI).startEditMode();
		this.isEditing = true;
		log("Enter edit mode ...");
	}
	
	public void endEditMode() {
		for(SceneContent si : content) si.endEditMode();
		this.isEditing = false;
		log("Leaving edit mode ...");
	}
	
	public void save() {
		String c = "";
		for(SceneContent si : content) c = c+si.getConfigString();
		try {
			TextFile.setContents(new File("data/scenes/"+name+"/"+name+".txt"), c);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log("Scene saved!");
	}
	
	@Override
	public void draw(PGraphics g) {
		for(SceneContent sc : content) sc.draw(g);
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

	@Override
	public void start() {
		for(SceneContent sc : content) sc.start();
	}

	@Override
	public void stop() {
		for(SceneContent sc : content) sc.stop();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == 81) {
			content.get(editI).endEditMode();
			
			if(editI < content.size()-1) editI++;
			else editI = 0;
			
			content.get(editI).startEditMode();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
}
