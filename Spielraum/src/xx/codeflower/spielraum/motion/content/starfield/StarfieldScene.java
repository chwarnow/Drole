package xx.codeflower.spielraum.motion.content.starfield;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.signals.SineWave;
import ddf.minim.signals.WhiteNoise;
import processing.core.PApplet;
import processing.core.PGraphics;
import xx.codeflower.spielraum.motion.detection.MotionDetectionListener;
import xx.codeflower.spielraum.motion.detection.WalkDetector;
import xx.codeflower.spielraum.motion.scene.SimpleMotionScene;

public class StarfieldScene extends SimpleMotionScene implements MotionDetectionListener {

	public WalkDetector wd;
	
	public Minim minim;
	public AudioOutput out;
	public SineWave sine;
	public WhiteNoise whiteNoise;

	public int backR = 0, backG = 0, backB = 0, backAlpha = 230, overallParticles = 8000, overallParticleSize = 3;
	public float latestHumanPosX, latestHumanPosZ, humanCenterX, humanCenterY, easingFactorWalking = 0.0001f, easingFactorStanding = 0.0006f;
	public boolean particlesCreated = false, silenceMode = false, walking, standing;
	public float[][] particles = new float[overallParticles][8];
	
	public StarfieldScene(PApplet p, int code, int fadeSpeed) {
		super(p, code, fadeSpeed);
		
		wd = new WalkDetector(60.0f);
		wd.addListener(this);
		
		
		 // Sound
		minim = new Minim(p);
		  out = minim.getLineOut(Minim.STEREO);
		  sine = new SineWave(1000, 0.5f, out.sampleRate());
		  whiteNoise = new WhiteNoise();
		  sine.portamento(200);
		  out.addSignal(sine);
		  out.addSignal(whiteNoise);
		  out.disableSignal(sine);
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
		 g.fill(backR, backG, backB, backAlpha);
		 g.rect(0, 0, g.width, g.height);
		  
		 if(data != null) {
			 wd.update(data);
		  // Render Environement
		  renderEnvironement(g);
		  
		  // Body Vertex Shape
		  // drawBodyVertexShape();
		  
		  // Play Sounds
		  // playSounds();
		  
		  // Check Body Movement
		  checkBodyMovement(g);	
		 }
	}
	void checkBodyMovement(PGraphics g) {
		  // Orbit (Feet)
		  float theYL = PApplet.map(data.LEFT_FOOT.z, 1600, 4000, g.height, 0);
		  float theYR = PApplet.map(data.RIGHT_FOOT.z, 1600, 4000, g.height, 0);
		  humanCenterX = data.LEFT_FOOT.x + ((data.LEFT_FOOT.x - data.RIGHT_FOOT.x)/2);
		  humanCenterY = theYL + ((theYL - theYR)/2);
		  //g.fill(100, 0, 100, 100);
		  //g.ellipse(humanCenterX, humanCenterY, 100, 100);
		  
		  // Hands
		  //g.ellipse(data.LEFT_HAND.x, theYL, 60, 60);
		  //g.ellipse(data.RIGHT_HAND.x, theYR, 60, 60);
		  /*
		  if((theYL - theYR) < 200) {
			  System.out.println("KNIEEEEENNNNNN");
		  }
		  */
		}

		public void motionDetected(String motion) {
		  if(motion == "STANDIGN") {
			  System.out.println("standing");
		    walking = false;
		    standing = true;
		  }
		  if(motion == "WALKING") {
		    System.out.println("walking");
		    walking = true;
		    standing = false;
		  }
		  
		}
		
		void drawBodyVertexShape(PGraphics g) {
			  
	        /* Draw something with MotionData */
	  
			g.noStroke();
			g.fill(200, 20, 0, 60);
	        
	        

			g.beginShape();
	            // Head & Shoulder ;)
			g.vertex(data.LEFT_SHOULDER.x, data.LEFT_SHOULDER.y, -data.LEFT_SHOULDER.z);
			g.vertex(data.HEAD.x, data.HEAD.y, -data.HEAD.z);
			g.vertex(data.RIGHT_SHOULDER.x, data.RIGHT_SHOULDER.y, -data.RIGHT_SHOULDER.z);
	            
	            // Right Arm
			g.vertex(data.RIGHT_ELBOW.x, data.RIGHT_ELBOW.y, -data.RIGHT_ELBOW.z);
			g.vertex(data.RIGHT_HAND.x, data.RIGHT_HAND.y, -data.RIGHT_HAND.z);
	            
	            // Torso
			g.vertex(data.TORSO.x-10, data.TORSO.y, -data.TORSO.z);
	            
	            // Right Leg
			g.vertex(data.RIGHT_HIP.x, data.RIGHT_HIP.y, -data.RIGHT_HIP.z);
			g.vertex(data.RIGHT_KNEE.x, data.RIGHT_KNEE.y, -data.RIGHT_KNEE.z);
			g.vertex(data.RIGHT_FOOT.x, data.RIGHT_FOOT.y, -data.RIGHT_FOOT.z);
	        
	            // Left Leg
			g.vertex(data.LEFT_FOOT.x, data.LEFT_FOOT.y, -data.LEFT_FOOT.z);
			g.vertex(data.LEFT_KNEE.x, data.LEFT_KNEE.y, -data.LEFT_KNEE.z);
			g.vertex(data.LEFT_HIP.x, data.LEFT_HIP.y, -data.LEFT_HIP.z);
	        
	            // Torso again
			g.vertex(data.TORSO.x+10, data.TORSO.y, -data.TORSO.z);
	            
	            // Left Arm
			g.vertex(data.LEFT_HAND.x, data.LEFT_HAND.y, -data.LEFT_HAND.z);
			g.vertex(data.LEFT_ELBOW.x, data.LEFT_ELBOW.y, -data.LEFT_ELBOW.z);
			g.endShape();
	  
	}
		
