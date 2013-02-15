package drole.engine;

import java.util.ArrayList;

import drole.DroleMain;

public class Drawlist extends Drawable {

	protected ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	public Drawlist(DroleMain parent) {
		super(parent);
	}

	private static final long serialVersionUID = 1L;

	public void update() {
		for(Drawable d : drawables) {
			if(d.mode() != Drawable.OFF_SCREEN) d.update();
		}
	}
	
	public void draw() {
		for(Drawable d : drawables) {
			if(d.mode() != Drawable.OFF_SCREEN) d.draw();
		}		
	}
	
	public void add(Drawable d) {
		drawables.add(d);
	}
	
	public Drawable get(int index) {
		return drawables.get(index);
	}
	
	public int indexOf(Drawable d) {
		return drawables.indexOf(d);
	}
	
	public boolean contains(Drawable d) {
		return drawables.contains(d);
	}
	
	public void fadeAllIn(float time) {
		for(Drawable d : drawables) d.fadeIn(time);
	}
	
	public void fadeAllOut(float time) {
		for(Drawable d : drawables) d.fadeOut(time);
	}
	
	public void hideAll() {
		for(Drawable d : drawables) d.hide();
	}
	
	public void showAll() {
		for(Drawable d : drawables) d.show();
	}
	
}
