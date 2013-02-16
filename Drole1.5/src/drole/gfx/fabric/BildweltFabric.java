package drole.gfx.fabric;

import processing.core.PApplet;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import codeanticode.glgraphics.GLTexture;

public class BildweltFabric extends Drawable {

	HatchingFabric fabric;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	private GLTexture angelA, angelB;
	
	public BildweltFabric(Engine e) {
		super(e);
		
		position(0.0f, -1000.0f, -1500f);
		
		fabric = new HatchingFabric(e.p, "data/images/Karte_1.jpg", 30, 30, 1.0f, .8427f);
		
		angelA = new GLTexture(e.p, "data/images/fabricAngelA.png");
		angelB = new GLTexture(e.p, "data/images/fabricAngelB.png");
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
	}

	@Override
	public void draw() {
		
		g.pushStyle();
		g.pushMatrix();
		
		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateY(smoothedRotation);// - parent.HALF_PI/2);
		
		fabric.draw();
		
		// draw angels holding the fabric
		g.hint(PApplet.DISABLE_DEPTH_TEST);
		
		g.tint(255);
		g.imageMode(PApplet.CORNERS);
		g.pushMatrix();
		g.translate(fabric.endA.x-130, fabric.endA.y-20, fabric.endA.z);
		g.image(angelA, 0, 0);
		g.popMatrix();
		
		g.pushMatrix();
		g.translate(fabric.endB.x-105, fabric.endB.y-40, fabric.endB.z);
		g.image(angelB, 0, 0);
		g.popMatrix();
		
		g.hint(PApplet.ENABLE_DEPTH_TEST);
		
		g.popMatrix();
		g.popStyle();
	}

}

