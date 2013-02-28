package com.marctiedemann.spektakel;

import java.util.Iterator;

import com.madsim.engine.Engine;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import drole.tests.spektakel.T_ShapeParticle;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.SphericalHarmonics;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.AttractionBehavior;

public class ToxicSystem extends ParticleSystem {

	private TriangleMesh toxiMesh = new TriangleMesh();
	
	
	//size and shooting angle
	
	
			protected float decay = 0.9f;
	
			protected int targetSize = 3;
			protected int targetYOffset = 200;

			protected int meshSize = 12; 
			
			protected Vec3D targetAngle = new Vec3D(0,-targetYOffset/2,0);

	public ToxicSystem(Engine e, VerletPhysics _physics, float mySize, float x,
			float y, float z) {

		super(e, _physics, x, y, z);
		
		trailLength=20;
		
		trailAlpha=0.1f;

		
	}
	
	public void init(){
       
		randomizeShootingVector();
		
		spawnNew();
	}

	public void update() {
		super.update();

	}

	public void draw(GLGraphics renderer) {
		e.setPointSize(15);

		super.draw(renderer);

	}

	void randomizeMesh() {
		float[] m = new float[8];
		for (int i = 0; i < 8; i++) {
			m[i] = (int) e.p.random(20);
		}
		SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
		toxiMesh = (TriangleMesh) b.createMesh(null, meshSize, 100);
	}

	protected void randomizeShootingVector(){

		targetAngle = new Vec3D(e.p.random(-1, 1)*targetYOffset ,
				e.p.random(-1, 1)*targetYOffset , e.p.random(-1, 1)*targetYOffset
						);
	}
	
	public void spawnNew() {

		bigParticle.clear();
		cleanSytstem();
		
		resetPowers();
		
		shockwave = true;
		
		//global shockwave
		boomForce = new AttractionBehavior(this, 2000, getBoomPower() * 0.2f, 0.1f);
		physics.addBehavior(boomForce);

		//build mesh for target shape
		randomizeMesh();

		

		for (Iterator i = toxiMesh.faces.iterator(); i.hasNext();) {
			Face face = (Face) i.next();

			//the actual partzicle
			ShapedParticle newPart = new ShapedParticle(e.p, x() + face.a.x
					- targetAngle.x / 2, y() + face.a.y - targetAngle.y / 2,
					z() + face.a.z - targetAngle.z / 2,trailLength,decay,1);

			// p.println("x "+f.a.x+" y "+f.a.y+" z "+f.a.z);

			//the Target
			Vec3D toxicTarget = new Vec3D(x() + targetAngle.x + face.a.x
					* targetSize, y() + targetAngle.y + face.a.y * targetSize,
					z() + targetAngle.z + face.a.z * targetSize);

			VerletParticle targetPoint = new VerletParticle(toxicTarget);
			targetPoint.lock();
			
			
			//targetForce
			VerletSpring toxicForce = new VerletSpring(newPart,targetPoint,
					0, getSpringPower());
//			physics.addParticle(targetPoint);
			physics.addParticle(newPart);
			physics.addSpring(toxicForce);
			newPart.giveSpring(toxicForce);
			// newPart.addBehavior(boomForce);

			bigParticle.add(newPart);
		

			/*
			 * newPart = new Particle(p, mySize / 2, f.c.x+x, f.c.y+y, f.c.z+z);
			 * bigParticle.add(newPart); physics.addParticle(newPart);
			 */
		}
		
		numPoints = bigParticle.size();

		// one size fits all


		initSprites();
		initTrails();

	}

	

}
