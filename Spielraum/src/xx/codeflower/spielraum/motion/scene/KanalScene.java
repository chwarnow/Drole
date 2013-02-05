package xx.codeflower.spielraum.motion.scene;

import processing.core.PApplet;
import processing.core.PGraphics;

public class KanalScene extends EditableScene {

	public KanalScene(PApplet p, int code, int fadeSpeed) {
		super(p, "Kanal", code, fadeSpeed);
	}

	@Override
	public void draw(PGraphics g) {
		if(state == FADEIN) for(SceneContent sc : content) sc.alpha = PApplet.map(fadeTick, 0, fadeSpeed, 0, 255);
		else if(state == FADEOUT) for(SceneContent sc : content) sc.alpha = PApplet.map(fadeTick, 0, fadeSpeed, 0, 255);
		
		for(SceneContent sc : content) sc.draw(g);
	}
	
}
