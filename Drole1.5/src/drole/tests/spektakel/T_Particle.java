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


public class T_Particle extends VerletParticle{
	

	
	private float lifeSpan=255;
	protected float decay = 0.9f;
	PApplet p;

	public T_Particle(PApplet p,float x, float y , float z){
		
		super(x,y,z);
		this.p =p;
		
	//	lifeSpan=p.random(mySize*2,mySize*4);
		
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
	
	public float getTimeToLife(){
		return lifeSpan;
	}
	
	
}







