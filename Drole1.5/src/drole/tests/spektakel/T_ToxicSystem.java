package drole.tests.spektakel;
import java.util.ArrayList;
import java.util.Iterator;

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
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.AttractionBehavior;

public class T_ToxicSystem extends T_ParticleSystem{
	
	public T_ToxicSystem(PApplet p,
			VerletPhysics _physics,float x, float y, float z){
		
		super(p,_physics,x,y,z);
		
		spawnNew();
	}
	
	
public void update(){
		super.update();
		
		
	}
	
public void draw(GLGraphics renderer){
	
	super.draw(renderer);
	

	
}
	
	void randomizeMesh() {
		float[] m = new float[8];
		for (int i = 0; i < 8; i++) {
			m[i] = (int) p.random(20);
		}
		SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
		mesh = (TriangleMesh) b.createMesh(null,18, 100);
	}

	public void spawnNew() {

		bigParticle.clear();
		clean();

		shockwave = true;
		boomPower = initalBoomPower;
		boomForce = new AttractionBehavior(this, 1000, boomPower * 0.3f, 0.1f);
		physics.addBehavior(boomForce);

		randomizeMesh();

		int targetSize = 7;
		int targetYOffset = 400;

		Vec3D targetAngle = new Vec3D(p.random(-1, 1)*targetYOffset ,
				p.random(-1, 1)*targetYOffset , p.random(-1, 1)*targetYOffset
						);

		for (Iterator i = mesh.faces.iterator(); i.hasNext();) {
			Face face = (Face) i.next();

			T_ShapeParticle newPart = new T_ShapeParticle(p, x() + face.a.x
					- targetAngle.x / 2, y() + face.a.y - targetAngle.y / 2,
					z() + face.a.z - targetAngle.x / 2);

			// p.println("x "+f.a.x+" y "+f.a.y+" z "+f.a.z);

			Vec3D toxicTarget = new Vec3D(x() + targetAngle.x + face.a.x
					* targetSize, y() + targetAngle.x + face.a.y * targetSize,
					z() + targetAngle.x + face.a.z * targetSize);

			VerletParticle targetPoint = new VerletParticle(toxicTarget);
			targetPoint.lock();
			
			
			//maybe replacxe with low force spring to reduce bouncing
			VerletSpring toxicForce = new VerletSpring(newPart,targetPoint,
					0, springPower);
//			physics.addParticle(targetPoint);
			physics.addParticle(newPart);
			physics.addSpring(toxicForce);
			newPart.giveSpring(toxicForce);
			// newPart.addBehavior(boomForce);

			bigParticle.add(newPart);
		

			/*
			 * newPart = new Particle(p, mySize / 2, f.c.x+x, f.c.y+y, f.c.z+z);
			 * bigParticle.add(newPart); physics.addParticle(newPart);
			 */
		}
		
		numPoints = bigParticle.size();

		// one size fits all
		trailLength = bigParticle.get(0).tailSize;

		initSprites();
		initTrails();

	}
	
	


}
