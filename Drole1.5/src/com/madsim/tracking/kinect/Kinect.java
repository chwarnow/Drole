package com.madsim.tracking.kinect;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PVector;

import com.madsim.engine.EngineApplet;

import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.SimpleOpenNIConstants;

public class Kinect implements SimpleOpenNIConstants {
	
	private EngineApplet p;
	
	private SimpleOpenNI c;
	
	private HashMap<Integer, PVector> stdMotionData = new HashMap<Integer, PVector>();
	
	private boolean autoCalib = true;
	
	private ArrayList<Integer> userPool = new ArrayList<Integer>();

	private static int NO_USER = 9999999;
	
	private int currentUser = 0;

	private boolean fakeMode;
	
	public Kinect(EngineApplet p) {
		this(p, false);
	}
	
	public Kinect(EngineApplet p, boolean fakeMode) {
		this.p = p;
		this.fakeMode = fakeMode;
		
		init();
		startTracking();
	}
	
	public void update() {
		if(c != null) c.update();
	}
	
	public int[] getDepthMap() {
		return c.depthMap();
	}

	public int depthWidth() {
		return c.depthWidth();
	}
	
	public int depthHeight() {
		return c.depthHeight();
	}
	
	public PVector[] depthMapRealWorld() {
		return c.depthMapRealWorld();
	}
	
	public int getCurrentUserID() {
		return currentUser;
	}
	
	public void setJointStd(int joint, PVector std) {
		stdMotionData.put(joint, std);
	}
	
	private void init() {
		if(!fakeMode) {
			c = new SimpleOpenNI(p);
		}
	}
	
	private void startTracking() {
		// enable skeleton generation for all joints
		if(c != null) c.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
	}
	
	public boolean enableDepth() {
		if(c != null) return c.enableDepth();
		return true;
	}
	
	public PVector getJoint(int joint) {
		PVector j = new PVector(0, 0, 0);
		if(currentUser != 0) c.getJointPositionSkeleton(currentUser, joint, j);
		return j;
	}

	// Handle Users
	// -----------------------------------------------------------------
	// SimpleOpenNI user events

	public void onNewUser(int uid) {
		p.logLn("[Kinect]: new user ("+uid+")");

		if(autoCalib) {
			c.requestCalibrationSkeleton(uid, true);
		} else {
			c.startPoseDetection("Psi", uid);
		}
	}

	public void onLostUser(int uid) {
		p.logLn("[Kinect]: lost user ("+uid+")");
	}

	public void onExitUser(int uid) {
		p.logLn("[Kinect]: exit user ("+uid+")");
		userPool.remove(uid);
		updateCurrentUser();
	}

	public void onReEnterUser(int uid) {
		p.logLn("[Kinect]: reenter user ("+uid+")");
	}

	public void onStartCalibration(int uid) {
		p.logLn("[Kinect]: start calibration for user ("+uid+")");
	}

	public void onEndCalibration(int uid, boolean successfull) {
		if (successfull) {
			p.logLn("[Kinect]: user ("+uid+") calibrated.");
			c.startTrackingSkeleton(uid);
			userPool.add(uid);
			updateCurrentUser();
		} else {
			p.logLn("[Kinect]: failed to calibrate user ("+uid+")");
			c.startPoseDetection("Psi", uid);
		}
	}

	public void onStartPose(String pose, int uid) {
		p.logLn("[Kinect]: start pose for user ("+uid+")");

		c.stopPoseDetection(uid);
		c.requestCalibrationSkeleton(uid, true);
	}

	public void onEndPose(String pose, int uid) {
		p.logLn("[Kinect]: end pose for user ("+uid+")");
	}	
	
	public void updateCurrentUser() {
		if(!c.isTrackingSkeleton(currentUser) || !userPool.contains(currentUser)) {
			// WAH, we lost a user it seems ...
			int newUser = NO_USER;
			for(Integer uid : userPool) {
				if(uid < newUser) newUser = uid;
			}
			currentUser = newUser;
			
			p.logLn("[Kinect]: Giving control to user ("+currentUser+")");
		}
	}
	
	public void getBodyDirection(int uid, PVector centerPoint, PVector dir) {
		PVector jointL = new PVector();
		PVector jointH = new PVector();
		PVector jointR = new PVector();

		// draw the joint position
		c.getJointPositionSkeleton(uid, SimpleOpenNI.SKEL_LEFT_SHOULDER, jointL);
		c.getJointPositionSkeleton(uid, SimpleOpenNI.SKEL_HEAD, jointH);
		c.getJointPositionSkeleton(uid, SimpleOpenNI.SKEL_RIGHT_SHOULDER, jointR);

		// take the neck as the center point
		c.getJointPositionSkeleton(uid, SimpleOpenNI.SKEL_NECK, centerPoint);

		/*
		 * // manually calc the centerPoint PVector shoulderDist =
		 * PVector.sub(jointL,jointR);
		 * centerPoint.set(PVector.mult(shoulderDist,.5));
		 * centerPoint.add(jointR);
		 */

		PVector up = new PVector();
		PVector left = new PVector();

		up.set(PVector.sub(jointH, centerPoint));
		left.set(PVector.sub(jointR, centerPoint));

		dir.set(up.cross(left));
		dir.normalize();
	}
	
	/*
	@Override
	public void jointEnteredTarget(String name) {
		p.logLn("Joint in " + name);
		if(name == "ROTATION_TARGET" && holdingTarget.inTarget()) {
			switchMode(ROTATING);
			globe.rotationSpeed = 0.0f;
			PVector rightHand = new PVector(0, 0, 0);
			c.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
			rotationMapStart = rightHand.x - 600;
			rotationMapStart = rightHand.x + 600;
		}
	}
	
	@Override
	public void jointLeftTarget(String name) {
		p.logLn("Joint left " + name);
		if(name == "HOLDING_TARGET") {
			globe.easeToScale(new PVector(1, 1, 1), 300);
			globe.rotationSpeed = 0.04f;
			switchMode(LIVE);
		}
		if(name == "ROTATION_TARGET" && holdingTarget.inTarget()) {
			switchMode(ZOOMING);
			globe.rotationSpeed = 0.04f;
		}
	}
	*/
	
}
