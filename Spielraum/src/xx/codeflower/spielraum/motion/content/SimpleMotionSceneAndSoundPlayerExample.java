package xx.codeflower.spielraum.motion.content;

import ddf.minim.AudioOutput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PGraphics;
import xx.codeflower.sound.audioeffects.CallAndResponseRepeater;
import xx.codeflower.spielraum.motion.scene.SimpleMotionScene;

public class SimpleMotionSceneAndSoundPlayerExample extends SimpleMotionScene {
	
	private Minim minim;
	private AudioPlayer track;
	private CallAndResponseRepeater effect;
	private AudioOutput out;
	
	public SimpleMotionSceneAndSoundPlayerExample(PApplet p, int code, int fadeSpeed, Minim minim) {
		super(p, code, fadeSpeed);
		
		// AUDIO STUFF
		/*
		track = minim.loadFile("data/sound/shlohmo.mp3");
		track.loop();
		*/
		 // make a band pass filter with a center frequency of 440 Hz and a bandwidth of 20 Hz
		 // the third argument is the sample rate of the audio that will be filtered
		 // it is required to correctly compute values used by the filter
		/*
		 effect = new CallAndResponseRepeater();
		 track.addEffect(effect);
		 */
	
	}

	@Override
	public void draw(PGraphics g) {
//		g.background(100);
		
		/*
		g.fill(0);
		g.noStroke();
		g.rect(0, 0, g.width, g.height);
		*/
		
		if(data != null) {
				
			// map the position of the mouse to useful values
//			float lof = PApplet.map(data.LEFT_HAND.x, 0, data.width, 1f, 10.0f);
			/*
			float lof = PApplet.map(data.LEFT_HAND.x, 0, data.width-400, 1f, 10.0f);

			effect.setTimesToRepeat(lof);
			*/
			/*
			for(int i = 0; i < 25; i++) {
				g.rect(data.LEFT_HAND.x+p.random(-10, 10), data.LEFT_HAND.y+p.random(-10, 10), data.RIGHT_HAND.x+p.random(-10, 10), data.RIGHT_HAND.y+p.random(-10, 10));
			}
			*/
			
			
			System.out.println(data.LEFT_FOOT.z);
			
			g.fill(PApplet.map(data.HEAD.y, 0, data.height, 255, 0));
			
			g.fill(200, 0, 0);
			g.ellipse(data.LEFT_FOOT.x, PApplet.map(data.LEFT_FOOT.z, 1600, 4000, g.height, 0), 50, 50);
			
			
			/*
			g.beginShape();
//				System.out.println(data.HEAD.z);
				g.vertex(data.HEAD.x, data.HEAD.y, -(data.HEAD.z/50));
				g.vertex(data.TORSO.x, data.TORSO.y, -(data.TORSO.z/50));
				g.vertex(data.LEFT_KNEE.x, data.LEFT_KNEE.y, -(data.LEFT_KNEE.z/50));
				g.vertex(data.RIGHT_SHOULDER.x, data.LEFT_SHOULDER.y, -(data.LEFT_ELBOW.z-200));
			g.endShape();
			*/
			
			/*
			g.fill(0, 200);
			for(int i = 0; i < 20; i++) {
				g.ellipse(p.random(data.HEAD.x-100, data.HEAD.x+100)+500, p.random(data.HEAD.y, data.LEFT_FOOT.y), 100, 100);
			}
			*/
			
			/*
			g.ellipse(data.HEAD.x, data.HEAD.y, data.HEAD.z/20, data.HEAD.z/20);
			
			g.ellipse(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y, data.RIGHT_SHOULDER.z/20, data.RIGHT_SHOULDER.z/20);
			g.ellipse(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y, data.LEFT_SHOULDER.z/20, data.LEFT_SHOULDER.z/20);
	
			g.ellipse(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y, data.LEFT_ELBOW.z/20, data.LEFT_ELBOW.z/20);
			g.ellipse(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y, data.RIGHT_ELBOW.z/20, data.RIGHT_ELBOW.z/20);
			
			g.ellipse(data.LEFT_HAND.x, data.LEFT_HAND.y, data.LEFT_HAND.z/20, data.LEFT_HAND.z/20);
			g.ellipse(data.RIGHT_HAND.x, data.RIGHT_HAND.y, data.RIGHT_HAND.z/20, data.RIGHT_HAND.z/20);
			
			g.ellipse(data.TORSO.x, data.TORSO.y, data.TORSO.z/20, data.TORSO.z/20);

			g.ellipse(data.LEFT_HIP.x, data.LEFT_HIP.y, data.LEFT_HIP.z/20, data.LEFT_HIP.z/20);
			g.ellipse(data.RIGHT_HIP.x, data.RIGHT_HIP.y, data.RIGHT_HIP.z/20, data.RIGHT_HIP.z/20);

			g.ellipse(data.LEFT_KNEE.x, data.LEFT_KNEE.y, data.LEFT_KNEE.z/20, data.LEFT_KNEE.z/20);
			g.ellipse(data.RIGHT_KNEE.x, data.RIGHT_KNEE.y, data.RIGHT_KNEE.z/20, data.RIGHT_KNEE.z/20);
			
			g.ellipse(data.LEFT_FOOT.x, data.LEFT_FOOT.y, data.LEFT_FOOT.z/20, data.LEFT_FOOT.z/20);
			g.ellipse(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y, data.RIGHT_FOOT.z/20, data.RIGHT_FOOT.z/20);	
			*/
			
			/*
				g.fill(200, 20);
				g.stroke(255, 20);
			
				g.beginShape(PApplet.POLYGON);
					g.vertex(data.HEAD.x, data.HEAD.y, -2);
					g.vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y, -2);
					g.vertex(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y, -2);
					g.vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y, -2);
					g.vertex(data.TORSO.x, data.TORSO.y, -2);
					g.vertex(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y, -2);
					g.vertex(data.LEFT_FOOT.x, data.LEFT_FOOT.y, -2);
					g.vertex(data.TORSO.x, data.TORSO.y, -2);
					g.vertex(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y, -2);
					g.vertex(data.LEFT_HAND.x, data.LEFT_HAND.y, -2);
					g.vertex(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y, -2);
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
