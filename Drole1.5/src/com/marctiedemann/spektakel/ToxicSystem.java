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
	
	
		
	
			protected int targetSize = 3;
			protected int targetYOffset = 200;

			protected int meshSize = 12; 
			
			protected Vec3D targetAngle = new Vec3D(0,-targetYOffset/2,0);

			
			
	public ToxicSystem(Engine e, VerletPhysics _physics, float mySize, float x,
			float y, float z) {

		super(e, _physics, x, y, z);
		
		trailLength=15;
		
		trailAlpha=0.4f;
		
		decay = 0.95f;
		
		 boomFalloff = 0.1f;
		 springFallOff = 0.05f;
		 setSpringPower(0.0002f);
		 setBoomPower(-3.5f);

		
	}
	
	public void init(){
       
		randomizeShootingVector();
		
		spawnNew(true);
	}

	public void update() {
		super.update();

	}

	public void draw(GLGraphics renderer) {

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
	

	public void spawnNew(boolean randomDecay) {

		bigParticle.clear();
		cleanSytstem();
		
		resetPowers();
		
		shockwave = true;
		
		//global shockwave
		boomForce = new AttractionBehavior(this, 2000, getBoomPower() * 0.2f, 0.1f);
		physics.addBehavior(boomForce);

		//build mesh for target shape
		randomizeMesh();

		
		float theDecay = decay;
		int count=0;
		
		System.out.println("start spawning");

		
		
		for (Iterator i = toxiMesh.faces.iterator(); i.hasNext();) {
			Face face = (Face) i.next();

		
			//longest particle can't have lower decay then system
			//also 1st particle needs to be lonest so we know where we can hook up the light
			
			
			if(randomDecay && count!=0) theDecay =e.p.random(decay,2);
			
			//the actual particle
			ShapedParticle newPart = new ShapedParticle(e.p, x() + face.a.x
					- targetAngle.x / 2, y() + face.a.y - targetAngle.y / 2,
					z() + face.a.z - targetAngle.z / 2,trailLength,theDecay,1);

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
		
//			System.out.println("spawning particle "+count);

			count++;
		}
		
		numPoints = bigParticle.size();

		// one size fits all


		initSprites();
		initTrails();

	}

	

}
