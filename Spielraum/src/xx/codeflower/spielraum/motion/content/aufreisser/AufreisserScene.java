package xx.codeflower.spielraum.motion.content.aufreisser;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.scene.SimpleMotionScene;

public class AufreisserScene extends SimpleMotionScene {

	boolean riss_zeichnen = false;
	Minim minim;
	AudioPlayer reissen;
	AudioPlayer rascheln;
	PVector startpos;
	float start_posX;
	float start_posY;
	float start_posZ;

	int[] oben_punkte = {0, 0, 640, 0, 1280, 0};
	int[] unten_punkte = {0, 800, 640, 800, 1280, 800};
	int[] links_punkte = {0, 400};
	int[] rechts_punkte = {1280, 400};

	float[] savePointsX = new float[0];
	float[] savePointsY = new float[0];
	float[] savePointsStartX = new float[0];
	float[] savePointsStartY = new float[0];
	int[] farbe = new int[0];
	
	public AufreisserScene(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
		
		minim = new Minim(p);
		  reissen = minim.loadFile("data/aufreisser/langsamerReisser.mp3");
		  rascheln = minim.loadFile("data/aufreisser/Knister10min.MP3");
		  
		  rascheln.play();
		  p.g.stroke(180, 140, 50);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(PGraphics g) {
			if(data == null) return;
			
			  g.background(255);
			  if (savePointsX.length > 0) {
			  for (int i = 0; i < savePointsX.length; i++) {
				  g.stroke(farbe[i]);
				  g.line(savePointsStartX[i], savePointsStartY[i], savePointsX[i], savePointsY[i]);
			  }
			  }
			  
			  
			  if (((data.LEFT_HAND.y > 600) || (data.RIGHT_HAND.y > 600)) && (riss_zeichnen == false)) {
			      
				  g.strokeWeight(10000);
			     
			      startpos = data.RIGHT_HAND.get();
			      start_posX = startpos.x;
			      start_posY = PApplet.map(data.RIGHT_HAND.z, 1600, 4000, g.height, 0);
			      start_posZ = startpos.z;
			      
			      riss_zeichnen = true;
			      System.out.println(startpos);
			      System.out.println("unten");

			    }  
			    
			  if (((data.LEFT_HAND.y < 600) && (data.RIGHT_HAND.y < 600)) && (riss_zeichnen == true)) {
			    riss_zeichnen = false;
			    reissen.pause();
			    reissen.rewind();
			    PVector endpos = data.RIGHT_HAND.get();
			    savePointsStartX = PApplet.append(savePointsStartX, start_posX);
			    savePointsStartY = PApplet.append(savePointsStartY, start_posY); 
			    savePointsX = PApplet.append(savePointsX, endpos.x);
			    savePointsY = PApplet.append(savePointsY, PApplet.map(endpos.z, 1600, 4000, g.height, 0));
			    farbe = PApplet.append(farbe, g.color(160+p.random(40), 120+p.random(40), p.random(100)));
			  }
			  
			  if (riss_zeichnen == true) {
			    
			    reissen.play();
			    g.beginShape();
			    g.vertex(start_posX, start_posY);
			    g.vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y);
			    g.endShape();
			  
			  }

	}
	
	@Override
	public void onNewUserData(int userid, MotionDataSet data) {
		super.onNewUserData(userid, data);
	}

}
