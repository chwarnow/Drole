package com.marctiedemann.spektakel;

import java.util.Iterator;

import com.madsim.engine.Engine;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.SphericalHarmonics;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;

public class ToxicSystem extends ParticleSystem{
	
	private TriangleMesh toxiMesh = new TriangleMesh();
	private int trailLength;

	
	public ToxicSystem(Engine e, VerletPhysics _physics, float mySize, float x, float y, float z){
		
		super(e,_physics,mySize,x,y,z);
		
		spawnNew();
	}
	
	
public void update(){
		super.update();
		
		updateTrailPositions();
	}
	
public void draw(GLGraphics renderer){
	
	super.draw(renderer);
	
	e.setupModel(trails);
	renderer.model(trails);
	
}
	
	void randomizeMesh() {
		float[] m = new float[8];
		for (int i = 0; i < 8; i++) {
			m[i] = (int) e.p.random(20);
		}
		SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
		toxiMesh = (TriangleMesh) b.createMesh(null,24, 100);
	}

	public void spawnNew() {

		bigParticle.clear();
		clean();

		shockwave = true;
		boomPower = initalBoomPower;
		boomForce = new AttractionBehavior(new VerletParticle(new Vec3D(x, y, z)), 1000, boomPower * 0.3f, 0.1f);
		physics.addBehavior(boomForce);

		randomizeMesh();

		int targetSize = 5;
		int targetYOffset = 500;

		Vec3D targetAngle = new Vec3D(e.p.random(-1, 1) * targetYOffset,
				e.p.random(-1, 1) * targetYOffset, e.p.random(-1, 1)
						* targetYOffset);

		for (Iterator i = toxiMesh.faces.iterator(); i.hasNext();) {
			Face face = (Face) i.next();

			ShapedParticle newPart = new ShapedParticle(e.p, x + face.a.x
					- targetAngle.x / 2, y + face.a.y - targetAngle.y / 2,
					z + face.a.z - targetAngle.x / 2);

			// p.println("x "+f.a.x+" y "+f.a.y+" z "+f.a.z);

			Vec3D toxicTarget = new Vec3D(x + targetAngle.x + face.a.x
					* targetSize, y + targetAngle.x + face.a.y * targetSize,
					z + targetAngle.x + face.a.z * targetSize);

			
			//maybe replacxe with low force spring to reduce bouncing
			AttractionBehavior toxicForce = new AttractionBehavior(toxicTarget,
					3000, -boomPower * 2, 0.005f);
			newPart.setUniqueTarget(toxicForce);
			// newPart.addBehavior(boomForce);

			bigParticle.add(newPart);
			physics.addParticle(newPart);

			/*
			 * newPart = new Particle(p, mySize / 2, f.c.x+x, f.c.y+y, f.c.z+z);
			 * bigParticle.add(newPart); physics.addParticle(newPart);
			 */
		}

		initSprites();
		initTrails();

	}
	
	void initTrails() {

		numPoints = bigParticle.size();

		// one size fits all
		trailLength = bigParticle.get(0).tailSize;

		trails = new GLModel(e.p, numPoints * (trailLength + 1) * 4*2, GLModel.LINES, GLModel.DYNAMIC);

		updateTrailPositions();

		trails.initColors();
		trails.setColors(250, 10);
		
		trails.setLineWidth(2);

		trails.setBlendMode(PApplet.ADD);

	}

	void updateTrailPositions() {

		numPoints = bigParticle.size();

		coords = new float[4 * numPoints * (trailLength + 1)*2];

		// p.println("updatimng" + myID + " num " + numPoints + " size "
		// + bigParticle.size());

		
		int numSections=trailLength+1;
		int pointsToMesh = 2;
		
		
		for (int i = 0; i < numPoints; i++) {

			ShapedParticle oneParticle = bigParticle.get(i);
			
			
			for (int j = 0; j < trailLength - 1; j++) {
				
				int step = (i*numSections*pointsToMesh*4)+(j*pointsToMesh*4);
				
				coords[step + 0] = bigParticle.get(i).x;
				coords[step + 1] = bigParticle.get(i).y;
				coords[step + 2] = bigParticle.get(i).z;
				coords[step + 3] = 1.0f; // The W coordinate of each point
				
				Vec3D trailPoint = oneParticle.getTailPoint(0);
				
				coords[step + 0] = trailPoint.x;
				coords[step + 1] = trailPoint.y;
				coords[step + 2] = trailPoint.z;
				coords[step + 3] = 1.0f; // The W coordinate of each point
				
				
				for (int k = 0; k < pointsToMesh; k++) {

					step+=(k*4);
									
					trailPoint = oneParticle.getTailPoint(j + k);

					coords[step + 0] = trailPoint.x;
					coords[step + 1] = trailPoint.y;
					coords[step + 2] = trailPoint.z;
					coords[step + 3] = 1.0f; // The W coordinate of each point
												// must
												// be
				}

			}
		}

		// p.println(coords);

		trails.updateVertices(coords);
	}


}

