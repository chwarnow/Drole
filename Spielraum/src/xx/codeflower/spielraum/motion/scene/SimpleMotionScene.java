package xx.codeflower.spielraum.motion.scene;

import processing.core.PApplet;
import processing.core.PVector;
import xx.codeflower.spielraum.motion.data.MotionDataSet;

public abstract class SimpleMotionScene extends MotionScene {

	protected MotionDataSet data;
	
	private PVector viewVector;
	
	public SimpleMotionScene(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
		
		viewVector = new PVector(p.width, p.height, 1);
	}
	
	@Override
	public void onNewUser(int userid) {
	}

	@Override
	public void onLostUser(int userid) {
	}

	@Override
	public void onNewUserData(int userid, MotionDataSet data) {
		this.data = data.normalize();
		data.mult(viewVector);
	}

}
