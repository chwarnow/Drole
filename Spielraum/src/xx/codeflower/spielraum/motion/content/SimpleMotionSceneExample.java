package xx.codeflower.spielraum.motion.content;

import processing.core.PApplet;
import processing.core.PGraphics;
import xx.codeflower.spielraum.motion.scene.SimpleMotionScene;

public class SimpleMotionSceneExample extends SimpleMotionScene {

	public SimpleMotionSceneExample(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
	}

	@Override
	public void draw(PGraphics g) {
		g.fill(20, 120);
		g.noStroke();
		g.rect(0, 0, g.width, g.height);
		
		g.fill(200, 20);
		g.stroke(255, 20);
		
		if(data != null) {
			g.beginShape();
				g.vertex(data.HEAD.x, data.HEAD.y);
				g.vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y);
				g.vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y);
				g.vertex(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y);
				g.vertex(data.LEFT_FOOT.x, data.LEFT_FOOT.y);
				g.vertex(data.LEFT_HAND.x, data.LEFT_HAND.y);
				g.vertex(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y);
			g.endShape();
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
