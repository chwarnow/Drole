package com.marctiedemann.spektakel;

import java.util.ArrayList;
import java.util.Timer;

import javax.media.opengl.GL;

import processing.core.PVector;

import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;
import toxi.physics.behaviors.GravityBehavior;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.engine.drawable.Drawable;

import drole.settings.Settings;

public class Spektakel extends Drawable {

	VerletPhysics physics, physics2;

	ArrayList<ParticleSystem> ermitters;
	private CenterSystem centerSystem;

	int DIM = 3000;

	private boolean pauseMotion = false;

	private float PAUSE_EASING = 0.5f;
	private float PAUSE_MOTION_AT = 2;
	private float NORMAL_DRAG = 0.05f;

	private float drag = 0.05f;

	private int toxiTime = 1000;
	private int toxicCounter = 0;
	private int totalNumberOfToxics = 5;
	private int toxicNum = 0;

	private int dudeTime = 20000;
	private int dudeCounter = 10000;


	private boolean centerSpawned = false;
	private int centerTime = 500;
	private int centerCounter = 0;

	private PVector mouseHead;

	boolean pauseSystem = false;

	public Spektakel(Engine e) {
		super(e);

		physics = new VerletPhysics();
		physics2 = new VerletPhysics();

		initPhysics(physics);
		initPhysics(physics2);
		physics2.setDrag(0.5f);

		ermitters = new ArrayList<ParticleSystem>();

		centerSystem = new CenterSystem(e, physics2, 50, 0, 0, 0);


		e.requestTexture("images/particle4.png");

		useLights();
		// i x y z r g b f1 f2 f3


	}

	@Override
	public void update() {

		super.update();

		// pause system 1


		if(!pauseSystem)updateTimers();

		if (centerSpawned)
			centerSystem.update();

		if (pauseMotion && drag < PAUSE_MOTION_AT)
			drag *= 1.0f + PAUSE_EASING;
		else if (!pauseMotion && drag > NORMAL_DRAG)
			drag *= 1.0 - PAUSE_EASING;

		physics.setDrag(drag);
		if (drag < PAUSE_MOTION_AT) {

			physics.update();
		}
		physics2.update();

	}

	private void updateTimers() {

		int randomness = 40;

		if (toxicNum < totalNumberOfToxics)
			toxicCounter += e.p.random(0, randomness);
		if (toxicCounter > toxiTime) {
			spawnNewToxicSystem();
			toxicCounter = 0;
			toxicNum++;
		}

		dudeCounter += e.p.random(0, randomness);

		if (dudeCounter > dudeTime) {
			spawnNewDude();
			dudeCounter = 0;
		}


		if (!centerSpawned) {
			centerCounter += e.p.random(0, randomness);

			if (centerCounter > centerTime) {
				centerSystem.init();
				centerSpawned = true;
			}
		}
	}

	@Override
	public void draw() {
		e.usePoints();

		setAmbient(0.4f, 0.3f, 0.3f);

		e.g.setDepthMask(false);
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateX(rotation.x);
		g.rotateY(rotation.y);
		g.rotateZ(rotation.z);

		g.pushMatrix();


		float falloff = 0.004f;

		if (centerSpawned) {
			falloff = EngineApplet.map(centerSystem.getTimeToLife(), 255, 0, 0.0006f, 0.001f);

			setPointLight(0, 0, 0, -0, 120, 225, 255, 0.3f,
					falloff + e.p.random(-0.00015f, 0.00015f), 0.0f);

		}

		if (mode() == ON_SCREEN) {

			g.pushMatrix();
			if (centerSpawned)
				centerSystem.draw(e.g);
			g.popMatrix();

			if (centerSystem.isDead()) {
				physics2.clear();
				centerSystem = new CenterSystem(e, physics2, 50, 0, 0, 0);
				if (!pauseSystem)
					centerSystem.spawnNew(false);

			}

			
			int lightCount = 1;
			
			for (int i = 0; i < ermitters.size(); i++) {

				ParticleSystem er = ermitters.get(i);

				falloff = 0.002f;

				if (er.getTimeToLife() > 50 && lightCount<6) {

					falloff = EngineApplet.map(er.bigParticle.get(0)
							.getTimeToLife(), 255, 0, 0.0005f, 0.01f);

					setPointLight(lightCount, er.bigParticle.get(0).x,
							er.bigParticle.get(0).y, er.bigParticle.get(0).z,
							255, 70 + e.p.random(-30, 30), 30, 0.1f, falloff
									+ e.p.random(-0.0005f, 0.0005f), 0.0f);
			//		System.out.println("lights" +lightCount);
					lightCount++;
				}

				er.update();
				er.draw(e.g);

				if (er.isDead()) {
					er.cleanSytstem();
					ermitters.remove(i);

					if (!pauseSystem)
						spawnNewToxicSystem();

				}
			}

		}

		g.popMatrix();

		g.popMatrix();

		e.g.setDepthMask(true);
		
		
		

	}

	private void initPhysics(VerletPhysics thePhysics) {

		GravityBehavior gravity = new GravityBehavior(new Vec3D(0, 0.6f, 0));
		AttractionBehavior center = new AttractionBehavior(new Vec3D(0, 0, 0),
				3000, 0.1f, 0.5f);
		AttractionBehavior ring = new AttractionBehavior(new Vec3D(0, 0, 0),
				500, -2.8f, 0.5f);
		// thePhysics.addBehavior(center);
		// thePhysics.addBehavior(ring);
		thePhysics.addBehavior(gravity);
		thePhysics.setDrag(drag);

	}

	public void spawnNewToxicSystem() {
		ToxicSystem newOne = new ToxicSystem(e, physics, 50, e.p.random(
				-Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM / 2,
				Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM / 2), e.p.random(
				-Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM / 2, 0),
				e.p.random(-Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM, 0));
		newOne.init();

		ermitters.add(newOne);
	}

	public void spawnNewDude() {
		FlyingDude newOne = new FlyingDude(e, physics, e.p.random(
				-Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM / 2,
				Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM / 2), e.p.random(

				-Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM / 3,
				Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM / 3), e.p.random(
				-Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM, 0));
		ermitters.add(newOne);

		ToxicSystem newOne2 = new ToxicSystem(e, physics, 50, 400,
				Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM / 2 - 400,
				e.p.random(-Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM * 0.75f,
						-Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM));
		newOne2.init();
		ermitters.add(newOne2);
	}

	public void updateRotaion(int mousePos) {

		// centerSystem.setRotation(EngineApplet.map(mousePos,0,e.p.width,-1,1));
	}

	public void pauseSystem() {
		pauseSystem = !pauseSystem;

	}

	public void printForces() {
		System.out.println("1 " + physics.behaviors);
		System.out.println("1 " + physics.springs);
		System.out.println("2 " + physics2.behaviors);
		System.out.println("2 " + physics2.springs);
	}

}
