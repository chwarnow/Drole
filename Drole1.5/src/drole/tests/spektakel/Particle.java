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
	
	PApplet p;
	float mySize=30;	
	
	float lifeSpan=0;
	float decay = 0.9f;

	public Particle(PApplet p,float mySize, float x, float y , float z){
		
		super(x,y,z);
		this.p = p;
		
		this.mySize=mySize;
	//	lifeSpan=p.random(mySize*2,mySize*4);
		lifeSpan= 600;
		
	//	setWeight(0.1f);
	}

		
	public void update(){
		super.update();
		lifeSpan-=decay;
	}
	
	
	
	
	public boolean isDead(){
		if(lifeSpan<0.5f)return true;
		else return false;
	}
	
	
}







