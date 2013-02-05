package offcenterprojection;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;
	
	public SimpleOpenNI kinect;
	
	float headX = 0, headY = 0;
	
	private boolean pmode = false;
	
	float a = 0; 
	GL2 gl; 
	PGraphicsOpenGL pgl;
	float[] projMatrix;
	float[] mvMatrix;
	
	// 700.0:2700.0:-6150.0 @ 1000
//	private float x = 700, y = 1000, z = -6150, s = 1000;
	private float x = 600, y = -150, z = -1050, s = 1000;
	
	public void setup() {
		size(1200, 800, OPENGL);
		
		pgl = (PGraphicsOpenGL) g;  // g may change
		  gl = pgl.beginPGL().gl.getGL2();
		  projMatrix = new float[16];
		  mvMatrix = new float[16]; 
		
		println("Starting kinect ...");
		kinect = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
		println("enable depth ...");
		kinect.enableDepth();
		println("enable user tracking ...");
		kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
		println("kinect up!");
	}
	
	public void setOffCenterProjection(PVector pe, float n, float f) {
		println(pe);
		
		// Description of the frustum
		float l = 0;
		float r = 0;
		float b = 0;
		float t = 0;
		float d = 0;
		
		// Lower left corner of our screen in real-world-coords (mm) 
		PVector pa = new PVector(-700, -1200, 0);
		
		// Lower right corner of our screen in real-world-coords (mm) 
		PVector pb = new PVector(720, -1200, 0);
		
		// Upper left corner of our screen in real-world-coords (mm) 
		PVector pc = new PVector(-700, 145, 0);
		
		// Orthonormal basis of our screen space
		// right vector
		PVector vr = new PVector();
		// up vector
		PVector vu = new PVector();
		// z
		PVector vn = new PVector();

		// Screen corner vectors
		PVector va = new PVector();
		// up vector
		PVector vb = new PVector();
		// z
		PVector vc = new PVector();
		
		// Compute an orthonormal basis for the screen
		PVector.sub(pb, pa, vr);
		PVector.sub(pc, pa, vu);
		
		vr.normalize();
		vu.normalize();	
		PVector.cross(vr, vu, vn);
		vn.normalize();
		
		// Compute the screen corner vectors.
		
		PVector.sub(pa, pe, va);
		PVector.sub(pb, pe, vb);
		PVector.sub(pc, pe, vc);
		
		// Find the distance form the eye to screen plane
		
		d = -PVector.dot(va, vn);
		
		// Find the extend of the perpendicular projection.
		
		l = PVector.dot(vr, va) * n / d;
		r = PVector.dot(vr, vb) * n / d;
		b = PVector.dot(vu, va) * n / d;
		t = PVector.dot(vu, vc) * n / d;
		
		// Load the perpendicular projection.
		
//		resetMatrix(); // glLoadIdentity
		
//		beginCamera();
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glFrustum(l, r, b, t, n, f);
		
//		frustum(100, 100, 100, 100, n, f);
		//-0.035:0.036:-0.06725:0.0:0.1:3000.0
//		frustum(-10, 0, 0, 10, 10, 200);
		
		println(l+":"+r+":"+b+":"+t+":"+n+":"+f);
		
		// Rotate the projection to be non-perpendicular.
		
		// Final projection matrix

		PMatrix3D M = new PMatrix3D(
			vr.x, vu.x, vn.x, 0.0f,
			vr.y, vu.y, vn.y, 0.0f, 
			vr.z, vu.z, vn.z, 0.0f,
			0	, 0	  , 0	, 1.0f
		);

/*
		PMatrix3D M = new PMatrix3D(
			vr.x, vr.y, vr.z, 0.0f,
			vu.x, vu.y, vu.z, 0.0f, 
			vn.x, vn.y, vn.z, 0.0f,
			0	, 0	  , 0	, 1.0f
		);
*/
		
		float[] MM = new float[16];
		M.get(MM);
		gl.glMultMatrixf(MM, 0);
		
		gl.glTranslatef(-pe.x, -pe.y, -pe.z);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
//		camera(vr.x, vu.x, vn.x, vr.y, vu.y, vn.y, vr.z, vu.z, vn.z);
//		translate(-pe.x, -pe.y, -pe.z);
	}
	
	public void draw() {
		
		kinect.update();
		
		PVector head = new PVector(0, 145, 2000);
		
		if(kinect.isTrackingSkeleton(1)) {
			kinect.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_HEAD, head);
		}
		
//		loadMatrix();
//		setOffCenterProjection(head, 0.1f, 10000f);
		
		pgl.beginPGL();
		
		setOffCenterProjection(head, 0.1f, 10000f);
		
		background(255);
		
//		fill(200, 120, 0);
//		if(kinect.isTrackingSkeleton(1)) fill(0, 200, 120);
		
		println(x+":"+y+":"+z);
		/*
		gl.glPushMatrix();
//			rotateY(frameCount/60.0f);
			
//			box(s);
			gl.glColor4f(0.7f, 0.7f, 0.7f, 0.8f);
//			  gl.glTranslatef(x, y, z);
				gl.glTranslatef(width/2, height/2, 0);
			  gl.glRotatef(a, 1, 0, 0);
			  gl.glRotatef(a*2, 0, 1, 0);
			  gl.glRectf(-200, -200, 200, 200);
			  gl.glRotatef(90, 1, 0, 0);
			  gl.glRectf(-200, -200, 200, 200);
			
		gl.glPopMatrix();
		*/

		gl.glPushMatrix();
