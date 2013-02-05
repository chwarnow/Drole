package xx.codeflower.spielraum.motion.content;

import processing.core.PApplet;
import processing.core.PGraphics;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.scene.MotionScene;

public class TestMotionScene extends MotionScene {

	private MotionDataSet data;
	
	public TestMotionScene(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
	}

	/** ZEICHNEN **/
	@Override
	public void draw(PGraphics g) {
		if(data != null) {
			g.fill(255);
			g.text(data.HEAD.toString(), g.width-250, 25);
			g.text(data.NECK.toString(), g.width-250, 50);
			g.text(data.LEFT_SHOULDER.toString(), g.width-250, 75);
			g.text(data.RIGHT_SHOULDER.toString(), g.width-250, 100);
			g.text(data.LEFT_ELBOW.toString(), g.width-250, 125);
			g.text(data.RIGHT_ELBOW.toString(), g.width-250, 150);
			g.text(data.LEFT_HAND.toString(), g.width-250, 175);
			g.text(data.RIGHT_HAND.toString(), g.width-250, 200);
			g.text(data.TORSO.toString(), g.width-250, 225);
			g.text(data.LEFT_HIP.toString(), g.width-250, 250);
			g.text(data.RIGHT_HIP.toString(), g.width-250, 275);
			g.text(data.LEFT_KNEE.toString(), g.width-250, 300);
			g.text(data.RIGHT_KNEE.toString(), g.width-250, 325);
			g.text(data.LEFT_FOOT.toString(), g.width-250, 350);
			g.text(data.RIGHT_FOOT.toString(), g.width-250, 375);
			
			g.fill(0, 120, 80);
			g.noStroke();
			
			g.ellipse(data.HEAD.x, data.HEAD.y, data.HEAD.z/30, data.HEAD.z/30);
			g.ellipse(data.NECK.x, data.NECK.y, data.NECK.z/30, data.NECK.z/30);
			
			g.ellipse(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y, data.LEFT_SHOULDER.z/30, data.LEFT_SHOULDER.z/30);
			g.ellipse(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y, data.RIGHT_SHOULDER.z/30, data.RIGHT_SHOULDER.z/30);

			g.ellipse(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y, data.LEFT_ELBOW.z/30, data.LEFT_ELBOW.z/30);
			g.ellipse(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y, data.RIGHT_ELBOW.z/30, data.RIGHT_ELBOW.z/30);
			
			g.ellipse(data.LEFT_HAND.x, data.LEFT_HAND.y, data.LEFT_HAND.z/30, data.LEFT_HAND.z/30);
			g.ellipse(data.RIGHT_HAND.x, data.RIGHT_HAND.y, data.RIGHT_HAND.z/30, data.RIGHT_HAND.z/30);
			
			g.ellipse(data.TORSO.x, data.TORSO.y, data.TORSO.z/30, data.TORSO.z/30);
			
			g.ellipse(data.LEFT_HIP.x, data.LEFT_HIP.y, data.LEFT_HIP.z/30, data.LEFT_HIP.z/30);
			g.ellipse(data.RIGHT_HIP.x, data.RIGHT_HIP.y, data.RIGHT_HIP.z/30, data.RIGHT_HIP.z/30);
			
			g.ellipse(data.LEFT_KNEE.x, data.LEFT_KNEE.y, data.LEFT_KNEE.z/30, data.LEFT_KNEE.z/30);
			g.ellipse(data.RIGHT_KNEE.x, data.RIGHT_KNEE.y, data.RIGHT_KNEE.z/30, data.RIGHT_KNEE.z/30);
			
			g.ellipse(data.LEFT_FOOT.x, data.LEFT_FOOT.y, data.LEFT_FOOT.z/30, data.LEFT_FOOT.z/30);
			g.ellipse(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y, data.RIGHT_FOOT.z/30, data.RIGHT_FOOT.z/30);
		}
	}
	
	
	@Override
	public void onNewUser(int userid) {
	}

	@Override
	public void onLostUser(int userid) {
	}

	@Override
	public void onNewUserData(int userid, MotionDataSet data) {
		this.data = data;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
