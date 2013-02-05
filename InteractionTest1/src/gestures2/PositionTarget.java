package gestures2;

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
	
	private boolean inTarget = false;
	
	private long timeSinceChangeRequest = 0;
	
	private boolean enterRequested = false;
	private boolean exitRequested = false;
	
	public long millisBeforeAcceptingEnter = 1000;
	public long millisBeforeAcceptingExit = 1800;
	
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
		if(kinect.isTrackingSkeleton(1)) {
			
			if(mode == RELATIVE) {
				PVector newTP = new PVector();
				kinect.getJointPositionSkeleton(1, pinnedJoint, newTP);
				newTP.add(target.offset);
				target.position = newTP;
			}
			
			kinect.getJointPositionSkeleton(1, observedJoint, lastJointPos);
			
			long ctime = System.currentTimeMillis();
			
			if(target.contains(lastJointPos)) {
				if(!inTarget && !enterRequested) {
					enterRequested = true;
					exitRequested = false;
					timeSinceChangeRequest = ctime;
	//				System.out.println("Enter requested!");
				} 
				if(exitRequested) exitRequested = false;
			} else {
				if(inTarget && !exitRequested) {
					exitRequested = true;
					enterRequested = false;
					timeSinceChangeRequest = ctime;
	//				System.out.println("Exit requested!");
				}
				if(enterRequested) enterRequested = false;
			}
			
	//		if(enterRequested) System.out.println("Checking enter time at "+(ctime-timeSinceChangeRequest));
			if(enterRequested && ctime-timeSinceChangeRequest >= millisBeforeAcceptingEnter) {
				enteredTarget();
			}
			
	//		if(exitRequested) System.out.println("Checking exit time at "+(ctime-timeSinceChangeRequest));
			if(exitRequested && ctime-timeSinceChangeRequest >= millisBeforeAcceptingExit) {
				leftTarget();
			}
		
		} else {
			if(inTarget) leftTarget();
		}
	}
	
	private void enteredTarget() {
		enterRequested = false;
		inTarget = true;
		listener.jointEnteredTarget(name);		
	}
	
	private void leftTarget() {
		exitRequested = false;
		inTarget = false;
		listener.jointLeftTarget(name);
	}
	
	public void drawTarget(PGraphics g) {
//		if(inTarget) target.draw(g);
		target.draw(g);
	}
	
	public boolean inTarget() {
		return inTarget;
	}
	
}
