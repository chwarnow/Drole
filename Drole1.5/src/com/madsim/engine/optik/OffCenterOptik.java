package com.madsim.engine.optik;

import javax.media.opengl.GL;

import com.madsim.engine.Engine;

import processing.core.PVector;

public class OffCenterOptik extends Optik {
	
	// Real World Screen Dimensions
	public PVector realScreenDim;
	public PVector realScreenPos;

	// Tracker Space Position of the users head
	private PVector pe;
	
	// Real World Screen Positions
	private PVector pa = new PVector();
	private PVector pb = new PVector();
	private PVector pc = new PVector();
	private PVector pd = new PVector();

	// Real World Screen Orthonormal Basis
	private PVector vr = new PVector();
	private PVector vu = new PVector();
	private PVector vn = new PVector();

	// Description of the frustum
	private float l = 0;
	private float r = 0;
	private float b = 0;
	private float t = 0;
	private float d = 0;

	private float n = 0.1f;
	private float f = 100000f;

	// Screen corner vectors
	private PVector va = new PVector();
	// up vector
	private PVector vb = new PVector();
	// z
	private PVector vc = new PVector();
	
	public OffCenterOptik(Engine e, float w, float h, float d, float x, float y, float z, PVector head) {
		super(e, head);
		
		realScreenDim = new PVector(w, h, d);
		realScreenPos = new PVector(x, y, z);
		
		calcRealWorldScreenSetup();
		
		updateHeadPosition(new PVector(realScreenPos.x+(realScreenDim.x/2f), realScreenPos.y+(realScreenDim.y/2f), 3000));
	}

	public PVector updateHeadPosition(PVector pe) {
		stdPOV = pe.get();
		
//		p.logLn(stdPOV.y + " : " + (realScreenPos.y + (realScreenDim.y / 2f)));
		stdPOV.y -= (realScreenDim.y / 2f);
//		stdPOV.y *= -1;
//		stdPOV.x *= -1;
		
		return stdPOV;
	}

	private void calcRealWorldScreenSetup() {
		// Lower left corner of our screen in real-world-coords (mm)
		pa = realScreenPos;

		// Lower right corner of our screen in real-world-coords (mm)
		pb = new PVector(realScreenPos.x + realScreenDim.x, realScreenPos.y, realScreenPos.z);

		// Upper left corner of our screen in real-world-coords (mm)
		pc = new PVector(realScreenPos.x, realScreenPos.y + realScreenDim.y, realScreenPos.z);

		// Upper right corner of our screen in real-world-coords (mm)
		pd = new PVector(pb.x, pc.y, realScreenPos.z);

		// Orthonormal basis of our screen space
		// right vector
		vr = new PVector();
		// up vector
		vu = new PVector();
		// z
		vn = new PVector();

		// Compute an orthonormal basis for the screen
		PVector.sub(pb, pa, vr);
		PVector.sub(pc, pa, vu);

		vr.normalize();
		vu.normalize();
		PVector.cross(vr, vu, vn);
		vn.normalize();
	}
	
	@Override
	public void calculate(float povX, float povY, float povZ) {
		calculate(new PVector(povX, povY, povZ));
	}
	
	@Override
	public void calculate(PVector pov) {
		pe = pov.get();
		
		// logLn(pe);

		// Compute the screen corner vectors.

		PVector.sub(pa, pe, va);
		PVector.sub(pb, pe, vb);
		PVector.sub(pc, pe, vc);

		// println("va");
		// println(va);
		// println("vb");
		// println(vb);
		// println("vc");
		// println(vc);

		// Find the distance from the eye to screen plane

		d = -PVector.dot(va, vn);

		// println("d: "+d);

		// Find the extend of the perpendicular projection.

		l = PVector.dot(vr, va) * n / d;
		r = PVector.dot(vr, vb) * n / d;
		b = PVector.dot(vu, va) * n / d;
		t = PVector.dot(vu, vc) * n / d;
	}
	
	@Override
	public void set() {
		// Load the perpendicular projection.
		g.gl.glMatrixMode(GL.GL_PROJECTION);
		g.gl.glLoadIdentity();
		
		g.gl.glFrustum(l, r, b, t, n, f);

		// println(l+":"+r+":"+b+":"+t+":"+n+":"+f);

		// Rotate the projection to be non-perpendicular.

		// Final projection matrix

		float[] M = new float[]{
			vr.x, vr.y, vr.z, 0.0f,
			vu.x, vu.y, vu.z, 0.0f,
			vn.x, vn.y, vn.z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
		};

		g.gl.glMultMatrixf(M, 0);
		
		g.gl.glTranslatef(-pe.x, -pe.y, -pe.z);
		
		g.gl.glMatrixMode(GL.GL_MODELVIEW);
		g.gl.glLoadIdentity();
		
		g.gl.glScalef(1.0f, -1.0f, 1.0f);
		g.gl.glTranslatef(0, -(realScreenPos.y+realScreenDim.y)+(realScreenDim.y/2), 0);
	}
	
	public void drawOffCenterVectors() {
		g.pushStyle();
		g.pushMatrix();
		g.stroke(0, 200, 200);
			drawLine(pe, pa);
			drawLine(pe, pb);
			drawLine(pe, pc);
			drawLine(pe, pd);
		g.strokeWeight(1);
		g.popMatrix();
		g.popStyle();
	}
	
	private void drawLine(PVector p1, PVector p2) {
		g.line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}
	
	public void drawRealWorldScreen() {
		g.pushStyle();
		g.pushMatrix();
			g.stroke(200, 0, 0);
			g.noFill();
			g.beginShape();
				
				/*
				println("Positions");
				
				println(pa);
				println(pb);
				println(pc);
				println(pd);
				
				println("Orthos");
				
				println(vr);
				println(vu);
				println(vn);
				*/

				g.vertex(pc.x, pc.y, pc.z); // Upper Left Corner of Screen	
				g.vertex(pd.x, pd.y, pd.z); // Upper Right Corner of Screen
				g.vertex(pb.x, pb.y, pb.z); // Lower Right Corner of Screen
				g.vertex(pa.x, pa.y, pa.z); // Lower Left Corner of Screen
			g.endShape();
			
			// Draw Real World Screen Orthonormal
			PVector ox = vr.get();
			ox.mult(200);
			
			PVector oy = vu.get();
			oy.mult(200);
			
			PVector oz = vn.get();
			oz.mult(200);
			
			g.stroke(255, 200, 200);
			g.line(pa.x, pa.y, pa.z, pa.x+ox.x, pa.y+ox.y, pa.z+ox.z);
			g.line(pa.x, pa.y, pa.z, pa.x+oy.x, pa.y+oy.y, pa.z+oy.z);
			g.line(pa.x, pa.y, pa.z, pa.x+oz.x, pa.y+oz.y, pa.z+oz.z);
			
			g.strokeWeight(1);
			
			// Draw Screen-Space Origin
			g.pushMatrix();
				g.noStroke();
				g.fill(0, 0, 200);
				
				g.translate(pe.x, pe.y, pa.z);
				g.ellipse(0, 0, 20, 20);
			g.popMatrix();
			
			g.pushMatrix();
				g.translate(-pe.x, -pe.y, -pe.z);
				g.ellipse(0, 0, 20, 20);
			g.popMatrix();
			
		g.popStyle();
		g.popMatrix();
	}
	
}
