package com.madsim.drole.mmworld.drop;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

public class Drop extends Drawable {

	private DropPhysics physics;
	private DropMesh mesh;
	
	public Drop(Engine e) {
		super(e);
		
		physics = new DropPhysics(e.p, 12);
		mesh = new DropMesh(200);
	}

	@Override
	public void update() {
		super.update();
		
		physics.updateParticles();
		mesh.update(physics.getPhysics());
	}
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
	
}
