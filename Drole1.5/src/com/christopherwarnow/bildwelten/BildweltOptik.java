package com.christopherwarnow.bildwelten;

import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import drole.DroleMain;
import drole.engine.Drawable;
import drole.gfx.ribbon.RibbonGroup;

import com.christopherwarnow.bildwelten.utils.Ray;

public class BildweltOptik extends Drawable {
	
	PVector[] face = new PVector[3];
	float ai = PApplet.TWO_PI/3;//angle increment
	float r  = 300;//overall radius
	float ro = 150;//random offset
	
	PFont font;
	GLTexture optikAkteur;
	
	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	public BildweltOptik(DroleMain parent) {
		super(parent);
		// TODO Auto-generated constructor stub
		this.parent = parent;
		
		position(position);
		dimension(dimension);
		
		font = parent.loadFont("data/fonts/HoeflerText-Regular-48.vlw");
		parent.textFont(font);
		
		optikAkteur = new GLTexture(parent, "data/images/optikAkteur.png");
	}

	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
	}
	
	@Override
	public void draw() {
		parent.g.pushStyle();
		parent.g.pushMatrix();

			parent.g.translate(position.x, position.y-(fade*700), position.z);
			parent.g.scale(scale.x+1.0f, scale.y+1.0f, scale.z+1.0f);
			parent.g.rotateY(smoothedRotation);
			
		float rectSize = 500;
		
		parent.startShader("JustColor");
		
		parent.tint(255);
		parent.stroke(105, 90, 97);
		parent.fill(199, 186, 177);

		parent.imageMode(parent.CORNERS);
		
		parent.pushMatrix();
		// parent.translate(parent.width/2, parent.height/2+200 - fade*500);
		// TODO:use parent rotation
		// parent.rotateY(parent.radians(parent.mouseX));
		// parent.rotateZ(.1f + parent.radians(-parent.mouseY)*.05f);
		
		//floor
		parent.pushMatrix();		
		parent.box(rectSize, 10, rectSize*0.6181f); // golden ratio
		parent.popMatrix();

		// wall
		parent.pushMatrix();
		// translate(-rectSize*(0.6181f*.33f), -rectSize*(0.6181f*.25f)-5, 0);
		// box(10, rectSize*(0.6181f*.5f), rectSize*0.6181f); // golden ratio
		parent.popMatrix();

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
		PVector pointE = new PVector(-rectSize*(.6181f*.66f) + parent.cos(parent.frameCount*.005f)*30, -5, 0 + parent.sin(parent.frameCount*.0025f)*30);
		// generic triangle b
		PVector pointF = new PVector(-rectSize*(.6181f*.75f) + parent.cos(parent.frameCount*.01f)*10, -5, rectSize*.28f + parent.sin(parent.frameCount*.01f)*10);
		// generic triangle c
		PVector pointR = new PVector(-rectSize*(.6181f*.5f) + parent.cos(parent.frameCount*.001f)*30, -5, rectSize*.1f + parent.sin(parent.frameCount*.005f)*30);

		// intersection g-f
		PVector pointO = linePlaneIntersection(new Ray(pointG, pointF), face);
		// intersection g-e
		PVector pointN = linePlaneIntersection(new Ray(pointG, pointE), face);
		// intersection g-r
		PVector pointS = linePlaneIntersection(new Ray(pointG, pointR), face);
		
		// g-e
		parent.beginShape();
		parent.vertex(pointG.x, pointG.y, pointG.z);
		parent.vertex(pointE.x, pointE.y, pointE.z);
		parent.endShape();

		// g-f
		parent.beginShape();
		parent.vertex(pointG.x, pointG.y, pointG.z);
		parent.vertex(pointF.x, pointF.y, pointF.z);
		parent.endShape();

		// g-r
		parent.beginShape();
		parent.vertex(pointG.x, pointG.y, pointG.z);
		parent.vertex(pointR.x, pointR.y, pointR.z);
		parent.endShape();
		
		// triangle on floor
		parent.beginShape();
		parent.vertex(pointF.x, pointF.y, pointF.z);
		parent.vertex(pointE.x, pointE.y, pointE.z);
		parent.vertex(pointR.x, pointR.y, pointR.z);
		parent.vertex(pointF.x, pointF.y, pointF.z);
		parent.endShape();
		
		// triangle on wall
		parent.beginShape();
		parent.vertex(pointO.x, pointO.y, pointO.z);
		parent.vertex(pointN.x, pointN.y, pointN.z);
		parent.vertex(pointS.x, pointS.y, pointS.z);
		parent.vertex(pointO.x, pointO.y, pointO.z);
		parent.endShape();

		// intersection example
		
		parent.pushStyle();
		
		parent.strokeWeight(5);
		parent.point(pointO.x,pointO.y,pointO.z);//point of ray-plane intersection
		parent.point(pointN.x,pointN.y,pointN.z);//point of ray-plane intersection
		parent.point(pointS.x,pointS.y,pointS.z);//point of ray-plane intersection
		
		parent.popStyle();

		// draw wall plane
		parent.pushMatrix();
		parent.fill(0, 20);
		parent.beginShape();
		parent.vertex(pointI.x+1, pointI.y, pointI.z);
		parent.vertex(pointK.x+1, pointK.y, pointK.z);
		parent.vertex(pointL.x+1, pointL.y, pointL.z);
		parent.vertex(pointM.x+1, pointM.y, pointM.z);
		parent.vertex(pointI.x+1, pointI.y, pointI.z);
		parent.endShape();
		parent.popMatrix();
		
		
		parent.stopShader();
		
		parent.startShader("ColorAndTexture");
		
		parent.fill(0);
		
		// texts
		float textOffset = 13;
		parent.noStroke();
		parent.fill(0);
		parent.textSize(12);
		
		parent.pushMatrix();
		parent.translate(pointI.x-textOffset, pointI.y, pointI.z);
		parent.text("I", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointK.x-textOffset, pointK.y, pointK.z);
		parent.text("K", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointL.x-textOffset, pointL.y, pointL.z);
		parent.text("L", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointM.x-textOffset, pointM.y, pointM.z);
		parent.text("M", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointG.x-textOffset, pointG.y, pointG.z);
		parent.text("G", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointH.x-textOffset, pointH.y, pointH.z);
		parent.text("H", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointO.x-textOffset, pointO.y, pointO.z);
		parent.text("O", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointN.x-textOffset, pointN.y, pointN.z);
		parent.text("N", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointS.x-textOffset, pointS.y, pointS.z);
		parent.text("S", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointF.x, pointF.y, pointF.z);
		parent.text("F", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointE.x, pointE.y, pointE.z);
		parent.text("E", 0, 0);
		parent.popMatrix();
		
		parent.pushMatrix();
		parent.translate(pointR.x, pointR.y, pointR.z);
		parent.text("R", 0, 0);
		parent.popMatrix();
		
		
		// sehender akteur
		parent.pushMatrix();
		parent.translate(pointG.x-40, pointG.y-12, pointG.z);
		parent.scale(.455f);
		parent.image(optikAkteur, 0, 0);
		parent.popMatrix();
		
		parent.popMatrix();
		
		parent.g.popStyle();
		parent.g.popMatrix();
		
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
	
}