//		rotateY(frameCount/60.0f);
		
//		box(s);
		gl.glColor4f(0.7f, 0.7f, 0.7f, 0.8f);
//			gl.glTranslatef(x, y, z);
			gl.glTranslatef(x, y, z);
		  gl.glRotatef(a, 1, 0, 0);
		  gl.glRotatef(a*2, 0, 1, 0);
		  gl.glRectf(-200, -200, 200, 200);
		  gl.glRotatef(90, 1, 0, 0);
		  gl.glRectf(-200, -200, 200, 200);
		
		  gl.glPopMatrix();
		  
		pgl.endPGL();
		
		a += 0.5f;
	}
	
	// SimpleOpenNI events
	public void onNewUser(int userid) {
		System.out.println("New User found: " + userid);
		System.out.println("Start pose detection ...");

		kinect.startPoseDetection("Psi", userid);
	}

	public void onLostUser(int userid) {
		System.out.println("Lost User: " + userid);
	}

	public void onStartCalibration(int userid) {
		System.out.println("Start calibration for user: " + userid);
	}

	public void onEndCalibration(int userid, boolean successfull) {
		System.out.println("End calibration for user: " + userid + ", successfull: "
				+ successfull);

		if (successfull) {
			System.out.println("User calibrated!");
			kinect.startTrackingSkeleton(userid);
		} else {
			System.out.println("Failed to calibrate user: "+userid);
			System.out.println("Start pose detection ...");
			kinect.startPoseDetection("Psi", userid);
		}
	}

	public void onStartPose(String pose, int userid) {
		System.out.println("Start pose for user: " + userid + ", pose: " + pose);
		System.out.println("Stop pose detection ...");

		kinect.stopPoseDetection(userid);
		kinect.requestCalibrationSkeleton(userid, true);

	}

	public void onEndPose(String pose, int userid) {
		System.out.println("End pose for user: " + userid + ", pose: " + pose);
	}	
	
	
	public void keyPressed() {
		if(key == 'k') {
			println("enable depth ...");
			kinect.enableDepth();
			println("enable user tracking ...");
			kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
			println("kinect up!");
		}
		
		if(key == 'q') x-=50;
		if(key == 'w') x+=50;
		if(key == 'a') y-=50;
		if(key == 's') y+=50;
		if(key == 'y') z-=50;
		if(key == 'x') z+=50;
		
		if(key == 'e') s-=20;
		if(key == 'r') s+=20;		
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { 
		"--present",
		"--bgcolor=#000000",
		"--present-stop-color=#000000",
		"--display=1",
		"offcenterprojection.Main"
	    });
	}

	void loadMatrix() {
		 
		  gl.glMatrixMode(GL2.GL_PROJECTION);
		  projMatrix[0] = pgl.projection.m00;
		  projMatrix[1] = pgl.projection.m10;
		  projMatrix[2] = pgl.projection.m20;
		  projMatrix[3] = pgl.projection.m30;
		 
		  projMatrix[4] = pgl.projection.m01;
		  projMatrix[5] = pgl.projection.m11;
		  projMatrix[6] = pgl.projection.m21;
		  projMatrix[7] = pgl.projection.m31;
		 
		  projMatrix[8] = pgl.projection.m02;
		  projMatrix[9] = pgl.projection.m12;
		  projMatrix[10] = pgl.projection.m22;
		  projMatrix[11] = pgl.projection.m32;
		 
		  projMatrix[12] = pgl.projection.m03;
		  projMatrix[13] = pgl.projection.m13;
		  projMatrix[14] = pgl.projection.m23;
		  projMatrix[15] = pgl.projection.m33;
		 
		  gl.glLoadMatrixf(projMatrix, 0);
		 
		  gl.glMatrixMode(GL2.GL_MODELVIEW);
		  mvMatrix[0] = pgl.modelview.m00;
		  mvMatrix[1] = pgl.modelview.m10;
		  mvMatrix[2] = pgl.modelview.m20;
		  mvMatrix[3] = pgl.modelview.m30;
		 
		  mvMatrix[4] = pgl.modelview.m01;
		  mvMatrix[5] = pgl.modelview.m11;
		  mvMatrix[6] = pgl.modelview.m21;
		  mvMatrix[7] = pgl.modelview.m31;
		 
		  mvMatrix[8] = pgl.modelview.m02;
		  mvMatrix[9] = pgl.modelview.m12;
		  mvMatrix[10] = pgl.modelview.m22;
		  mvMatrix[11] = pgl.modelview.m32;
		 
		  mvMatrix[12] = pgl.modelview.m03;
		  mvMatrix[13] = pgl.modelview.m13;
		  mvMatrix[14] = pgl.modelview.m23;
		  mvMatrix[15] = pgl.modelview.m33;
		  gl.glLoadMatrixf(mvMatrix, 0);
		}
	
}
