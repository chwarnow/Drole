package com.madsim.tracking.kinect;

import processing.core.PMatrix3D;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;

public class KinectGFXUtils {
	
	// draw the skeleton with the selected joints
	public static void drawSkeleton(int uid) {
		/*
		strokeWeight(3);
		
		// to get the 3d joint data
		drawLimb(uid, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		drawLimb(uid, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		drawLimb(uid, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		drawLimb(uid, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

		drawLimb(uid, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		drawLimb(uid, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		drawLimb(uid, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

		drawLimb(uid, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		drawLimb(uid, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

		drawLimb(uid, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		drawLimb(uid, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		drawLimb(uid, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

		drawLimb(uid, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		drawLimb(uid, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		drawLimb(uid, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);

		// draw body direction
		getBodyDirection(uid, bodyCenter, bodyDir);

		bodyDir.mult(200); // 200mm length
		bodyDir.add(bodyCenter);

		stroke(255, 200, 200);
		line(bodyCenter.x, bodyCenter.y, bodyCenter.z, bodyDir.x, bodyDir.y,
				bodyDir.z);

		strokeWeight(1);
		*/
	}
/*
	public void drawLimb(int uid, int jointType1, int jointType2) {
		PVector jointPos1 = new PVector();
		PVector jointPos2 = new PVector();
		float confidence;

		// draw the joint position
		confidence = context.getJointPositionSkeleton(uid, jointType1, jointPos1);
		confidence = context.getJointPositionSkeleton(uid, jointType2, jointPos2);

		stroke(255, 0, 0, confidence * 200 + 55);
		line(jointPos1.x, jointPos1.y, jointPos1.z, jointPos2.x, jointPos2.y, jointPos2.z);

		drawJointOrientation(uid, jointType1, jointPos1, 50);
	}

	public void drawJointOrientation(int uid, int jointType, PVector pos,
			float length) {
		// draw the joint orientation
		PMatrix3D orientation = new PMatrix3D();
		float confidence = context.getJointOrientationSkeleton(uid, jointType, orientation);
		if(confidence < 0.001f)
			// nothing to draw, orientation data is useless
		return;

		pushMatrix();
		translate(pos.x, pos.y, pos.z);

		// set the local coordsys
		applyMatrix(orientation);

		// coordsys lines are 100mm long
		// x - r
		stroke(255, 0, 0, confidence * 200 + 55);
		line(0, 0, 0, length, 0, 0);
		// y - g
		stroke(0, 255, 0, confidence * 200 + 55);
		line(0, 0, 0, 0, length, 0);
		// z - b
		stroke(0, 0, 255, confidence * 200 + 55);
		line(0, 0, 0, 0, 0, length);
		popMatrix();
	}
	*/
	
}
