package drole.tests.skybox;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * SkyboxViewer v1.0   2010-08-02   by Ing. Gerd Platl.
 *                     tested with processing 1.2.1
 *
 * Demonstrates use of u/v coords in vertex() and effect on texture().
 * The textures get distorted using the P3D renderer, but they look great using OPENGL.
 *
 * Please read a detailed description about how skyboxes works at Friedrich A.Lohmuellers page
 *   http://www.f-lohmueller.de/pov_tut/backgrnd/p_sky9.htm
 *
 * How to view skyboxes:
 * - download a skybox examples (6 texture images) e.g. from
 *     http://www.f-lohmueller.de/pov_tut/skyboxer/skyboxer_3.htm
 * - create a SkyboxViewer/data subdirectory for this project
 * - copy texture images to the SkyboxViewer/data subdirectory
 * - set the skybox name in this project:  see 'skyboxName = "<filename>";'  
 * - start this SkyboxViewer and look around...
 *
 *   mouse input:
 *      left mouse button   rotate skybox
 *     right mouse button   zoom skybox
 *
 *   key input:         r   reset
 *             left/right   auto scroll to left/right
 *                up/down   scroll up/down
 *                  blanc   stop scrolling
 *                  + / -   zoom in/out
 *                    esc   quit            
 */

public class SkyBoxViewer extends PApplet {

	private static final long serialVersionUID = 1L;
	
	float rotx = 0.0f;   // rotation about x-axis
	float roty = 0.0f;   // rotation about y-axis
	float rotd = 0.0f;   // rotation delta x
	float maxd = 0.01f;  // maximum rotation delta x
	float fov  = 1.8f;
	
	// vertical field-of-view angle (in radians)
	// set skybox filename without orientation part here...
	
	String skyboxName = "data/room/test/PalldioPalace_extern";  
	//String skyboxName = "Skybox_Water222";  
	
	float p = 40000;   // half skybox size
	float m = -p;
	// create cube edges
	PVector P000 = new PVector (m,m,m);
	PVector P010 = new PVector (m,p,m);
	PVector P110 = new PVector (p,p,m);
	PVector P100 = new PVector (p,m,m);
	PVector P001 = new PVector (m,m,p);
	PVector P011 = new PVector (m,p,p);
	PVector P111 = new PVector (p,p,p);
	PVector P101 = new PVector (p,m,p);
	PImage tex1,tex2,tex3,tex4,tex5,tex6;   // texture images
	
	
	public void setup() {
	  size(800, 600, OPENGL);
	//  size(screenWidth, screenHeight, OPENGL);  // fullscreen
	  frameRate(60);
	  loadSkybox(skyboxName, ".jpg");
	}
	
	public void draw() {
	  background(0);
	  noStroke();  // comment it to see cube edges
	  if(fov < 0.1f) fov = 0.1f;
	  if(fov > 3.0f) fov = 3.0f;
	  
	  perspective(fov, (float)width/height, 1, 100000);
	  translate(width/2.0f, height/2.0f, -100);
	  
	  if (rotd > maxd) rotd = maxd;
	  else if (rotd < -maxd) rotd = -maxd;
	  
	  roty += rotd;
	  rotateX(rotx);
	  rotateY(roty);
	  TexturedCube();
	}
	
	// load six skybox images as cube texture
	void loadSkybox(String skyboxName, String fExt) {
	  tex1 = loadImage(skyboxName + "_front" + fExt);
	  tex2 = loadImage(skyboxName + "_back" + fExt);
	  tex3 = loadImage(skyboxName + "_left" + fExt);
	  tex4 = loadImage(skyboxName + "_right" + fExt);
	  tex5 = loadImage(skyboxName + "_base" + fExt);
	  tex6 = loadImage(skyboxName + "_top" + fExt);
	  textureMode(NORMALIZED);
	}
	// Assign six texture to the six cube faces
	void TexturedCube() {
	  TexturedCubeSide (P100, P000, P010, P110, tex1);   // -Z "front" face
	  TexturedCubeSide (P001, P101, P111, P011, tex2);   // +Z "back" face
	  TexturedCubeSide (P000, P001, P011, P010, tex3);   // -X "left" face
	  TexturedCubeSide (P101, P100, P110, P111, tex4);   // +X "right" face
	  TexturedCubeSide (P110, P010, P011, P111, tex5);   // +Y "base" face
	  TexturedCubeSide (P101, P001, P000, P100, tex6);   // -Y "top" face
	}
	// create a cube side given by 4 edge vertices and a texture
	void TexturedCubeSide(PVector P1, PVector P2, PVector P3, PVector P4, PImage tex) {
	  beginShape(QUADS);
	    texture(tex);
	    vertex (P1.x, P1.y, P1.z, 1, 0);
	    vertex (P2.x, P2.y, P2.z, 0, 0);
	    vertex (P3.x, P3.y, P3.z, 0, 1);
	    vertex (P4.x, P4.y, P4.z, 1, 1);
	  endShape();
	}
	// handle mouse input
	public void mouseDragged() {
	  if (mouseButton == LEFT) {
		  rotx += (pmouseY-mouseY) * 0.01;     // LEFT
		  roty += (mouseX-pmouseX) * 0.01;
	  }
	  else fov -= (pmouseY-mouseY) * 0.005;  // RIGHT
	}
	// handle key input
	public void keyPressed() {
	  println (keyCode);
	  if (keyCode ==    82) { rotx = 0.0f;   roty = 0.0f;   rotd = 0.0f; }   // r: Reset
	  else if (keyCode ==  LEFT) rotd += -0.001f; // auto scroll to left
	  else if (keyCode == RIGHT) rotd += 0.001f;  // auto scroll to right
	  else if (keyCode ==    32) rotd = 0.0f;     // space: stop scrolling
	  else if (keyCode ==    UP) rotx += 0.01f;   // scroll up
	  else if (keyCode ==  DOWN) rotx -= 0.01f;   // scroll down
	  else if (keyCode ==   107) fov /= 1.01f;    // +  zoom in
	  else if (keyCode ==   109) fov *= 1.01f;    // -  zoom out
	  else if (keyCode ==    76) loadSkybox("another_Landscape", ".png");  // L: load
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.skybox.SkyBoxViewer"
		});
	}
	
}
