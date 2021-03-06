package drole.tests.glg;

import codeanticode.glgraphics.GLGraphics;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public class Room {
	
	private PGraphicsOpenGL g;
	private PApplet parent;
	
	private float p = 1800;
	private float m = p/2f;
	
	// create cube edges
	// Left
	private PVector l1 	= new PVector(-m, -m, 0);
	private PVector l2 	= new PVector(-m, -m, -p);
	private PVector l3 	= new PVector(-m, m, -p);
	private PVector l4 	= new PVector(-m, m, 0);
	private PVector ln  = new PVector(-1, 0, 0);
	
	// Right
	private PVector r1 = new PVector(m, -m, 0);
	private PVector r2 = new PVector(m, -m, -p);
	private PVector r3 = new PVector(m, m, -p);
	private PVector r4 = new PVector(m, m, 0);
	private PVector rn = new PVector(1, 0, 0);

	// Top
	private PVector t1 = new PVector(-m, -m, 0);
	private PVector t2 = new PVector(m, -m, 0);
	private PVector t3 = new PVector(m, -m, -p);
	private PVector t4 = new PVector(-m, -m, -p);
	private PVector tn = new PVector(0, -1, 0);

	// Bottom
	private PVector b1 = new PVector(-m, m, 0);
	private PVector b2 = new PVector(m, m, 0);
	private PVector b3 = new PVector(m, m, -p);
	private PVector b4 = new PVector(-m, m, -p);
	private PVector bn = new PVector(0, 1, 0);

	// Back
	private PVector f1 = new PVector(-m, -m, -p);
	private PVector f2 = new PVector(m, -m, -p);
	private PVector f3 = new PVector(m, m, -p);
	private PVector f4 = new PVector(-m, m, -p);
	private PVector fn = new PVector(0, 0, -1);
	
	private PImage backTex, leftTex, rightTex, bottomTex, topTex;   // texture images
	
	public Room(PApplet parent, PGraphicsOpenGL offG, String fileBasename) {
		this.g = offG;
		this.parent = parent;
		loadSkybox(fileBasename, ".jpg");
	}
	
	// load six skybox images as cube texture
	void loadSkybox(String skyboxName, String fExt) {
		backTex 	= parent.loadImage(skyboxName + "back" + fExt);
		leftTex 	= parent.loadImage(skyboxName + "left" + fExt);
		rightTex 	= parent.loadImage(skyboxName + "right" + fExt);
		bottomTex 	= parent.loadImage(skyboxName + "bottom" + fExt);
		topTex 		= parent.loadImage(skyboxName + "top" + fExt);
	}
	
	// Assign six texture to the six cube faces
	private void TexturedCube() {
		TexturedCubeSide(l1, l2, l3, l4, ln, leftTex);
		TexturedCubeSide(r1, r2, r3, r4, rn, rightTex);
		TexturedCubeSide(t1, t2, t3, t4, tn, topTex);
		TexturedCubeSide(b1, b2, b3, b4, bn, bottomTex);
		TexturedCubeSide(f1, f2, f3, f4, fn, backTex);
	}
	
	// create a cube side given by 4 edge vertices and a texture
	private void TexturedCubeSide(PVector P1, PVector P2, PVector P3, PVector P4, PVector normal, PImage tex) {
		g.beginShape(GLGraphics.QUADS);
			g.texture(tex);
			g.normal(normal.x, normal.y, normal.z);
			g.vertex(P1.x, P1.y, P1.z, 0, 0);
			g.vertex(P2.x, P2.y, P2.z, 1, 0);
			g.vertex(P3.x, P3.y, P3.z, 1, 1);
			g.vertex(P4.x, P4.y, P4.z, 0, 1);
		g.endShape();
	}
	
	public void draw() {
		g.pushStyle();
		g.pushMatrix();
		
			g.noFill();
			g.noStroke();
			
			g.textureMode(PApplet.CORNERS);
			
			g.tint(255, 255);
			g.fill(255);
			g.noStroke();
			
			TexturedCube();
			
		g.popMatrix();
		g.popStyle();
	}
	
}
