package drole.tests.spektakel;

import java.util.ArrayList;
import java.util.Iterator;

import codeanticode.glgraphics.*;

import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;
import toxi.physics.behaviors.GravityBehavior;
import processing.core.PApplet;

public class FireworksTest extends PApplet {

	/**
	 * 
	 */

	VerletPhysics physics;
	VerletPhysics physics2;

	ToxicSystem startErmitter;

	ArrayList<ParticleSystem> ermitters;

	private static final long serialVersionUID = 1L;

	int DIM = 3000;

	float drag = 0.01f;
	boolean pauseMotion = false;

	public void setup() {

		size(1080, 1080, GLConstants.GLGRAPHICS);

		
		physics = new VerletPhysics();
		physics2 = new VerletPhysics();
		
		initPhysics(physics);
		initPhysics(physics2);

		background(0);

		ermitters = new ArrayList<ParticleSystem>();

		startErmitter = new ToxicSystem(this,physics, 50, 0, 0,
				0);

	}

	public void update() {

		println(drag);

		float pauseEasing = 0.5f;
		float pauseAt = 2;
		float normalAt = 0.01f;

		if (pauseMotion && drag < pauseAt)
			drag *= 1.0f + pauseEasing;
		else if (!pauseMotion && drag > normalAt)
			drag *= 1.0 - pauseEasing;

		physics.setDrag(drag);

		physics2.update();
		
		if (drag < pauseAt)
			physics.update();

	}

	public void draw() {

		GLGraphics renderer = (GLGraphics) g;
		renderer.beginGL();

		renderer.background(0);
		renderer.setDepthMask(false);
		// println(frameRate);

		// move cam to fake CUBEENVIROMENT
		renderer.pushMatrix();
		renderer.translate(width / 2, height / 2, -4500);

		float rotationX = map(mouseY, 0, width, -PI / 2, PI / 2);
		float rotationY = map(mouseX, 0, height, -PI / 2, PI / 2);

		renderer.rotateX(rotationX);
		renderer.rotateY(rotationY);

		update();
		// startErmitter.drawErmitter();

		if (!startErmitter.isEmpty()){
			startErmitter.update();
			startErmitter.draw(renderer);}
		
		else
			startErmitter.clean();

		for (int i = 0; i < ermitters.size(); i++) {
			ParticleSystem er = ermitters.get(i);

			er.update();
			er.draw(renderer);
			
			
			if (er.isEmpty()) {
				er.clean();
				ermitters.remove(i);
			}
		}

		renderer.setDepthMask(true);
		renderer.popMatrix();
		renderer.endGL();

	}

	public void keyPressed() {

		if (key == 's') {

			ToxicSystem newOne = new ToxicSystem(this,
					physics, 50, random(-1500, 1500), random(-1500, 1500),
					random(-1500, 1500));
			ermitters.add(newOne);
		}

		if (key == 'a') {

			ToxicSystem newOne = new ToxicSystem(this,
					physics2, 50, random(-1500, 1500), random(-1500, 1500),
					random(-1500, 1500));
			ermitters.add(newOne);
		}
		
		if (key == 'i') {

			DudeSystem newOne = new DudeSystem(this,
					physics2, 50, random(-1500, 1500), random(-1500, 1500),
					random(-1500, 1500));
			ermitters.add(newOne);
		}

		if (key == ' ')
			pauseMotion = !pauseMotion;
	}

	private void initPhysics(VerletPhysics thePhysics) {

		
	//	thePhysics.setWorldBounds(new AABB(new Vec3D(),new Vec3D(DIM, DIM, DIM)));

		GravityBehavior gravity = new GravityBehavior(new Vec3D(0, 0.8f, 0));
		AttractionBehavior center = new AttractionBehavior(new Vec3D(0, 0, 0),
				3000, 0.1f, 0.5f);
		AttractionBehavior ring = new AttractionBehavior(new Vec3D(0, 0, 0),
				500, -2.8f, 0.5f);
		thePhysics.addBehavior(center);
//		thePhysics.addBehavior(ring);
		thePhysics.addBehavior(gravity); 
		thePhysics.setDrag(drag);

	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#000000",
				"drole.tests.spektakel.FireworksTest" });
	}
}
