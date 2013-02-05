package com.christopherwarnow.bildwelten;

/**
 * a swarm of droles that are floating around a sphere
 */

import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;
import toxi.physics.constraints.ParticleConstraint;
import toxi.physics.constraints.SphereConstraint;

public class DroleWelt {
	private PApplet parent;
	
	// ------ drole particles on sphere ------
	private int particleAmount = 100;
	private Drole[] particles;

	private float sphereSize = 120;
	private int REST_LENGTH=3;

	private VerletPhysics physics;
	private VerletParticle head;
	
	private float randomOffset = 0;
	
	public DroleWelt(PApplet parent, int particleAmount, float dimension) {
		this.parent = parent;
		this.particleAmount = particleAmount;
		this.sphereSize = (dimension==0) ? 600.0f : dimension;
		
		init();
	}
	
	/**
	 * create a swarm of spring-connected verlet physics particles that are constrained on a sphere
	 */
	private void init() {
		
		randomOffset = parent.random(-1000, 1000);
		
		// create drole particles
		particles = new Drole[particleAmount];

		// create particles
		for (int i=0;i<particleAmount;i++) {
			particles[i] = new Drole( parent,
					new PVector(
							parent.random(-3.1414f, 3.1414f),
							parent.random(-3.1414f, 3.1414f),
							parent.random(-3.1414f, 3.1414f)),
							sphereSize,
							i);
		}

		// create collision sphere at origin, replace OUTSIDE with INSIDE to keep particles inside the sphere
		ParticleConstraint sphereA=new SphereConstraint(new Sphere(new Vec3D(), sphereSize), SphereConstraint.OUTSIDE);
		ParticleConstraint sphereB=new SphereConstraint(new Sphere(new Vec3D(), sphereSize*1.1f), SphereConstraint.INSIDE);
		physics=new VerletPhysics();
		// weak gravity along Y axis
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 0.01f, 0)));
		// set bounding box to 110% of sphere radius
		physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(sphereSize, sphereSize, sphereSize).scaleSelf(1.1f)));
		VerletParticle prev=null;
		for (int i=0; i<particleAmount; i++) {
			// create particles at random positions outside sphere
			VerletParticle p=new VerletParticle(Vec3D.randomVector().scaleSelf(sphereSize*2));
			// set sphere as particle constraint
			p.addConstraint(sphereA);
			p.addConstraint(sphereB);
			physics.addParticle(p);
			if (prev!=null) {
				physics.addSpring(new VerletSpring(prev, p, REST_LENGTH*5, 0.005f));
				physics.addSpring(new VerletSpring(physics.particles.get((int)parent.random(i)), p, REST_LENGTH*20, 0.00001f + i*.0005f));
			}
			prev=p;
		}
		head=physics.particles.get(0);
		head.lock();
	}

	/**
	 * let the particles wander by setting position of certain particles
	 */
	public void update() {
		// update particle movement
		float myOffset = parent.frameCount + randomOffset;
		head.set(
				parent.noise(myOffset*(.005f + parent.cos(myOffset*.01f)*.00f))*parent.width-parent.width/2,
				parent.noise(myOffset*.005f + parent.cos(myOffset*.01f)*.05f)*parent.height-parent.height/2,
				parent.noise(myOffset*.01f + 100)*parent.width-parent.width/2
		);
		physics.particles.get(10).set(
				parent.noise(-myOffset*(.005f + parent.cos(myOffset*.001f)*.005f))*parent.width-parent.width/2,
				parent.noise(-myOffset*.005f + parent.cos(myOffset*.001f)*.005f)*parent.height-parent.height/2,
				parent.noise(-myOffset*.01f + 100)*parent.width-parent.width/2);

		// also apply sphere constraint to head
		// this needs to be done manually because if this particle is locked
		// it won't be updated automatically
		head.applyConstraints();
		// physics.particles.get(10).applyConstraints();

		// update sim
		physics.update();
		// then all particles as dots
		int index=0;
		for (Iterator i=physics.particles.iterator(); i.hasNext();) {
			VerletParticle p=(VerletParticle)i.next();
			particles[index++].addPosition(p.x, p.y, p.z);
		}
	}

	public void draw() {
		// TODO: set color
		parent.g.pushMatrix();
		for (int i=0;i<particleAmount;i++) {
			particles[i].draw();
		}
		parent.g.popMatrix();
	}
}
