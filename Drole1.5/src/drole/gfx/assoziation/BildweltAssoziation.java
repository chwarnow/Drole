package drole.gfx.assoziation;


import processing.core.PVector;
import processing.core.PApplet;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import codeanticode.glgraphics.GLGraphics;
import drole.Main;

public class BildweltAssoziation extends Drawable {

	private String[] imagePaths = {"data/images/menuAssoziationA.png", "data/images/menuAssoziationB.png", "data/images/menuAssoziationC.png", "data/images/menuAssoziationD.png", "data/images/menuAssoziationE.png"};
	private BildweltAssoziationPensee[] pensees;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	private int activePensee					= 0;
	
	public BildweltAssoziation(Engine e, PVector position, PVector dimension) {
		super(e);
		position(position);
		dimension(dimension);
		
		// init ribbon sculpture
		pensees = new BildweltAssoziationPensee[imagePaths.length];
		for(int i=0;i<imagePaths.length;i++) {
			BildweltAssoziationPensee pensee = new BildweltAssoziationPensee(e, imagePaths[i], dimension.x*scale.x, 3.0f, new PVector(0, 0, 0), new PVector(0, 0, 0));
			// pensee.loadPensee();
			pensee.setLooping(false);
			pensee.stop();
			pensee.setDelayTime(0);
			pensee.setPosition(.5f);
			pensees[i] = pensee;
		}
		
		setPensee(activePensee);
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		// set active slide
		if(!Float.isNaN(gestureScaling)) {
			int newSliceID = (int) (gestureScaling * (pensees.length-1));
			if(newSliceID != activePensee) setPensee(newSliceID);
		}
		// update pensees
		for(BildweltAssoziationPensee pensee:pensees) {
			// update pensee when being on screen
			pensee.update();
		}

	}
	
	/**
	 * hide the current visible pensee and opens the new one, depending on the given id
	 * @param activeID
	 */
	private void setPensee(int activeID) {
		// constrain id to pensee array length
		activeID = e.p.constrain(activeID, 0, pensees.length-1);
		pensees[activePensee].hideMe();
		activePensee = activeID;
		pensees[activePensee].showMe();
	}
	
	@Override
	public void fadeIn(float time) {
		super.fadeIn(time);
		// load pensees
		int penseeIndex = 0;
		for(BildweltAssoziationPensee p:pensees) {
			p.fadeIn(time);
			if(!p.isReady()) {
				p.loadNewImage(
					imagePaths[penseeIndex++], dimension.x*scale.x, new PVector(0, 0, 0), new PVector(0, 0, 0)
				);
			}
		}
		pensees[activePensee].showMe();
		
	}
	
	@Override
	public void fadeOut(float time) {
		super.fadeOut(time);
		pensees[activePensee].hideMe();
		for(BildweltAssoziationPensee p:pensees) {
			p.fadeOut(time);
		}
	}

	@Override
	public void draw() {
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z + dimension.x*scale.x*.5f);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateY(smoothedRotation);
		
		// draw sculpture
		for(BildweltAssoziationPensee pensee:pensees) {
			// clear pensee data when being offline
			if(fade == 0 && pensee.mode() == pensee.FADING_OUT) {
				if(!pensee.isCleared()) pensee.clear();
				System.out.println("clear pensee");
			}
			if(pensee.isVisible()) pensee.draw();
		}

		g.popMatrix();
		g.popStyle();
		
	}

}
