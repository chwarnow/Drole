package xx.codeflower.spielraum.motion.content;

import processing.core.PApplet;
import processing.core.PGraphics;
import xx.codeflower.spielraum.motion.scene.SimpleMotionScene;

public class SimpleMotionScenePlayerExample extends SimpleMotionScene {
	
	public SimpleMotionScenePlayerExample(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
	}

	@Override
	public void draw(PGraphics g) {		
		g.fill(255);
		g.noStroke();
		g.rect(0, 0, g.width, g.height);
		
		if(data != null) {
		
			g.fill(0);
			
			g.ellipse(data.HEAD.x, data.HEAD.y, data.HEAD.z/42, data.HEAD.z/42);
			
			g.ellipse(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y, data.RIGHT_SHOULDER.z/42, data.RIGHT_SHOULDER.z/42);
			g.ellipse(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y, data.LEFT_SHOULDER.z/42, data.LEFT_SHOULDER.z/42);
	
			g.ellipse(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y, data.LEFT_ELBOW.z/42, data.LEFT_ELBOW.z/42);
			g.ellipse(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y, data.RIGHT_ELBOW.z/42, data.RIGHT_ELBOW.z/42);
			
			g.ellipse(data.LEFT_HAND.x, data.LEFT_HAND.y, data.LEFT_HAND.z/42, data.LEFT_HAND.z/42);
			g.ellipse(data.RIGHT_HAND.x, data.RIGHT_HAND.y, data.RIGHT_HAND.z/42, data.RIGHT_HAND.z/42);
			
			g.ellipse(data.TORSO.x, data.TORSO.y, data.TORSO.z/42, data.TORSO.z/42);

			g.ellipse(data.LEFT_HIP.x, data.LEFT_HIP.y, data.LEFT_HIP.z/42, data.LEFT_HIP.z/42);
			g.ellipse(data.RIGHT_HIP.x, data.RIGHT_HIP.y, data.RIGHT_HIP.z/42, data.RIGHT_HIP.z/42);

			g.ellipse(data.LEFT_KNEE.x, data.LEFT_KNEE.y, data.LEFT_KNEE.z/42, data.LEFT_KNEE.z/42);
			g.ellipse(data.RIGHT_KNEE.x, data.RIGHT_KNEE.y, data.RIGHT_KNEE.z/42, data.RIGHT_KNEE.z/42);
			
			g.ellipse(data.LEFT_FOOT.x, data.LEFT_FOOT.y, data.LEFT_FOOT.z/42, data.LEFT_FOOT.z/42);
			g.ellipse(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y, data.RIGHT_FOOT.z/42, data.RIGHT_FOOT.z/42);	
			
			/*
				g.fill(200, 20);
				g.stroke(255, 20);
			
				g.beginShape();
					g.vertex(data.HEAD.x, data.HEAD.y);
					g.vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y);
					g.vertex(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y);
					g.vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y);
					g.vertex(data.TORSO.x, data.TORSO.y);
					g.vertex(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y);
					g.vertex(data.LEFT_FOOT.x, data.LEFT_FOOT.y);
					g.vertex(data.TORSO.x, data.TORSO.y);
					g.vertex(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y);
					g.vertex(data.LEFT_HAND.x, data.LEFT_HAND.y);
					g.vertex(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y);
				g.endShape();
			 */
		}
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
