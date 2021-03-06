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

import processing.core.PApplet;
import processing.core.PVector;

public class RibbonGlobe extends Drawlist {
	
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
			RibbonGroup b = new RibbonGroup(
					e,
					dimension.x*scale.x, // sphere size
					500, // amount
					2 + (int)e.p.random(50), // joints per ribbon
					2f, // quadheight
					i); // id
			drawables.add( b );
		}
	}
	
	@Override
	public void fadeOut(float time) {
		super.fadeOut(time);
		hide();
	}
	
	public void hide() {
		super.hide();
		
		// set all ribbons to die mode
		int drawableIndex = 0;
		for(int i=0;i<drawables.size();i++) {				
			// draw associations
			if(drawableIndex++ < associationsAmount) {
				BildweltAssoziationPensee p = (BildweltAssoziationPensee) drawables.get(i);
				p.hideMe();
			} else {
				RibbonGroup rG = (RibbonGroup)drawables.get(i);
				//  menu swarms
				rG.dieOut();
			}
		}
	}
	
	public void show() {
		super.show();
	}

	@Override
	public void fadeIn(float time) {
		super.fadeIn(time);
		
		// set all ribbons to die mode
		int drawableIndex = 0;
		for(int i=0;i<drawables.size();i++) {				
			// draw associations
			if(drawableIndex++ < associationsAmount) {
				BildweltAssoziationPensee p = (BildweltAssoziationPensee) drawables.get(i);
				p.resume();
			} else {
				RibbonGroup rG = (RibbonGroup)drawables.get(i);
				//  menu swarms
				rG.makeAlive();
			}
		}
	}
	
	@Override
	public void draw() {
		// load pensees now
		for(int i=0;i<associationsAmount;i++) {
			// draw associations
			BildweltAssoziationPensee b = (BildweltAssoziationPensee) drawables.get(i);
			if(b.isReady() && b.isAnimationDone() && b.isVisible()) {
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
					
			g.fill(255, fade*255);
			g.noStroke();
			
			int drawableIndex = 0;
			float penseeRotation = .0f;
			for(Drawable r : drawables) {
				
				// draw associations
				if(drawableIndex++ < associationsAmount) {
					g.pushMatrix();
					g.rotateY(penseeRotation);
					
					g.translate(0, e.p.cos(e.p.frameCount*.03f + penseeRotation)*50f, 0);

					BildweltAssoziationPensee p = (BildweltAssoziationPensee)drawables.get(drawableIndex-1);
					if(p.isVisible()) p.draw();
					
					g.popMatrix();
					
					penseeRotation += 10f;
				} else {
					// draw menu swarms
					r.draw();
				}
			}

		g.popMatrix();
		g.popStyle();
	}
}
