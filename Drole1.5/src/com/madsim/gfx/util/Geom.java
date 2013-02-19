package com.madsim.gfx.util;

import processing.core.PVector;

public class Geom {
	
    /*
     *  http://www.opengl.org/wiki/Calculating_a_Surface_Normal
     *  
     * 	Begin Function CalculateSurfaceNormal (Input Triangle) Returns Vector
     * 
     * 	Set Vector U to (Triangle.p2 minus Triangle.p1)
     * 	Set Vector V to (Triangle.p3 minus Triangle.p1)
     * 
     * 	Set Normal.x to (multiply U.y by V.z) minus (multiply U.z by V.y)
     * 	Set Normal.y to (multiply U.z by V.x) minus (multiply U.x by V.z)
     * 	Set Normal.z to (multiply U.x by V.y) minus (multiply U.y by V.x)
     * 
     * 	Returning Normal
     * 
     * 	End Function
     */
    
	public static PVector calculateNormal(PVector v1, PVector v2, PVector v3) {
		PVector U = PVector.sub(v2, v1);
		PVector V = PVector.sub(v3, v1);
		
		PVector Normal = new PVector(
			(U.y * V.z) - (U.z * V.y),
			(U.z * V.x) - (U.x * V.z),
			(U.x * V.y) - (U.y * V.x)
		);
		
		return Normal;
	}
	
}
