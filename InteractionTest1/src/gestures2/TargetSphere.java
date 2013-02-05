package gestures2;

import processing.core.PGraphics;
import processing.core.PVector;

public class TargetSphere extends TargetShape {
	
	private PVector point;
	
	public TargetSphere(float x, float y, float z, float r) {
		offset 		= new PVector(x, y, z);
		position 	= new PVector(x, y, z);
		dimension 	= new PVector(r, r, r);
	}
	
	public boolean contains(PVector point) {
		this.point = point;
		
		return point.dist(position) < dimension.x;
	}

	@Override
	public void draw(PGraphics g) {
		g.pushMatrix();
			if(point != null) if(contains(point)) g.fill(0, 200, 0);
			g.translate(position.x, position.y, position.z);
			g.sphere(dimension.x);
		g.popMatrix();
	}
	
}
