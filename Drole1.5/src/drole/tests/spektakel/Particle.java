package drole.tests.spektakel;

import toxi.physics.VerletParticle;
import processing.core.PApplet;
import java.util.ArrayList;

import codeanticode.glgraphics.GLConstants;

import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import processing.core.PApplet;


public class Particle extends VerletParticle{
	

	
	private float lifeSpan=0;
	private float DECAY = 0.9f;

	public Particle(float x, float y , float z){
		
		super(x,y,z);
		
	//	lifeSpan=p.random(mySize*2,mySize*4);
		lifeSpan= 600;
		
	//	setWeight(0.1f);
	}

		
	public void update(){
		super.update();
		lifeSpan-=DECAY;
	}
	
	public boolean isDead(){
		if(lifeSpan<0.5f)return true;
		else return false;
	}
	
	
}






