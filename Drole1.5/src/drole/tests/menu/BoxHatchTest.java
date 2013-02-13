package drole.tests.menu;

import javax.media.opengl.GL;

import com.christopherwarnow.bildwelten.HatchingFabric;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;
import processing.core.PVector;

public class BoxHatchTest extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PVector[] face = new PVector[3];
	float ai = TWO_PI/3;//angle increment
	float r  = 300;//overall radius
	float ro = 150;//random offset
	
	PFont font;
	GLTexture optikAkteur;
	
	HatchingFabric fabric;
	
	public void setup() {
		size(1200, 720, GLConstants.GLGRAPHICS);
		
		font = loadFont("data/fonts/HoeflerText-Regular-48.vlw");
		textFont(font);
		
		optikAkteur = new GLTexture(this, "data/images/optikAkteur.png");
		
		fabric = new HatchingFabric(this, "data/images/optikFahne.png", 10, 20, 1.0f, .13f);
	}

	public void draw() {
		background(255);

		//lights();
		float rectSize = 500;

		stroke(105, 90, 97);
		fill(199, 186, 177);

		pushMatrix();
		translate(width/2, height/2+200);
		rotateY(radians(mouseX));
		rotateZ(.1f + radians(-mouseY)*.05f);
		
		//floor
		pushMatrix();		
		box(rectSize, 10, rectSize*0.6181f); // golden ratio
		popMatrix();

		// wall
		pushMatrix();
		// translate(-rectSize*(0.6181f*.33f), -rectSize*(0.6181f*.25f)-5, 0);
		// box(10, rectSize*(0.6181f*.5f), rectSize*0.6181f); // golden ratio
		popMatrix();

		// rays
		
		// wall coords
		PVector pointI = new PVector(-rectSize*(0.6181f*.35f), -rectSize*(0.6181f*.5f)-5, -rectSize*.31f);
		PVector pointK = new PVector(pointI.x, pointI.y, rectSize*.31f);
		PVector pointM = new PVector(pointI.x, -5, -rectSize*.31f);
		PVector pointL = new PVector(pointK.x, -5, rectSize*.31f);
		
		// define wall plane for intersection test
		face[0] = pointI;
		face[1] = pointK;
		face[2] = pointL;
		
		// head
		PVector pointG = new PVector(rectSize*(0.6181f*.7f), -rectSize*(0.6181f*.4f)-5);
		// feet
		PVector pointH = new PVector(pointG.x, -5, pointG.z);
		
		// generic triangle a
		PVector pointE = new PVector(-rectSize*(.6181f*.66f) + cos(frameCount*.005f)*30, -5, 0 + sin(frameCount*.0025f)*30);
		// generic triangle b
		PVector pointF = new PVector(-rectSize*(.6181f*.75f) + cos(frameCount*.01f)*10, -5, rectSize*.28f + sin(frameCount*.01f)*10);
		// generic triangle c
		PVector pointR = new PVector(-rectSize*(.6181f*.5f) + cos(frameCount*.001f)*30, -5, rectSize*.1f + sin(frameCount*.005f)*30);

		// intersection g-f
		PVector pointO = linePlaneIntersection(new Ray(pointG, pointF), face);
		// intersection g-e
		PVector pointN = linePlaneIntersection(new Ray(pointG, pointE), face);
		// intersection g-r
		PVector pointS = linePlaneIntersection(new Ray(pointG, pointR), face);
		
		// g-e
		beginShape();
		vertex(pointG.x, pointG.y, pointG.z);
		vertex(pointE.x, pointE.y, pointE.z);
		endShape();

		// g-f
		beginShape();
		vertex(pointG.x, pointG.y, pointG.z);
		vertex(pointF.x, pointF.y, pointF.z);
		endShape();

		// g-r
		beginShape();
		vertex(pointG.x, pointG.y, pointG.z);
		vertex(pointR.x, pointR.y, pointR.z);
		endShape();
		
		// triangle on floor
		beginShape();
		vertex(pointF.x, pointF.y, pointF.z);
		vertex(pointE.x, pointE.y, pointE.z);
		vertex(pointR.x, pointR.y, pointR.z);
		vertex(pointF.x, pointF.y, pointF.z);
		endShape();
		
		// triangle on wall
		beginShape();
		vertex(pointO.x, pointO.y, pointO.z);
		vertex(pointN.x, pointN.y, pointN.z);
		vertex(pointS.x, pointS.y, pointS.z);
		vertex(pointO.x, pointO.y, pointO.z);
		endShape();

		// intersection example
		
		pushStyle();
		
		strokeWeight(5);
		point(pointO.x,pointO.y,pointO.z);//point of ray-plane intersection
		point(pointN.x,pointN.y,pointN.z);//point of ray-plane intersection
		point(pointS.x,pointS.y,pointS.z);//point of ray-plane intersection
		
		popStyle();

		// draw wall plane
		// wall
		pushMatrix();
		fill(0, 20);
		beginShape();
		vertex(pointI.x+1, pointI.y, pointI.z);
		vertex(pointK.x+1, pointK.y, pointK.z);
		vertex(pointL.x+1, pointL.y, pointL.z);
		vertex(pointM.x+1, pointM.y, pointM.z);
		vertex(pointI.x+1, pointI.y, pointI.z);
		endShape();
		popMatrix();
		
		
		// texts
		GLGraphics renderer = (GLGraphics)g;
		renderer.gl.glActiveTexture(GL.GL_TEXTURE0);
		
		float textOffset = 13;
		noStroke();
		fill(0);
		textSize(12);
		
		pushMatrix();
		translate(pointI.x-textOffset, pointI.y, pointI.z);
		text("I", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointK.x-textOffset, pointK.y, pointK.z);
		text("K", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointL.x-textOffset, pointL.y, pointL.z);
		text("L", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointM.x-textOffset, pointM.y, pointM.z);
		text("M", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointG.x-textOffset, pointG.y, pointG.z);
		text("G", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointH.x-textOffset, pointH.y, pointH.z);
		text("H", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointO.x-textOffset, pointO.y, pointO.z);
		text("O", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointN.x-textOffset, pointN.y, pointN.z);
		text("N", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointS.x-textOffset, pointS.y, pointS.z);
		text("S", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointF.x, pointF.y, pointF.z);
		text("F", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointE.x, pointE.y, pointE.z);
		text("E", 0, 0);
		popMatrix();
		
		pushMatrix();
		translate(pointR.x, pointR.y, pointR.z);
		text("R", 0, 0);
		popMatrix();
		
		// sehender akteur
		
		pushMatrix();
		translate(pointG.x-40, pointG.y-12, pointG.z);
		scale(.455f);
		image(optikAkteur, 0, 0);
		popMatrix();
		
		// fabric
		pushMatrix();
		translate(rectSize*.5f, 0, 0);
		fabric.draw();

		popMatrix();
		
		popMatrix();
	}
	
	/**
	 * intersection util
	 * http://paulbourke.net/geometry/planeline/
	 * line to plane intersection u = N dot ( P3 - P1 ) / N dot (P2 - P1), P = P1 + u (P2-P1), where P1,P2 are on the line and P3 is a point on the plane
	 * 
	 * @param r
	 * @param face
	 * @return
	 */
	private PVector linePlaneIntersection(Ray r, PVector[] face) {
		// face normal
		PVector c = new PVector();//centroid
		for(PVector p : face) c.add(p);
		c.div(3.0f);
		PVector cb = PVector.sub(face[2],face[1]);
		PVector ab = PVector.sub(face[0],face[1]);
		PVector n = cb.cross(ab);//compute normal
		
		PVector P2SubP1 = PVector.sub(r.end,r.start);
		PVector P3SubP1 = PVector.sub(face[0],r.start);
		float u = n.dot(P3SubP1) / n.dot(P2SubP1);
		PVector P = PVector.add(r.start,PVector.mult(P2SubP1,u));
		return P;
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
				"--bgcolor=#000000",
				"drole.tests.menu.BoxHatchTest"
		});
	}
}
