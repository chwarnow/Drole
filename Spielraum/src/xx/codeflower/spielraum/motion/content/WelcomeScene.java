package xx.codeflower.spielraum.motion.content;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import xx.codeflower.spielraum.motion.scene.Scene;

public class WelcomeScene extends Scene {

	private PFont font;
	
	private float alpha = 255;
	
	public WelcomeScene(PApplet p, int code, int fadeSpeed, PFont font) {
		super(p, code, fadeSpeed);
		this.font = font;
	}

	@Override
	public void draw(PGraphics g) {
		if(state == FADEIN) alpha = PApplet.map(fadeTick, 0, fadeSpeed, 0, 255);
		else if(state == FADEOUT) alpha = PApplet.map(fadeTick, 0, fadeSpeed, 0, 255);
		
		g.background(255);
		g.fill(0, alpha);
		g.noStroke();
		g.textFont(font);
		g.text("Kunst & Zensur", (g.width/2)-100, (g.height/2)-20);
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
