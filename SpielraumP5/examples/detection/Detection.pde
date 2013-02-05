import xx.codeflower.base.*;
import xx.codeflower.spielraump5.*;
import xx.codeflower.spielraum.motion.source.*;
import xx.codeflower.spielraum.motion.detection.*;

SpielraumP5 spielraum;
MotionDataSet data;
	
void setup() {
	size(800, 600, OPENGL);
	smooth();
		
	spielraum = new SpielraumP5(this, "data/walking.motion");
	spielraum.start();
}
	
void draw() {
	// Get next MotionDataSet
	data = spielraum.getMotionData();
	
	background(255);
	
	/* Draw something with MotionData */
	
	fill(20);
	stroke(120);
	
	beginShape();
		// Head & Shoulder ;)
		vertex(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y, -data.LEFT_SHOULDER.z);
		vertex(data.HEAD.x, data.HEAD.y, -data.HEAD.z);
		vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y, -data.RIGHT_SHOULDER.z);
		
		// Right Arm
		vertex(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y, -data.RIGHT_ELBOW.z);
		vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y, -data.RIGHT_HAND.z);
		
		// Torso
		vertex(data.TORSO.x-10, data.TORSO.y, -data.TORSO.z);
		
		// Right Leg
		vertex(data.RIGHT_HIP.x, data.RIGHT_HIP.y, -data.RIGHT_HIP.z);
		vertex(data.RIGHT_KNEE.x, data.RIGHT_KNEE.y, -data.RIGHT_KNEE.z);
		vertex(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y, -data.RIGHT_FOOT.z);

		// Left Leg
		vertex(data.LEFT_FOOT.x, data.LEFT_FOOT.y, -data.LEFT_FOOT.z);
		vertex(data.LEFT_KNEE.x, data.LEFT_KNEE.y, -data.LEFT_KNEE.z);
		vertex(data.LEFT_HIP.x, data.LEFT_HIP.y, -data.LEFT_HIP.z);

		// Torso again
		vertex(data.TORSO.x+10, data.TORSO.y, -data.TORSO.z);
		
		// Left Arm
		vertex(data.LEFT_HAND.x, data.LEFT_HAND.y, -data.LEFT_HAND.z);
		vertex(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y, -data.LEFT_ELBOW.z);
	endShape();
}

void motionDetected(String motion) {
	println(motion);
}