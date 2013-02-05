package xx.codeflower.spielraum.motion.content.raindrops;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Platsch {

	float x, y;
	float trans = 255;
	float mintrans = 0;
	float size = .4f;
	float offSetX, offSetY;
	float maxSize;
	float speed;

	boolean alive = true;
	PApplet p;
	PImage img;

	Platsch(PApplet p, float x, float y) {
		this.x = x;
		this.y = y;

		this.p = p;

		if (p.random(1) < .5f) {
			img = p.loadImage("data/raindrops/I_platsch-04.png");
		} else {
			img = p.loadImage("data/raindrops/I_platsch-07.png");
		}

		offSetX = p.random(-30, 30);
		offSetY = p.random(-30, 30);

		maxSize = p.random(2, 4f);
		speed = p.random(.004f, .1f);
	}

	void draw() { // void hei§t, es wird nix zurŸckgegeben .. . = eine funktion

		p.imageMode(PConstants.CENTER);
		p.pushMatrix();
		p.translate(x + offSetX, y + offSetY);
		p.scale(size);
		p.tint(255, trans);
		p.image(img, 0, 0);
		p.popMatrix();
	}

	void update() {
		if (size < maxSize) {
			trans -= 10;
			size += speed;
		} else {
			alive = false;
		}
	}

}
