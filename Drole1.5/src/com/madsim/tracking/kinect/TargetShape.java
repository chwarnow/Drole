package com.madsim.tracking.kinect;

import processing.core.PGraphics;
import processing.core.PVector;

public abstract class TargetShape {

	public PVector offset, position, dimension;
	
	public abstract boolean contains(PVector point);
	
	public abstract void draw(PGraphics g);
	
}
