package com.madsim.drole.mmworld;

import java.util.ArrayList;

import com.madsim.common.FileUtils;

import processing.core.PApplet;
import processing.core.PVector;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;

public class DLAUtils {
	
	@SuppressWarnings("unchecked")
	public static ArrayList<PVector> getDLAFromFile(String filename) {
		return (ArrayList<PVector>)FileUtils.loadSerializedObjectFromFile(filename);
	}
	
	public static GLModel loadDLAToModel(String filename, PApplet p, int method) {
		ArrayList<PVector> points = getDLAFromFile(filename);
		return initializeModelWithDLA(p, method, points);
	}
	
	public static GLModel initializeModelWithDLA(PApplet p, int method, ArrayList<PVector> points) {
		GLModel model = new GLModel(p, points.size(), method, GLGraphics.STATIC);

		float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
		float x, y, z;
		
		model.beginUpdateVertices();
			for(int i = 0; i < points.size(); i++) {
				x = points.get(i).x;
				y = points.get(i).y;
				z = points.get(i).z;
				if(x < minX) minX = x;
				if(x > maxX) maxX = x;
				
				if(y < minY) minY = y;
				if(y > maxY) maxY = y;
				
				if(z < minZ) minZ = z;
				if(z > maxZ) maxZ = z;				
				model.updateVertex(i, x, y, z);
			}
			model.endUpdateVertices();
		
		return model;
	}
	
}
