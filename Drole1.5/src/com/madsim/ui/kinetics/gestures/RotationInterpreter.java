package com.madsim.ui.kinetics.gestures;

import com.madsim.ui.kinetics.PositionalMovementInput;

public class RotationInterpreter extends PositionalGestureInterpreter {

	private int axisToObserve;

	private float lastPos;

	private float rotation = 0;

	private float spin = 0;

	private float drag = 0;

	private float spinDamping = 0.90f;

	private boolean isDragging = false;
	private float dragThreshold = 50.0f;
	private int dragDirection = 0;

	private boolean inDragCooldown = false;
	private long dragCooldownTime = 400;
	private long dragCooldownStart = 0;

	private boolean locked = false;
	
	public RotationInterpreter(PositionalMovementInput input, int axisToObserve) {
		super(input);
		this.axisToObserve = axisToObserve;
		lastPos = input.getPosition()[axisToObserve];
	}
	
	public void lock() {
		locked = true;
	}
	
	public void unlock() {
		locked = false;
	}

	@Override
	public float[] get() {
		updateRotationForce();
		updateRotation();
		
		return new float[]{rotation};
	}
	
	private void updateRotation() {
		spin += drag;
		spin *= spinDamping;

		if (Math.abs(spin) < 0.001)
			spin = 0;

		rotation -= spin;

		// println("Spin: "+spin);
		// println("Rotation: "+rotation);
	}

	void checkDragCooldown() {
		if (inDragCooldown && millis() - dragCooldownStart > dragCooldownTime) {
			stopDragCooldown();
		}
	}

	void startDragCooldown() {
		inDragCooldown = true;
		dragCooldownStart = millis();
//		println("Starting drag cooldown at " + dragCooldownStart);
	}

	void interruptDragCooldown() {
		inDragCooldown = false;

//		println("Interrupting drag cooldown");
	}

	void stopDragCooldown() {
		inDragCooldown = false;
		isDragging = false;
		dragDirection = 0;

//		println("Ending drag cooldown");
	}

	private void updateRotationForce() {
		float currentPos = input.getPosition()[axisToObserve];
		
		if(currentPos == PositionalMovementInput.IGNORED_VALUE) {
			drag = 0.0f;
			return;
		}

		float dist = lastPos - currentPos;
		lastPos = currentPos;
		
		if(Math.abs(dist) > dragThreshold) {
			int newDragDirection = dist > 0 ? 1 : -1;

			if (dragDirection == 0) {
				dragDirection = newDragDirection;
				isDragging = true;
			}

			if (isDragging && newDragDirection != dragDirection) {
				isDragging = false;
				interruptDragCooldown();
			}

			if (!isDragging && newDragDirection == dragDirection) {
				isDragging = true;
			}

			if (isDragging) {
				drag = dist / 3000.0f;
				if (inDragCooldown)
					interruptDragCooldown();
			}

		} else {
			drag = 0.0f;
			if (!inDragCooldown)
				startDragCooldown();
		}
		
		if(locked) drag = 0.0f;

		checkDragCooldown();
	}

}
