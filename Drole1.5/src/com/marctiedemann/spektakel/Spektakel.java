package com.marctiedemann.spektakel;


import java.util.ArrayList;

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

	ArrayList<ToxicSystem> ermitters;

	int DIM = 3000;

	private boolean pauseMotion = false;

	private float PAUSE_EASING = 0.5f;
	private float PAUSE_MOTION_AT = 2;
	private float NORMAL_DRAG = 0.01f;

	private float drag = 0.01f;

	public Spektakel(Engine e) {
		super(e);

		physics = new VerletPhysics();
		physics2 = new VerletPhysics();

		initPhysics(physics);
		initPhysics(physics2);

		ermitters = new ArrayList<ToxicSystem>();

		for (int i = 0; i < 1; i++) {
			ToxicSystem startErmitter = new ToxicSystem(e, physics, 50, 0, 0, 0);
			ermitters.add(startErmitter);
		}

		e.requestTexture("images/particle.png");
		
		useLights();
		/// i x y z r g b f1 f2 f3
		

	}

	@Override
	public void update() {

		
		super.update();

		// pause system 1

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
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateX(rotation.x);
		g.rotateY(rotation.y);
		g.rotateZ(rotation.z);

		g.pushMatrix();
		
		g.noFill();
		g.noStroke();
		g.texture(e.requestTexture("data/images/particle.png"));
		g.rect(0, 0, 100, 100);

		// float rotationX = PApplet.map(e.p.mouseY, 0, e.p.width, -PApplet.PI /
		// 2, PApplet.PI / 2);
		// float rotationY = PApplet.map(e.p.mouseX, 0, e.p.height, -PApplet.PI
		// / 2, -PApplet.PI / 2);

		// g.rotateX(rotationX);
		// g.rotateY(rotationY);
		

		update();
		// startErmitter.drawErmitter();

		e.setPointSize(10);
		
		for (int i = 0; i < ermitters.size(); i++) {
			
			
			ToxicSystem er = ermitters.get(i);

			if(i<4)
			setPointLight(i, er.x, er.y, er.z, 255, 150, 100, 0.3f, .01f,0.0f );
			
			
			er.update();
			er.draw(e.g);

			if (er.isEmpty()) {
				er.clean();
				ermitters.remove(i);
				
				spawnNewToxicSystem();
			}
		}

		g.popMatrix();

		g.popMatrix();
	}

	private void initPhysics(VerletPhysics thePhysics) {

		GravityBehavior gravity = new GravityBehavior(new Vec3D(0, 0.8f, 0));
		AttractionBehavior center = new AttractionBehavior(new Vec3D(0, 0, 0),
				3000, 0.1f, 0.5f);
		AttractionBehavior ring = new AttractionBehavior(new Vec3D(0, 0, 0),
				500, -2.8f, 0.5f);
//		thePhysics.addBehavior(center);
//		thePhysics.addBehavior(ring);
		thePhysics.addBehavior(gravity);
		thePhysics.setDrag(drag);

	}

	public void spawnNewToxicSystem() {
		ToxicSystem newOne = new ToxicSystem(e, physics, 50,
				e.p.random( -Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM / 2,
				Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM / 2), 
				e.p.random( -Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM / 2, 0), 
				e.p.random( -Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM, 0));
		ermitters.add(newOne);
	}

}
