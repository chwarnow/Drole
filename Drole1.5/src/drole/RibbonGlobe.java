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

import java.util.ArrayList;

import drole.engine.Drawable;
import drole.gfx.ribbon.RibbonGroup;

import processing.core.PImage;
import processing.core.PVector;

public class RibbonGlobe extends Drawable {
	
	public float rotation 						= 0;
	public float rotationSpeed 					= 0.04f;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;

	private int numRibbonHandler 				= 1;
	private ArrayList<RibbonGroup> ribbons 		= new ArrayList<RibbonGroup>();
	private float[] ribbonSeeds 				= new float[numRibbonHandler];

	public RibbonGlobe(DroleMain parent, PVector position, PVector dimension, PImage globeTexture) {
		super(parent);

		position(position);
		dimension(dimension);
		
		for(int i = 0; i < numRibbonHandler; i++) {
			ribbons.add(new RibbonGroup(parent, dimension.x, 100, 20, 100));
		}
	}

	public ArrayList<RibbonGroup> getRibbons() {
		return ribbons;
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		for(RibbonGroup r : ribbons) r.update();
	}

	@Override
	public void draw() {		
		parent.g.pushStyle();
		parent.g.pushMatrix();

			parent.startShader("JustColor");
		
			parent.g.translate(position.x, position.y, position.z);
			parent.g.scale(scale.x, scale.y, scale.z);
			parent.g.rotateY(smoothedRotation);
			
			parent.fill(200, 200, 200, fade*255);
			parent.noStroke();
			
			for(RibbonGroup r : ribbons) r.draw();
//			for(RibbonGroup r : ribbons) r.drawAsLines();
		
			parent.stopShader();
			
		parent.g.popMatrix();
		parent.g.popStyle();
	}
	
}