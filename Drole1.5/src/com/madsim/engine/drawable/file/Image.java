package com.madsim.engine.drawable.file;


import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import processing.core.PApplet;
import processing.core.PImage;

public class Image extends Drawable {

	private PImage image;

	public Image(Engine e, String file) {
		super(e);
		image = e.p.loadImage(file);
	}

	public void draw() {
		if (image != null) {
			e.p.imageMode(PApplet.CENTER);
			g.pushMatrix();
			g.translate(position.x, position.y);
			g.scale(scale.x, scale.y, scale.z);
			g.tint(255, PApplet.map(fade, 0, 1, 0, 255));
			g.image(image, 0, 0);
			g.popMatrix();
		}
	}
}
