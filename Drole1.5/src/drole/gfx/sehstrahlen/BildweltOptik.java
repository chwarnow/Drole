package drole.gfx.sehstrahlen;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import com.christopherwarnow.bildwelten.utils.Ray;
import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import drole.gfx.assoziation.BildweltAssoziationPensee;

public class BildweltOptik extends Drawable {

	PVector[] face = new PVector[3];
	float ai = PApplet.TWO_PI/3;//angle increment
	float r  = 300;//overall radius
	float ro = 150;//random offset

	PFont font;

	public float rotation 						= 0;
	private float smoothedRotation 				= 0;
	private float smoothedRotationSpeed 		= .1f;
	
	// sehender akteur pensee
	private BildweltAssoziationPensee sehenderAkteur;
	
	// floor pensee
	private BildweltAssoziationPensee floorPensee;
	
	public BildweltOptik(Engine e, PVector position, PVector dimension) {
		super(e);

		position(position);
		dimension(dimension);
		
		font = e.p.loadFont("data/fonts/HoeflerText-Regular-48.vlw");
		e.p.textFont(font);
		
		// create sehender akteur pensee
		sehenderAkteur = new BildweltAssoziationPensee(
			e,
			"data/images/optikAkteurSmaller.png",
			dimension.x*scale.x*.5f,
			.85f,
			new PVector(0, 0, 0),
			new PVector(0, 0, 0)
		);
		sehenderAkteur.setLooping(false);
		sehenderAkteur.setPosition(.5f);
		// TODO: do on fadein
		sehenderAkteur.loadPensee();
		
		// create floor pensee
		floorPensee = new BildweltAssoziationPensee(
			e,
			"data/images/optikFloor.png",
			dimension.x*scale.x*.5f,
			7.5f,
			new PVector(0, 0, -96),
			new PVector(0, 0, 0)
		);
		floorPensee.setLooping(false);
		floorPensee.setPosition(.5f);
		// TODO: do on fadein
		floorPensee.loadPensee();
		
	}

	@Override
	public void fadeOut(float time) {
		super.fadeOut(time);
		sehenderAkteur.hideMe();
		floorPensee.hideMe();
	}
	
	@Override
	public void fadeIn(float time) {
		super.fadeIn(time);
		sehenderAkteur.showMe();
		floorPensee.showMe();
	}
	
	@Override
	public void update() {
		super.update();
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		sehenderAkteur.update();
		floorPensee.update();
	}
	
	

	@Override
	public void draw() {
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y + e.p.cos(e.p.frameCount*.02f)*10f, position.z);
		g.scale(scale.x+1.0f, scale.y+1.0f, scale.z+1.0f);
		g.rotateY(smoothedRotation);

		float rectSize = 500;
		
		e.startShader("PolyLightAndColor"); // RoomShader
		
		g.tint(255);
		g.stroke(105, 90, 97, fade*255);
		g.fill(199, 186, 177, fade*255);

		g.pushMatrix();

		//floor
		g.pushMatrix();		
		// draw floor pensee
		g.rotateX(3.1414f/2);
		if(floorPensee.isVisible()) {
			floorPensee.draw();
		}
/*
		g.popMatrix();
		g.pushMatrix();
		g.box(rectSize, 10, rectSize*0.6181f); // golden ratio
		g.popMatrix();
*/
		// e.stopShader();
		// e.startShader("PolyLightAndColor");
		
		// wall
		g.pushMatrix();
		// translate(-rectSize*(0.6181f*.33f), -rectSize*(0.6181f*.25f)-5, 0);
		// box(10, rectSize*(0.6181f*.5f), rectSize*0.6181f); // golden ratio
		g.popMatrix();

		// rays
		
		float mainY = 100;

		// wall coords
		PVector pointI = new PVector(-rectSize*(0.6181f*.35f), -rectSize*(0.6181f*.5f)-5 + mainY, -rectSize*.31f);
		PVector pointK = new PVector(pointI.x, pointI.y, rectSize*.31f);
		PVector pointM = new PVector(pointI.x, -5 + mainY, -rectSize*.31f);
		PVector pointL = new PVector(pointK.x, -5 + mainY, rectSize*.31f);

		// define wall plane for intersection test
		face[0] = pointI;
		face[1] = pointK;
		face[2] = pointL;

		// head
		PVector pointG = new PVector(rectSize*(0.6181f*.7f), -rectSize*(0.6181f*.4f)-5 + mainY, 0);
		// feet
		PVector pointH = new PVector(pointG.x, -5 + mainY, pointG.z);

