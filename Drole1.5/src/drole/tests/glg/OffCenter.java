package drole.tests.glg;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import drole.settings.Settings;
import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;

public class OffCenter extends PApplet implements MouseWheelListener {

	private static final long serialVersionUID = 1L;

	private Room room;
	
	// Real World Screen Dimensions
	private PVector realScreenDim;
	private PVector realScreenPos;
	
	// Real World Screen Positions
	private PVector pa = new PVector();
	private PVector pb = new PVector();
	private PVector pc = new PVector();
	private PVector pd = new PVector();
	
	// Head Position in Tracker Space
	private PVector pe = new PVector();

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
	
	public void setup() {
		size(720, 720, GLConstants.GLGRAPHICS);
		
		GLGraphics gl = (GLGraphics)g;
		room = new Room(this, gl, "data/room/drolebox2/panorama03.");
		
		realScreenDim = new PVector(Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM, Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM, Settings.REAL_SCREEN_DIMENSIONS_DEPTH_MM);
		realScreenPos = new PVector(Settings.REAL_SCREEN_POSITION_X_MM, Settings.REAL_SCREEN_POSITION_Y_MM, Settings.REAL_SCREEN_POSITION_Z_MM);
		
		updateHeadPosition();
		
		calcRealWorldScreenSetup();
		
		addMouseWheelListener(this);
	}
	
	public PVector updateHeadPosition() {
		pe = new PVector(0, 0, 0);
		
//		p.logLn(pe.y + " : " + (realScreenPos.y + (realScreenDim.y / 2f)));
		pe.x *= -1;
		pe.y = (realScreenPos.y + (realScreenDim.y / 2f));
		pe.z = 3000;
		
		return pe;
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
	
	public void calculatep() {		
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

	public void setp() {
		// Load the perpendicular projection.

		g.frustum(l, r, b, t, n, f);

		// println(l+":"+r+":"+b+":"+t+":"+n+":"+f);

		// Rotate the projection to be non-perpendicular.

		// Final projection matrix

		PMatrix3D M = new PMatrix3D(
			vr.x, vr.y, vr.z, 0.0f,
			vu.x, vu.y, vu.z, 0.0f,
			vn.x, vn.y, vn.z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
		);

		g.applyMatrix(M);

		g.translate(-pe.x, pe.y, -pe.z);
	}	
	
	
	public void draw() {
		GLGraphics gl = (GLGraphics)g;
		
		gl.resetMatrix();
		
		gl.beginGL();
		
		gl.resetMatrix();
		
		calculatep();
		setp();
		
		gl.translate(0, -(realScreenPos.y+realScreenDim.y)+(realScreenDim.y/2), 0);
		
		gl.background(0);
		
		gl.fill(200, 200, 0);
		gl.ellipse(0, 0, 20, 20);
		
		gl.fill(200);
		gl.stroke(0);
//		gl.box(1800);
		
		room.draw();
		
		gl.endGL();
	}

	public void mouseMoved(MouseEvent e) {
		pe.x = map(e.getX(), 0, width, -realScreenDim.x/2f, realScreenDim.x/2f);
		pe.y = map(e.getY(), height, 0, realScreenPos.y, realScreenPos.y+realScreenDim.y);
		
		println(pe);
	}
	
	public void mouseDragged(MouseEvent e) {
		println("Drag");
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		pe.z += e.getWheelRotation()*30f;
		
		println(pe);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"drole.tests.glg.OffCenter"
		});
	}
	
}
