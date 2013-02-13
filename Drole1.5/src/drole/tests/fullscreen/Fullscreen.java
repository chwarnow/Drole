package drole.tests.fullscreen;

import processing.core.PApplet;

public class Fullscreen extends PApplet {

	private static final long serialVersionUID = 1L;

	public void setup() {
		size(1080, 1080, OPENGL);
	}
	
	public void draw() {
		background(200, 0, 0);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--present",
			"--bgcolor=#000000",
			"--present-stop-color=#000000", 
			"--display=1",
			"drole.tests.fullscreen.Fullscreen"
		});	
	}
	
}
