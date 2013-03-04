package com.madsim.ui.kinetics.gestures;

import java.util.ArrayList;

import processing.core.PVector;

import com.madsim.tracking.kinect.Kinect;

public class AngleDetection {

	private String name;
	
	private Kinect kinect;
	
	private int joint1, joint2;
	
	private float targetAngle;
	
	private int inDir;
	
	private ArrayList<AngleDetectionListener> listeners = new ArrayList<AngleDetectionListener>();
	
	private int sampleSize = 10;
	
	private int[] inOutSampling = new int[sampleSize];
	
	private int sampleIndex = 0;
	
	private int lastSampleResult = -1;
	
	public AngleDetection(String name, Kinect kinect, int joint1, int joint2, float targetAngle, int inDir) {
		this.name			= name;
		this.kinect			= kinect;
		this.joint1			= joint1;
		this.joint2			= joint2;
		this.targetAngle 	= targetAngle;
		this.inDir			= inDir;
		

		for(int i = 0; i < sampleSize; i++) inOutSampling[i] = 0;
	}
	
	public void addListener(AngleDetectionListener l) {
		listeners.add(l);
	}
	
	public void update() {
		PVector v1 = kinect.getJoint(joint1);
		PVector v2 = kinect.getJoint(joint2);
		
		if(v1 == Kinect.IGNORED_POSITION) return;
		
		float ca = PVector.angleBetween(v1, v2);
		
		if(inDir > 0 && ca >= targetAngle) {
			addSample(1);
		} else if(inDir < 0 && ca < targetAngle) {
			addSample(1);
		} else {
			addSample(0);
		}
		
		checkSample();
	}
	
	private void addSample(int sample) {
		if(sampleIndex == sampleSize) sampleIndex = 0;
		inOutSampling[sampleIndex] = sample;
		sampleIndex++;
	}
	
	private void checkSample() {
		float sampleD = 0;
		
		for(int i = 0; i < sampleSize; i++) sampleD += inOutSampling[i];
		
		sampleD /= sampleSize;
		
		System.out.println(sampleD);
		
		if(sampleD >= 0.5f) inAngle();
		else lostAngle();
	}
	
	private void inAngle() {
		if(lastSampleResult != 1 || lastSampleResult == -1) {
			lastSampleResult = 1;
			System.out.println("In angle "+name);
			for(AngleDetectionListener l : listeners) l.inAngle(name);
		}
	}

	private void lostAngle() {
		if(lastSampleResult != 0 || lastSampleResult == -1) {	
			lastSampleResult = 0;
			System.out.println("Out angle "+name);
			for(AngleDetectionListener l : listeners) l.lostAngle(name);
		}
	}
	
}
