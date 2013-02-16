package drole.tests.glg;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import processing.core.PApplet;

public class Matrix extends PApplet {

	private static final long serialVersionUID = 1L;
	
	public void setup() {
		size(720, 720, GLConstants.GLGRAPHICS);
	}
	
	public void draw() {
		GLGraphics gl = (GLGraphics)g;
		gl.beginGL();
		
		gl.resetMatrix();
		gl.translate(gl.width/2, gl.height/2, 0);
		
		gl.background(0);
		
		gl.noStroke();
		
		gl.fill(200);
		gl.rectMode(PApplet.CENTER);
		gl.rect(0, 0, width, height);
		
		gl.fill(200, 200, 0);
		gl.ellipse(0, 0, 20, 20);
		
		gl.endGL();
	}

}
