package com.marctiedemann.micromacro;

import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.engine.drawable.Drawable;

public class BildweltNewMicroMacro  extends Drawable {
	
	
	
	GLTexture bigWorld;
	
	public BildweltNewMicroMacro(Engine e){
		super(e);
	
	
		bigWorld = e.requestTexture("images/Mikro_Makro_BigWorld.png");

		g.imageMode(EngineApplet.CENTER);
	}
	
	
		
	@Override
	public void update(){			
		
	}
	
	@Override
	public void draw(){
	
		g.pushStyle();
		g.pushMatrix();
		
		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateX(rotation.x);
		g.rotateY(rotation.y);
		g.rotateZ(rotation.z);
		
		
		g.image(bigWorld,0,0);
		
		g.popMatrix();
		g.popStyle();
		
		
	}

}
