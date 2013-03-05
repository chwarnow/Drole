package com.madsim.ui.kinetics.gestures;

import java.util.ArrayList;

import processing.core.PVector;

import com.madsim.tracking.kinect.Kinect;

public class AngleDetection {

	private String name;
	
	private Kinect kinect;
	
	public static short IN_ANGLE = 10, OUT_ANGLE = 20;
	
	private int joint1, joint2;
	
	private float targetAngle;
	
	private int inDir;
	
	private ArrayList<AngleDetectionListener> listeners = new ArrayList<AngleDetectionListener>();
	
	private int sampleSize = 30;
	
	private int[] inOutSampling = new int[sampleSize];
	
	private int sampleIndex = 0;
	
	private int lastSampleResult = -1;
	
	private short status = OUT_ANGLE;
	
	private boolean locked = false;
	
	public AngleDetection(String name, Kinect kinect, int joint1, int joint2, float targetAngle, int inDir) {
		this.name			= name;
		this.kinect			= kinect;
		this.joint1			= joint1;
		this.joint2			= joint2;
		this.targetAngle 	= targetAngle;
		this.inDir			= inDir;
		
		for(int i = 0; i < sampleSize; i++) inOutSampling[i] = 0;
	}
	
	public void lock() {
		locked = true;
		lostAngle();
	}

	public void unlock() {
		locked = false;
	}	
	
	public short status() {
		return status;
	}
	
	public void addListener(AngleDetectionListener l) {
		listeners.add(l);
	}
	
	public void update() {
		PVector v1 = kinect.getJoint(joint1);
		PVector v2 = kinect.getJoint(joint2);
		
		if(v1 == Kinect.IGNORED_POSITION) return;
		
//		float ca = PVector.angleBetween(v1, v2);
		float ca = v1.y;
		
//		System.out.println(name+" : "+(ca + (v1.z * 0.002909))+" : "+v1.z);
//		System.out.println(name+" : "+(ca)+" : "+(v1.z - v2.z));
//		System.out.println(name+" :  hand > "+v1.y);
//		System.out.println(name+" :  shoulder > "+v2.y);
		
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
		
		if(sampleD >= 0.5f) inAngle();
		else lostAngle();
	}
	
	private void inAngle() {
		if((lastSampleResult != 1 || lastSampleResult == -1) && !locked) {
			lastSampleResult = 1;
			System.out.println("In angle "+name);
			status = IN_ANGLE;
			for(AngleDetectionListener l : listeners) l.inAngle(name);
		}
	}

	private void lostAngle() {
		if((lastSampleResult != 0 || lastSampleResult == -1) && !locked) {	
			lastSampleResult = 0;
			System.out.println("Out angle "+name);
			status = OUT_ANGLE;
			for(AngleDetectionListener l : listeners) l.lostAngle(name);
		}
	}
	
}
