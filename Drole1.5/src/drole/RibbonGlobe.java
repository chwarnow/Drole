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
	
	public float rotation 						= 0;
	public float rotationSpeed 					= 0.04f;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;

	private int 			numRibbonHandler 	= 5;
	private RibbonHandler[] ribbons 			= new RibbonHandler[numRibbonHandler];
	private float[] 		ribbonSeeds 		= new float[numRibbonHandler];

	public RibbonGlobe(PApplet parent, PVector position, PVector dimension, PImage globeTexture) {
		super(parent);

		position(position);
		dimension(dimension);
		
		for(int i = 0; i < numRibbonHandler; i++) {
			ribbons[i] 	= new RibbonHandler(parent, dimension.x, 10, 100, 100);
		}
	}

	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		for(RibbonHandler r : ribbons) r.update();
	}

	@Override
	public void draw() {		
		parent.g.pushStyle();
		parent.g.pushMatrix();

			parent.g.translate(position.x, position.y, position.z);
			parent.g.scale(scale.x, scale.y, scale.z);
			parent.g.rotateY(smoothedRotation);
			
			parent.g.tint(255, 255);
			
			for(RibbonHandler r : ribbons) r.draw();
		
		parent.g.popMatrix();
		parent.g.popStyle();
	}
	
}
