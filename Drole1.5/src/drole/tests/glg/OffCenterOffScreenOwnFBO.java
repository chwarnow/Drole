package drole.tests.glg;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import drole.settings.Settings;
import processing.core.PApplet;
import processing.core.PVector;

public class OffCenterOffScreenOwnFBO extends PApplet implements MouseWheelListener {

	private static final long serialVersionUID = 1L;

	private GLGraphicsOffScreen offG;
	
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
	
	private GLGraphics gl;
	
	public void setup() {
		size(720, 720, GLConstants.GLGRAPHICS);
		
		gl = (GLGraphics)g;
		
		offG = new GLGraphicsOffScreen(this, width, height);
		
		room = new Room(this, offG, "data/room/drolebox2/panorama03.");
		
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
		gl.gl.glMatrixMode(GL.GL_PROJECTION);
		gl.gl.glLoadIdentity();
		
		gl.gl.glFrustum(l, r, b, t, n, f);

		// println(l+":"+r+":"+b+":"+t+":"+n+":"+f);

		// Rotate the projection to be non-perpendicular.

		// Final projection matrix

		float[] M = new float[]{
			vr.x, vr.y, vr.z, 0.0f,
			vu.x, vu.y, vu.z, 0.0f,
			vn.x, vn.y, vn.z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
		};

		gl.gl.glMultMatrixf(M, 0);
		
		gl.gl.glTranslatef(-pe.x, -pe.y, -pe.z);
		
		gl.gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.gl.glLoadIdentity();
		gl.gl.glScalef(1.0f, -1.0f, 1.0f);
		gl.gl.glTranslatef(0, -(realScreenPos.y+realScreenDim.y)+(realScreenDim.y/2), 0);
	}	
	
	
	public void draw() {
		gl = (GLGraphics)g;
		
		gl.resetMatrix();
		
		gl.beginGL();
		
		offG.beginDraw();
		
			offG.resetMatrix();
			offG.beginGL();
		
			calculatep();
			setp();
		
			// Flip x-axis back as this is already done in GLGraphicsOffScreen 
			offG.gl.glScalef(1.0f, -1.0f, 1.0f);
			
			offG.background(200, 0, 100);
			room.draw();

			offG.endGL();
		offG.endDraw();
		
		gl.background(255);
		
		/*
		offG.beginDraw();
			calculatep();
			setp();
			
			offG.background(200, 0, 100);
			room.draw();
			
			offG.gl.glMatrixMode(GL.GL_MODELVIEW);
			offG.gl.glPopMatrix();
			
			offG.gl.glMatrixMode(GL.GL_PROJECTION);
			offG.gl.glPopMatrix();
		offG.endDraw();
		*/
		/*
		gl.translate(width/2, height/2, 0);
		
		gl.background(255);
		*/
		/*
		gl.translate(width/2, height/2, 0);
		
		gl.imageMode(PApplet.CENTER);
		gl.image(offG.getTexture(), 0, 0, width, height);
		*/
		
		gl.gl.glMatrixMode(GL.GL_PROJECTION);
		gl.gl.glLoadIdentity();
		
		gl.gl.glOrtho(0, width, height, 0, 10, -10);
		
		gl.gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.gl.glLoadIdentity();
//		gl.gl.glScalef(1.0f, -1.0f, 1.0f);
		
		gl.translate(width/2, height/2, 0);
		
		gl.imageMode(PApplet.CENTER);
		gl.image(offG.getTexture(), 0, 0, width, height);
		
		gl.fill(200, 200, 0);
		gl.ellipse(0, 0, 20, 20);
		
		gl.endGL();
	}

	public void mouseMoved(MouseEvent e) {
		pe.x = map(e.getX(), 0, width, -realScreenDim.x/2f, realScreenDim.x/2f);
		pe.y = map(e.getY(), height, 0, realScreenPos.y, realScreenPos.y+realScreenDim.y);
		
		println(pe);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		pe.z += e.getWheelRotation()*30f;
		
		println(pe);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"drole.tests.glg.OffCenterOffScreenOwnFBO"
		});
	}
	
}
