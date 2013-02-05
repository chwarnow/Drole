package xx.codeflower.spielraum.motion.content.raindrops;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.effects.BandPass;

import processing.core.PApplet;
import processing.core.PVector;
import xx.codeflower.spielraum.motion.data.MotionDataSet;

public class Field {

	private float radius;
	private float x, y;
	private float xSpeed;
	private float ySpeed;
	private Ellipse2D.Float circle;
	private float volume = -60;

	private ArrayList<Drop> dropList = new ArrayList<Drop>();
	private ArrayList<Drop> killList = new ArrayList<Drop>();
	private PApplet p;

	Minim minim;
	AudioPlayer groove;
	BandPass bpf;

	float dropMaxSize;
	boolean dynamic;

	public Field(PApplet p, int dropAmount, float radius, float x, float y,
			float xSpeed, float ySpeed, String fileName, float dropSize,
			boolean dynamic) {
		this.p = p;

		this.radius = radius;
		this.x = x;
		this.y = y;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.circle = new Ellipse2D.Float(x - radius, y - radius, radius * 2,
				radius * 2);
		this.dropMaxSize = dropSize;
		this.dynamic = dynamic;

		if (!dynamic) {
			volume = -22;
		}

		for (int i = 0; i < dropAmount; i++) {
			makeDropInsideField();
		}

		minim = new Minim(p);

		groove = minim.loadFile("data/raindrops/" + fileName);
		groove.loop();
		groove.setGain(volume);
		// groove.pause();
		// make a band pass filter with a center frequency of 440 Hz and a
		// bandwidth of 20 Hz
		// the third argument is the sample rate of the audio that will be
		// filtered
		// it is required to correctly compute values used by the filter
		bpf = new BandPass(440, 20, groove.sampleRate());
//		groove.addEffect(bpf);

	}

	public void runField(MotionDataSet data, float schirmHeight) {

		if (dynamic) {
			x += xSpeed;
			y += ySpeed;

			circle.x = x - radius;
			circle.y = y - radius;

			if (circle.contains(data.LEFT_FOOT.x, PApplet.map(data.LEFT_FOOT.z, 1600, 4000, 1200, 0))) {
			/*
			if (schirmHeight > 0) {
					groove.enableEffect(bpf);
					float passBand = PApplet.map(schirmHeight, 100, 260, 300,
							400);
					bpf.setFreq(passBand);
					float bandWidth = 500; // map(mouseY, 0, height, 50, 500);
					bpf.setBandWidth(bandWidth);
				} else {
					groove.disableEffect(bpf);
					
//					float passBand = 500;
//					bpf.setFreq(passBand);
//					float bandWidth = 1500; // map(mouseY, 0, height, 50, 500);
//					bpf.setBandWidth(bandWidth);
					
					
				}
			*/

				PVector center = new PVector(x, y);
				float userX = data.LEFT_FOOT.x;
				float userY = PApplet.map(data.LEFT_FOOT.z, 1600, 4000, 1200, 0);
				
				System.out.println(x+","+y+" - "+userX+", "+userY);
				
				PVector mouse = new PVector(userX, userY);
//				PVector mouse = new PVector(p.mouseX, p.mouseY);
				
				volume = PApplet.map(center.dist(mouse), radius, radius
						- radius / 3, -60, -5);

				if (volume > -5) {
					volume = -5;
				}

				groove.setGain(volume);
			} else {
				groove.setGain(-60);
			}
		}

		// p.noStroke();
		// p.fill(255, 0, 0, 30);
		// p.ellipse(x, y, 2 * radius, 2 * radius);

		drawAndUpdateDrops();

		checkBoundries();
	}

	private void checkBoundries() {
		if (y > p.height + radius + 50 || x > p.width + radius + 50
				|| x < 0 - radius - 50) {
			x = p.random(p.width);
			y = p.random(-radius + 50, -radius + 300);
		}
	}

	private void drawAndUpdateDrops() {
		for (Drop d : dropList) {
			d.update();
			p.pushMatrix();
			p.translate(x, y);
			d.draw();
			p.popMatrix();

			if (!d.alive) {
				killList.add(d);
			}
		}
		for (Drop d : killList) {
			dropList.remove(d);
			makeDropInsideField();
		}
		killList.clear();
	}

	void makeDropInsideField() {
		float xInsideCircle = p.random(-radius, radius);
		float yInsideCircle = p.random(-1, 1)
				* PApplet.sqrt(radius * radius - xInsideCircle * xInsideCircle);
		dropList.add(new Drop(p, xInsideCircle, yInsideCircle, dropMaxSize));
	}

	void stop() {
		// always close Minim audio classes when you finish with them
		groove.close();
		// always stop Minim before exiting
		minim.stop();

		// super.stop();
	}
}
