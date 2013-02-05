package xx.codeflower.spielraum.motion.scene;

import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class Scene {

	public final static short OFFSCREEN = 0, DRAWING = 10, FADEIN = 20, FADEOUT = 30;
	
	public short state = OFFSCREEN;
	
	protected PApplet p;
	
	public int code;
	
	protected int fadeEnd = 1000, fadeTick = 0, fadeSpeed;
	
	public Scene(PApplet p, int code, int fadeSpeed) {
		this.p 			= p;
		this.code		= code;
		this.fadeSpeed 	= fadeSpeed;
	}
	
	public void metaDraw(PGraphics g) {
		if(state != OFFSCREEN) draw(g);
		if(state == FADEIN)	{
			fadeTick++;
			if(fadeTick == fadeEnd) {
				state = DRAWING;
				log("Drawing!");
			}
		}
		if(state == FADEOUT)	{
			fadeTick--;
			if(fadeTick == fadeEnd) {
				state = OFFSCREEN;
				log("Bye Bye!");
				stop();
			}
		}
	}
	
	public abstract void start();
	public abstract void stop();
	
	public abstract void draw(PGraphics g);
	
	public void fadeIn() {
		fadeTick = 0;
		fadeEnd = fadeSpeed;
		this.state = FADEIN;
		log("Fading in ...");
		start();
	}

	public void fadeOut() {
		fadeTick = fadeSpeed;
		fadeEnd = 0;
		this.state = FADEOUT;
		log("Fading out ...");
	}
	
	protected void log(Object msg) {
		System.out.println(this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1)+"["+code+"] "+msg);
	}
	
}
