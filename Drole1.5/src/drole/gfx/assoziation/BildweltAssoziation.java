package drole.gfx.assoziation;

import codeanticode.glgraphics.GLGraphics;
import drole.DroleMain;
import drole.engine.Drawable;

public class BildweltAssoziation extends Drawable {

	private BildweltAssoziationPensee penseeA;
	private float sphereConstraintRadius = 150.0f;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	public BildweltAssoziation(DroleMain parent) {
		super(parent);
		
		scale(4.0f, 4.0f, 4.0f);
		position(0.0f, -900.0f, 0.0f);
		
		// init ribbon sculpture
		penseeA = new BildweltAssoziationPensee(parent, "data/images/contentB.png", sphereConstraintRadius);
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		// update sculpture
		penseeA.update();
	}

	@Override
	public void draw() {
		
		parent.g.pushStyle();
		parent.g.pushMatrix();

		parent.g.translate(position.x, position.y, position.z);
		parent.g.scale(scale.x, scale.y, scale.z);
		parent.g.rotateY(smoothedRotation);
		
		// draw sculpture
		GLGraphics renderer = (GLGraphics)parent.g;
		renderer.beginGL();
		penseeA.draw(renderer);
		renderer.endGL();
		
		parent.g.popMatrix();
		parent.g.popStyle();
		
	}

}
