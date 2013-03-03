package com.madsim.common.geom;

import java.awt.Point;

public class Convertion {

	public static float[] getFloat2Array(Point p) {
		return new float[]{p.x, p.y};
	}
	
	public static float[] getFloat3Array(Point p) {
		return new float[]{p.x, p.y, 0.0f};
	}
	
}
