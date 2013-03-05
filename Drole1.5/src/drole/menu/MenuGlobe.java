package drole.menu;

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

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import drole.gfx.assoziation.BildweltAssoziationPensee;
import drole.gfx.ribbon.RibbonGroup;

import processing.core.PApplet;
import processing.core.PVector;

public class MenuGlobe extends Drawable {
	
	private ArrayList<Drawable> drawables 		= new ArrayList<Drawable>();

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
	
	public MenuGlobe(Engine e, PVector position, PVector dimension) {
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
	}
	
	public void hide() {
		super.hide();
		
		// set all ribbons to die mode
		int drawableIndex = 0;
		for(int i=0;i<drawables.size();i++) {				
			// draw associations
			if(drawableIndex++ < associationsAmount) {
				BildweltAssoziationPensee p = (BildweltAssoziationPensee) drawables.get(i);
				p.fadeOut(200);
				p.hideMe();
			} else {
				RibbonGroup rG = (RibbonGroup)drawables.get(i);
				rG.fadeOut(200);
				//  menu swarms
				rG.dieOut();
			}
		}
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
				p.fadeIn(time);
				
				// load new pensee
				float randomRadius = dimension.x*scale.x*.5f;
				p.loadNewImage(
						penseeImages[(int)e.p.random(penseeImages.length)],
						dimension.x*scale.x,
						new PVector(e.p.random(-randomRadius, randomRadius),
								e.p.random(-randomRadius, randomRadius),
								e.p.random(-randomRadius, randomRadius)),
						new PVector(0, 0, 0));
				p.resume();
				
			} else {
				RibbonGroup rG = (RibbonGroup)drawables.get(i);
				rG.fadeIn(time);
				//  menu swarms
				rG.makeAlive();
			}
		}
	}
	
	public void update() {
		super.update();
		for(Drawable d : drawables) d.update();
	}
	
	@Override
	public void draw() {
		// Lights
		// TODO: set that globally
		useLights();
		
		setAmbient(0.4f, 0.4f, 0.4f);
		
		float basicLightValueX = 586.0f - e.p.noise(e.p.frameCount*.005f)*250f;
		float basicLightValueY = 426.0f + e.p.noise(e.p.frameCount*.005f + 100)*150f;
		
		PVector basicLightPosition = new PVector(
				basicLightValueX,
				basicLightValueY,
				-1600 + e.p.noise(e.p.frameCount*.005f)*550f);
		
		setPointLight(0, basicLightPosition.x, basicLightPosition.y, basicLightPosition.z, 255, 255, 255, 0.5f, 0.0001f, 0.0f);

		setPointLight(1, basicLightPosition.x, basicLightPosition.y + 700, basicLightPosition.z + 450, 255, 255, 255, 0.5f, 0.0001f, 0.0f);

		setPointLight(2, basicLightPosition.x - 500, basicLightPosition.y + 100, basicLightPosition.z + 450, 255, 255, 255, 0.5f, 0.0001f, 0.01f);
		
		
		// load pensees now
		for(int i=0;i<associationsAmount;i++) {
			// draw associations
			BildweltAssoziationPensee p = (BildweltAssoziationPensee) drawables.get(i);
			if(p.isReady() && p.isAnimationDone() && p.isVisible()) {
				float randomRadius = dimension.x*scale.x*.5f;
				p.setPosition(.4f);
				p.loadNewImage(
						penseeImages[(int)e.p.random(penseeImages.length)],
						dimension.x*scale.x,
						new PVector(e.p.random(-randomRadius, randomRadius),
								e.p.random(-randomRadius, randomRadius),
								e.p.random(-randomRadius, randomRadius)),
						new PVector(0, 0, 0));
			}
			if(p.mode() == Drawable.OFF_SCREEN) {
				if(!p.isCleared()) p.clear();
			}
		}
		
		g.pushStyle();
		g.pushMatrix();
		
			g.translate(position.x, position.y, position.z);
			g.scale(scale.x, scale.y, scale.z);
			g.rotateY(gestureRotation);
			
			g.fill(255, fade*255);
			g.noStroke();
			
			int drawableIndex = 0;
			float penseeRotation = .0f;
			for(Drawable r : drawables) {
				
				// draw associations
				if(drawableIndex++ < associationsAmount) {
					g.pushMatrix();
					g.rotateY(penseeRotation);
					
					g.translate(0, PApplet.cos(e.p.frameCount*.03f + penseeRotation)*50f, 0);

					BildweltAssoziationPensee p = (BildweltAssoziationPensee)drawables.get(drawableIndex-1);
					
					p.draw();
					
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
