package drole.tests.dla;

import java.util.ArrayList;

import javax.media.opengl.GL;

import com.madsim.common.FileUtil;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import processing.core.PApplet;
import processing.core.PVector;

public class MassPointsDLA2 extends PApplet {

	private static final long serialVersionUID = 1L;

	private GLModel pointModel, lineModel;
	
	private GLGraphics glg;
	
	private ArrayList<PVector> points;
	
	@SuppressWarnings("unchecked")
	public void setup() {
		size(1200, 1000, GLGraphics.GLGRAPHICS);
		smooth();
		
		points = (ArrayList<PVector>)FileUtil.loadSerializedObjectFromFile("data/dla/spiral.dla");
		
		pointModel = initializeModelWithDLA(POINTS, points, 1);
		
		lineModel = initializeModelWithDLA(LINES, points, 2);
	}

	private GLModel initializeModelWithDLA(int method, ArrayList<PVector> points, int cm) {
		GLModel model = new GLModel(this, points.size(), method, GLGraphics.STATIC);
		model.setPointSize(20f);
		
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
		
		println(minX+":"+maxX+":"+minY+":"+maxY+":"+minZ+":"+maxZ);
		
		model.initColors();
		
		model.beginUpdateColors();
			for(int i = 0; i < points.size(); i++) {
				if(cm == 1) model.updateColor(i, map(points.get(i).x, minX, maxX, 100, 200), map(points.get(i).y, minY, maxY, 100, 200), map(points.get(i).z, minZ, maxZ, 100, 200));
				else model.updateColor(i, 100, 100);
			}
		model.endUpdateColors();
		
		return model;
	}
	
	public void draw() {
		glg = (GLGraphics)g;

//		glg.beginGL();
		background(40);
		
		translate(width/2, height/2, 0);
		scale(30, 30, 30);
		rotateX(map(mouseY, 0, height, radians(0), radians(360)));
		rotateY(map(mouseX, 0, width, radians(0), radians(360)));
		
			strokeWeight(10);
			beginShape(QUADS);
				for(int i = 0; i < points.size(); i++) {
					stroke(map(i, 0, points.size(), 100, 200), 0, 100, 200);
//					fill(200, 20);
					vertex(points.get(i).x, points.get(i).y, points.get(i).z);
				}
			endShape();
			
			
//			glg.model(pointModel);
			/*
			glg.beginGL();
			
				glg.model(lineModel);
			
			glg.endGL();
			*/
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[]{"drole.tests.dla.MassPointsDLA2"});
	}
	
}
