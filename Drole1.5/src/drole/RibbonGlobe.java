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

import drole.engine.Drawable;
import drole.engine.Drawlist;
import drole.gfx.ribbon.RibbonGroup;

import processing.core.PVector;

public class RibbonGlobe extends Drawlist {
	
	public static short MENU					= 10;
	public static short LIGHTS					= 20;
	
	private short menuMode 						= RibbonGlobe.MENU;
	
	public float rotation 						= 0;
	public float rotationSpeed 					= 0.04f;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;

	private int numRibbonHandler 				= 25;
	private float[] ribbonSeeds 				= new float[numRibbonHandler];

	public RibbonGlobe(DroleMain parent, PVector position, PVector dimension) {
		super(parent);

		position(position);
		dimension(dimension);
		
		for(int i = 0; i < numRibbonHandler; i++) {
			drawables.add(new RibbonGroup(parent, dimension.x*scale.x, 10, 20, 100));
		}
	}
	
	public void switchToLights() {
		parent.logLn("[Globe]: Switching to mode LIGHTS!");
		
		int i = 0;
		for(int x = 0; x < 5; x++) {
			for(int y = 0; y < 5; y++) {
				RibbonGroup r = (RibbonGroup)drawables.get(i++);
//				r.easeToScale(new PVector(.1f, .1f, .1f), 300);
				//r.easeToPosition(-2500+(x*500), -1000, -2500+(y*500), 300);
//				r.easeToPosition(-1250+(x*500), -900, -2500+(y*500), 300);
				r.createPivotAt(0, 0, 0);
			}
		}
		
		menuMode = RibbonGlobe.LIGHTS;
	}
	
	public void switchToMenu() {
		parent.logLn("[Globe]: Switching to mode MENU!");
		
		for(Drawable d : drawables) {
			RibbonGroup r = (RibbonGroup)d;
//			r.easeToScale(new PVector(1f, 1f, 1f), 300);
//			r.easeToPosition(0f, 0f, 0f, 300);
			r.deletePivot();
		}
		
		menuMode = RibbonGlobe.MENU;		
	}
	
	public short menuMode() {
		return menuMode;
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
			
			for(Drawable r : drawables) {
				r.update();
				r.draw();
			}
//			for(RibbonGroup r : ribbons) r.drawAsLines();
		
			parent.stopShader();
			
		parent.g.popMatrix();
		parent.g.popStyle();
	}
	
}
