package test2;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;

	private SimpleOpenNI kinect;
	
	public void setup() {
		size(640, 480, OPENGL);
		
		startKinect();
	}
	
	private void startKinect() {
		System.out.println("Starting Kinect Hardware ...");
		kinect = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_DEFAULT);
		
		System.out.println("Initializing Depth Map");
		kinect.enableDepth();
		
		System.out.println("Enable User Tracking");
		kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
		
		System.out.println("Kinect Ready!");
	}
	
	public void draw() {
		if(kinect.isInit()) {
			kinect.update();
			image(kinect.depthImage(), (width/2f)-(kinect.depthWidth()/2f), (height/2f)-(kinect.depthHeight()/2f));
		}

//		if(frameCount%100 == 0) println(frameRate);
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
		System.out.println("End calibration for user: " + userid + ", successfull: "+ successfull);

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
	
	public void stop() {
		kinect.dispose();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { 
		"--present",
		"--bgcolor=#000000",
		"--present-stop-color=#000000",
		"--display=0",
		"test2.Main"
	    });
	}
	
}
