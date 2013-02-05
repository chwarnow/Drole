package offcenterprojection2;

/* --------------------------------------------------------------------------
 * SimpleOpenNI User3d Test
 * --------------------------------------------------------------------------
 * Processing Wrapper for the OpenNI/Kinect library
 * http://code.google.com/p/simple-openni
 * --------------------------------------------------------------------------
 * prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
 * date:  02/16/2011 (m/d/y)
 * ----------------------------------------------------------------------------
 * this demos is at the moment only for 1 user, will be implemented later
 * ----------------------------------------------------------------------------
 */
 
import javax.media.opengl.GL2;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PMatrix3D;
import processing.core.PVector;
import processing.event.MouseEvent;
import SimpleOpenNI.*;

public class OffCenter3 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	private short DEBUG 	= 10;
	private short LIVE		= 20;
	private short MODE 		= DEBUG;
	
	
	private SimpleOpenNI context;
	private float        zoomF =0.5f;
	private float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
	
	// the data from openni comes upside down
	private float        rotY = radians(0);
	private boolean      autoCalib=true;
	
	private PVector      bodyCenter = new PVector();
	private PVector      bodyDir = new PVector();
	
	
	// Real World Screen Dimensions
	private PVector realScreenDim = new PVector(300, 300, 0);
	private PVector realScreenPos = new PVector(-150, 40, -165);
	
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
	float l = 0;
	float r = 0;
	float b = 0;
	float t = 0;
	float d = 0;
	
	float n = 0.1f;
	float f = 4000f;
	
	// Screen corner vectors
	PVector va = new PVector();
	// up vector
	PVector vb = new PVector();
	// z
	PVector vc = new PVector();	
	
	
	private PVector head = new PVector(0, 0, 1000);
	private PVector objectPosition = new PVector(0, 260, -165);
	private PVector objectSize = new PVector(120, 200, 200);
	
	private float horizontalViewAlpha 		= 0.0f;
	private float verticalViewAlpha 		= 0.0f;
	private float distanceHead2Object		= 0.0f;
	private float distanceXHead2Object		= 0.0f;
	private float distanceZHead2Object		= 0.0f;
	
	private float lastMouseX = 0.0f, lastMouseY = 0.0f; 
	
	private PFont mainFont;
	
	public void setup() {
	  size(1080, 1080, OPENGL);  // strange, get drawing error in the cameraFrustum if i use P3D, in opengl there is no problem
	  
	  context = new SimpleOpenNI(this);
	
	  // enable depthMap generation 
	  if(context.enableDepth() == false) {
	     println("Can't open the depthMap, maybe the camera is not connected!"); 
	     exit();
	     return;
	  }
	
	  // enable skeleton generation for all joints
	  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

	  stroke(255,255,255);
	  smooth();
	  
	  lastMouseX = mouseX;
	  lastMouseY = mouseY;
	  
	  mainFont = createFont("Helvetica", 12);
	  textFont(mainFont);
	  
	  calcRealWorldScreenSetup();
	}
	
	private void setViewAlpha() {
		horizontalViewAlpha = atan(abs(head.x-objectPosition.x)/abs(head.z-objectPosition.z));
		horizontalViewAlpha = (head.x <= objectPosition.x) ? horizontalViewAlpha : -horizontalViewAlpha;
		
		verticalViewAlpha = atan(abs(head.x-objectPosition.x)/abs(head.y-objectPosition.y));
		verticalViewAlpha = (head.y <= objectPosition.y) ? verticalViewAlpha : -verticalViewAlpha;
	}
	
	private void drawLine(PVector p1, PVector p2) {
		line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}
	
	private void updateHead() {
		if(context.isTrackingSkeleton(1)) {
			context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_HEAD, head);
			head.y = realScreenPos.y+(realScreenDim.y/2f);
			head.x *= -1;
		}
	}
	
	private void drawViewAlphaAndLine() {
		pushStyle();
		pushMatrix();
			alpha(200);
			
			stroke(200, 0, 0);
			drawLine(head, objectPosition);
			
			fill(160, 0, 0);
			
			/*
			beginShape();
				vertex(head.x, head.y, head.z);
				vertex(object.x, object.y, object.z);
				vertex(head.x+((head.x-object.x)/2f), head.y+((head.y-object.y)/2f), head.z+((head.z-object.z)/2f));
			endShape();
			*/
			
			/*
			beginShape();
				vertex(head.x, head.y, head.z);
				vertex(object.x, object.y, object.z);
				vertex(head.x+((head.x-object.x)/2f), head.y+((head.y-object.y)/2f), head.z+((head.z-object.z)/2f));
			endShape();
			*/
			
			beginShape();
				vertex(head.x, head.y, head.z);
				vertex(objectPosition.x, objectPosition.y, objectPosition.z);
				vertex(objectPosition.x, head.y, head.z);
			endShape();

			fill(0, 0, 160);
			beginShape();
				vertex(head.x, head.y, head.z);
				vertex(objectPosition.x, objectPosition.y, objectPosition.z);
				vertex(objectPosition.x, objectPosition.y, head.z);
			endShape();
			
			/*
			beginShape();
				vertex(head.x, head.y, head.z);
				vertex(object.x, object.z, object.y);
				vertex(head.x, object.z, ((head.y-object.y)/2f));
			endShape();
			 */
			
		popMatrix();
		popStyle();
	}
	
	private void drawOffCenterVectors(PVector pe) {
		pushStyle();
		pushMatrix();
			stroke(0, 200, 200);
			drawLine(pe, pa);
			drawLine(pe, pb);
			drawLine(pe, pc);
			drawLine(pe, pd);
			strokeWeight(1);
		popMatrix();
		popStyle();
	}
	
	private void drawOffCenterFrustum(PVector pe) {
		pushStyle();
		pushMatrix();
			stroke(0, 200, 200);
			drawLine(pe, pa);
			drawLine(pe, pb);
			drawLine(pe, pc);
			drawLine(pe, pd);
			strokeWeight(1);
		popMatrix();
		popStyle();
	}	
	
	public void calcRealWorldScreenSetup() {
		// Lower left corner of our screen in real-world-coords (mm) 
		pa = realScreenPos;
		
		// Lower right corner of our screen in real-world-coords (mm)
		pb = new PVector(realScreenPos.x+realScreenDim.x, realScreenPos.y, realScreenPos.z);
		
		// Upper left corner of our screen in real-world-coords (mm)
		pc = new PVector(realScreenPos.x, realScreenPos.y+realScreenDim.y, realScreenPos.z);
		
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
	
	private void calcOffCenterProjection(PVector pe) {
		println(pe);
		
		// Compute the screen corner vectors.
		
		PVector.sub(pa, pe, va);
		PVector.sub(pb, pe, vb);
		PVector.sub(pc, pe, vc);
		
		println("va");
		println(va);
		println("vb");
		println(vb);
		println("vc");
		println(vc);
		
		// Find the distance from the eye to screen plane
		
		d = -PVector.dot(va, vn);
		
		println("d: "+d);
		
		// Find the extend of the perpendicular projection.
		
		l = PVector.dot(vr, va) * n / d;
		r = PVector.dot(vr, vb) * n / d;
		b = PVector.dot(vu, va) * n / d;
		t = PVector.dot(vu, vc) * n / d;
	}
	
	private void setOffCenterProjection(PVector pe) {
		// Load the perpendicular projection.
		
		g.resetMatrix();
		
		g.frustum(l, r, b, t, n, f);
		
		println(l+":"+r+":"+b+":"+t+":"+n+":"+f);
		
		// Rotate the projection to be non-perpendicular.
		
		// Final projection matrix
		
		PMatrix3D M = new PMatrix3D(
			vr.x, vr.y, vr.z, 0.0f,
			vu.x, vu.y, vu.z, 0.0f, 
			vn.x, vn.y, vn.z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
		);
		
		g.applyMatrix(M);
		
		g.translate(-pe.x, -pe.y, -pe.z);
	}
	
	public void draw() {
	  // update the cam
	  context.update();
	  
	  updateHead();
	  
	  setViewAlpha();
	  
	  background(0, 0, 0);
	  
	  fill(255);
	  text("H-Alpha: "+horizontalViewAlpha, 170, 150);
	  text("V-Alpha: "+verticalViewAlpha, 170, 170);
	  
	  // set the scene pos
	  translate(width/2, height/2, 0);
	  
	  if(MODE == DEBUG) {		  
		  resetMatrix();
		  
		  perspective(radians(45), (float)width/(float)height, 10, 150000);
		  
		  translate(0, 0, -1000);  // set the rotation center of the scene 1000 in front of the camera
		  
		  rotateX(rotX);
		  rotateY(rotY);
		  scale(zoomF);
		  
		  int[]   depthMap = context.depthMap();
		  int     steps   = 3;  // to speed up the drawing, draw every third point
		  int     index;
		  PVector realWorldPoint;
			
		  stroke(100); 
		  for(int y=0;y < context.depthHeight();y+=steps)
		  {
		    for(int x=0;x < context.depthWidth();x+=steps)
		    {
		      index = x + y * context.depthWidth();
		      if(depthMap[index] > 0)
		      { 
		        // draw the projected point
		        realWorldPoint = context.depthMapRealWorld()[index];
		        point(realWorldPoint.x,realWorldPoint.y,realWorldPoint.z);
		      }
		    } 
		  } 
		  
		  // draw the skeleton if it's available
		  int[] userList = context.getUsers();
		  for(int i=0;i<userList.length;i++)
		  {
		    if(context.isTrackingSkeleton(userList[i]))
		      drawSkeleton(userList[i]);
		  }    
		 
		  // draw the kinect cam
		  context.drawCamFrustum();
	  } else {
		  calcOffCenterProjection(head);
		  setOffCenterProjection(head);
	  }
	  
	  drawOffCenterVectors(head);
	  
	  // Draw our holo object
	  drawHoloObject();
	  
	  // Draw Real World Screen
	  drawRealWorldScreen();
	  
	  // Draw the View triangle of Head and Object
//	  drawViewAlphaAndLine();
//	  if(MODE != DEBUG) endCamera();
	}
	
	// draw the skeleton with the selected joints
	public void drawSkeleton(int userId) {
	  strokeWeight(3);
	
	  // to get the 3d joint data
	  drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);
	
	  drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
	
	  drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);
	
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
	
	  drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
	  drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);
	
	  drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
	  drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  
	
	  // draw body direction
	  getBodyDirection(userId,bodyCenter,bodyDir);
	  
	  bodyDir.mult(200);  // 200mm length
	  bodyDir.add(bodyCenter);
	  
	  stroke(255,200,200);
	  line(bodyCenter.x,bodyCenter.y,bodyCenter.z,
	       bodyDir.x ,bodyDir.y,bodyDir.z);
	
	  strokeWeight(1);
	}
	
	public void drawLimb(int userId,int jointType1,int jointType2) {
	  PVector jointPos1 = new PVector();
	  PVector jointPos2 = new PVector();
	  float  confidence;
	  
	  // draw the joint position
	  confidence = context.getJointPositionSkeleton(userId,jointType1,jointPos1);
	  confidence = context.getJointPositionSkeleton(userId,jointType2,jointPos2);
	
	  stroke(255,0,0,confidence * 200 + 55);
	  line(jointPos1.x,jointPos1.y,jointPos1.z,
	       jointPos2.x,jointPos2.y,jointPos2.z);
	  
	  drawJointOrientation(userId,jointType1,jointPos1,50);
	}
	
	public void drawJointOrientation(int userId,int jointType,PVector pos,float length) {
	  // draw the joint orientation  
	  PMatrix3D  orientation = new PMatrix3D();
	  float confidence = context.getJointOrientationSkeleton(userId,jointType,orientation);
	  if(confidence < 0.001f) 
	    // nothing to draw, orientation data is useless
	    return;
	    
	  pushMatrix();
	    translate(pos.x,pos.y,pos.z);
	    
	    // set the local coordsys
	    applyMatrix(orientation);
	    
	    // coordsys lines are 100mm long
	    // x - r
	    stroke(255,0,0,confidence * 200 + 55);
	    line(0,0,0,
	         length,0,0);
	    // y - g
	    stroke(0,255,0,confidence * 200 + 55);
	    line(0,0,0,
	         0,length,0);
	    // z - b    
	    stroke(0,0,255,confidence * 200 + 55);
	    line(0,0,0,
	         0,0,length);
	  popMatrix();
	}
	
	// -----------------------------------------------------------------
	// SimpleOpenNI user events
	
	public void onNewUser(int userId)
	{
	  println("onNewUser - userId: " + userId);
	  println("  start pose detection");
	  
	  if(autoCalib)
	    context.requestCalibrationSkeleton(userId,true);
	  else    
	    context.startPoseDetection("Psi",userId);
	}
	
	public void onLostUser(int userId)
	{
	  println("onLostUser - userId: " + userId);
	}
	
	public void onExitUser(int userId)
	{
	  println("onExitUser - userId: " + userId);
	}
	
	public void onReEnterUser(int userId)
	{
	  println("onReEnterUser - userId: " + userId);
	}
	
	
	public void onStartCalibration(int userId)
	{
	  println("onStartCalibration - userId: " + userId);
	}
	
	public void onEndCalibration(int userId, boolean successfull)
	{
	  println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);
	  
	  if (successfull) 
	  { 
	    println("  User calibrated !!!");
	    context.startTrackingSkeleton(userId); 
	  } 
	  else 
	  { 
	    println("  Failed to calibrate user !!!");
	    println("  Start pose detection");
	    context.startPoseDetection("Psi",userId);
	  }
	}
	
	public void onStartPose(String pose,int userId)
	{
	  println("onStartdPose - userId: " + userId + ", pose: " + pose);
	  println(" stop pose detection");
	  
	  context.stopPoseDetection(userId); 
	  context.requestCalibrationSkeleton(userId, true);
	 
	}
	
	public void onEndPose(String pose,int userId)
	{
	  println("onEndPose - userId: " + userId + ", pose: " + pose);
	}
	
	// -----------------------------------------------------------------
	// Keyboard events
	
	public void keyPressed() {
	  switch(key) {
	  	case ' ': context.setMirror(!context.mirror()); break;
	  	case 'l': MODE = LIVE; break;
	  	case 'd': MODE = DEBUG; break;
	  }
	    
	  switch(keyCode)
	  {
	    case LEFT:
	      rotY += 0.1f;
	      break;
	    case RIGHT:
	      // zoom out
	      rotY -= 0.1f;
	      break;
	    case UP:
	      if(keyEvent.isShiftDown())
	        zoomF += 0.1f;
	      else
	        rotX += 0.1f;
	      break;
	    case DOWN:
	      if(keyEvent.isShiftDown())
	      {
	        zoomF -= 0.01f;
	        if(zoomF < 0.01f)
	          zoomF = 0.01f;
	      }
	      else
	        rotX -= 0.1f;
	      break;
	  }
	}
	
	public void getBodyDirection(int userId,PVector centerPoint,PVector dir) {
	  PVector jointL = new PVector();
	  PVector jointH = new PVector();
	  PVector jointR = new PVector();
	  float  confidence;
	  
	  // draw the joint position
	  confidence = context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_LEFT_SHOULDER,jointL);
	  confidence = context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_HEAD,jointH);
	  confidence = context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_SHOULDER,jointR);
	  
	  // take the neck as the center point
	  confidence = context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,centerPoint);
	  
	  /*  // manually calc the centerPoint
	  PVector shoulderDist = PVector.sub(jointL,jointR);
	  centerPoint.set(PVector.mult(shoulderDist,.5));
	  centerPoint.add(jointR);
	  */
	  
	  PVector up = new PVector();
	  PVector left = new PVector();
	  
	  up.set(PVector.sub(jointH,centerPoint));
	  left.set(PVector.sub(jointR,centerPoint));
	  
	  dir.set(up.cross(left));
	  dir.normalize();
	}
	
	public void drawHoloObject() {
		pushStyle();
			lights();
		
			pointLight(200, 0, 120, objectPosition.x+500, objectPosition.y+500, objectPosition.z+500);
			
			pushMatrix();
				translate(objectPosition.x, objectPosition.y, objectPosition.z);
//				rotateY(radians(45)-horizontalViewAlpha);
//				rotateX(verticalViewAlpha);
				
				noStroke();
				fill(200);
				
				sphere(objectSize.x-10);
				
				stroke(0, 0, 120);
				noFill();
				
				sphere(objectSize.x);
			popMatrix();

			noLights();
			
		popStyle();
	}

	public void mouseDragged(MouseEvent e) {
		rotY += (mouseX - lastMouseX)/10f;
		//rotY += (mouseY - lastMouseY);
		
		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}
	
	private void drawRealWorldScreen() {
		pushStyle();
		pushMatrix();
			stroke(200, 0, 0);
			noFill();
			beginShape();
				
				println("Positions");
				
				println(pa);
				println(pb);
				println(pc);
				println(pd);
				
				println("Orthos");
				
				println(vr);
				println(vu);
				println(vn);

				vertex(pc.x, pc.y, pc.z); // Upper Left Corner of Screen	
				vertex(pd.x, pd.y, pd.z); // Upper Right Corner of Screen
				vertex(pb.x, pb.y, pb.z); // Lower Right Corner of Screen
				vertex(pa.x, pa.y, pa.z); // Lower Left Corner of Screen
			endShape();
			
			// Draw Real World Screen Orthonormal
			PVector ox = vr.get();
			ox.mult(200);
			
			PVector oy = vu.get();
			oy.mult(200);
			
			PVector oz = vn.get();
			oz.mult(200);
			
			stroke(255, 200, 200);
			line(pa.x, pa.y, pa.z, pa.x+ox.x, pa.y+ox.y, pa.z+ox.z);
			line(pa.x, pa.y, pa.z, pa.x+oy.x, pa.y+oy.y, pa.z+oy.z);
			line(pa.x, pa.y, pa.z, pa.x+oz.x, pa.y+oz.y, pa.z+oz.z);
			
			strokeWeight(1);
			
			// Draw Screen-Space Origin
			pushMatrix();
				noStroke();
				fill(0, 0, 200);
				
				translate(head.x, head.y, pa.z);
				ellipse(0, 0, 20, 20);
			popMatrix();
			
			pushMatrix();
				translate(-head.x, -head.y, -head.z);
				ellipse(0, 0, 20, 20);
			popMatrix();
			
		popStyle();
		popMatrix();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { 
		"--present",
		"--bgcolor=#000000",
		"--present-stop-color=#000000",
		"--display=1",
		"offcenterprojection2.OffCenter3"
	    });
	}
	
}