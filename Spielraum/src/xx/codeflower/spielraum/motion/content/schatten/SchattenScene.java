package xx.codeflower.spielraum.motion.content.schatten;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import xx.codeflower.spielraum.motion.detection.MotionDetectionListener;
import xx.codeflower.spielraum.motion.detection.WalkDetector;
import xx.codeflower.spielraum.motion.scene.SimpleMotionScene;

public class SchattenScene extends SimpleMotionScene implements MotionDetectionListener {

	 public Minim minim;
	 public  AudioPlayer player1;
	 public AudioPlayer player2;
	 public AudioPlayer player3;
	 public AudioPlayer player4;
	  
	 public long startzeit = 0;
	 public boolean grow =false;
	 public float kopf = 0;
	 public float rotation = 0;
	 public float wachstum_1 = 0;
	 public float wachstum_2 = 0;
	 public float wachstum_3 = 0;
	 public float wachstum_4 = 0;
	 public long now = 0;
	 public float growFaktor = 0;
	 
	 private WalkDetector wd;
	
	public SchattenScene(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
		
		wd = new WalkDetector(60.0f);
		wd.addListener(this);
		
		 minim = new Minim(p);
		    
		    player1 = minim.loadFile("data/schatten/sound/Thats not Gizmo_1-Intro.mp3");
		    player1.play();
		    
		    player2 = minim.loadFile("data/schatten/sound/Thats not Gizmo_2-crescendo.mp3");
		    
		    player3 = minim.loadFile("data/schatten/sound/Thats not Gizmo_4-FinalBoss.mp3");
		    
		    player4 = minim.loadFile("data/schatten/sound/Thats not Gizmo_3-Outro.mp3");
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
		if(data != null) {
			wd.update(data);
		
		g.background(255);
	    
		growFaktor = (now/10000f)/3f;
		/*
		g.pushMatrix();
		g.translate(data.LEFT_FOOT.x, PApplet.map(data.RIGHT_FOOT.z, 1000, 3700, g.height, 0));
		g.scale(growFaktor, growFaktor);
		*/
	    /* Draw something with MotionData */
	    
		g.fill(20);
		g.stroke(120);
	    
	  if(grow==true) {
		  now = System.currentTimeMillis()-startzeit;
	      /* Draw something with MotionData */
	      
	      // SCHRITT 1
	      if(now > 200) {
	          player1.pause();
	          player1.rewind();
	          player2.play();
	        
	          g.fill(20);
	          g.stroke(120, 0, 0);
	          g.strokeWeight(0);
	          
	                //ellipse(width/2, height/2, data.HEAD.x, data.HEAD.x);
	                
	                kopf= kopf+0.01f;
	                rotation += 0.01; 
	                wachstum_1 = PApplet.map(PApplet.sin(rotation*5),-1, 1, 0.8f, 1.5f);
	                wachstum_2 = PApplet.map(PApplet.sin(rotation*10),-1, 1, 0.8f, 1.5f);
	                wachstum_3 = PApplet.map(PApplet.sin(rotation*12),-1, 1, 0.8f, 1.5f);
	                wachstum_4 = PApplet.map(PApplet.sin(rotation*7.5f),-1, 1, 0.8f, 1.5f);
	          
	            // Head & Shoulder ;)
	                g.pushMatrix();
	                g.translate(data.LEFT_SHOULDER.x-30, data.LEFT_SHOULDER.y);
	                g.rotateZ(rotation);
	                g.ellipse(0, 0, 264*wachstum_1*1.2f, 240*wachstum_1*1.2f);
	                g.popMatrix();
	                        
	                g.pushMatrix();
	                g.translate(data.HEAD.x, data.HEAD.y-70);
	                g.rotateZ(rotation*-0.5f);
	                g.ellipse(0, 0, 225*wachstum_2*0.9f, 240*wachstum_2*0.9f);
	                g.popMatrix();
	        
	        
	                g.pushMatrix();
	                g.translate(data.RIGHT_SHOULDER.x+30, data.RIGHT_SHOULDER.y);
	                g.rotateZ(rotation*7.5f);
	                g.ellipse(0, 0, 225*wachstum_3, 264*wachstum_3);
	                g.popMatrix();
	        
	                        // Torso
	                g.pushMatrix();
	                g.translate(data.TORSO.x, data.TORSO.y+70);
	                g.rotateZ(rotation*1);
	                g.ellipse(0, 0, 300*wachstum_4*1.0f, 255*wachstum_4*1.2f);
	                g.popMatrix();
	      }
	      
	      
	      // SCHRITT 2
	      if(now > 13000) {
	    	  g.fill(20);
	    	  g.stroke(120, 0, 0);
	    	  g.strokeWeight(0);
	    	  g.rectMode(PConstants.CENTER);
	              //rect(width/2, height/2, data.HEAD.x, data.HEAD.x);
	              
	              kopf= kopf+0.01f;
	              rotation += 0.008; 
	              //wachstum = mouseX/1000.0;
	              wachstum_1 = PApplet.map(PApplet.sin(rotation*30),-1, 1, 0.8f, 1.5f);
	              wachstum_2 = PApplet.map(PApplet.sin(rotation*10),-1, 1, 0.8f, 1.5f);
	              wachstum_3 = PApplet.map(PApplet.sin(rotation*20),-1, 1, 0.8f, 1.5f);
	              wachstum_4 = PApplet.map(PApplet.sin(rotation*40),-1, 1, 0.8f, 1.5f);
	          
	                      // Head & Shoulder ;)
	              g.pushMatrix();
	              g.translate(data.LEFT_SHOULDER.x-30, data.LEFT_SHOULDER.y);
	              g.rotateZ(rotation*50);
	              g.rect(0, 0, 264*wachstum_1*1.2f, 240*wachstum_1*1.2f);
	              g.popMatrix();
	                      
	              g.pushMatrix();
	              g.translate(data.HEAD.x, data.HEAD.y-50);
	              g.rotateZ(rotation*-75);
	              g.rect(0, 0, 225*wachstum_2*0.9f, 240*wachstum_2*0.9f);
	              g.popMatrix();
	      
	      
	              g.pushMatrix();
	              g.translate(data.RIGHT_SHOULDER.x+30, data.RIGHT_SHOULDER.y);
	              g.rotateZ(rotation*75);
	              g.rect(0, 0, 225*wachstum_3, 264*wachstum_3);
	              g.popMatrix();
	      
	                      // Torso
	              g.pushMatrix();
	              g.translate(data.TORSO.x, data.TORSO.y+70);
	              g.rotateZ(rotation*-100);
	              g.rect(0, 0, 155*wachstum_4*1.5f, 155*wachstum_4*1.5f);
	              g.popMatrix();
	      }
	      
	      // SCHRITT 3
	      if(now > 47000) {
	            g.fill(20, PApplet.map(PApplet.sin(rotation*100),-1, 1, 255, 0));
	            g.stroke(120, 0, 0);
	            g.strokeWeight(0);
	            g.rectMode(PConstants.CENTER);
	            //rect(width/2, height/2, data.HEAD.x, data.HEAD.x);
	            
	            kopf= kopf+0.01f;
	            rotation += 0.008; 
	            //wachstum = mouseX/1000.0;
	            wachstum_1 = PApplet.map(PApplet.sin(rotation*30),-1, 1, 0f, 1.5f);
	            wachstum_2 = PApplet.map(PApplet.sin(rotation*25),-1, 1, 0.2f, 1.5f);
	            wachstum_3 = PApplet.map(PApplet.sin(rotation*35),-1, 1, 0.2f, 1.5f);
	            wachstum_4 = PApplet.map(PApplet.sin(rotation*40),-1, 1, 0.2f, 1.5f);
	    
	            System.out.println(wachstum_1);
	    
	            g.beginShape();
	                      // Head & Shoulder ;)
	            g.vertex(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y);
	            g.vertex(data.HEAD.x-10*wachstum_4*3, data.HEAD.y-200*wachstum_4);
	            g.vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y);
	        
	                     // Right Arm
	            g.vertex(data.RIGHT_ELBOW.x+75*wachstum_4*1.5f, data.RIGHT_ELBOW.y+125*wachstum_4);
	            g.vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y);
	                     
	                     // Left Arm
	            g.vertex(data.LEFT_HAND.x, data.LEFT_HAND.y-50);
	            g.vertex(data.LEFT_ELBOW.x-150*wachstum_3*1.5f, data.LEFT_ELBOW.y-120*wachstum_3*1.5f);
	    
	        
	    
	            g.endShape();
	                     
	            g.beginShape();
	                      // Head & Shoulder ;)
	            g.vertex(data.LEFT_SHOULDER.x-30, data.LEFT_SHOULDER.y+30);
	            g.vertex(data.HEAD.x-30*wachstum_4*5, data.HEAD.y+190*wachstum_4);
	            g.vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y+30);
	        
	                     // Right Arm
	            g.vertex(data.RIGHT_ELBOW.x+150*wachstum_2*1.5f, data.RIGHT_ELBOW.y-125*wachstum_2);
	            g.vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y);
	                     
	                     // Left Arm
	            g.vertex(data.LEFT_HAND.x, data.LEFT_HAND.y);
	            g.vertex(data.LEFT_ELBOW.x-25*wachstum_3*1.5f, data.LEFT_ELBOW.y+150*wachstum_3*1.5f);
	    
	            g.endShape();
	      }
	      
	      if(now > 79000) {
	          player2.pause();
	          player2.rewind();
	          player3.rewind();
	          player3.play();
	          grow = false;
	      }
	    
	  }
	  
	 // g.popMatrix();
	}
	}

	@Override
	public void motionDetected(String motion) {
	      if(motion=="STANDIGN") {
	          startzeit=System.currentTimeMillis();
	          grow=true;
	        } else {
	          grow = false;
	          player4.rewind();
	          player4.play();
	          player2.rewind();
	          player1.rewind();
	          player1.loop();
	        }
	}

}
