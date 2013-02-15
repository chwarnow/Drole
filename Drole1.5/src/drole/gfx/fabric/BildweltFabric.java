package drole.gfx.fabric;

import drole.DroleMain;
import drole.engine.Drawable;

public class BildweltFabric extends Drawable {

	HatchingFabric fabric;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	public BildweltFabric(DroleMain parent) {
		super(parent);
		
		fabric = new HatchingFabric(parent, "data/images/Karte_1.jpg", 10, 50, 1.0f, 1.0f);
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
	}

	@Override
	public void draw() {
		
		parent.g.pushStyle();
		parent.g.pushMatrix();

		parent.g.translate(position.x, position.y, position.z);
		parent.g.scale(scale.x, scale.y, scale.z);
		parent.g.rotateY(smoothedRotation);
		
		fabric.draw();
		
		parent.g.popMatrix();
		parent.g.popStyle();
		
	}

}

