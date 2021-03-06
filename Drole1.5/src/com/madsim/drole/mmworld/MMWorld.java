package com.madsim.drole.mmworld;


import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

import codeanticode.glgraphics.GLModel;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

public class MMWorld extends Drawable {

	private float constantRotY = -0.001f;
	
	private ArrayList<PVector> points;
	
	private GLModel pointDLA, lineDLA;
	
	private float knockOut = 1.0f;
	
	public MMWorld(Engine e) {
		super(e);
		
		position(0, 0, -1000);
		scale(35, 35, 35);
		rotation(PApplet.radians(-90), 0, 0);
		
		useLights();
		setPointLight(0, -800, 0, -1000, 0, 0, 255, 1.0f, 0.0001f, 0.0f);
		setPointLight(1, 700, 0, -800, 255, 0, 0, 1.0f, 0.0001f, 0.0f);
		
		setPointLight(1, 0, 0, -800, 255,255, 255, 1.0f, 0.0001f, 0.0f);
		setPointLight(1, -100, 100, -800, 255,255, 255, 1.0f, 0.0001f, 0.0f);
		
		points = DLAUtils.getDLAFromFile("data/dla/spiral.dla");
		
		pointDLA = DLAUtils.initializeModelWithDLA(e.p, GLModel.POINTS, points);
		lineDLA = DLAUtils.initializeModelWithDLA(e.p, GLModel.LINES, points);
		
		pointDLA.centerVertices(0, 0, 0);
		lineDLA.centerVertices(0, 0, 0);
		
		pointDLA.initColors();
			pointDLA.setColors(255);
		
		lineDLA.initColors();
			lineDLA.setColors(255, 100);
		
		pointDLA.initTextures(1);
		pointDLA.setTexture(0, e.requestTexture("data/images/1d-white.jpg"));
		pointDLA.beginUpdateTexCoords(0);
			for(int i = 0; i < points.size(); i++) pointDLA.updateTexCoord(i, (1.0f/points.size())*i, (1.0f/points.size())*i);
		pointDLA.endUpdateTexCoords();
	}

	@Override
	public void update() {
		super.update();
		
		rotation(rotation.x, rotation.y, rotation.z+constantRotY);
	}
	
	@Override
	public void draw() {
		g.pushStyle();
		g.pushMatrix();
		
			g.translate(position.x, position.y, position.z);
			g.scale(scale.x, scale.y, scale.z);
			g.rotateX(rotation.x);
			g.rotateY(rotation.y);
			g.rotateZ(rotation.z);
			
			e.setPixelKnockOut(knockOut);
			knockOut -= 0.001f;
			
			e.setupModel(pointDLA);
				pointDLA.render();
			
//			e.setupModel(lineDLA);
//				lineDLA.render();
			
		g.popMatrix();
		g.popStyle();
	}

}
