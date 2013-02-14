package drole.engine.optik;

import processing.core.PApplet;

public class StdOptik extends Optik {

	public float cameraZ;
	
	public StdOptik(PApplet p) {
		super(p);
	}

	@Override
	public void calculate() {
		cameraZ = ((p.height/2.0f) / PApplet.tan(PApplet.PI*60.0f/360.0f));
	}

	@Override
	public void set() {
		p.resetMatrix();
		p.perspective(PApplet.PI/3.0f, p.width/p.height, cameraZ/10.0f, cameraZ*10.0f);
	}

}
