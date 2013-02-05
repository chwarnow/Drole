package gestures;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PVector;
import processing.core.PGraphics;

public class PositionTarget {

	public static short RELATIVE = 10;
	public static short FIXED = 20;
	
	private int observedJoint;
	
	private int pinnedJoint;
	
	private SimpleOpenNI kinect;
	
	private short mode;
	
	private TargetShape target;
	
	private PVector lastJointPos = new PVector();
	
	private String name;
	
	private PositionTargetListener listener;
	
	private boolean inTarget;
	
	public PositionTarget(PositionTargetListener listener, String name, SimpleOpenNI kinect, TargetShape target, int observedJoint) {
		this.listener		= listener;
		this.name			= name;
		this.kinect			= kinect;
		this.target			= target;
		this.observedJoint 	= observedJoint;
		this.mode			= FIXED;
	}

	public PositionTarget(PositionTargetListener listener, String name, SimpleOpenNI kinect, TargetShape target, int observedJoint, int pinnedJoint) {
		this(listener, name, kinect, target, observedJoint);
		this.pinnedJoint	= pinnedJoint;
		this.mode			= RELATIVE;
	}
	
	public void check() {
		if(mode == RELATIVE) {
			PVector newTP = new PVector();
			kinect.getJointPositionSkeleton(1, pinnedJoint, newTP);
			System.out.println(newTP.toString());
			newTP.add(target.offset);
			target.position = newTP;
			System.out.println(newTP.toString());
		}
		
		kinect.getJointPositionSkeleton(1, observedJoint, lastJointPos);
		if(target.contains(lastJointPos)) {
			if(!inTarget) {
				listener.jointEnteredTarget(name);
				inTarget = true;
			}
		} else {
			if(inTarget) {
				listener.jointLeftTarget(name);
				inTarget = false;
			}
		}
	}
	
	public void drawTarget(PGraphics g) {
		g.pushStyle();
			if(inTarget) {
				g.noStroke();
				g.fill(0, 200, 0);
			} else {
				g.noFill();
				g.stroke(200, 0, 0);
			}
			target.draw(g);
		g.popStyle();
	}
	
}
