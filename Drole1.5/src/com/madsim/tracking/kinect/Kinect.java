package com.madsim.tracking.kinect;


import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PVector;

import com.madsim.engine.EngineApplet;
import com.madsim.ui.kinetics.PositionalMovementInput;

import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.SimpleOpenNIConstants;

public class Kinect implements SimpleOpenNIConstants {
	
	public static short VERBOSE = 2;
	public static short CHATTY = 1;
	public static short QUIET = 0;
	
	private short logLevel = VERBOSE;
	
	private EngineApplet p;
	
	private SimpleOpenNI c;
	
	private ArrayList<KinectUserEventListener> userEventListener = new ArrayList<KinectUserEventListener>();
	
	private HashMap<Integer, PVector> stdMotionData = new HashMap<Integer, PVector>();
	
	private HashMap<Integer, PVector> lastPositions = new HashMap<Integer, PVector>();
	
	private boolean autoCalib = true;

	public static int NO_USER = 9999999;
	
	private int currentUser = 0;

	private boolean fakeMode;
	
	public static PVector IGNORED_POSITION = new PVector(PositionalMovementInput.IGNORED_VALUE, PositionalMovementInput.IGNORED_VALUE, PositionalMovementInput.IGNORED_VALUE);
	
	public Kinect(EngineApplet p, short logLevel) {
		this(p, logLevel, false);
	}
	
	public Kinect(EngineApplet p, short logLevel, boolean fakeMode) {
		this.p = p;
		this.fakeMode = fakeMode;
		
		init();
	}
	
	private void init() {
		if(!fakeMode) {
			c = new SimpleOpenNI(p, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
			if(logLevel >= 1) p.logLn("[Kinect]: Hardware is running!");

			// !!! NEEDS TO BE ENABLED FOR USER TRACKING !!!
			c.enableDepth();
			if(logLevel >= 1) p.logLn("[Kinect]: DepthMap initialized.");
			
			c.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
			if(logLevel >= 1) p.logLn("[Kinect]: User tracking is running.");
			
			c.setMirror(true);
		}
	}
	
	public void addUserEventListener(KinectUserEventListener l) {
		if(!userEventListener.contains(l)) {
			userEventListener.add(l);
		}
	}
	
	public void update() {
		if(c != null) {
			c.update();
			updateCurrentUser();
		}
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
	
	public PVector getJoint(int joint) {
		return getJoint(joint, new PVector(0, 0, 0));
	}	
	
	public PVector getJoint(int joint, PVector std) {
		if(c != null) {
			 if(currentUser != NO_USER) {
				 c.getJointPositionSkeleton(currentUser, joint, std);
				 lastPositions.put(joint, std.get());
			 } else {
				 std = IGNORED_POSITION.get();
			 }
		}
		
		return std;
	}
	
	public PVector getLastPosition(int joint, PVector std) {
		if(lastPositions.containsKey(joint)) return lastPositions.get(joint);
		else return std;
	}

	// Handle Users
	// -----------------------------------------------------------------
	// SimpleOpenNI user events

	public void onNewUser(int uid) {
		if(logLevel >= 2) p.logLn("[Kinect]: New user ("+uid+")");

		if(autoCalib) {
			c.requestCalibrationSkeleton(uid, true);
		} else {
			c.startPoseDetection("Psi", uid);
		}
	}

	public void onLostUser(int uid) {
		if(logLevel >= 2) p.logLn("[Kinect]: Lost user ("+uid+")");
		c.stopTrackingSkeleton(uid);
	}

	public void onExitUser(int uid) {
		if(logLevel >= 2) p.logLn("[Kinect]: Exit user ("+uid+")");
		c.stopTrackingSkeleton(uid);
	}

	public void onReEnterUser(int uid) {
		if(logLevel >= 2) p.logLn("[Kinect]: Reenter user ("+uid+")");
		c.startTrackingSkeleton(uid);
	}

	public void onStartCalibration(int uid) {
		if(logLevel >= 2) p.logLn("[Kinect]: Start calibration for user ("+uid+")");
	}

	public void onEndCalibration(int uid, boolean successfull) {
		if (successfull) {
			if(logLevel >= 2) p.logLn("[Kinect]: User ("+uid+") calibrated.");
			c.startTrackingSkeleton(uid);
		} else {
			if(logLevel >= 2) p.logLn("[Kinect]: Failed to calibrate user ("+uid+")");
			c.startPoseDetection("Psi", uid);
		}
	}

	public void onStartPose(String pose, int uid) {
		if(logLevel >= 2) p.logLn("[Kinect]: Start pose for user ("+uid+")");

		c.stopPoseDetection(uid);
		c.requestCalibrationSkeleton(uid, true);
	}

	public void onEndPose(String pose, int uid) {
		if(logLevel >= 2) p.logLn("[Kinect]: End pose for user ("+uid+")");
	}	
	
	public void updateCurrentUser() {
//		p.logLn("[Kinect]: Revalidating users ...");
//		
//		p.logLn("[Kinect]: Current user pool: "+c.getUsers().length);
		
		// WAH, we lost a user it seems ...
		int newUser = NO_USER;
		for(int uid : c.getUsers()) {
			if(uid < newUser && c.isTrackingSkeleton(uid)) newUser = uid;
		}
		
		if(currentUser != NO_USER && newUser == NO_USER) {
			currentUser = newUser;
			if(logLevel >= 1) p.logLn("[Kinect]: No user to track ...");
			for(KinectUserEventListener l : userEventListener) l.lostUser();
		}
		if(newUser != NO_USER && newUser != currentUser) {
			currentUser = newUser;
			if(logLevel >= 1) p.logLn("[Kinect]: Giving control to user ("+currentUser+")");
			for(KinectUserEventListener l : userEventListener) l.trackingUser();
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
