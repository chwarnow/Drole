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
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;
import toxi.physics.constraints.BoxConstraint;
import toxi.physics.constraints.ParticleConstraint;
import toxi.physics.constraints.SphereConstraint;

public class RibbonGroup extends Drawable {

	private static final long serialVersionUID = 1L;

	public float 				ribbonR = .0f;
	public float 				ribbonG = .0f;
	public float 				ribbonB = .0f;
	
	private float 				seed;
	private int 				numPhysicParticles;
	private int 				numQuadsPerRibbon;
	private float 				sphereSize;
	private int 				vertexCount = 0;
	private float 				seedSpeed = .09f;
	private float 				quadHeight;
	
	private Ribbon3D[] 			particles;

	private VerletPhysics 		physics;
	private VerletParticle 		head, tail;
	private int 				REST_LENGTH;
	
	private VerletParticle 		pivot;
	private VerletSpring[] 		pivotSprings;
	private ArrayList<Float> 	springLengths = new ArrayList<Float>();
	
	private AABB 				worldBox;
	private ParticleConstraint 	sphereA, sphereB, cubeConst;
	
	private boolean 			isPivoting = false;
	
	private GLModel 			imageQuadModel;
	
	private int					noiseID = 0;
	private int					maxNoiseID = 50;
	private float[]				noiseLUT = new float[maxNoiseID];
	
