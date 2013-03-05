package com.madsim.ui.kinetics.gestures;

import com.madsim.ui.kinetics.PositionalMovementInput;

public class TwoHandPushInterpreter {

	private int axisToObserve;
	
	private TwoHandPushListener listener;
	
	private PositionalMovementInput hand1, hand2;
	
	private int ripDirection;
	
	private float lastPosition1, lastPosition2;
	private float ripThreshold = 70.0f;
	
	private boolean inGestureCooldown = false;
	private long gestureCooldownStart = 0;
	private long gestureCooldownTime = 1200;
	
	private boolean locked = false;
	
	public TwoHandPushInterpreter(TwoHandPushListener listener, PositionalMovementInput hand1, PositionalMovementInput hand2, int ripDirection, int axisToObserve) {
		this.listener		=	listener;
		this.hand1 			= 	hand1;
		this.hand2 			= 	hand2;
		this.axisToObserve	=	axisToObserve;
		this.ripDirection	=	ripDirection;
		
		lastPosition1 = hand1.getPosition()[axisToObserve];
		lastPosition2 = hand2.getPosition()[axisToObserve];		
	}

	public void lock() {
		locked = true;
	}
	
	public void unlock() {
		locked = false;
	}
	
	public void gestureOccured() {
		if(!locked) {
			listener.pushGestureFound();
			inGestureCooldown = true;
			gestureCooldownStart = System.currentTimeMillis();
		}
	}
	
	public void update() {
		float position1 = hand1.getPosition()[axisToObserve];
		float position2 = hand2.getPosition()[axisToObserve];
		
		float dist1 = lastPosition1 - position1;
		float dist2 = lastPosition2 - position2;		
		
		if(Math.abs(dist1) > ripThreshold && Math.abs(dist2) > ripThreshold && !inGestureCooldown) {
			if(ripDirection < 0 && dist1 < 0 && dist2 < 0) gestureOccured();
			if(ripDirection > 0 && dist1 > 0 && dist2 > 0) gestureOccured();
		}
		
		lastPosition1 = position1;
		lastPosition2 = position2;
		
		if(inGestureCooldown && (System.currentTimeMillis() - gestureCooldownStart) > gestureCooldownTime) {
			inGestureCooldown = false;
		}
	}

}
