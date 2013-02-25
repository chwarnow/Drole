package drole.tests.dla;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.madsim.common.FileUtils;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.PointOctree;
import toxi.geom.Vec3D;
import toxi.sim.dla.DLA;
import toxi.sim.dla.DLAConfiguration;
import toxi.sim.dla.DLAGuideLines;
import toxi.util.FileUtils;

public class MassPointsDLA extends PApplet {

	private static final long serialVersionUID = 1L;

	private GLModel pointModel, lineModel;
	
	private GLGraphics glg;
	
	private int numPoints = 2000000;
	
	private int currentPoint = 0;
	
	private float dim = 1000;
	
	private DLA dla;
	
	private ArrayList<PVector> points;
	
	@SuppressWarnings("unchecked")
	public void setup() {
		size(1200, 1000, GLGraphics.GLGRAPHICS);
		smooth();
		
		points = (ArrayList<PVector>)FileUtils.loadSerializedObjectFromFile("data/dla/spiral.dla");
		
		pointModel = new GLModel(this, points.size(), POINTS, GLGraphics.STATIC);
		lineModel = new GLModel(this, points.size(), LINES, GLGraphics.STATIC);
		
		float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
		float x, y, z;
		
		pointModel.beginUpdateVertices();
		lineModel.beginUpdateVertices();
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
				pointModel.updateVertex(i, x, y, z);
				lineModel.updateVertex(i, x, y, z);
			}
		pointModel.endUpdateVertices();
		lineModel.endUpdateVertices();
		
		println(minX+":"+maxX+":"+minY+":"+maxY+":"+minZ+":"+maxZ);
		
		pointModel.initColors();
		lineModel.initColors();
		
		pointModel.beginUpdateColors();
		lineModel.beginUpdateColors();
			for(int i = 0; i < points.size(); i++) {
				pointModel.updateColor(i, map(points.get(i).x, minX, maxX, 100, 200), map(points.get(i).y, minY, maxY, 100, 200), map(points.get(i).z, minZ, maxZ, 100, 200));
				lineModel.updateColor(i, map(points.get(i).x, minX, maxX, 100, 200), map(points.get(i).y, minY, maxY, 100, 200), map(points.get(i).z, minZ, maxZ, 100, 200));
			}
		pointModel.endUpdateColors();
		lineModel.endUpdateColors();
		
		/*
		initDLA();
		//dla.update(13040000);
		
		model.beginUpdateVertices();
			walkOctreeAndSavePointsToModel(dla.getParticleOctree(), 0xffff0000);
		model.endUpdateVertices();
		
		println(currentPoint);
		*/
	}
	
	private void initDLA() {
		// compute spiral key points (every 45 degrees)
		ArrayList<Vec3D> points = new ArrayList<Vec3D>();
		for (float theta = -TWO_PI, r = 20; theta < 0.2f * TWO_PI; theta += QUARTER_PI) {
			Vec3D p = Vec3D.fromXYTheta(theta).scale(r);
			p.z = theta * 4;
			points.add(p);
			r *= 0.92;
		}
		// use points to compute a spline and
		// use resulting segments as DLA guidelines
		DLAGuideLines guides = new DLAGuideLines();

		// guides.addCurveStrip(new Spline3D(points).computeVertices(8));
		guides.addPoint(new Vec3D(0, 0, 0));

		// create DLA 3D simulation space 128 units wide (cubic)
		dla = new DLA(128);
		// use default configuration
		dla.setConfig(new DLAConfiguration());
		// add guide lines
		dla.setGuidelines(guides);
		// set leaf size of octree
		dla.getParticleOctree().setMinNodeSize(1);
		// add a listener for simulation events
		// listener=new DLAListener();
		// dla.addListener(listener);
	}
	
	// this method recursively paints an entire octree structure
	public void walkOctreeAndSavePointsToModel(PointOctree node, int col) {
		if (node.getNumChildren() > 0) {
			PointOctree[] children = node.getChildren();
			for (int i = 0; i < 8; i++) {
				if (children[i] != null) {
					walkOctreeAndSavePointsToModel(children[i], col);
				}
			}
		} else {
			java.util.List<Vec3D> points = node.getPoints();
			if (points != null) {
				int numP = points.size();
				for (int i = 0; i < numP; i += 10) {
					Vec3D p = (Vec3D) points.get(i);
					pointModel.updateVertex(i, p.x(), p.y(), p.z());
					currentPoint++;
				}
			}
		}
	}	
	
	public void draw() {
		glg = (GLGraphics)g;
		glg.beginGL();
		background(40);
		
		translate(width/2, height/2, 0);
		scale(30, 30, 30);
		rotateX(map(mouseY, 0, height, radians(0), radians(360)));
		rotateY(map(mouseX, 0, width, radians(0), radians(360)));
		
			noFill();
			strokeWeight(5);
			glg.model(pointModel);
			
			strokeWeight(1);
			glg.model(lineModel);
			
		glg.endGL();
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[]{"drole.tests.dla.MassPointsDLA"});
	}
	
}
