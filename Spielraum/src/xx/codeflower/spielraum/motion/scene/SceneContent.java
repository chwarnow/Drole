package xx.codeflower.spielraum.motion.scene;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class SceneContent implements MouseMotionListener, MouseListener {

	public String name;
	
	protected PApplet parent;
	
	protected int[] pos = new int[8];
	protected String contentFolder;
	private Rectangle box = new Rectangle(0, 0, 0, 0);
	
	protected boolean isEditable;
	private boolean editMode = false;
	
	private boolean mouseInBox = false;
	private short mouseInBoxNo = 0;
	
	private Point oldPoint = new Point(-1, -1);
	
	public float alpha = 255;
	
	public SceneContent(PApplet parent, String contentFolder, String[] setup) {
		this.parent = parent;
		this.contentFolder = contentFolder;
		parseSetup(setup);
		parent.addMouseMotionListener(this);
	}
	
	protected void parseSetup(String[] setup) {
		name = setup[0];
		System.out.println(name);
		parseSetupPositions(setup);
		parseEditHint(setup);
		parseTypeRelatedSetup(setup);
	}
	
	private int parseSetupPosition(String s) {
		if(s.equals("w")) return parent.width;
		if(s.equals("h")) return parent.height;
		return Integer.parseInt(s);
	}
	
	protected void parseEditHint(String[] setup) {
		isEditable = setup[9].equals("editable");
	}
	
	protected void parseSetupPositions(String[] setup) {
		for(int i = 1; i < 9; i++) pos[i-1] = parseSetupPosition(setup[i]);
		updateBox();
	}
	
	protected abstract void parseTypeRelatedSetup(String[] setup);
	
	protected abstract String getConfigString();
	
	public void startEditMode() {
		if(isEditable) editMode = true;
	}

	public void endEditMode() {
		editMode = false;
	}
	
	public abstract void start();
	public abstract void stop();
	
	protected void updateBox() {
		int minX = -1, minY = -1, maxX = -1, maxY = -1;
		for(int i = 0; i < 8; i+=2) {
			if(minX == -1) minX = pos[i];
			if(maxX == -1) maxX = pos[i];
			if(minY == -1) minY = pos[i+1];
			if(maxY == -1) maxY = pos[i+1];
			
			if(pos[i] < minX) minX = pos[i];
			if(pos[i] > maxX) maxX = pos[i];
			
			if(pos[i+1] < minY) minY = pos[i+1];
			if(pos[i+1] > maxY) maxY = pos[i+1];
		}
		box.x = minX;
		box.y = minY;
		box.width = maxX-minX;
		box.height = maxY-minY;
	}
	
	public void draw(PGraphics g) {
		drawContent(g);
		
		if(editMode) {
			g.noStroke();
			
			g.fill(200, 0, 0);
			g.rect(pos[0]-10, pos[1]-10, 20, 20);
			
			g.fill(0, 200, 0);
			g.rect(pos[2]-10, pos[3]-10, 20, 20);
			
			g.fill(0, 0, 200);
			g.rect(pos[4]-10, pos[5]-10, 20, 20);
			
			g.fill(0, 200, 200);
			g.rect(pos[6]-10, pos[7]-10, 20, 20);
			
			g.stroke(200, 0, 0);
			g.noFill();
			g.rect(box.x, box.y, box.width, box.height);
		}
	}
	
	protected abstract void drawContent(PGraphics g);

	@Override
	public void mouseDragged(MouseEvent e) {
		if(editMode) {
			if(mouseInBox && mouseInBoxNo == 1) {
				pos[0] = e.getX();
				pos[1] = e.getY();
			}
			if(mouseInBox && mouseInBoxNo == 2) {
				pos[2] = e.getX();
				pos[3] = e.getY();
			}
			if(mouseInBox && mouseInBoxNo == 3) {
				pos[4] = e.getX();
				pos[5] = e.getY();
			}
			if(mouseInBox && mouseInBoxNo == 4) {
				pos[6] = e.getX();
				pos[7] = e.getY();
			}
			
			if(mouseInBox && mouseInBoxNo == -1) {
				if(oldPoint.x == -1) {
					oldPoint = e.getPoint();
				} else {
					moveByDistance(e.getPoint().x-oldPoint.x, e.getPoint().y-oldPoint.y);
					oldPoint = e.getPoint();
				}
			}
			
			updateBox();
		}
	}
	
	public void moveByDistance(int x, int y) {
		pos[0] += x;
		pos[1] += y;

		pos[2] += x;
		pos[3] += y;
		
		pos[4] += x;
		pos[5] += y;
		
		pos[6] += x;
		pos[7] += y;
		
		updateBox();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(editMode) {
			if(new Rectangle(pos[0]-10, pos[1]-10, 20, 20).contains(e.getPoint())) {
				mouseInBox = true;
				mouseInBoxNo = 1;
			}
			if(new Rectangle(pos[2]-10, pos[3]-10, 20, 20).contains(e.getPoint())) {
				mouseInBox = true;
				mouseInBoxNo = 2;
			}
			if(new Rectangle(pos[4]-10, pos[5]-10, 20, 20).contains(e.getPoint())) {
				mouseInBox = true;
				mouseInBoxNo = 3;
			}
			if(new Rectangle(pos[6]-10, pos[7]-10, 20, 20).contains(e.getPoint())) {
				mouseInBox = true;
				mouseInBoxNo = 4;
			}
			
			if(!mouseInBox && box.contains(e.getPoint())) {
				mouseInBox = true;
				mouseInBoxNo = -1;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseInBox = false;
		updateBox();
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
