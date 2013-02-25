package drole.gfx.ribbon;

/**
 * 
 * Initial Class by Denny Koch
 * 
 * particles that float around an invisible sphere
 * using toxiclibs verlet physics
 * being the menu (Christopher Warnow)
 * 
 */

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.drawable.Drawlist;

import drole.gfx.assoziation.BildweltAssoziationPensee;

import processing.core.PVector;

public class RibbonGlobe extends Drawlist {
	
	public static short MENU					= 10;
	public static short LIGHTS					= 20;
	
	private short menuMode 						= RibbonGlobe.MENU;
	
	public float rotation 						= 0;
	public float rotationSpeed 					= 0.04f;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;

	private int numRibbonHandler 				= 0;//25;
	private float[] ribbonSeeds 				= new float[numRibbonHandler];
	
	/* assoziationen that are flying around in the menu */
	BildweltAssoziationPensee penseeA, penseeB, penseeC, penseeD, penseeE;
	boolean isAssociations = true;

	public RibbonGlobe(Engine e, PVector position, PVector dimension) {
		super(e);

		position(position);
		dimension(dimension);
		
		for(int i = 0; i < numRibbonHandler; i++) {
			drawables.add(new RibbonGroup(e, dimension.x*scale.x, 10, 20, 500));
		}
		
		if(isAssociations) {
			// init menu pensees 
			float randomRadius = dimension.x*scale.x*.5f;
			drawables.add(new BildweltAssoziationPensee(e, "data/images/menuAssoziationA.png", dimension.x*scale.x, 2.0f, new PVector(e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius)), new PVector()));
			
			BildweltAssoziationPensee b = (BildweltAssoziationPensee)drawables.get(drawables.size()-1);
			b.currPosition += 50;
			drawables.add(new BildweltAssoziationPensee(e, "data/images/menuAssoziationB.png", dimension.x*scale.x, 2.0f, new PVector(e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius)), new PVector()));
			b = (BildweltAssoziationPensee)drawables.get(drawables.size()-1);
			b.currPosition += 100;
			drawables.add(new BildweltAssoziationPensee(e, "data/images/menuAssoziationC.png", dimension.x*scale.x, 2.0f, new PVector(e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius)), new PVector()));
			
			// drawables.add(new BildweltAssoziationPensee(e, "data/images/menuAssoziationD.png", dimension.x*scale.x, .75f, new PVector(e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius)), new PVector()));
			// drawables.add(new BildweltAssoziationPensee(e, "data/images/menuAssoziationE.png", dimension.x*scale.x, .75f, new PVector(e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius), e.p.random(-randomRadius, randomRadius)), new PVector()));
		}
	}
	
	public void switchToLights() {
		e.p.logLn("[Globe]: Switching to mode LIGHTS!");
		
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
		e.p.logLn("[Globe]: Switching to mode MENU!");
		
		for(Drawable d : drawables) {
			RibbonGroup r = (RibbonGroup)d;
//			r.easeToScale(new PVector(1f, 1f, 1f), 300);
//			r.easeToPosition(0f, 0f, 0f, 300);
			r.deletePivot();
		}
		
		menuMode = RibbonGlobe.MENU;		
	}
	
	public short menuMode(short menuMode) {
		this.menuMode = menuMode;
		return menuMode();
	}

	public short menuMode() {
		return menuMode;
	}
	
	@Override
	public void draw() {
		g.pushStyle();
		g.pushMatrix();
		
			g.translate(position.x, position.y, position.z + 300.0f);
			g.scale(scale.x, scale.y, scale.z);
			g.rotateY(smoothedRotation);
			
			g.fill(255, fade*255);
			g.noStroke();
			
			e.startShader("PolyLightAndColor");
			
			float penseeRotation = .0f;
			for(Drawable r : drawables) {
				r.update();
				g.pushMatrix();
				g.rotateY(penseeRotation);
				g.translate(0, e.p.cos(e.p.frameCount*.01f + penseeRotation)*50f, 0);
				r.draw();
				g.popMatrix();
				
				penseeRotation += 10f;
			}
			
			// e.stopShader();
			
			
			
		g.popMatrix();
		g.popStyle();
	}
	
}
