package com.marctiedemann.spektakel;

import java.util.ArrayList;
import java.util.Timer;

import javax.media.opengl.GL;

import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;
import toxi.physics.behaviors.GravityBehavior;

import com.madsim.engine.Engine;
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
	private float NORMAL_DRAG = 0.001f;

	private float drag = 0.001f;
	
	private int timeStamp = 0;
	boolean timeStampSet =false;
	
	
	
	public Spektakel(Engine e) {
		super(e);

		physics = new VerletPhysics();
		physics2 = new VerletPhysics();

		initPhysics(physics);
		initPhysics(physics2);
		physics2.setDrag(0.5f);

		ermitters = new ArrayList<ParticleSystem>();

		centerSystem = new CenterSystem(e, physics2, 50, 0, 0, -Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM/2);
		centerSystem.init();

		e.requestTexture("images/particle4.png");

		useLights();
		// i x y z r g b f1 f2 f3

		setPointLight(0, 0,
				-(Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM / 2f) + 20,
				-(Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM) + 20, 255, 100,
				100, 0.5f, .001f, 0.0f);
	}

	@Override
	public void update() {

		super.update();

		// pause system 1

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

	@Override
	public void draw() {
		e.usePoints();

		setAmbient(0.2f, 0.2f, 0.2f);
	
		e.g.setDepthMask(false);
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateX(rotation.x);
		g.rotateY(rotation.y);
		g.rotateZ(rotation.z);

		g.pushMatrix();
		
		setPointLight(0, 0, 500, -500, 150, 205, 255, 0.3f, .0006f, 0.0f);

		// float rotationX = PApplet.map(e.p.mouseY, 0, e.p.width, -PApplet.PI /
		// 2, PApplet.PI / 2);
		// float rotationY = PApplet.map(e.p.mouseX, 0, e.p.height, -PApplet.PI
		// / 2, -PApplet.PI / 2);

		// g.rotateX(rotationX);
		// g.rotateY(rotationY);

		if(mode()==ON_SCREEN){
			
			if(!timeStampSet){
				timeStamp=e.p.millis();
				timeStampSet= true;
			}
		
	//	update();
		// startErmitter.drawErmitter();

		centerSystem.draw(e.g);
		
		
		if (centerSystem.isEmpty())
			centerSystem.spawnNew();
	

		// System.out.println("systemcount "+ermitters.size());

		for (int i = 0; i < ermitters.size(); i++) {

			ParticleSystem er = ermitters.get(i);

			if (i < 6)
				setPointLight(i + 1, er.bigParticle.get(0).x,
						er.bigParticle.get(0).y, er.bigParticle.get(0).z, 255,
						70 + e.p.random(-30, 30), 30, 0.3f,
						.001f + e.p.random(-0.0005f, 0.0005f), 0.0f);

			er.update();
			er.draw(e.g);

			if (er.isEmpty()) {
				er.cleanSytstem();
				ermitters.remove(i);

			//	System.out.println(physics.behaviors);
			//	System.out.println(physics.springs);
				
			//	spawnNewToxicSystem();
			}
		}
		
		}
		else timeStampSet=false;

		g.popMatrix();

		g.popMatrix();

		e.g.setDepthMask(true);

	}

	private void initPhysics(VerletPhysics thePhysics) {

		GravityBehavior gravity = new GravityBehavior(new Vec3D(0, 0.8f, 0));
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
		FlyingDude newOne = new FlyingDude(e, physics,
				400, Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM/2,
				e.p.random(-Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM, 0));
		ermitters.add(newOne);
	}

}
