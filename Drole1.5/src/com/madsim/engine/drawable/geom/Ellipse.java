package com.madsim.engine.drawable.geom;


import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import processing.core.PApplet;

public class Ellipse extends Drawable {

	  public float w, h;
	  public int tcolor;
	  
	  public Ellipse(Engine e, float w, float h, int tcolor) {
		  super(e);
		  this.w = w;
		  this.h = h;
		  this.tcolor = tcolor;
	  }

	  public void draw() {
		  g.pushStyle();
		  g.pushMatrix();
		      g.translate(position.x, position.y);
		      g.scale(scale.x, scale.y);
		      g.noStroke();
		      g.fill(tcolor, PApplet.map(fade, 0, 1, 0, 255));
		      g.ellipse(0, 0, w, h);
	      g.popMatrix();
	      g.pushStyle();
	  }
	  
}
