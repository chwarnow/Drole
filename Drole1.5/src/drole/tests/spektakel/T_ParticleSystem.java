package drole.tests.spektakel;

import toxi.physics.VerletParticle;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Iterator;

import com.marctiedemann.spektakel.ShapedParticle;

import codeanticode.glgraphics.*;

import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletSpring;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;

import toxi.math.waves.*;
import toxi.geom.*;
import toxi.geom.mesh.*;

import processing.core.PApplet;

public class T_ParticleSystem extends T_Particle {

	boolean shockwave = false;
	float initalBoomPower = -5.5f;
	float boomPower = initalBoomPower;
	float boomFalloff = 0.01f;
	
	float initalSpringPower = 0.0001f;
	float springPower = initalSpringPower;
	float springFallOff = 0.001f;
	
	AttractionBehavior boomForce;

	boolean exploded = false;
	
	
	PApplet p;
	VerletPhysics physics;

	ArrayList<T_ShapeParticle> bigParticle;

	TriangleMesh mesh = new TriangleMesh();

	protected GLModel sprites;
	protected GLModel trails;

	GLTexture tex;
	float[] coords;
	float[] colors;

	int numPoints = 0;
	int trailLength;

	int myID;

	public T_ParticleSystem(PApplet p, 
			VerletPhysics _physics, float x, float y, float z) {

		super(p,  x, y, z);

		this.p = p;

		this.physics = _physics;

		bigParticle = new ArrayList<T_ShapeParticle>();

		myID = (p.frameCount);
	
		
	}

	void spawnNew(){
	
		
		initSprites();
		
	}

	void initSprites() {

		numPoints = bigParticle.size();
		p.println("Initializingr" + numPoints + " Sparks");

		sprites = new GLModel(p, numPoints * 4, GLModel.POINT_SPRITES,
				GLModel.DYNAMIC);
		tex = new GLTexture(p, "images/particle.png");

		updateSpritePositions();
		sprites.initColors();
		updateSpriteColors();

		sprites.initTextures(1);
		sprites.setTexture(0, tex);
		// Setting the maximum sprite to the 90% of the maximum point size.
		// model.setMaxSpriteSize(0.9f * pmax);
		// Setting the distance attenuation function so that the sprite size
		// is 20 when the distance to the camera is 400.
		sprites.setSpriteSize(100, 1000);
		sprites.setBlendMode(PApplet.ADD);

	}

	
	void updateSpritePositions() {

		numPoints = bigParticle.size();

		coords = new float[4 * numPoints];

		// p.println("updatimng" + myID + " num " + numPoints + " size "
		// + bigParticle.size());

		for (int i = 0; i < numPoints; i++) {

			T_Particle oneParticle = bigParticle.get(i);

			coords[4 * i + 0] = oneParticle.x;
			coords[4 * i + 1] = oneParticle.y;
			coords[4 * i + 2] = oneParticle.z;

			coords[4 * i + 3] = 1.0f; // The W coordinate of each point must
										// be
		}

		sprites.updateVertices(coords);
	}

	void updateSpriteColors() {

		colors = new float[4 * numPoints];

		for (int i = 0; i < numPoints; i++) {

			float newAlpha = (bigParticle.get(i).getTimeToLife() * 0.003921f)+ p.random(-0.5f, 0.5f);

			colors[4 * i + 0] = 1;
			colors[4 * i + 1] = 0.1f + newAlpha * 0.4f;
			colors[4 * i + 2] = newAlpha - 1;
			colors[4 * i + 3] = newAlpha*bigParticle.get(i).myAlpha;

		}

		// THISNOGOOD
		try {
			sprites.updateColors(colors);
		} catch (Exception e) {
			System.out.println("UUUUUUUPAS  " + e);
		}
		;
	}

	void updateSpriteColor(int num, float rgb, float alpha) {

		sprites.updateColor(num, rgb, alpha);
	}

