package com.madsim.common.geom;

import processing.core.PApplet;
import processing.core.PVector;

public class CircleUtils {

	public static PVector getPointOnCircleXZ(float a, float world, float d, float xOff, float yOff) {
		float xca = a + (((PApplet.TWO_PI / NUM_WORLDS) + (d * xOff)) * world);
		float zca = a + ((PApplet.TWO_PI / NUM_WORLDS) * world);

		float x = r * sin(xca);
		float y = yOff;
		float z = (r * cos(zca)) - r;

		return new PVector(x, y, z);
	}
	
}
