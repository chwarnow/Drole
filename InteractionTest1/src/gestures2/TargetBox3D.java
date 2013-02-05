package gestures2;

import processing.core.PGraphics;
import processing.core.PVector;

public class TargetBox3D extends TargetShape {
	
	private PVector point;
	
	public TargetBox3D(float x, float y, float z, float w, float h, float d) {
		offset 		= new PVector(x, y, z);
		position 	= new PVector(x, y, z);
		dimension 	= new PVector(w, h, d);
		
		point = position.get();
	}
	
	public boolean contains(PVector point) {
		this.point = point.get();
		
		System.out.println("Checking "+point);
		
		//Check if the point is less than max and greater than min
	    return point.x > position.x && point.x < (position.x+dimension.x) &&
	    	   point.y > position.y && point.y < (position.y+dimension.y) &&
	    	   point.z > position.z && point.z < (position.z+dimension.z);
	}

	@Override
	public void draw(PGraphics g) {
		g.pushStyle();
		g.pushMatrix();
			g.noFill();
			g.stroke(200, 200, 0);
			if(contains(point)) g.fill(200, 200, 0);
			
			g.translate(position.x, position.y, position.z);
			g.box(dimension.x, dimension.y, dimension.z);
		g.popMatrix();
		g.popStyle();
		
		g.pushStyle();
		g.pushMatrix();
			g.fill(200, 200, 0);
			
			g.translate(point.x, point.y, point.z);
			g.sphere(10);
		g.popMatrix();
		g.popStyle();		
	}
	
}
