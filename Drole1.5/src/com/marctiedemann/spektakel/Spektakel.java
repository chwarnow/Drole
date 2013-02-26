package com.marctiedemann.spektakel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;
import toxi.physics.behaviors.GravityBehavior;

import codeanticode.glgraphics.GLModel;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;


public class Spektakel extends Drawable implements KeyListener {

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

		for(int i=0;i<1;i++){
			ToxicSystem startErmitter = new ToxicSystem(e, physics, 50, 0, 0, 0);
			ermitters.add(startErmitter);
		}

		
		e.requestTexture("images/particle.png");
		
		e.p.addKeyListener(this);
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
			try {
				physics.update();
			} catch(ConcurrentModificationException e) {}
		}
		// always run system 2

		try {
			physics2.update();
		} catch(ConcurrentModificationException e) {}

	}

	@Override
	public void draw() {

		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateX(rotation.x);
		g.rotateY(rotation.y);
		g.rotateZ(rotation.z);

		g.pushMatrix();
		// e.g.setDepthMask(false);

		float rotationX = PApplet.map(e.p.mouseY, 0, e.p.width, -PApplet.PI / 2, PApplet.PI / 2);
		float rotationY = PApplet.map(e.p.mouseX, 0, e.p.height, -PApplet.PI / 2, -PApplet.PI / 2);

		g.rotateX(rotationX);
		g.rotateY(rotationY);

		update();
		// startErmitter.drawErmitter();

		for (int i = 0; i < ermitters.size(); i++) {
			ToxicSystem er = ermitters.get(i);

			er.update();
			er.draw(e.g);

			if (er.isEmpty()) {
				er.clean();
				ermitters.remove(i);
			}
		}

//		 e.g.setDepthMask(true);

		g.popMatrix();

		g.popMatrix();
		g.popStyle();

	}
	
	private void initPhysics(VerletPhysics thePhysics) {

		
			GravityBehavior gravity = new GravityBehavior(new Vec3D(0, 0.8f, 0));
			AttractionBehavior center = new AttractionBehavior(new Vec3D(0, 0, 0), 3000, 0.1f, 0.5f);
			AttractionBehavior ring = new AttractionBehavior(new Vec3D(0, 0, 0), 500, -2.8f, 0.5f);
			thePhysics.addBehavior(center);
//			thePhysics.addBehavior(ring);
			thePhysics.addBehavior(gravity); 
			thePhysics.setDrag(drag);

		}

	
	public void spawnNewToxicSystem() {
		ToxicSystem newOne = new ToxicSystem(e, physics, 50, e.p.random(-1500, 1500), e.p.random(-1500, 1500), e.p.random(-1500, 1500));
		ermitters.add(newOne);
	}

	@Override
	public void keyPressed(KeyEvent event) {

		char key = event.getKeyChar();

		if (key == 's') {

			GLModel mog = new GLModel(e.p, 20, GLModel.POINT_SPRITES, GLModel.DYNAMIC);
			
//			ToxicSystem newOne = new ToxicSystem(e, physics, 50, e.p.random(-1500, 1500), e.p.random(-1500, 1500), e.p.random(-1500, 1500));
//			ermitters.add(newOne);
		}

		if (key == 'a') {

			ToxicSystem newOne = new ToxicSystem(e, physics2, 50, e.p.random(
					-1500, 1500), e.p.random(-1500, 1500), e.p.random(-1500,
					1500));
			ermitters.add(newOne);
		}

		if (key == 'i') {
/*
			DudeSystem newOne = new DudeSystem(e.p, physics2, 50, e.p.random(
					-1500, 1500), e.p.random(-1500, 1500), e.p.random(-1500,
					1500));
			ermitters.add(newOne);
			*/
		}

		if (key == ' ')
			pauseMotion = !pauseMotion;

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
