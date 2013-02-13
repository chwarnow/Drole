package drole.tests.menu;

import processing.core.PVector;

public class Ray {
	PVector start = new PVector(), end = new PVector();
	
	Ray(PVector s,PVector e){
		start = s ; end = e;
	}
}
