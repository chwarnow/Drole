package drole.gfx.assoziation;

import codeanticode.glgraphics.GLGraphics;
import drole.DroleMain;
import drole.engine.Drawable;

public class BildweltAssoziation extends Drawable {

	private BildweltAssoziationPensee penseeA, penseeB, penseeC;
	private float sphereConstraintRadius = 150.0f;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	public BildweltAssoziation(DroleMain parent) {
		super(parent);
		// van she idea of happiness
		scale(4.0f, 4.0f, 4.0f);
		position(0.0f, -900.0f, 200.0f);
		
		// init ribbon sculpture
		penseeA = new BildweltAssoziationPensee(parent, "data/images/associationA.png", sphereConstraintRadius);
		penseeB = new BildweltAssoziationPensee(parent, "data/images/associationB.png", sphereConstraintRadius);
		penseeC = new BildweltAssoziationPensee(parent, "data/images/associationC.png", sphereConstraintRadius);
		
		penseeB.positionSteps = 33;
		penseeC.positionSteps = 66;
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		// update sculpture
		penseeA.update();
		penseeB.update();
		penseeC.update();
	}

	@Override
	public void draw() {
		
		parent.g.pushStyle();
		parent.g.pushMatrix();

		parent.g.translate(position.x, position.y, position.z);
		parent.g.scale(scale.x, scale.y, scale.z);
		parent.g.rotateY(smoothedRotation+parent.HALF_PI/2);
		
		// draw sculpture
		GLGraphics renderer = (GLGraphics)parent.g;
		renderer.beginGL();
		penseeA.draw(renderer);
		penseeB.draw(renderer);
		penseeC.draw(renderer);
		renderer.endGL();
		
		parent.g.noFill();
		parent.g.stroke(155, 100);
		parent.g.ellipse(0, 0, 300, 300);
		
		parent.g.pushMatrix();
		parent.g.rotateY(parent.HALF_PI + parent.HALF_PI/2);
		parent.g.ellipse(0, 0, 300, 300);
		parent.g.popMatrix();
		
		parent.g.pushMatrix();
		parent.g.rotateY(parent.HALF_PI - parent.HALF_PI/2);
		parent.g.ellipse(0, 0, 300, 300);
		parent.g.popMatrix();
		
		parent.g.pushMatrix();
		parent.g.rotateY(parent.HALF_PI);
		parent.g.ellipse(0, 0, 300, 300);
		parent.g.popMatrix();
		
		parent.g.popMatrix();
		parent.g.popStyle();
		
	}

}
