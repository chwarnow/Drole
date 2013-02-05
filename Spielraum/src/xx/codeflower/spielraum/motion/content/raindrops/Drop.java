package xx.codeflower.spielraum.motion.content.raindrops;



import processing.core.PApplet;

public class Drop {

	float x, y;
	float maxRadius;
	float currentRadius = 0;
	float speed;
	public boolean alive = true;
	private PApplet p;
	float trans = 255;

	public Drop(PApplet p, float x, float y, float maxRadius) {
		this.p = p;
		this.speed = p.random(10, 20) * .07f;
		this.x = x;
		this.y = y;

		this.maxRadius = maxRadius;
	}

	public void draw() {
		//p.noFill();
		p.fill(255, trans/2);
		p.stroke(255, trans+20);
		p.ellipse(x, y, currentRadius, currentRadius);
	}

	public void update() {
		if (currentRadius < maxRadius) {
			currentRadius += speed;
			trans -= 10;
		} else {
			alive = false;
		}

	}

}
