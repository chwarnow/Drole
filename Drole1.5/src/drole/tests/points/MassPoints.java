package drole.tests.points;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import processing.core.PApplet;
import processing.core.PVector;

public class MassPoints extends PApplet {

	private static final long serialVersionUID = 1L;

	private GLModel model;
	
	private GLGraphics glg;
	
	private int numPoints = 2000000;
	
	private PVector[] points = new PVector[numPoints];
	
	private int currentPoint = 0;
	
	private float dim = 1000;
	
	public void setup() {
		size(1200, 1000, GLGraphics.GLGRAPHICS);
		smooth();
		
		model = new GLModel(this, numPoints, POINTS, GLGraphics.STATIC);

		for(int i = 0; i < numPoints; i++) {
			points[i] = new PVector(random(-dim, dim), random(-dim, dim), random(-dim, dim));
		}
		
		model.beginUpdateVertices();
			for(int i = 0; i < numPoints; i++) {
				model.updateVertex(i, random(-dim, dim), random(-dim, dim), random(-dim, dim));
			}
		model.endUpdateVertices();
		
		model.initColors();
		model.setColors(200);
	}
	
	public void update() {
		model.beginUpdateVertices();
			model.updateVertex(currentPoint, points[currentPoint].x, points[currentPoint].y, points[currentPoint].z);
			currentPoint++;
		model.endUpdateVertices();
	}
	
	public void draw() {
//		update();
		
		glg = (GLGraphics)g;
		glg.beginGL();
		background(40);
		
		translate(width/2, height/2, 0);
		rotateX(map(mouseY, 0, height, radians(0), radians(360)));
		rotateY(map(mouseX, 0, width, radians(0), radians(360)));
		
			glg.model(model);
			
		glg.endGL();
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[]{"drole.tests.points.MassPoints"});
	}
	
}
