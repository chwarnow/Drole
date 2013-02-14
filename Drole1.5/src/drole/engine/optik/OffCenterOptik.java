package drole.engine.optik;

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;

public class OffCenterOptik extends Optik {
	
	// Real World Screen Dimensions
	private PVector realScreenDim;
	private PVector realScreenPos;
	
	// Real World Screen Positions
	private PVector pa = new PVector();
	private PVector pb = new PVector();
	private PVector pc = new PVector();
	private PVector pd = new PVector();
	
	// Head Position in Tracker Space
	private PVector pe;

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
	
	public OffCenterOptik(PApplet p, float w, float h, float d, float x, float y, float z) {
		super(p);
		realScreenDim = new PVector(w, h, d);
		realScreenPos = new PVector(x, y, z);
		
		calcRealWorldScreenSetup();
		
		updateHeadPosition(new PVector(realScreenPos.x+(realScreenDim.x/2f), realScreenPos.y+(realScreenDim.y/2f), 3000));
	}

	public PVector updateHeadPosition(PVector pe) {
//		p.logLn(pe.y + " : " + (realScreenPos.y + (realScreenDim.y / 2f)));
		pe.y = realScreenPos.y + (realScreenDim.y / 2f);
//		pe.y *= -1;
		pe.x *= -1;
		
		this.pe = pe;
		
		return this.pe;
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
	public void calculate() {		
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

		p.g.resetMatrix();

		p.g.frustum(l, r, b, t, n, f);

		// println(l+":"+r+":"+b+":"+t+":"+n+":"+f);

		// Rotate the projection to be non-perpendicular.

		// Final projection matrix

		PMatrix3D M = new PMatrix3D(
			vr.x, vr.y, vr.z, 0.0f,
			vu.x, vu.y, vu.z, 0.0f,
			vn.x, vn.y, vn.z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
		);

		p.g.applyMatrix(M);

		p.g.translate(-pe.x, -pe.y, -pe.z);
	}

	public void drawOffCenterVectors() {
		p.g.pushStyle();
		p.g.pushMatrix();
		p.g.stroke(0, 200, 200);
			drawLine(pe, pa);
			drawLine(pe, pb);
			drawLine(pe, pc);
			drawLine(pe, pd);
		p.g.strokeWeight(1);
		p.g.popMatrix();
		p.g.popStyle();
	}
	
	private void drawLine(PVector p1, PVector p2) {
		p.g.line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}
	
	public void drawRealWorldScreen() {
		p.g.pushStyle();
		p.g.pushMatrix();
			p.g.stroke(200, 0, 0);
			p.g.noFill();
			p.g.beginShape();
				
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

				p.g.vertex(pc.x, pc.y, pc.z); // Upper Left Corner of Screen	
				p.g.vertex(pd.x, pd.y, pd.z); // Upper Right Corner of Screen
				p.g.vertex(pb.x, pb.y, pb.z); // Lower Right Corner of Screen
				p.g.vertex(pa.x, pa.y, pa.z); // Lower Left Corner of Screen
			p.g.endShape();
			
			// Draw Real World Screen Orthonormal
			PVector ox = vr.get();
			ox.mult(200);
			
			PVector oy = vu.get();
			oy.mult(200);
			
			PVector oz = vn.get();
			oz.mult(200);
			
			p.g.stroke(255, 200, 200);
			p.g.line(pa.x, pa.y, pa.z, pa.x+ox.x, pa.y+ox.y, pa.z+ox.z);
			p.g.line(pa.x, pa.y, pa.z, pa.x+oy.x, pa.y+oy.y, pa.z+oy.z);
			p.g.line(pa.x, pa.y, pa.z, pa.x+oz.x, pa.y+oz.y, pa.z+oz.z);
			
			p.g.strokeWeight(1);
			
			// Draw Screen-Space Origin
			p.g.pushMatrix();
				p.g.noStroke();
				p.g.fill(0, 0, 200);
				
				p.g.translate(pe.x, pe.y, pa.z);
				p.g.ellipse(0, 0, 20, 20);
			p.g.popMatrix();
			
			p.g.pushMatrix();
				p.g.translate(-pe.x, -pe.y, -pe.z);
				p.g.ellipse(0, 0, 20, 20);
			p.g.popMatrix();
			
		p.g.popStyle();
		p.g.popMatrix();
	}
	
}