		void renderEnvironement(PGraphics g) {
			 
			 if(!particlesCreated) {
			   createParticles(g, overallParticles, overallParticleSize);
			 } else if(particlesCreated) {
			   moveParticles(g);
			   renderParticles(g);
			 }
			  
			}

			void createParticles(PGraphics g, int particleCount, int particleSize) {
			  for(int i = 0; i < particleCount; i++) {
				  g.noStroke();
			      // X
			      particles[i][0] = p.random(-1000, g.width + 1000);
			      // Y
			      particles[i][1] = p.random(-1000, g.height + 1000);
			      // SIZE
			      particles[i][2] = particleSize;
			      // ALPHA FACTOR
			      particles[i][3] = p.random(31);
			      // ATTRACTION FACTOR
			      particles[i][4] = (particles[i][3]/6000);
			      // COLOR R
			      particles[i][5] = 255;
			      // COLOR G
			      particles[i][6] = 255;
			      // COLOR B
			      particles[i][7] = 255;
			  }
			  particlesCreated = true;
			}

			void moveParticles(PGraphics g) {
			  if(standing) {
			    
			    out.disableSignal(whiteNoise);
			    out.enableSignal(sine);
			    
			    for(int i = 0; i < particles.length; i++) {
			      float particleTargetX = humanCenterX;
			      float dx = particleTargetX - particles[i][0];
			      if(Math.abs(dx) > 1) {
			        particles[i][0] += dx * (easingFactorStanding + particles[i][4]);
			      }
			      float particleTargetY = humanCenterY;
			      float dy = particleTargetY - particles[i][1];
			      if(Math.abs(dy) > 1) {
			        particles[i][1] += dy * (easingFactorStanding + particles[i][4]);
			      }
			      // Wenn Hand Ÿber Parikel geht: einfŠrben!
			      if(particles[i][0] >= (data.LEFT_HAND.x - 20) && particles[i][0] <= (data.LEFT_HAND.x + 20) && particles[i][1] >= (data.LEFT_HAND.y - 20) && particles[i][1] <= (data.LEFT_HAND.y + 20)) {
			        float freq = PApplet.map(particles[i][3] * 100, 0, g.height, 1500, 60);
			        sine.setFreq(freq);
			        float pan = PApplet.map(data.HEAD.x, 0, g.width, -1, 1);
			        sine.setPan(pan);
			        particles[i][6] = 200;
			      }
			      if(particles[i][0] >= (data.RIGHT_HAND.x - 20) && particles[i][0] <= (data.RIGHT_HAND.x + 20) && particles[i][1] >= (data.RIGHT_HAND.y - 20) && particles[i][1] <= (data.RIGHT_HAND.y + 20)) {
			        float freq = PApplet.map(particles[i][3] * 100, 0, g.height, 1500, 60);
			        sine.setFreq(freq);
			        float pan = PApplet.map(data.HEAD.x, 0, g.width, -1, 1);
			        sine.setPan(pan);
			        particles[i][5] = 100;
			      }
			    }
			  } else if(walking) {
			    
			    out.disableSignal(sine);
			    out.enableSignal(whiteNoise);
			    
			    for(int i = 0; i < particles.length; i++) {
			      float particleTargetX = humanCenterX;
			      float dx = particleTargetX - particles[i][0];
			      if(Math.abs(dx) > 1) {
			        particles[i][0] -= dx * (easingFactorWalking + particles[i][4]);
			      }
			      float particleTargetY = humanCenterY;
			      float dy = particleTargetY - particles[i][1];
			      if(Math.abs(dy) > 1) {
			        particles[i][1] -= dy * (easingFactorWalking + particles[i][4]);
			      }
			    }
			  }
			}

			void renderParticles(PGraphics g) {
			 
				g.noStroke();
			  for(int i = 0; i < particles.length; i++) {
				  g.fill(particles[i][5], particles[i][6], particles[i][7], 100 + (particles[i][3] * 5));
				  g.ellipse(particles[i][0], particles[i][1], particles[i][2], particles[i][2]);
			    if(!standing) {
			      g.fill(particles[i][5], particles[i][6], particles[i][7], (particles[i][3] * 5));
			      g.ellipse(particles[i][0] + p.random(-1, 1), particles[i][1] + p.random(-1, 1), particles[i][2], particles[i][2]);
			    }
			  }
			  
			}
			
			void playSounds() {
				  playNoises();
				  playSoundOfSilence();
				}

				void playSoundOfSilence() {
				  if(silenceMode) {
				    // Hochregeln!
				  }
				}

				void playNoises() {
				    if(silenceMode) {
				      // Runterregeln!  
				    }
				    
				}

}
