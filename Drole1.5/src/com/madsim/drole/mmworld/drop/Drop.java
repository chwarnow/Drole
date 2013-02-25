package com.madsim.drole.mmworld.drop;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

public class Drop extends Drawable {

	private FullDrop drop;
	
	public Drop(Engine e) {
		super(e);
		
		position(0, 0, -1000);
		
		drop = new FullDrop(e.p);
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void draw() {
		g.pushMatrix();
		g.pushStyle();
			g.translate(position.x, position.y, position.z);
			g.scale(scale.x, scale.y, scale.z);
			g.rotateX(rotation.x);
			g.rotateY(rotation.y);
			g.rotateZ(rotation.z);
			
			g.noStroke();
			g.fill(200, 10);
			
			drop.draw(g);
			
		g.popStyle();
		g.popMatrix();
	}
	
}
