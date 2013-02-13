package drole.gfx.room;

import drole.Drawable;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Room extends Drawable {
	
	private float p = 40000;   // half skybox size
	private float m = -p;
	// create cube edges
	private PVector P000 = new PVector (m,m,m);
	private PVector P010 = new PVector (m,p,m);
	private PVector P110 = new PVector (p,p,m);
	private PVector P100 = new PVector (p,m,m);
	private PVector P001 = new PVector (m,m,p);
	private PVector P011 = new PVector (m,p,p);
	private PVector P111 = new PVector (p,p,p);
	private PVector P101 = new PVector (p,m,p);
	private PImage tex1,tex2,tex3,tex4,tex5,tex6;   // texture images
	
	public Room(PApplet parent, String fileBasename) {
		super(parent);
		
		loadSkybox(fileBasename, ".jpg");
	}
	
	// load six skybox images as cube texture
	void loadSkybox(String skyboxName, String fExt) {
		tex1 = parent.loadImage(skyboxName + "_front" + fExt);
		tex2 = parent.loadImage(skyboxName + "_back" + fExt);
		tex3 = parent.loadImage(skyboxName + "_left" + fExt);
		tex4 = parent.loadImage(skyboxName + "_right" + fExt);
		tex5 = parent.loadImage(skyboxName + "_base" + fExt);
		tex6 = parent.loadImage(skyboxName + "_top" + fExt);
	}
	
	// Assign six texture to the six cube faces
	private void TexturedCube() {
		TexturedCubeSide(P100, P000, P010, P110, tex1);   // -Z "front" face
		TexturedCubeSide(P001, P101, P111, P011, tex2);   // +Z "back" face
		TexturedCubeSide(P000, P001, P011, P010, tex3);   // -X "left" face
		TexturedCubeSide(P101, P100, P110, P111, tex4);   // +X "right" face
		TexturedCubeSide(P110, P010, P011, P111, tex5);   // +Y "base" face
		TexturedCubeSide(P101, P001, P000, P100, tex6);   // -Y "top" face
	}
	
	// create a cube side given by 4 edge vertices and a texture
	private void TexturedCubeSide(PVector P1, PVector P2, PVector P3, PVector P4, PImage tex) {
		parent.beginShape(PGraphics.QUADS);
			parent.texture(tex);
			parent.vertex(P1.x, P1.y, P1.z, 1, 0);
			parent.vertex(P2.x, P2.y, P2.z, 0, 0);
			parent.vertex(P3.x, P3.y, P3.z, 0, 1);
			parent.vertex(P4.x, P4.y, P4.z, 1, 1);
		parent.endShape();
	}
	
	@Override
	public void draw() {
		parent.pushStyle();
		parent.pushMatrix();
			
			parent.translate(position.x, position.y, position.z);
			
			TexturedCube();
		
		parent.popMatrix();
		parent.popStyle();
	}
	
}
