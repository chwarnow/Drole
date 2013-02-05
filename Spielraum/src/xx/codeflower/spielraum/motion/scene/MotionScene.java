package xx.codeflower.spielraum.motion.scene;

import processing.core.PApplet;
import xx.codeflower.spielraum.motion.source.MotionListener;

public abstract class MotionScene extends Scene implements MotionListener {
	
	public MotionScene(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
	}

}
