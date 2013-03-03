package com.madsim.tests.gestures;

import com.madsim.tracking.fake.MouseXY;
import com.madsim.ui.kinetics.gestures.RipInterpreter;
import com.madsim.ui.kinetics.gestures.RipMotionListener;

import processing.core.PApplet;

public class RipInterpreterTest extends PApplet implements RipMotionListener {

	private static final long serialVersionUID = 1L;

	private RipInterpreter rip;
	private MouseXY mouseInput;
	
	public void setup() {
		size(1000, 800, OPENGL);
		
		mouseInput = new MouseXY();
		addMouseMotionListener(mouseInput);
		
		rip = new RipInterpreter(this, mouseInput, 1, 0);
	}
	
	public void draw() {
		rip.udpate();
		
		background(200);
		
		translate(width/2, height/2 + (PApplet.map(mouseY, 0, height, -(height/2), height/2)), -300);
		
		noFill();
		stroke(200, 200, 0);
		
		sphere(200);
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[]{ "com.madsim.tests.gestures.RipInterpreterTest" });
	}

	@Override
	public void ripGestureFound() {
		println("RIP Occured!");
	}

}
