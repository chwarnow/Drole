package xx.codeflower.spielraum.motion.scene;

import processing.core.PApplet;
import processing.core.PGraphics;

public class BeatScene extends EditableScene {

	public BeatScene(PApplet p, int code, int fadeSpeed) {
		super(p, "Beat", code, fadeSpeed);
	}

	@Override
	public void draw(PGraphics g) {
		if(state == FADEIN) for(SceneContent sc : content) sc.alpha = PApplet.map(fadeTick, 0, fadeSpeed, 0, 255);
		else if(state == FADEOUT) for(SceneContent sc : content) sc.alpha = PApplet.map(fadeTick, 0, fadeSpeed, 0, 255);
		
		for(SceneContent sc : content) sc.draw(g);
	}
	
}
