package drole.gfx.fabric;

import codeanticode.glgraphics.GLTexture;
import drole.DroleMain;
import drole.engine.Drawable;

public class BildweltFabric extends Drawable {

	HatchingFabric fabric;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	private GLTexture angelA, angelB;
	
	public BildweltFabric(DroleMain parent) {
		super(parent);
		
		position(0.0f, -1000.0f, -1500f);
		
		fabric = new HatchingFabric(parent, "data/images/Karte_1.jpg", 30, 30, 1.0f, .8427f);
		
		angelA = new GLTexture(parent, "data/images/fabricAngelA.png");
		angelB = new GLTexture(parent, "data/images/fabricAngelB.png");
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
		parent.g.rotateY(smoothedRotation);// - parent.HALF_PI/2);
		
		fabric.draw();
		
		// draw angels holding the fabric
		parent.g.hint(parent.DISABLE_DEPTH_TEST);
		
		parent.g.tint(255);
		parent.g.imageMode(parent.CORNERS);
		parent.g.pushMatrix();
		parent.g.translate(fabric.endA.x-130, fabric.endA.y-20, fabric.endA.z);
		parent.g.image(angelA, 0, 0);
		parent.g.popMatrix();
		
		parent.g.pushMatrix();
		parent.g.translate(fabric.endB.x-105, fabric.endB.y-40, fabric.endB.z);
		parent.g.image(angelB, 0, 0);
		parent.g.popMatrix();
		
		parent.g.hint(parent.ENABLE_DEPTH_TEST);
		
		parent.g.popMatrix();
		parent.g.popStyle();
		
	}

}

