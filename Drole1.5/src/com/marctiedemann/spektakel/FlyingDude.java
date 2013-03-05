package com.marctiedemann.spektakel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;

import codeanticode.glgraphics.GLGraphics;
import drole.settings.Settings;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.AttractionBehavior;

public class FlyingDude extends ParticleSystem {

	private PImage dudeImage;

	private float[][] greyLevels;

	private int counter = 0;

	private int imageHeight;
	private int imageWidth;

	private boolean imageLoadedAndParticlesSpawned = false;

	private List<Vec3D> path;
	int pathID = 0;

	public FlyingDude(Engine e, VerletPhysics _physics, float x, float y,
			float z) {

		super(e, _physics, x, y, z);
		loadImage();

		spawnNew();

		setSpringPower(0.000002f);
		setBoomPower(-30.0f);

		springFallOff = -0.08f;
		boomFalloff = 0.2f;

		trailLength = 2;

		trailAlpha = 0.1f;

		spriteSize = 13;

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

		imageHeight = greyLevels.length;
		imageWidth = greyLevels[0].length;
	}

	public void spawnNew() {

		System.out.println("spawn Dude");

		counter = 0;
		bigParticle.clear();
		// cleanSytstem();

		shockwave = true;

		resetPowers();

		boomForce = new AttractionBehavior(this, 2000, getBoomPower() * 0.5f,
				0.1f);
		physics.addBehavior(boomForce);

		// spread must be at least 1
		int spread = 5;

		// initial spread
		int iSize = 5;

		float decay = 0.9f;

		int targetXCenter = 0;
		int targetYCenter = 0;

		// System.out.println("center y "+targetYCenter);

		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {

				if (greyLevels[i][j] > 0.1f) {

					ShapedParticle newPart = new ShapedParticle(e.p, x()
							+ e.p.random(-iSize, iSize), y()
							+ e.p.random(-iSize, iSize) - 100, z()
							+ e.p.random(-iSize, iSize), trailLength, decay,
							greyLevels[i][j]);

					newPart.setWeight(e.p.random(0.3f));

					int xPos = targetXCenter
							+ (int) ((i * spread) - (imageWidth * spread) * 0.5f);
					int yPos = targetYCenter
							+ ((int) ((j * spread) - (imageHeight * spread) * 0.5f));

					// System.out.println(" y pos "+yPos);

					newPart.storeTargetPoint(new VerletParticle(x() + xPos, y()
							+ yPos, z()));
					newPart.getTargetPoint().lock();

					// maybe replacxe with low force spring to reduce bouncing
					VerletSpring toxicForce = new VerletSpring(newPart,
							newPart.getTargetPoint(), 0,
							e.p.random(getSpringPower()));
					// physics.addParticle(targetPoint);
					physics.addParticle(newPart);
					physics.addSpring(toxicForce);
					newPart.giveSpring(toxicForce);
					// newPart.addBehavior(boomForce);

					newPart.hideAndLock();

					bigParticle.add(newPart);

				}
			}
		}

		initSprites();
		// sprites.setSpriteSize(50, 200);
		initTrails();

		imageLoadedAndParticlesSpawned = true;

	}

	void setRandomPath() {

		ArrayList<Vec3D> points = new ArrayList<Vec3D>();

		for (int i = 0; i < 5; i++) {
			Vec3D p = new Vec3D(e.p.random(0, -100), e.p.random(0, -100),
					e.p.random(-100, 100));
			points.add(p);
		}

		Spline3D s = new Spline3D(points);
		s.computeVertices(10);
		path = s.getDecimatedVertices(4);
	}

	void updateTargets() {

		// if (pathID<path.size()-1) {

		
		float xOff = - e.p.random(5);
		float yOff = - e.p.random(5);
		float zOff =   e.p.random(-10,10);
		
		for (int i = 0; i < bigParticle.size(); i++) {

			bigParticle.get(i).getTargetPoint().x += xOff;
			bigParticle.get(i).getTargetPoint().y += yOff;
			bigParticle.get(i).getTargetPoint().z += zOff;



			// System.out.println(bigParticle.get(i).getTargetPoint());

		}
		// }

		// pathID++;

	}

	@Override
	public void update() {
		// System.out.println("still alive yeah "+bigParticle.get(0).getTimeToLife());

		// System.out.println("sp "+getSpringPower());

		int steps = 500;

		if (counter < bigParticle.size() - steps) {
			for (int i = 0; i < steps; i++) {
				// System.out.println("unlocking "+counter);
				bigParticle.get(counter + i).unHideAndLock();
			}
		}
		counter += steps;

		setSpriteColors();

		// System.out.println(bigParticle.get(10).y);
		super.update();

	}

	@Override
	public void draw(GLGraphics g) {

		updateTargets();
		e.setPointSize(2);

		super.draw(g);
	}

}
