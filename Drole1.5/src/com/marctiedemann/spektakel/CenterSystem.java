package com.marctiedemann.spektakel;

import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;

import codeanticode.glgraphics.GLGraphics;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;

import drole.settings.Settings;

public class CenterSystem extends ToxicSystem{

	public float rotationY =0;
	
	
	public CenterSystem(Engine e, VerletPhysics physics, float mySize, float x,
			float y, float z){
		
	
		super(e,physics,mySize,x,y,z);
		

		
		targetAngle = new Vec3D(0,-targetYOffset*2,0);
		
		trailLength = 100;
		meshSize = 24;

		setBoomPower(-10);
		setSpringPower(0.00005f);

		
		
	     boomFalloff = 0.005f;

		 springFallOff = -0.008f;
		 decay=0.5f;
		 
		 trailAlpha=0.4f;

		 spriteSize=25;
		

		 
		 
	}
	
	@Override
	public void init(){
		
		spawnNew();
		
		
	}
	
	
	@Override
	public void spawnNew(){
		super.spawnNew();
	}
	
	@Override
	public void update(){
		super.update();
	}
	
	@Override
	public void draw(GLGraphics renderer){
		e.setPointSize(5);
		
		
		
		
		renderer.pushMatrix();


		

		super.draw(renderer);
		renderer.popMatrix();
	}
	
	public void setRotation(float rotation){
		
		
		//should be between -1 & 1
		rotationY = rotation*EngineApplet.PI*2;
		
	}
	
}
