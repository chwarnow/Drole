package com.madsim.ui.kinetics;

import com.madsim.common.geom.Convertion;
import com.madsim.tracking.kinect.Kinect;

public class KinectInput implements PositionalMovementInput {

	private Kinect kinect;
	
	private int joint;
	
	public KinectInput(Kinect kinect, int joint) {
		this.kinect = kinect;
		this.joint = joint;
	}
	
	@Override
	public float[] getPosition() {
		return Convertion.getFloat3Array(kinect.getJoint(joint));
	}

}
