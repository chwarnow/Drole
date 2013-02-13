package drole;

/**
 * 
 * Initial Class by Denny Koch
 * 
 * particles that float around an invisible sphere
 * using toxiclibs verlet physics
 * being the menu (Christopher Warnow)
 * 
 */

import drole.gfx.ribbon.RibbonHandler;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class RibbonGlobe extends Drawable {
	
	public float rotation 		= 0;
	public float rotationSpeed = 0.04f;
	private float smoothedRotation = 0;
	private float smoothedRotationSpeed = .1f;

	private RibbonHandler ribbons;

	public RibbonGlobe(PApplet parent, PVector position, PVector dimension, PImage globeTexture) {
		super(parent);

		position(position);
		dimension(dimension);
		
		ribbons = new RibbonHandler(parent, 10, 10);
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
			
			// dimension must be respected!
			parent.g.tint(255, 255);
			ribbons.draw();
		
		parent.g.popMatrix();
		parent.g.popStyle();
	}
	
}
