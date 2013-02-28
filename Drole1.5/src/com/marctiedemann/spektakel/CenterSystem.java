package com.marctiedemann.spektakel;

import toxi.physics.VerletPhysics;

import codeanticode.glgraphics.GLGraphics;

import com.madsim.engine.Engine;

public class CenterSystem extends ToxicSystem{

	
	public CenterSystem(Engine e, VerletPhysics physics, float mySize, float x,
			float y, float z){
		
		super(e,physics,mySize,x,y,z,false);
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
		super.draw(renderer);
	}
	
}
