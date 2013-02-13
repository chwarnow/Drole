package com.christopherwarnow.bildwelten.utils;

import processing.core.PVector;

public class Ray {
	public PVector start = new PVector(), end = new PVector();
	
	public Ray(PVector s,PVector e){
		start = s ; end = e;
	}
}