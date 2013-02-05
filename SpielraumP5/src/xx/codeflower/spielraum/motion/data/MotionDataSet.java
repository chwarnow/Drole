package xx.codeflower.spielraum.motion.data;

import java.io.Serializable;

import processing.core.PVector;

public class MotionDataSet implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	public int width, height;
	
	public PVector 	NECK,
					HEAD,
					LEFT_SHOULDER, RIGHT_SHOULDER,
					LEFT_ELBOW, RIGHT_ELBOW,
					LEFT_HAND, RIGHT_HAND,
					TORSO,
					LEFT_HIP, RIGHT_HIP,
					LEFT_KNEE, RIGHT_KNEE,
					LEFT_FOOT, RIGHT_FOOT;
	
	public MotionDataSet() {
		this.NECK 				= new PVector(0, 0, 0);
		this.HEAD 				= new PVector(0, 0, 0);
		this.LEFT_SHOULDER		= new PVector(0, 0, 0);
		this.RIGHT_SHOULDER 	= new PVector(0, 0, 0);
		this.LEFT_ELBOW 		= new PVector(0, 0, 0);
		this.RIGHT_ELBOW 		= new PVector(0, 0, 0);
		this.LEFT_HAND 			= new PVector(0, 0, 0);
		this.RIGHT_HAND 		= new PVector(0, 0, 0);
		this.TORSO 				= new PVector(0, 0, 0);
		this.LEFT_HIP 			= new PVector(0, 0, 0);
		this.RIGHT_HIP 			= new PVector(0, 0, 0);
		this.LEFT_KNEE 			= new PVector(0, 0, 0);
		this.RIGHT_KNEE 		= new PVector(0, 0, 0);
		this.LEFT_FOOT 			= new PVector(0, 0, 0);
		this.RIGHT_FOOT 		= new PVector(0, 0, 0);
		this.width				= 0;
		this.height				= 0;
	}
	
	public MotionDataSet(
			PVector NECK,
			PVector HEAD,
			PVector LEFT_SHOULDER, PVector RIGHT_SHOULDER,
			PVector LEFT_ELBOW, PVector RIGHT_ELBOW,
			PVector LEFT_HAND, PVector RIGHT_HAND,
			PVector TORSO,
			PVector LEFT_HIP, PVector RIGHT_HIP,
			PVector LEFT_KNEE, PVector RIGHT_KNEE,
			PVector LEFT_FOOT, PVector RIGHT_FOOT,
			int width, int height
	) {
		this.NECK 				= NECK;
		this.HEAD 				= HEAD;
		this.LEFT_SHOULDER		= LEFT_SHOULDER;
		this.RIGHT_SHOULDER 	= RIGHT_SHOULDER;
		this.LEFT_ELBOW 		= LEFT_ELBOW;
		this.RIGHT_ELBOW 		= RIGHT_ELBOW;
		this.LEFT_HAND 			= LEFT_HAND;
		this.RIGHT_HAND 		= RIGHT_HAND;
		this.TORSO 				= TORSO;
		this.LEFT_HIP 			= LEFT_HIP;
		this.RIGHT_HIP 			= RIGHT_HIP;
		this.LEFT_KNEE 			= LEFT_KNEE;
		this.RIGHT_KNEE 		= RIGHT_KNEE;
		this.LEFT_FOOT 			= LEFT_FOOT;
		this.RIGHT_FOOT 		= RIGHT_FOOT;
		this.width				= width;
		this.height				= height;
	}
	
	public MotionDataSet clone() {		
		return new MotionDataSet(NECK.get(), HEAD.get(), LEFT_SHOULDER.get(), RIGHT_SHOULDER.get(), LEFT_ELBOW.get(), RIGHT_ELBOW.get(), LEFT_HAND.get(), RIGHT_HAND.get(), TORSO.get(), LEFT_HIP.get(), RIGHT_HIP.get(), LEFT_KNEE.get(), RIGHT_KNEE.get(), LEFT_FOOT.get(), RIGHT_FOOT.get(), width, height);
	}
	
	public MotionDataSet mult(PVector v) {
		this.NECK.mult(v);
		this.HEAD.mult(v);
		this.LEFT_SHOULDER.mult(v);
		this.RIGHT_SHOULDER.mult(v);
		this.LEFT_ELBOW.mult(v);
		this.RIGHT_ELBOW.mult(v);
		this.LEFT_HAND.mult(v);
		this.RIGHT_HAND.mult(v);
		this.TORSO.mult(v);
		this.LEFT_HIP.mult(v);
		this.RIGHT_HIP.mult(v);
		this.LEFT_KNEE.mult(v);
		this.RIGHT_KNEE.mult(v);
		this.LEFT_FOOT.mult(v);
		this.RIGHT_FOOT.mult(v);
		
		return this;
	}
	
	public MotionDataSet add(PVector v) {
		this.NECK.add(v);
		this.HEAD.add(v);
		this.LEFT_SHOULDER.add(v);
		this.RIGHT_SHOULDER.add(v);
		this.LEFT_ELBOW.add(v);
		this.RIGHT_ELBOW.add(v);
		this.LEFT_HAND.add(v);
		this.RIGHT_HAND.add(v);
		this.TORSO.add(v);
		this.LEFT_HIP.add(v);
		this.RIGHT_HIP.add(v);
		this.LEFT_KNEE.add(v);
		this.RIGHT_KNEE.add(v);
		this.LEFT_FOOT.add(v);
		this.RIGHT_FOOT.add(v);
		
		return this;
	}	
	
	public MotionDataSet normalize() {
		return mult(new PVector(1f/width, 1f/height, 1f));
	}
	
	public void calcDirVectors(MotionDataSet mds) {
		
	}
	
	@Override
	public String toString() {
		return HEAD+":"+NECK+":"+LEFT_SHOULDER+":"+RIGHT_SHOULDER;
	}
	
}
