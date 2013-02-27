package com.marctiedemann.spektakel;

import com.madsim.engine.EngineApplet;

import toxi.geom.Vec3D;

import toxi.physics.behaviors.AttractionBehavior;
import processing.core.PApplet;

public class ShapedParticle extends Particle {

	private AttractionBehavior shapeForce;

	public int tailSize = 50;
	private Vec3D[] tailPoint = new Vec3D[tailSize];

	public ShapedParticle(EngineApplet p, float x, float y, float z) {
		super(p, x, y, z);

		for (int i = 0; i < tailSize; i++) {
			tailPoint[i] = new Vec3D(x, y, z);
		}

	}

	public void update() {

		bounce();

	
			for (int i = tailSize - 1; i > 0; i--) {
				tailPoint[i].x = tailPoint[i - 1].x;
				tailPoint[i].y = tailPoint[i - 1].y;
				tailPoint[i].z = tailPoint[i - 1].z;
				// p.println("i "+i+" x "+tailPoint[i].x);
			}

			tailPoint[0].x = this.x;
			tailPoint[0].y = this.y;
			tailPoint[0].z = this.z;
		

		super.update();

	}

	

	public Vec3D getTailPoint(int num) {
		return tailPoint[num];
	}

	public void setUniqueTarget(AttractionBehavior shapeForce) {
		this.shapeForce = shapeForce;
		this.addBehavior(shapeForce);
	}

	public void setBehaviorStrenght(float newStrenght) {
		shapeForce.setStrength(newStrenght);
	}

}
