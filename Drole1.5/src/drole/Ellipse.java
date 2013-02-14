package drole;

import drole.engine.Drawable;
import processing.core.PApplet;

public class Ellipse extends Drawable {

	  public float w, h;
	  public int tcolor;
	  
	  public Ellipse(DroleMain parent, float w, float h, int tcolor) {
	    super(parent);
	    this.w = w;
	    this.h = h;
	    this.tcolor = tcolor;
	  }

	  public void draw() {
	    parent.g.pushMatrix();
	      parent.g.translate(position.x, position.y);
	      parent.g.scale(scale.x, scale.y);
	      parent.g.noStroke();
	      parent.g.fill(tcolor, PApplet.map(fade, 0, 1, 0, 255));
	      parent.g.ellipse(0, 0, w, h);
	    parent.g.popMatrix();
	  }
	  
}