	public RibbonGroup(Engine e, float sphereSize, int numRibbons, int numJointsPerRibbon, int REST_LENGTH, float quadHeight) {
		super(e);
		
		this.seed				= e.p.random(1000);
		this.numPhysicParticles	= numRibbons;
		this.numQuadsPerRibbon	= numJointsPerRibbon;
		this.sphereSize			= sphereSize;
		this.REST_LENGTH		= REST_LENGTH;
		this.quadHeight			= quadHeight;

		// create drole particles
		particles = new Ribbon3D[numRibbons];

		// create particles
		for (int i = 0; i < numRibbons; i++) {			
//			PVector startPosition = new PVector(parent.random(-3.1414f, 3.1414f), parent.random(-3.1414f, 3.1414f), parent.random(-3.1414f, 3.1414f));
			PVector startPosition = new PVector(0, 0, 0);
			particles[i] = new Ribbon3D(e, startPosition, numJointsPerRibbon, false);
			particles[i].dimension(30, 0, 0);
			vertexCount += particles[i].getVertexCount();
		}

		// create collision sphere at origin, replace OUTSIDE with INSIDE to
		// keep particles inside the sphere
		sphereA = new SphereConstraint(new Sphere(new Vec3D(), sphereSize * .8f), SphereConstraint.OUTSIDE);
		sphereB = new SphereConstraint(new Sphere(new Vec3D(), sphereSize), SphereConstraint.INSIDE);
		
		worldBox = new AABB(new Vec3D(), 1500);
		cubeConst = new BoxConstraint(worldBox);

		physics = new VerletPhysics();

		// weak gravity along Y axis
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 0.01f, 0)));

		// set bounding box to 100% of out virtual world
		physics.setWorldBounds(worldBox);

		VerletParticle prev = null;

		for (int i = 0; i < numRibbons; i++) {
			// create particles at random positions outside sphere
			VerletParticle p = new VerletParticle(Vec3D.randomVector().scaleSelf(sphereSize * 2));

			// set sphere as particle constraint
			p.addConstraint(sphereA);
			p.addConstraint(sphereB);

			physics.addParticle(p);

			if(prev != null) {
				float thisLength = e.p.random(REST_LENGTH);
				springLengths.add(thisLength);
				physics.addSpring(new VerletSpring(prev, p, thisLength, 0.05f));
				springLengths.add(thisLength * 20);
				physics.addSpring(new VerletSpring(physics.particles.get((int) e.p.random(i)), p, thisLength * 20, 0.01f));
			}

			prev = p;
		}

		head = physics.particles.get(0);
		head.lock();
		
		// tail = physics.particles.get(physics.particles.size() - 1);
		// tail.lock();
		
		// create a model that uses quads
		imageQuadModel = new GLModel(e.p, vertexCount*4, PApplet.QUADS, GLModel.DYNAMIC);
		imageQuadModel.initColors();
		imageQuadModel.initNormals();
		
		// create a noise lookuptable
		for(int i=0;i<maxNoiseID;i++) {
			noiseLUT[i] = e.p.noise((float)i*.01f);
		}
	}

	public void update() {
		super.update();
		if(!isPivoting) {
			seed += seedSpeed;
			
			// update particle movement
			head.set(
					e.p.noise(seed * (.015f + PApplet.cos(seed * .001f) * .015f)) * e.p.width -e.p. width / 2,
					e.p.noise(seed * .015f + PApplet.cos(seed * .001f) * .015f) * e.p.height - e.p.height / 2,
					e.p.noise(seed * .001f + 100) * e.p.width - e.p.width / 2);
			/*
			float tailSeed = seed + 10.0f;
			tail.set(
					e.p.noise(tailSeed * (.015f + PApplet.cos(tailSeed * .001f) * .015f)) * e.p.width - e.p.width / 2,
					e.p.noise(tailSeed * .015f + PApplet.cos(tailSeed * .001f) * .005f) * e.p.height - e.p.height / 2,
					e.p.noise(tailSeed * .01f + 100) * e.p.width - e.p.width / 2);
	*/
			// also apply sphere constraint to head
			// this needs to be done manually because if this particle is locked
			// it won't be updated automatically
			head.applyConstraints();
			//tail.applyConstraints();
			
			// iterate through springs and stiffen or widen them to achieve noise like aesthetics?
			int springID = 0;
			for(VerletSpring s : physics.springs) {
				s.setRestLength(springLengths.get(springID++) * noiseLUT[noiseID++]);
				if(noiseID == maxNoiseID) noiseID = 0;
			}
		}
		
		// update sim
		physics.update();
		
		// then all particles as dots
		int index = 0;
		
		for(int i = 0; i < particles.length; i++) {
			VerletParticle p = physics.particles.get(i);
			particles[index++].update(p.x, p.y, p.z);
		}
	}
	
	public void createPivotAt(float x, float y, float z) {
		pivot = new VerletParticle(x, y, z);
		pivot.lock();
		physics.addParticle(pivot);
		
		/* First tighten all springs*/
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
	}
	
	public void deletePivot() {
		// First remove all pivot prings
		/*
		for(int i = 0; i < numPhysicParticles; i++) {
			physics.removeSpring(pivotSprings[i]);
		}
		*/
		
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
		
		for (int i = 0; i < numPhysicParticles; i++) {
			
			
			Ribbon3D agent = particles[i];
			// create quads from ribbons
			PVector[] agentsVertices = agent.getVertices();
			int agentVertexNum = agentsVertices.length;

			for(int j=0;j<agentVertexNum-1;j++) {
				
				// cosinus from lookup table
				// float ratio = cosLUT[(int)(((float)j/agentVertexNum) * cosDetail)];
				
				PVector thisP = agentsVertices[j];
				PVector nextP = agentsVertices[j+1];
				//PVector thirdP = agentsVertices[j+1];

				// create quad from above vertices and save in glmodel, then add colors
				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y + quadHeight;//*ratio*2.0f;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y + quadHeight;//*ratio*2.0f;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

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

				// add colors
				float theAlpha = 1.0f;//agent.a;// * ((!gaps[gapIndex++]) ? 1.0f : 0.0f);

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
			}

		}

		imageQuadModel.updateVertices(floatQuadVertices);
		imageQuadModel.updateColors(floatQuadColors);
		imageQuadModel.updateNormals(floatQuadVertices);
		
		// A model can be drawn through the GLGraphics renderer:
	    GLGraphics renderer = (GLGraphics)e.g;
		renderer.model(imageQuadModel);

		g.popStyle();
	}
	
	public PVector getHead() {
		return new PVector(head.x, head.y, head.z);
	}

}