	public void drawGrid() {

		for (int i = 0; i < bigParticle.size(); i++) {

			VerletParticle p1 = bigParticle.get(i);

			for (int j = i + 1; j < bigParticle.size(); j++) {

				VerletParticle p2 = bigParticle.get(j);
				p.pushStyle();
				p.stroke(155, 50);
				p.line(p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z());
				p.popStyle();
			}
		}
	}

	protected void updateForce() {

		if (shockwave) {

			boomPower *= 1-boomFalloff;
			springPower *= 1-springFallOff;
			
			boomForce.setStrength(boomPower * 0.5f);

			for (int i = 0; i < bigParticle.size(); i++) {
				// boomForce.setStrength(boomPower);
				bigParticle.get(i).setBehaviorStrenght(springPower);
			}

			if (boomPower > -0.00001) {

				/*
				 * boomPower=-5.0f;
				 * 
				 * boomForce.setStrength(boomPower*0.1f);
				 * 
				 * for(int i=0;i<bigParticle.size();i++){
				 * boomForce.setStrength(boomPower);
				 * bigParticle.get(i).setBehaviorStrenght(boomPower); }
				 */

				physics.removeBehavior(boomForce);
			}
				if (springPower < 0.0000001) {

				for (int i = 0; i < bigParticle.size(); i++) {
				//	bigParticle.get(i).removeBehavior(boomForce);
					
					physics.removeSpring(bigParticle.get(i).shapeForce);
				}

				shockwave = false;
			}

		}

	}
	
	

	
	public void update(){
		super.update();
		

		for (int i = 0; i < bigParticle.size(); i++) {

			T_Particle pa = bigParticle.get(i);

			if (pa.isDead()) {

				// if dead make new system
				p.println("aaahhhhhrrrrrgggg.........");

			
				// as it's tricky to delete from VBO just make invisible.
				sprites.updateColor(i, 0, 0);

				bigParticle.remove(i);

			}
		}

		/*
		 * int count=0; Iterator<Particle> ip = bigParticle.iterator(); while
		 * (ip.hasNext()) { Particle pa = ip.next(); // pa.draw(); count++; if
		 * (pa.isDead()) {
		 * 
		 * // if dead make new system p.println("aaahhhhhrrrrrgggg.........");
		 * 
		 * float chance = p.random(1000);
		 * 
		 * if (chance < 2) { p.println("yipee i am born again!!"); float newSize
		 * = mySize * 0.75f; ParticleSystem newSystem = new ParticleSystem(p,
		 * pSystem, physics, newSize, pa.x, pa.y, pa.z); pSystem.add(newSystem);
		 * }
		 * 
		 * // p.println(ip.hashCode());
		 * 
		 * //as it's tricky to delete from VBO just make invisible.
		 * model.updateColor(count, 0,0);
		 * 
		 * ip.remove(); } }
		 */
		
		updateTrailPositions();
		updateSpritePositions();
		updateSpriteColors();
		updateForce();

//		updateTrailPositions();
	}
	
	public void draw(GLGraphics renderer) {

		// drawGrid();
		// drawErmitter(renderer);

	

		renderer.model(sprites);

		
		renderer.model(trails);
	}
	
	void initTrails() {

		

		trails = new GLModel(p, numPoints * (trailLength + 1) * 4*2, GLModel.LINES, GLModel.DYNAMIC);

		updateTrailPositions();

		trails.initColors();
		trails.setColors(250, 30);
		
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

			T_ShapeParticle oneParticle = bigParticle.get(i);
			
			
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

	public void drawErmitter(GLGraphics renderer) {

		renderer.pushMatrix();

		float ermitterAlpha = p.map(boomPower, 0, -1.5f, 0, 255);

		// this is strange and i have no clue why i have to translate back
		// again....
		renderer.translate(p.width / 2, p.height / 2, 950);
		renderer.translate(x, y, z);
		renderer.stroke(255, ermitterAlpha);
		renderer.strokeWeight(150);
		renderer.point(0, 0);
		renderer.popMatrix();
	}

	public void clean() {

		physics.removeBehavior(boomForce);

		// model.delete();

	}

	public boolean isEmpty() {
		if (bigParticle.size() < 1)
			return true;
		else
			return false;
	}

}