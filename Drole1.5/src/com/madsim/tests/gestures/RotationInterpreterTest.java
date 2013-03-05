package com.madsim.tests.gestures;

import com.madsim.ui.kinetics.MouseXYInput;
import com.madsim.ui.kinetics.gestures.RotationInterpreter;

import processing.core.PApplet;

public class RotationInterpreterTest extends PApplet {

	private static final long serialVersionUID = 1L;

	private RotationInterpreter riY, riX;
	private MouseXYInput mouseInput;
	
	public void setup() {
		size(1000, 800, OPENGL);
		
		mouseInput = new MouseXYInput();
		addMouseMotionListener(mouseInput);
		
		riY = new RotationInterpreter(mouseInput, 0);
		riX = new RotationInterpreter(mouseInput, 1);
	}
	
	public void draw() {
		background(200);
		
		translate(width/2, height/2, -300);
		rotateY(riY.get()[0]);
		rotateX(riX.get()[0]);
		
		noFill();
		stroke(200, 200, 0);
		
		sphere(200);
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[]{ "com.madsim.tests.RotationInterpreterTest" });
	}

}
