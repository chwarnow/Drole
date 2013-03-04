package drole.gfx.assoziation;


import processing.core.PVector;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

public class BildweltAssoziation extends Drawable {

	private String[] imagePaths = {"data/images/menuAssoziationA.png", "data/images/menuAssoziationB.png", "data/images/menuAssoziationC.png", "data/images/menuAssoziationD.png", "data/images/menuAssoziationE.png"};
	private BildweltAssoziationPensee[] pensees;
	
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
		
		// set active slide
		int newSliceID = (int) (gestureScaling * (pensees.length-1));
		e.p.pinLog("New Slice ID : ", newSliceID);
		if(newSliceID != activePensee) setPensee(newSliceID);
		
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
	
	// TODO: in update function unload pensee when not visible

	@Override
	public void draw() {
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z + dimension.x*scale.x*.5f);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateY(gestureRotation);
		
		// draw sculpture
		for(BildweltAssoziationPensee pensee:pensees) {
			// clear pensee data when being offline
			if(fade == 0 && pensee.mode() == Drawable.FADING_OUT) {
				if(!pensee.isCleared()) pensee.clear();
				System.out.println("clear pensee");
			}
			if(pensee.isVisible()) pensee.draw();
		}
		
		/*
		g.noFill();
		g.stroke(155, 100);
		g.ellipse(0, 0, dimension.x*scale.x*2, dimension.x*scale.x*2);
		
		g.pushMatrix();
		g.rotateY(PApplet.HALF_PI + PApplet.HALF_PI/2);
		g.ellipse(0, 0, dimension.x*scale.x*2, dimension.x*scale.x*2);
		g.popMatrix();
		
		g.pushMatrix();
		g.rotateY(PApplet.HALF_PI - PApplet.HALF_PI/2);
		g.ellipse(0, 0, dimension.x*scale.x*2, dimension.x*scale.x*2);
		g.popMatrix();
		
		g.pushMatrix();
		g.rotateY(PApplet.HALF_PI);
		g.ellipse(0, 0, dimension.x*scale.x*2, dimension.x*scale.x*2);
		g.popMatrix();
		*/
		g.popMatrix();
		g.popStyle();
		
	}

}