		// generic triangle a
		PVector pointE = new PVector(-rectSize*(.6181f*.66f) + PApplet.cos(e.p.frameCount*.005f)*30, -5 + mainY, 0 + PApplet.sin(e.p.frameCount*.0025f)*30);
		// generic triangle b
		PVector pointF = new PVector(-rectSize*(.6181f*.75f) + PApplet.cos(e.p.frameCount*.01f)*10, -5 + mainY, rectSize*.28f + PApplet.sin(e.p.frameCount*.01f)*10);
		// generic triangle c
		PVector pointR = new PVector(-rectSize*(.6181f*.5f) + PApplet.cos(e.p.frameCount*.001f)*30, -5 + mainY, rectSize*.1f + PApplet.sin(e.p.frameCount*.005f)*30);

		// intersection g-f
		PVector pointO = linePlaneIntersection(new Ray(pointG, pointF), face);
		// intersection g-e
		PVector pointN = linePlaneIntersection(new Ray(pointG, pointE), face);
		// intersection g-r
		PVector pointS = linePlaneIntersection(new Ray(pointG, pointR), face);
		
		// g-e
		g.beginShape();
		g.vertex(pointG.x, pointG.y, pointG.z);
		g.vertex(pointE.x, pointE.y, pointE.z);
		g.endShape();

		// g-f
		g.beginShape();
		g.vertex(pointG.x, pointG.y, pointG.z);
		g.vertex(pointF.x, pointF.y, pointF.z);
		g.endShape();

		// g-r
		g.beginShape();
		g.vertex(pointG.x, pointG.y, pointG.z);
		g.vertex(pointR.x, pointR.y, pointR.z);
		g.endShape();

		// triangle on floor
		g.beginShape();
		g.vertex(pointF.x, pointF.y, pointF.z);
		g.vertex(pointE.x, pointE.y, pointE.z);
		g.vertex(pointR.x, pointR.y, pointR.z);
		g.vertex(pointF.x, pointF.y, pointF.z);
		g.endShape();

		// triangle on wall
		g.beginShape();
		g.vertex(pointO.x, pointO.y, pointO.z);
		g.vertex(pointN.x, pointN.y, pointN.z);
		g.vertex(pointS.x, pointS.y, pointS.z);
		g.vertex(pointO.x, pointO.y, pointO.z);
		g.endShape();

		// intersection example

		g.pushStyle();

		g.strokeWeight(5);
		g.point(pointO.x,pointO.y,pointO.z);//point of ray-plane intersection
		g.point(pointN.x,pointN.y,pointN.z);//point of ray-plane intersection
		g.point(pointS.x,pointS.y,pointS.z);//point of ray-plane intersection

		g.popStyle();

		// draw wall plane
		g.pushMatrix();
		g.fill(0, 20*fade);
		g.beginShape();
		g.vertex(pointI.x+1, pointI.y, pointI.z);
		g.vertex(pointK.x+1, pointK.y, pointK.z);
		g.vertex(pointL.x+1, pointL.y, pointL.z);
		g.vertex(pointM.x+1, pointM.y, pointM.z);
		g.vertex(pointI.x+1, pointI.y, pointI.z);
		g.endShape();
		g.popMatrix();
		
		e.stopShader();
		
		g.fill(0, 255*fade);
		
		// texts
		float textOffset = 13;
		g.noStroke();
		g.fill(0, 255*fade);
		g.textSize(12);

		g.pushMatrix();
		g.translate(pointI.x-textOffset, pointI.y, pointI.z);
		g.text("I", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointK.x-textOffset, pointK.y, pointK.z);
		g.text("K", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointL.x-textOffset, pointL.y, pointL.z);
		g.text("L", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointM.x-textOffset, pointM.y, pointM.z);
		g.text("M", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointG.x-textOffset, pointG.y, pointG.z);
		g.text("G", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointH.x-textOffset, pointH.y, pointH.z);
		g.text("H", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointO.x-textOffset, pointO.y, pointO.z);
		g.text("O", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointN.x-textOffset, pointN.y, pointN.z);
		g.text("N", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointS.x-textOffset, pointS.y, pointS.z);
		g.text("S", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointF.x, pointF.y, pointF.z);
		g.text("F", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointE.x, pointE.y, pointE.z);
		g.text("E", 0, 0);
		g.popMatrix();

		g.pushMatrix();
		g.translate(pointR.x, pointR.y, pointR.z);
		g.text("R", 0, 0);
		g.popMatrix();
		
		e.startShader("PolyLightAndColor"); // RoomShader
		
		// sehender akteur
		g.pushMatrix();
		g.translate(pointG.x-10, pointG.y+57, pointG.z);
		if(sehenderAkteur.isVisible()) sehenderAkteur.draw();
		g.popMatrix();

		// e.stopShader();
		
		g.popMatrix();

		g.popStyle();
		g.popMatrix();

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
