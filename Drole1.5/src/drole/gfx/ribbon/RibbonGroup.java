package drole.gfx.ribbon;

import java.util.ArrayList;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import drole.Main;
import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.math.noise.PerlinNoise;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;
import toxi.physics.constraints.BoxConstraint;
import toxi.physics.constraints.ParticleConstraint;
import toxi.physics.constraints.SphereConstraint;

public class RibbonGroup extends Drawable {

	private static final long serialVersionUID = 1L;

	private int					id;
	public float 				ribbonR = .01f;
	public float 				ribbonG = .01f;
	public float 				ribbonB = .01f;

	private float 				seed;
	private float 				seedSpeed = .0014f;
	private int 				numQuadsPerRibbon;
	private float 				sphereSize;
	private int 				vertexCount = 0;
	private float 				quadHeight;
	private int					numRibbons;

	private Ribbon3D[] 			particles;

	private VerletPhysics 		physics;
	private VerletParticle 		head, tail;
	//private int 				REST_LENGTH;

	// private VerletParticle 		pivot;
	// private VerletSpring[] 		pivotSprings;
	// private ArrayList<Float> 	springLengths = new ArrayList<Float>();

	private AABB 				worldBox;
	private ParticleConstraint 	sphereA, cubeConst;

	private boolean 			isPivoting = false;

	private GLModel 			imageQuadModel;

	private PerlinNoise			noise;
	private float				age = 0;
	
	private boolean				isFalling = false;
	
