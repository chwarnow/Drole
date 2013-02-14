package drole;

import drole.engine.Drawable;
import processing.core.PApplet;
import processing.core.PImage;

public class Image extends Drawable {

	private PImage image;

	public Image(PApplet parent, String file) {
		super(parent);
		image = parent.loadImage(file);
	}

	public void draw() {
		if (image != null) {
			parent.imageMode(PApplet.CENTER);
			parent.g.pushMatrix();
			parent.g.translate(position.x, position.y);
			parent.g.scale(scale.x, scale.y, scale.z);
			parent.g.tint(255, PApplet.map(fade, 0, 1, 0, 255));
			parent.g.image(image, 0, 0);
			parent.g.popMatrix();
		}
	}
}
