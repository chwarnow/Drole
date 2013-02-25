package com.madsim.drole.mmworld.drop;


import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

import codeanticode.glgraphics.GLGraphics;

import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;

public class DropPhysics {

	private PApplet p;
	
	private int 	NUM_PARTICLES = 12;
	private int 	DIM = 200;
	private float 	REST_LENGTH = DIM/2;

	private VerletPhysics physics;

	public DropPhysics(PApplet p, int numParticles) {
		this.p = p;
		this.NUM_PARTICLES = numParticles;
		
		initPhysics();
		initParticles();
	}

	public VerletPhysics getPhysics() {
		return physics;
	}
	
	public void drawPhysicSystem(PGraphicsOpenGL g) {
		g.noStroke();
		g.fill(200, 200, 0);
		for(int i = 0; i < physics.particles.size(); i++) {
			g.pushMatrix();
			g.translate(physics.particles.get(i).x(), physics.particles.get(i).y(), physics.particles.get(i).z());
			g.sphere(5);
			g.popMatrix();
		}

		g.strokeWeight(1);
		g.stroke(200);
		g.noFill();
		for (int i = 0; i < physics.springs.size(); i++) {
			g.line(
					physics.springs.get(i).a.x(), physics.springs.get(i).a.y(),
					physics.springs.get(i).a.z(), physics.springs.get(i).b.x(),
					physics.springs.get(i).b.y(), physics.springs.get(i).b.z()
			);
		}
	}
	
	public void initPhysics() {
		physics = new VerletPhysics();
		physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(DIM, DIM, DIM)));
	}

	public void initParticles() {
		VerletParticle center = new VerletParticle(0, 0, 0);
		center.lock();
		physics.addParticle(center);

		for (int i = 0; i < NUM_PARTICLES; i++) {
			// VerletParticle s = new VerletParticle(center.x(), center.y(),
			// center.z());
			VerletParticle s = new VerletParticle(p.random(-DIM/2, DIM/2), p.random(-DIM/2, DIM/2), p.random(-DIM/2, DIM/2));
			physics.addParticle(s);
			physics.addSpring(new VerletSpring(center, s, REST_LENGTH, 0.0001f));
		}
	}
	
	public void updateParticles() {
		physics.update();
	}

}
