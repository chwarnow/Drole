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

	private int numRibbonHandler 				= 4;
	private float[] ribbonSeeds 				= new float[numRibbonHandler];
	
	/* assoziationen that are flying around in the menu */
	private int associationsAmount = 3;
	private String[] penseeImages = {
		"data/images/menuAssoziationA.png",
		"data/images/menuAssoziationB.png",
		"data/images/menuAssoziationC.png",
		"data/images/menuAssoziationD.png",
		"data/images/menuAssoziationE.png",
		"data/images/menuAssoziationF.png",
		"data/images/menuAssoziationG.png",
		"data/images/menuAssoziationH.png",
		"data/images/menuAssoziationI.png",
		"data/images/menuAssoziationJ.png",
		"data/images/menuAssoziationK.png",
		"data/images/menuAssoziationL.png",
		"data/images/menuAssoziationM.png"
	};
	
	public RibbonGlobe(Engine e, PVector position, PVector dimension) {
		super(e);

		position(position);
		dimension(dimension);
		
		// init menu pensees 
		float randomRadius = dimension.x*scale.x*.5f;
		for(int i=0;i<associationsAmount;i++) {
			BildweltAssoziationPensee b = new BildweltAssoziationPensee(
				e,
				penseeImages[(int)e.p.random(penseeImages.length-1)],
				dimension.x*scale.x*1.0f,
				2.0f,
				new PVector(e.p.random(-randomRadius, randomRadius),
						e.p.random(-randomRadius, randomRadius),
						e.p.random(-randomRadius, randomRadius)
				),
				new PVector(0, 0, 0)
			);
			b.setDelayTime(0);
			// let animation begin from right point
			b.setPosition(.4f);
			b.setLooping(false);
			b.loadPensee();
			// add to drawables list
			drawables.add( b );
		}
		
		// create swarms
		for(int i = 0; i < numRibbonHandler; i++) {
			drawables.add(new RibbonGroup(
					e,
					dimension.x*scale.x, // sphere size
					500, // amount
					2 + (int)e.p.random(50), // joints per ribbon
					2f, // quadheight
					i)); // id
		}
	}
	
	@Override
	public void fadeOut(float time) {
		super.fadeOut(time);
		hide();
	}
	
	public void hide() {
		// TODO: set all ribbons to die mode
		int drawableIndex = 0;
		for(int i=0;i<drawables.size();i++) {				
			// draw associations
			if(drawableIndex++ < associationsAmount) {
				// r.draw();
			} else {
				RibbonGroup rG = (RibbonGroup)drawables.get(i);
				//  menu swarms
				rG.dieOut();
			}
		}
		
	}
	
	public void switchToLights() {
		e.p.logLn("[Globe]: Switching to mode LIGHTS!");
		/*
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
		*/
		menuMode = RibbonGlobe.LIGHTS;
		
	}
	
	public void switchToMenu() {
		e.p.logLn("[Globe]: Switching to mode MENU!");
		/*
		for(Drawable d : drawables) {
			RibbonGroup r = (RibbonGroup)d;
//			r.easeToScale(new PVector(1f, 1f, 1f), 300);
//			r.easeToPosition(0f, 0f, 0f, 300);
			r.deletePivot();
		}
		*/
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
		//if(mode().equals(ON_SCREEN)) {
		
		// load pensees now
		for(int i=0;i<associationsAmount;i++) {
			// draw associations
			BildweltAssoziationPensee b = (BildweltAssoziationPensee) drawables.get(i);
			if(b.isReady() && b.isAnimationDone()) {
				float randomRadius = dimension.x*scale.x*.5f;
				b.setPosition(.4f);
				b.loadNewImage(
						penseeImages[(int)e.p.random(penseeImages.length)],
						dimension.x*scale.x,
						new PVector(e.p.random(-randomRadius, randomRadius),
								e.p.random(-randomRadius, randomRadius),
								e.p.random(-randomRadius, randomRadius)),
						new PVector(0, 0, 0));
			}
		}
		
		g.pushStyle();
		g.pushMatrix();
		
			g.translate(position.x, position.y, position.z + 300.0f);
			g.scale(scale.x, scale.y, scale.z);
			g.rotateY(smoothedRotation);
			
			g.fill(255, fade*255);
			g.noStroke();
			
			e.startShader("PolyLightAndColor");
			
			int drawableIndex = 0;
			float penseeRotation = .0f;
			for(Drawable r : drawables) {
				
				
				// draw associations
				if(drawableIndex++ < associationsAmount) {
					// r.update();
					g.pushMatrix();
					g.rotateY(penseeRotation);
					g.translate(0, e.p.cos(e.p.frameCount*.03f + penseeRotation)*50f, 0);
					// r.draw();
					g.popMatrix();
					
					penseeRotation += 10f;
				} else {
					// r.update();
					// draw menu swarms
					r.draw();
				}
			}
			
			// e.stopShader();
			
			
			
		g.popMatrix();
		g.popStyle();
	//}
		// e.p.println(e.p.frameRate);
	}
}
