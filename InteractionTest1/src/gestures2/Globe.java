package gestures2;

/**
 * 
 * Initial Class by Denny Koch
 * 
 * particles that float around an invisible sphere
 * using toxiclibs verlet physics
 * being the menu (Christopher Warnow)
 * 
 */


import com.christopherwarnow.bildwelten.DroleWelt;
import processing.core.PApplet;

public class Globe extends Drawable {

	public float rotation 		= 0;
	public float rotationSpeed = 0.04f;


	// ------ drole particles on sphere ------
	private int droleAmount = 5;
	private int drolesPerWelt = 50;
	private DroleWelt[] droles;
	private DroleWelt droleA, droleB;

	public Globe(PApplet parent) {
		super(parent);

		// generate drole swarms
		droles = new DroleWelt[droleAmount];
		for(int i=0;i<droleAmount;i++) {
			droles[i] = new DroleWelt(parent, drolesPerWelt, dimension.x);
		}
	}

	@Override
	public void update() {
		super.update();
		// rotation += rotationSpeed;

		for(DroleWelt droleWelt:droles) {
			droleWelt.update();
		}
	}

	@Override
	public void draw() {
		parent.g.pushStyle();
		parent.g.lights();

		parent.g.tint(255, PApplet.map(fade, 0, 1, 0, 255));	

		parent.g.pushMatrix();
		// position, scale, rotation and dimension must be respected!
		parent.g.translate(position.x, position.y+PApplet.map(fade, 0, 1, 0, 0), position.z);
		parent.g.scale(scale.x, scale.y, scale.z);
		parent.g.rotateY(rotation);

		/* ACTUAL APPEARANCE OF THE OBJECT */
		parent.g.pointLight(255, 255, 255, position.x+500, position.y+1000, position.z+500);
		parent.g.pointLight(255, 255, 255, position.x-500, position.y-1000, position.z+200);

		parent.g.noStroke();
		parent.g.fill(200);

		// dimension must be respected!
		// parent.g.sphere(dimension.x*.5f);

		parent.g.stroke(120);
		parent.g.noFill();

		// parent.g.sphere(dimension.x+2);
		
		/* END APPEARANCE */

		// draw the droles

		for(DroleWelt droleWelt:droles) {
			droleWelt.draw();
		}

		parent.g.popMatrix();

		parent.g.noLights();

		parent.g.popStyle();
	}
}
