package drole.gfx.assoziation;


import processing.core.PVector;
import processing.core.PApplet;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import codeanticode.glgraphics.GLGraphics;
import drole.Main;

public class BildweltAssoziation extends Drawable {

	private BildweltAssoziationPensee penseeA, penseeB, penseeC;
	private float sphereConstraintRadius = 150.0f;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	public BildweltAssoziation(Engine e) {
		super(e);
		// van she idea of happiness
		scale(4.0f, 4.0f, 4.0f);
		position(0.0f, -900.0f, 0.0f);
		
		// init ribbon sculpture
		/*
		penseeA = new BildweltAssoziationPensee(e, "data/images/associationA.png", sphereConstraintRadius, 1.0f, new PVector(0, 0, 0), new PVector(0, 0, 0));
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
		// penseeA.update();
		// penseeB.update();
		// penseeC.update();
	}

	@Override
	public void draw() {
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateY(smoothedRotation+PApplet.HALF_PI/2);
		
		// draw sculpture
		// penseeA.draw(g);
		// penseeB.draw(g);
		// penseeC.draw(g);
		
		g.noFill();
		g.stroke(155, 100);
		g.ellipse(0, 0, 300, 300);
		
		g.pushMatrix();
		g.rotateY(PApplet.HALF_PI + PApplet.HALF_PI/2);
		g.ellipse(0, 0, 300, 300);
		g.popMatrix();
		
		g.pushMatrix();
		g.rotateY(PApplet.HALF_PI - PApplet.HALF_PI/2);
		g.ellipse(0, 0, 300, 300);
		g.popMatrix();
		
		g.pushMatrix();
		g.rotateY(PApplet.HALF_PI);
		g.ellipse(0, 0, 300, 300);
		g.popMatrix();
		
		g.popMatrix();
		g.popStyle();
		
	}

}
