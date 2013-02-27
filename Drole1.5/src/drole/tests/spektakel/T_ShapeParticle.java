package drole.tests.spektakel;

import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletConstrainedSpring;

import toxi.physics.behaviors.AttractionBehavior;
import processing.core.PApplet;

public class T_ShapeParticle extends T_Particle {

	VerletPhysics physics;

	AttractionBehavior shapeForce;

	int tailSize = 50;
	Vec3D[] tailPoint = new Vec3D[tailSize];

	public float myAlpha = 1.0f;
	
	public T_ShapeParticle(PApplet p,  float x, float y, float z) {
		super(p,x, y, z);
		

		for (int i = 0; i < tailSize; i++) {
			tailPoint[i] = new Vec3D(x, y, z);
		}
		
	
	}
	
	public T_ShapeParticle(PApplet p,  float x, float y, float z,float myAlpha) {
		this(p,x, y, z);
		this.myAlpha = myAlpha;
	}

	public void update() {

		bounce();
		
		if(p.frameCount%2==0){
		for (int i = tailSize-1; i > 0; i--) {
			tailPoint[i].x = tailPoint[i-1].x;
			tailPoint[i].y = tailPoint[i-1].y;
			tailPoint[i].z = tailPoint[i-1].z;
	//		p.println("i "+i+" x "+tailPoint[i].x);
		}
		
		tailPoint[0].x = this.x;
		tailPoint[0].y = this.y;
		tailPoint[0].z = this.z;
		}

		super.update();

	}

	
	void bounce(){
		
		int bounds=3000;
		
		Vec3D vel = getVelocity();
		
	
		if(x() > bounds ) {
			clearVelocity();
			x=bounds;
			addVelocity(new Vec3D(-vel.x/2,vel.y/2,vel.z/2));
		}
		
		if(x() < -bounds ) {
			clearVelocity();
			x=-bounds;
			addVelocity(new Vec3D(-vel.x/2,vel.y/2,vel.z/2));
		}
		
		if(y() > bounds){
			clearVelocity();
			
			y=bounds;
			addVelocity(new Vec3D(vel.x/2,-vel.y/100,vel.z/2));
		}
		
		
		
		if(z() > bounds) {
			clearVelocity();
			z=bounds;
			addVelocity(new Vec3D(vel.x/2,vel.y/2,-vel.z/2));
		}
		
		if( z() < -bounds) {
			clearVelocity();
			z=-bounds;
			addVelocity(new Vec3D(vel.x/2,vel.y/2,-vel.z/2));
		}
		
		
		
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
