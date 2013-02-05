package gestures;

import processing.core.PGraphics;
import processing.core.PVector;

public class TargetSphere extends TargetShape {
	
	public TargetSphere(float x, float y, float z, float r) {
		offset 		= new PVector(x, y, z);
		position 	= new PVector(x, y, z);
		dimension 	= new PVector(r, r, r);
	}
	
	public boolean contains(PVector point) {
		System.out.println("testing: "+point.dist(position)+" against "+dimension.x);
		
		return point.dist(position) < dimension.x;
		
		/*
		return (point.x >= position.x && point.x <= (position.x+dimension.x)) &&
			   (point.y >= position.y && point.y <= (position.y+dimension.y)) &&
			   (point.z >= position.z && point.z <= (position.z+dimension.z));
		*/
	}

	@Override
	public void draw(PGraphics g) {
		g.pushMatrix();
			g.translate(position.x, position.y, position.z);
			g.sphere(dimension.x);
		g.popMatrix();
	}
	
}
