package com.madsim.ui.kinetics.gestures;

import com.madsim.ui.kinetics.PositionalMovementInput;

public class RipInterpreter {

	private int axisToObserve;
	
	private RipMotionListener listener;
	
	private PositionalMovementInput input;
	
	private int ripDirection;
	
	private float lastPosition;
	private float ripThreshold = 70.0f;
	
	private boolean inGestureCooldown = false;
	private long gestureCooldownStart = 0;
	private long gestureCooldownTime = 2000;
	
	private boolean locked = false;
	
	public RipInterpreter(RipMotionListener listener, PositionalMovementInput input, int ripDirection, int axisToObserve) {
		this.listener		=	listener;
		this.input 			= 	input;
		this.axisToObserve	=	axisToObserve;
		this.ripDirection	=	ripDirection;
		
		lastPosition = input.getPosition()[axisToObserve];
	}

	public void lock() {
		locked = true;
	}
	
	public void unlock() {
		locked = false;
	}
	
	public void gestureOccured() {
		if(!locked) {
			listener.ripGestureFound();
			inGestureCooldown = true;
			gestureCooldownStart = System.currentTimeMillis();
		}
	}
	
	public void forceCooldown() {
		inGestureCooldown = true;
		gestureCooldownStart = System.currentTimeMillis();		
	}
	
	public void update() {
		float position = input.getPosition()[axisToObserve];
		
		float dist = lastPosition - position;
		
		if(Math.abs(dist) > ripThreshold && !inGestureCooldown) {
			if(ripDirection < 0 && dist < 0) gestureOccured();
			if(ripDirection > 0 && dist > 0) gestureOccured();
		}
		
		lastPosition = position;
		
		if(inGestureCooldown && (System.currentTimeMillis() - gestureCooldownStart) > gestureCooldownTime) {
			inGestureCooldown = false;
		}
	}

}
