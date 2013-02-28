package com.marctiedemann.spektakel;




import java.util.Iterator;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;

import codeanticode.glgraphics.GLGraphics;
import drole.settings.Settings;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.AttractionBehavior;

public class FlyingDude extends ParticleSystem {
	

		PImage dudeImage;

		float[][] greyLevels;

		public FlyingDude(Engine e, VerletPhysics _physics, float x, float y,
				float z) {

			super(e, _physics, x, y, z);
			loadImage();

			spawnNew();
			
		
			setSpringPower(0.00002f);
			setBoomPower(-5.0f);
			
			springFallOff = -0.0001f;
			boomFalloff = 0.001f;
			
			trailLength = 2;

		}

		void loadImage() {

			dudeImage = e.p.loadImage("images/flyingDude_200px.png");
			dudeImage.loadPixels();

			greyLevels = new float[dudeImage.width][dudeImage.height];

			for (int i = 0; i < dudeImage.height; i++) {
				for (int j = 0; j < dudeImage.width; j++) {

					int getPixel = (i * dudeImage.width) + j;

					float greyLevel = e.p.red(dudeImage.pixels[getPixel]) / 255;

					greyLevels[j][i] = greyLevel;

				}
			}
		}

	
		public void spawnNew() {

			bigParticle.clear();
			cleanSytstem();

			shockwave = true;
			
			resetPowers();
			
			boomForce = new AttractionBehavior(this, 2000, getBoomPower() * 0.3f, 0.1f);
			physics.addBehavior(boomForce);

			// spread must be at least 1
			int spread = 5;

			int imageHeight = greyLevels.length;
			int imageWidth = greyLevels[0].length;
			
			//initial spread
			int iSize = 50;

			float decay = 0.001f;
			
			int targetXCenter = 0;
			int targetYCenter = Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM/2-imageHeight;
			
			for (int i = 0; i < imageHeight; i++) {
				for (int j = 0; j < imageWidth; j++) {

					if (greyLevels[i][j] > 0.1f) {
					

						ShapedParticle newPart = new ShapedParticle(e.p, x() + e.p.random(-iSize,iSize), y() + e.p.random(-iSize,iSize)-400, z()+ e.p.random(-iSize,iSize),trailLength,decay,greyLevels[i][j]);
			
						newPart.setWeight(0.5f);
						
						int xPos = targetXCenter + (int) ((i * spread) - (imageWidth * spread) * 0.5f);
						int yPos = targetYCenter + ((int) ((j * spread) - (imageHeight * spread) * 0.5f));

						VerletParticle targetPoint = new VerletParticle(x()+xPos,y()+ yPos,z());
						targetPoint.lock();
						
						
						//maybe replacxe with low force spring to reduce bouncing
						VerletSpring toxicForce = new VerletSpring(newPart,targetPoint,
								0, getSpringPower());
//						physics.addParticle(targetPoint);
						physics.addParticle(newPart);
						physics.addSpring(toxicForce);
						newPart.giveSpring(toxicForce);
						// newPart.addBehavior(boomForce);

						bigParticle.add(newPart);
						
						
						bigParticle.add(newPart);
					}
					// physics.addParticle(newPart);

					/*
					 * newPart = new Particle(p, mySize / 2, f.c.x+x, f.c.y+y,
					 * f.c.z+z); bigParticle.add(newPart);
					 * physics.addParticle(newPart);
					 */
				}
			}

			initSprites();
			
			sprites.setSpriteSize(50, 200);



			// one size fits all
			trailLength = 5;
			initTrails();
			
			trails.setColors(255, 5);

		}


		
		

		@Override
		public void update() {
			super.update();
		}

		@Override
		public void draw(GLGraphics g) {
			
			e.setPointSize(2);

			super.draw(g);
		}



}
