package drole.tests.spektakel;

import java.util.Iterator;

import codeanticode.glgraphics.GLGraphics;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.AttractionBehavior;

public class T_DudeSystem extends T_ParticleSystem {

	PImage dudeImage;

	float[][] greyLevels;

	public T_DudeSystem(PApplet p, VerletPhysics _physics, float x, float y,
			float z) {

		super(p, _physics, x, y, z);
		loadImage();

		spawnNew();
		
		initalSpringPower =0.000006f;
		initalBoomPower= -2.0f;
		
		boomPower = initalBoomPower;
		springPower = initalSpringPower;
		
		springFallOff = -0.001f;
		boomFalloff = 0.01f;

	}

	void loadImage() {

		dudeImage = p.loadImage("images/flyingDude.png");
		dudeImage.loadPixels();

		greyLevels = new float[dudeImage.width][dudeImage.height];

		for (int i = 0; i < dudeImage.height; i++) {
			for (int j = 0; j < dudeImage.width; j++) {

				int getPixel = (i * dudeImage.width) + j;

				float greyLevel = p.red(dudeImage.pixels[getPixel]) / 255;

				greyLevels[j][i] = greyLevel;

			}
		}
	}

	@Override
	public void spawnNew() {

		bigParticle.clear();
		clean();

		shockwave = true;
		boomPower = initalBoomPower;
		boomForce = new AttractionBehavior(this, 2000, boomPower * 0.3f, 0.1f);
		physics.addBehavior(boomForce);

		// spread must be at least 1
		int spread = 10;

		int imageHeight = greyLevels.length;
		int imageWidth = greyLevels[0].length;
		
		int iSize = 50;

		float decay = 0.001f;
		
		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {

				if (greyLevels[i][j] > 0.1f) {
					int xPos = (int) ((i * spread) - (imageWidth * spread) * 0.5f);
					int yPos = (int) ((j * spread) - (imageHeight * spread) * 0.5f);

					T_ShapeParticle newPart = new T_ShapeParticle(p, x() + p.random(-iSize,iSize), y() + p.random(-iSize,iSize), z()+ p.random(-iSize,iSize),decay,greyLevels[i][j]);
		
				

					VerletParticle targetPoint = new T_ShapeParticle(p, x() + xPos, y() + yPos, z());
					targetPoint.lock();
					
					
					//maybe replacxe with low force spring to reduce bouncing
					VerletSpring toxicForce = new VerletSpring(newPart,targetPoint,
							0, springPower);
//					physics.addParticle(targetPoint);
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
		super.draw(g);
	}

}
