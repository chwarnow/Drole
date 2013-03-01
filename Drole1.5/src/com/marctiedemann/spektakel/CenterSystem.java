package com.marctiedemann.spektakel;

import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;

import codeanticode.glgraphics.GLGraphics;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;

import drole.settings.Settings;

public class CenterSystem extends ToxicSystem{

	private float rotationY =0;
	
	
	public CenterSystem(Engine e, VerletPhysics physics, float mySize, float x,
			float y, float z){
		
	
		super(e,physics,mySize,x,y,z);
		

		
		targetAngle = new Vec3D(0,-targetYOffset*2,0);
		
		trailLength = 100;
		meshSize = 24;

		setBoomPower(-5);
		setSpringPower(0.00009f);

		
		
	     boomFalloff = 0.005f;

		 springFallOff = -0.008f;
		 decay=0.8f;
		 
		 trailAlpha=0.8f;
		 
		 spriteAlpha = 2.0f;
		 
		 spriteSize=20;

		 
		 
	}
	
	@Override
	public void init(){
		
		spawnNew(false);
		
		
	}
	
	
	@Override
	public void spawnNew(boolean randomDecay){
		super.spawnNew(false);
	}
	
	@Override
	public void update(){
		
		
		float newDrag = -0.8f/getBoomPower();
		physics.setDrag(newDrag);
		super.update();
	}
	
	@Override
	public void draw(GLGraphics renderer){
		e.setPointSize(5);
		
		
		
		
		renderer.pushMatrix();
		renderer.translate(0, 0,-900);

		renderer.rotateY(rotationY);
		
		super.draw(renderer);
		renderer.popMatrix();
	}
	
	public void setRotation(float rotation){
		
		
		//should be between -1 & 1
		rotationY = rotation*EngineApplet.PI;
		
	}
	
}
