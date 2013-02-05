package offcenterprojection;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

public class Main2 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	private SimpleOpenNI  context; 
	private boolean       autoCalib=true;  

	private float fx,fy,fz=0;

	public void setup() { 
		size(800, 800, OPENGL);

		context = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
	} 

	public void keyPressed() {
		if(key == 'k') {
			System.out.println("Enable User Tracking");
			context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

			System.out.println("Started");
		}
	}
	
	public void draw() { 

	  background(0); 
	}
	
}
