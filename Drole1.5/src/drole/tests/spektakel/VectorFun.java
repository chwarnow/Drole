package drole.tests.spektakel;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class VectorFun extends PApplet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Vec3D originalVector = new Vec3D(0,100,0);
	Vec3D newVector = new Vec3D(0,1,0);
	Vec3D rotateVector = new Vec3D(0,0,0);


	public static void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#000000",
				"drole.tests.spektakel.VectorFun" });
	}
	
	public void setup(){
		size(400,400,P3D);
	}
	
	public void draw(){
		
		background(200);
		
		rotateVector = new Vec3D(mouseX-width/2,mouseY-height/2,0);

		
		float angle = originalVector.angleBetween(rotateVector,true);
		println(angle);
		
		newVector =  originalVector.getRotatedAroundAxis(new Vec3D(1,1,0),angle);
		
		
		translate(width/2,height/2,0);
		stroke(0);
		line(0,0,0,originalVector.x,originalVector.y,originalVector.z);
		stroke(255,0,0);
		line(0,0,0,newVector.x,newVector.y,newVector.z);
		stroke(0,0,255);
		line(0,0,0,rotateVector.x,rotateVector.y,rotateVector.z);
		
		
	}
	
	
	
	
}
