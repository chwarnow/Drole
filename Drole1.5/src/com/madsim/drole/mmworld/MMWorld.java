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
	
	public MMWorld(Engine e) {
		super(e);
		
		position(0, 0, -1000);
		scale(35, 35, 35);
		rotation(PApplet.radians(-90), 0, 0);
		
		points = DLAUtils.getDLAFromFile("data/dla/spiral.dla");
		
		pointDLA = DLAUtils.initializeModelWithDLA(e.p, GLModel.POINTS, points);
		lineDLA = DLAUtils.initializeModelWithDLA(e.p, GLModel.LINES, points);
		
		pointDLA.centerVertices(0, 0, 0);
		lineDLA.centerVertices(0, 0, 0);
		
		pointDLA.initColors();
			pointDLA.setColors(255);
		
		lineDLA.initColors();
//			lineDLA.setColors(255, 100);
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
			
			e.setupModel(pointDLA);
				pointDLA.render();
			
			e.setupModel(lineDLA);
				lineDLA.render();
			
		g.popMatrix();
		g.popStyle();
	}

}
