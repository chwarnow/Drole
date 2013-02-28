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

		if (x() > boundsX) {
			clearVelocity();
			x = boundsX;
			addVelocity(new Vec3D(-vel.x / 2, vel.y / 2, vel.z / 2));
		}

		if (x() < -boundsX) {
			clearVelocity();
			x = -boundsX;
			addVelocity(new Vec3D(-vel.x / 2, vel.y / 2, vel.z / 2));
		}

		if (y() > boundsY) {
			clearVelocity();

			y = boundsY;
			addVelocity(new Vec3D(vel.x / 2, -vel.y / 100, vel.z / 2));
		}

		if (z() > 0) {
			clearVelocity();
			z = 0;
			addVelocity(new Vec3D(vel.x / 2, vel.y / 2, -vel.z / 2));
		}

		if (z() < -boundsZ) {
			clearVelocity();
			z = -boundsZ;
			addVelocity(new Vec3D(vel.x / 2, vel.y / 2, -vel.z / 2));
		}

//
		
//		p.println(" x "+x +" y "+y+" z "+z);
		
	}

	public void update(){
		super.update();
		lifeSpan-=decay;
	}
	
	public boolean isDead(){
		if(lifeSpan<0.5f)return true;
		else return false;
	}
	
	public float getTimeToLife(){
		return lifeSpan;
	}
	
	
}