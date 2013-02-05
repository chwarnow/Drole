package raindrops;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import processing.core.PApplet;
import xx.codeflower.base.*;
import xx.codeflower.spielraump5.*;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.source.*;

public class Raindrops extends PApplet {

	SpielraumP5 spielraum;
	MotionDataSet data;

	float oldLeftHandY = 0;
	float oldRightHandY = 0;

	float thrishold = 200;
	float schirmHeight = -1;

	boolean schirm = false;
	
	boolean footUpL = false;
	boolean footUpR = false;
	
	boolean footUp = false;

	ArrayList<Field> fieldList = new ArrayList<Field>();
	ArrayList<Platsch> platschList = new ArrayList<Platsch>();

	public void setup() {
		size(800, 800, OPENGL);
		smooth();

		spielraum = new SpielraumP5(this,
				"data/motion/capture_1347643300368.motion");
		spielraum.start();

		fieldList.add(new Field(this,  20, 200, 400, 400, -.1f, .4f,
				"rain1.mp3", 20, true));
		fieldList.add(new Field(this,  600, 400, 100, 100, .6f, .3f,
				"rain4.aiff", 2, true));
		fieldList.add(new Field(this,  300, 300, 100, -210, -.6f, .3f,
				"rain4.aiff", 1,true));
		fieldList.add(new Field(this,  70, 500, 200, -150, .2f, .6f,
				"rain3.mp3", 10,true));
		fieldList.add(new Field(this,  30, 300, 20, -150, .3f, .1f,
				"rain3.mp3", 20,true));
		fieldList.add(new Field(this, 10, 200, 0, 100, .1f, .3f,
				"rain2.wav", 30,true));
		fieldList.add(new Field(this, 150, 200, 200, -200, .01f, .3f,
				"rain2.wav", 20,true));
		
		
		fieldList.add(new Field(this, 2, 500, width/2, height/2, 0, 0,
				"ZWITSCHER.wav", 2, false));


	}

	public void draw() {
		background(0);

		// Get next MotionDataSet
		data = spielraum.getMotionData();

		for (Field f : fieldList) {
			f.runField(data, schirmHeight);
		}

		try {
			for (Platsch p : platschList) {
				if (p.alive) {
					p.update();
					p.draw();
				} else {
					platschList.remove(p);
				}
			}
		} catch (ConcurrentModificationException e) {

		}

		stroke(0);

		drawHandBalls();
		prooveForSchirm();
		drawSkelleton();

		checkForPlatsch();
	}

	private void checkForPlatsch() {

		if (data.LEFT_FOOT.y < 690) {
			footUp = true;
		}
		
		if (data.LEFT_FOOT.y > 720 || data.RIGHT_FOOT.y > 720 ) {
			if(footUp) {
			 platschList.add(new Platsch(this, data.LEFT_FOOT.x,
			 data.LEFT_FOOT.y));
			 footUp = false;
			}
		}

	}


	private void drawSkelleton() {
		noFill();
		beginShape();
		// Head & Shoulder ;)
		vertex(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y,
				-data.LEFT_SHOULDER.z);
		vertex(data.HEAD.x, data.HEAD.y, -data.HEAD.z);
		vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y,
				-data.RIGHT_SHOULDER.z);

		// Right Arm
		vertex(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y, -data.RIGHT_ELBOW.z);
		vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y, -data.RIGHT_HAND.z);

		// Torso
		vertex(data.TORSO.x - 10, data.TORSO.y, -data.TORSO.z);

		// Right Leg
		vertex(data.RIGHT_HIP.x, data.RIGHT_HIP.y, -data.RIGHT_HIP.z);
		vertex(data.RIGHT_KNEE.x, data.RIGHT_KNEE.y, -data.RIGHT_KNEE.z);
		vertex(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y, -data.RIGHT_FOOT.z);

		// Left Leg
		vertex(data.LEFT_FOOT.x, data.LEFT_FOOT.y, -data.LEFT_FOOT.z);
		vertex(data.LEFT_KNEE.x, data.LEFT_KNEE.y, -data.LEFT_KNEE.z);
		vertex(data.LEFT_HIP.x, data.LEFT_HIP.y, -data.LEFT_HIP.z);

		// Torso again
		vertex(data.TORSO.x + 10, data.TORSO.y, -data.TORSO.z);

		// Left Arm
		vertex(data.LEFT_HAND.x, data.LEFT_HAND.y, -data.LEFT_HAND.z);
		vertex(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y, -data.LEFT_ELBOW.z);
		endShape();
	}

	private void prooveForSchirm() {
		float frameDifLH = abs(oldLeftHandY - data.LEFT_HAND.y);
		float frameDifRH = abs(oldRightHandY - data.RIGHT_HAND.y);

		float difRH = data.NECK.y + data.TORSO.y - data.NECK.y
				- data.RIGHT_HAND.y;
		float difLH = data.NECK.y + data.TORSO.y - data.NECK.y
				- data.LEFT_HAND.y;
		float difTH = abs(data.TORSO.y - data.HEAD.y);

		

		if (difTH > 60) {
			if (frameDifLH < thrishold || frameDifRH < thrishold) {
				if (difLH > 0 || difRH > 0) {
					schirm = true;
				} else {
					schirm = false;
				}
			}
		}
		
		if (schirm) {
			if(difLH > difRH) {
				schirmHeight = difLH;
			} else {
				schirmHeight = difRH;
			}
		} else {
			schirmHeight = -1;
		}
		
		println(schirmHeight);

		oldRightHandY = data.RIGHT_HAND.y;
		oldLeftHandY = data.LEFT_HAND.y;
	}

	private void drawHandBalls() {
		pushMatrix();
		translate(data.RIGHT_HAND.x, data.RIGHT_HAND.y, -data.RIGHT_HAND.z);
		fill(255, 0, 0);
		ellipse(0, 0, 20, 20);
		popMatrix();

		pushMatrix();
		translate(data.LEFT_HAND.x, data.LEFT_HAND.y, -data.LEFT_HAND.z);
		fill(0, 255, 0);
		ellipse(0, 0, 20, 20);
		popMatrix();
	}
}
