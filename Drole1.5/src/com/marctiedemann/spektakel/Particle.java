package com.marctiedemann.spektakel;


import toxi.physics.VerletParticle;
import processing.core.PApplet;


public class Particle extends VerletParticle {
	
	protected float lifeSpan=0;
	private float DECAY = 0.9f;

	public PApplet p;
	
	public Particle(PApplet p,float x, float y , float z){
		
		super(x,y,z);
		
		this.p = p;
		
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