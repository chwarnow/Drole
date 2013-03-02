package com.marctiedemann.spektakel;


import com.madsim.engine.EngineApplet;

import drole.settings.Settings;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import processing.core.PApplet;


public class Particle extends VerletParticle {
	

	private float lifeSpan=255;
	
	protected float decay = 0.9f;
	
	public EngineApplet p;
	
	protected boolean hidden = false;
	
	public Particle(EngineApplet p,float x, float y , float z){
		
		super(x,y,z);
		
		this.p = p;

	}

		
	protected void bounce() {

		int boundsX = (int)Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM/2;
		int boundsY = (int)Settings.VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM/2;
		int boundsZ = (int)Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM;
		
//		p.println("z depth "+boundsZ);

		Vec3D vel = getVelocity();

		float drag = 0.9f;
		float friction = 0.98f;
		
		
		
		if (x() > boundsX) {
			clearVelocity();
	//		x = boundsX;
			addVelocity(new Vec3D(-vel.x * drag, vel.y*friction, vel.z*friction));
		}

		if (x() < -boundsX) {
			clearVelocity();
//			x = -boundsX;
			addVelocity(new Vec3D(-vel.x * drag, vel.y*friction, vel.z*friction ));
		}

		if (y() > boundsY) {
		
			clearVelocity();

		//	y = boundsY;
			
	//		vel = new Vec3D(vel.x/friction, -vel.y/10000, vel.z/friction);
			
			addVelocity(new Vec3D(vel.x*drag, -vel.y*0.5f, vel.z*drag));
		}

	
		if (z() > 0) {
			clearVelocity();
			z = 0;
			addVelocity(new Vec3D(vel.x*friction, vel.y*friction  ,-vel.z * drag));
		}

		if (z() < -boundsZ) {
			clearVelocity();
		//	z = -boundsZ;
			addVelocity(new Vec3D(vel.x*friction, vel.y*friction , -vel.z * drag));
		}


//
		
//		p.println(" x "+x +" y "+y+" z "+z);
		
	}
	
	public void hideAndLock(){
		lock();
		hidden = true;
	}
	
	public void unHideAndLock(){
		unlock();
		hidden=false;
	}
	

	public void update(){
		super.update();
		if(lifeSpan>1)
		lifeSpan-=decay;
		if(lifeSpan<1)lifeSpan=0;
		
	}
	
	public boolean isDead(){
		if(lifeSpan<1)return true;
		else return false;
	}
	
	public float getTimeToLife(){
		return lifeSpan;
	}
	
	
}