	public RibbonGroup(Engine e, float sphereSize, int numRibbons, int numJointsPerRibbon, float quadHeight, int id) {
		super(e);

		this.seed				= e.p.random(1000);
		this.numRibbons			= numRibbons;
		this.numQuadsPerRibbon	= numJointsPerRibbon;
		this.sphereSize			= sphereSize;
		this.quadHeight			= quadHeight;
		this.id					= id;

		// create drole particles
		particles = new Ribbon3D[numRibbons];

		// create particles
		for (int i = 0; i < numRibbons; i++) {			
			// 			PVector startPosition = new PVector(parent.random(-3.1414f, 3.1414f), parent.random(-3.1414f, 3.1414f), parent.random(-3.1414f, 3.1414f));
			PVector startPosition = new PVector(e.p.random(-sphereSize, sphereSize),
					e.p.random(-sphereSize, sphereSize),
					e.p.random(-sphereSize, sphereSize));
			startPosition.normalize();
			startPosition.mult(sphereSize);
			particles[i] = new Ribbon3D(e, startPosition, numJointsPerRibbon, false);
			particles[i].dimension(30, 0, 0);
			vertexCount += particles[i].getVertexCount();
		}

		
		// create collision sphere at origin, replace OUTSIDE with INSIDE to
		// keep particles inside the sphere
		sphereA = new SphereConstraint(new Sphere(new Vec3D(), sphereSize * .8f), SphereConstraint.OUTSIDE);
		// sphereB = new SphereConstraint(new Sphere(new Vec3D(), sphereSize), SphereConstraint.INSIDE);

		worldBox = new AABB(new Vec3D(), sphereSize);
		cubeConst = new BoxConstraint(worldBox);

		physics = new VerletPhysics();

		// weak gravity along Y axis
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 1.0f, 0)));

		// set bounding box to 100% of out virtual world
		physics.setWorldBounds(worldBox);

		VerletParticle prev = null;

		float mainLength = e.p.random(5, 20);

		for (int i = 0; i < numRibbons; i++) {
			// create particles at random positions outside sphere
			VerletParticle p = new VerletParticle(Vec3D.randomVector().scaleSelf(sphereSize * .2f));

			// set sphere as particle constraint
			p.addConstraint(sphereA);
			p.addConstraint(cubeConst);
			//p.lock();
			physics.addParticle(p);
			/*
			if(prev != null) {
				float thisLength = e.p.random(REST_LENGTH);
				springLengths.add(thisLength);
				physics.addSpring(new VerletSpring(prev, p, thisLength, 0.0025f));
				springLengths.add(thisLength * mainLength);
				physics.addSpring(new VerletSpring(physics.particles.get((int) e.p.random(i)), p, thisLength * mainLength, 0.0025f));
			}

			prev = p;
			*/
		}
		/*
		head = physics.particles.get(0);
		head.lock();
		 */
		// tail = physics.particles.get(physics.particles.size() - 1);
		// tail.lock();

		// create a model that uses quads
		imageQuadModel = new GLModel(e.p, vertexCount*4, PApplet.QUADS, GLModel.DYNAMIC);
		imageQuadModel.initColors();
		imageQuadModel.initNormals();
		imageQuadModel.setColors(0);
		
		noise = new PerlinNoise();
		noise.noiseSeed(0);
	}

	public void update() {
		super.update();
		if(age++%100 == 0) noise.noiseSeed((long)age/100);
		/*
		if(!isPivoting) {
			seed += seedSpeed;
			// if(e.p.frameCount < 200 || e.p.frameCount > 400){
			// update particle movement
			float angleX = e.p.noise(seed)*3.1414f*4;
			float angleY = e.p.noise(seed*.5f)*3.1414f*4;
			float angleZ = seed*10f + angleX;
			float radius = sphereSize * (.8f + e.p.noise(head.x*.001f, head.y*.001f, head.z*.001f)*.1f);

			head.set(
					e.p.cos(angleX) * radius,
					e.p.sin(angleY) * radius,
					e.p.sin(angleZ) * radius
			);

			// iterate through springs and stiffen or widen them to achieve noise like aesthetics?
			int springID = 0;
			for(VerletSpring s : physics.springs) s.setRestLength(springLengths.get(springID++) * noiseLUT[noiseID]);
			noiseID = (int)e.p.abs(e.p.cos(head.x*.01f)*49f);
		}

		// update sim
		physics.update();
		 */

		/*
		// then all particles as dots
		int index = 0;
		for(int i = 0; i < particles.length; i++) {
			VerletParticle p = physics.particles.get(i);
			particles[index++].update(p.x, p.y, p.z);
		}
		 */

		float offsetA = 0;//e.p.frameCount*.01f;
		float offsetB = 10000;// + e.p.frameCount*.01f;
		float stepSize = 12;
		float noiseScale = 750;// + e.p.cos(e.p.frameCount*.1f + id) * 120;
		float noiseStrength = 20;
		
		for (int i = 0; i < numRibbons; i++) {	
			Ribbon3D agent = particles[i];
			if(!isFalling) {
			// let agent wander

				if(!agent.isDying) {
					// get current position
					PVector currPosition = new PVector(agent.getFirstPoint().x, agent.getFirstPoint().y, agent.getFirstPoint().z);
					
					float angleY = noise.noise(currPosition.x/noiseScale+offsetA, currPosition.y/noiseScale + offsetA, currPosition.z/noiseScale + offsetA) * noiseStrength; 
					float angleZ = noise.noise(currPosition.x/noiseScale+offsetB, currPosition.y/noiseScale + 10000, currPosition.z/noiseScale + 10000) * noiseStrength;
					// angleY += 3.1414f;//
					
					currPosition.x += e.p.cos(angleZ) * e.p.cos(angleY) * stepSize;// + e.p.sin(id*.1f);
					currPosition.y += e.p.sin(angleZ) * stepSize;// + e.p.sin(id*.1f);
					currPosition.z += e.p.cos(angleZ) * e.p.sin(angleY) * stepSize;// + e.p.sin(id*.1f);
					
					currPosition.normalize();
					currPosition.mult(sphereSize + e.p.abs(e.p.cos(e.p.frameCount*.01f + i*.001f)) * sphereSize*.125f);
					
					
					if(agent.age == agent.currAge) {
						agent.isDying = true;
					}
					agent.age++;
					
					// set new position
					agent.update(currPosition.x, currPosition.y, currPosition.z);
				} else {
					if(agent.age == agent.currAge + agent.getVertexCount()) {
						PVector startPosition = new PVector(e.p.random(-sphereSize, sphereSize),
								e.p.random(-sphereSize, sphereSize),
								e.p.random(-sphereSize, sphereSize));
						startPosition.normalize();
						startPosition.mult(sphereSize + e.p.abs(e.p.cos(e.p.frameCount*.01f + i*.001f)) * sphereSize*.125f);
						
						agent.reset(startPosition);
					} else {
						// get current position
						PVector currPosition = new PVector(agent.getFirstPoint().x, agent.getFirstPoint().y, agent.getFirstPoint().z);
						// set new position
						agent.update(currPosition.x, currPosition.y, currPosition.z);
						
						agent.age++;
					}
				}
			} else {
				// let agents fall down
				
				// copy particle position to agent position
				
				VerletParticle p = physics.particles.get(i);
				PVector fP = new PVector(p.x, p.y, p.z);
				// if(i<1) System.out.println(p.x + " " + p.y + " " + p.z);
				agent.update(fP.x, fP.y, fP.z);
				// if(i<1) System.out.println(agent.getFirstPoint());
			}
		}
		
		// update physics simulation
		if(isFalling) {
			physics.update();
		}
	}

	public void createPivotAt(float x, float y, float z) {
		/*
		pivot = new VerletParticle(x, y, z);
		pivot.lock();
		physics.addParticle(pivot);

		// First tighten all springs
		for(VerletSpring s : physics.springs) {
			s.setRestLength(0.00001f);
			s.setStrength(0.000001f);
		}

		// Add springs between all joints and the pivot
		pivotSprings = new VerletSpring[numPhysicParticles];
		for(int i = 0; i < numPhysicParticles; i++) {
			pivotSprings[i] = new VerletSpring(physics.particles.get(i), pivot, 5, 0.000001f);
			pivotSprings[i].lockB(true);

			physics.addSpring(pivotSprings[i]);
		}

		for(int i = 0; i < physics.particles.size()-2; i++) {
			VerletParticle p = physics.particles.get(i);
			p.removeConstraint(sphereA);
			p.removeConstraint(sphereB);
			p.addConstraint(cubeConst);

			p.applyConstraints();
			p.update();
		}

		head.unlock();

		isPivoting = true;
		 */
	}

	public void deletePivot() {
		// First remove all pivot prings
		/*

		// Remove pivot
		physics.removeParticle(pivot);

		// Relax all other springs
		for(VerletSpring s : physics.springs) s.setRestLength(REST_LENGTH * 20);

		// Randomize all particle positions
		for(VerletParticle p : physics.particles) {
			p.lock();
			p.set(Vec3D.randomVector().scaleSelf(sphereSize * 2));

			p.addConstraint(sphereA);
			p.addConstraint(sphereB);
			p.removeConstraint(cubeConst);

			p.applyConstraints();
			p.update();
			p.unlock();
		}

		head.lock();

		isPivoting = false;
		 */
	}

	public boolean isPivoting() {
		return isPivoting;
	}

	public void draw() {
		g.pushStyle();
		// arrays for storing ribbon vertices
		float[] floatQuadVertices = new float[vertexCount*16];
		float[] floatQuadNormals = new float[vertexCount*16];
		float[] floatQuadColors = new float[vertexCount*16];
		int quadVertexIndex = 0;
		int quadNormalIndex = 0;
		int quadColorIndex = 0;
		for (int i = 0; i < numRibbons; i++) {

			Ribbon3D agent = particles[i];
			// create quads from ribbons
			PVector[] agentsVertices = agent.getVertices();
			int agentVertexNum = agentsVertices.length;

			for(int j=0;j<agentVertexNum-1;j++) {

				PVector thisP = agentsVertices[j];
				PVector nextP = agentsVertices[j+1];
				
				// create quad from above vertices and save in glmodel, then add colors
				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y + quadHeight;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y + quadHeight;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;
/*
				// compute face normal
				// PVector v1 = new PVector(thisP.x - nextP.x, thisP.y - nextP.y, thisP.z - nextP.z);
				// PVector v2 = new PVector(nextP.x - thisP.x, (nextP.y+quadHeight) - thisP.y, nextP.z - thisP.z);
				PVector v3 = new PVector(thisP.x, thisP.y, thisP.z);//v1.cross(v2);
				v3.normalize();

				float nX = v3.x;
				float nY = v3.y;
				float nZ = v3.z;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;
				*/
				/*
				// add colors
				float theAlpha = .8f;//agent.a;// * ((!gaps[gapIndex++]) ? 1.0f : 0.0f);

				floatQuadColors[quadColorIndex++] = ribbonR;
				floatQuadColors[quadColorIndex++] = ribbonG;
				floatQuadColors[quadColorIndex++] = ribbonB;
				floatQuadColors[quadColorIndex++] = theAlpha;

				floatQuadColors[quadColorIndex++] = ribbonR;
				floatQuadColors[quadColorIndex++] = ribbonG;
				floatQuadColors[quadColorIndex++] = ribbonB;
				floatQuadColors[quadColorIndex++] = theAlpha;

				floatQuadColors[quadColorIndex++] = ribbonR;
				floatQuadColors[quadColorIndex++] = ribbonG;
				floatQuadColors[quadColorIndex++] = ribbonB;
				floatQuadColors[quadColorIndex++] = theAlpha;

				floatQuadColors[quadColorIndex++] = ribbonR;
				floatQuadColors[quadColorIndex++] = ribbonG;
				floatQuadColors[quadColorIndex++] = ribbonB;
				floatQuadColors[quadColorIndex++] = theAlpha;
				*/
			}

		}

		imageQuadModel.updateVertices(floatQuadVertices);
		imageQuadModel.updateNormals(floatQuadVertices);
		// imageQuadModel.updateNormals(floatQuadVertices);

		// A model can be drawn through the GLGraphics renderer:
		GLGraphics renderer = (GLGraphics)e.g;
		renderer.model(imageQuadModel);

		g.popStyle();
	}

	/*
	public PVector getHead() {
		return new PVector(head.x, head.y, head.z);
	}
	 */
	
	/**
	 * let all fall down
	 */
	public void dieOut() {
		// set physics particles to current agent positions
		for (int i = 0; i < numRibbons; i++) {
			Ribbon3D agent = particles[i];
			PVector agentPosition = agent.getFirstPoint();
			physics.particles.get(i).clear();
			physics.particles.get(i).set(agentPosition.x, agentPosition.y, agentPosition.z);
			physics.particles.get(i).scaleVelocity(0);
			physics.particles.get(i).addForce(new Vec3D(agentPosition.x, agentPosition.y, agentPosition.z).scaleSelf(.01f));
		}
		isFalling = true;
	}
}
