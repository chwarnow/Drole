package com.marctiedemann.micromacro;

import codeanticode.glgraphics.GLModel;
import toxi.geom.Vec2D;
import toxi.physics.VerletParticle;

public class Cell extends VerletParticle{
	
	public float mySize = 10;
	
	public Cell(float x,float y, float z, float mySize){
		super(x,y,z);
		
		this.mySize=mySize;
	}
	
	
	
	
	

}
