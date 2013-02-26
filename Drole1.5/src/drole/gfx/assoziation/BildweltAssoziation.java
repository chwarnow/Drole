package drole.gfx.assoziation;


import processing.core.PVector;
import processing.core.PApplet;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import codeanticode.glgraphics.GLGraphics;
import drole.Main;

public class BildweltAssoziation extends Drawable {

	private BildweltAssoziationPensee penseeA, penseeB, penseeC;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	private int activePensee					= 0;
	public BildweltAssoziation(Engine e, PVector position, PVector dimension) {
		super(e);
		position(position);
		dimension(dimension);
		
		// init ribbon sculpture
		
		penseeA = new BildweltAssoziationPensee(e, "data/images/menuAssoziationA.png", dimension.x*scale.x, 3.0f, new PVector(0, 0, 0), new PVector(0, 0, 0));
		penseeA.loadPensee();
		penseeA.setLooping(false);
		// penseeA.setPosition(.5f);
		/*
		penseeB = new BildweltAssoziationPensee(e, "data/images/associationB.png", sphereConstraintRadius, 1.0f, new PVector(0, 0, 0), new PVector(0, 0, 0));
		penseeC = new BildweltAssoziationPensee(e, "data/images/associationC.png", sphereConstraintRadius, 1.0f, new PVector(0, 0, 0), new PVector(0, 0, 0));
		
		penseeB.positionSteps = 33;
		penseeC.positionSteps = 66;
		*/
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		// update sculpture
		penseeA.update();
		// penseeB.update();
		// penseeC.update();
	}

	@Override
	public void draw() {
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z + dimension.x*scale.x*.5f);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateY(smoothedRotation);
		
		// draw sculpture
		penseeA.draw();
		// penseeB.draw(g);
		// penseeC.draw(g);
		
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
		
		g.popMatrix();
		g.popStyle();
		
	}

}
