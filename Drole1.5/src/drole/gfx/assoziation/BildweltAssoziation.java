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
			pensee.loadPensee();
			pensee.setLooping(false);
			pensee.stop();
			pensee.setDelayTime(0);
			pensee.setPosition(.5f);
			pensees[i] = pensee;
		}
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		if(e.p.frameCount%200 == 0) {
			int nextPenseeID = activePensee;
			if(nextPenseeID == pensees.length-1) nextPenseeID = 0;
			else nextPenseeID++;
			setPensee(nextPenseeID);
		}
		
		// update pensees
		for(BildweltAssoziationPensee pensee:pensees) pensee.update();

	}
	
	/**
	 * hide the current visible pensee and opens the new one, depending on the given id
	 * @param activeID
	 */
	private void setPensee(int activeID) {
		pensees[activePensee].hide();
		activePensee = activeID;
		pensees[activePensee].show();
	}
	
	@Override
	public void fadeIn(float time) {
		super.fadeIn(time);
		pensees[activePensee].show();
	}
	
	@Override
	public void fadeOut(float time) {
		super.fadeOut(time);
		pensees[activePensee].hide();
	}

	@Override
	public void draw() {
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z + dimension.x*scale.x*.5f);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateY(smoothedRotation);
		
		// draw sculpture
		for(BildweltAssoziationPensee pensee:pensees) if(pensee.isVisible()) pensee.draw();
		
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
