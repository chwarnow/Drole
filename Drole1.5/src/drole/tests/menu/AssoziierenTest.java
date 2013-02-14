package drole.tests.menu;

import codeanticode.glgraphics.GLConstants;
import drole.gfx.assoziation.BildweltAssoziationPensee;
import processing.core.PApplet;

/**
 * 
 * show assoziieren ribbons
 * 
 * @author brainsteen
 *
 */

public class AssoziierenTest extends PApplet {

	BildweltAssoziationPensee penseeA, penseeB, penseeC, penseeD;
	float sphereConstraintRadius = 160.0f;
	
	public void setup() {
		size(1200, 720, GLConstants.GLGRAPHICS);
	}
	
	public void draw() {
		background(255);
		
		penseeA = new BildweltAssoziationPensee(this, "Exp_Spektakel_7.JPG", sphereConstraintRadius);
		
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.AssoziierenTest"
		});
	}
	
}
