package drole.tests.menu;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLSLShader;
import drole.gfx.ribbon.Ribbon3D;
import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;
import toxi.physics.constraints.ParticleConstraint;
import toxi.physics.constraints.SphereConstraint;

public class RibbonGroupTest {
	PApplet parent;

	private static final long serialVersionUID = 1L;

	private float seed;
	private int numPhysicParticles;
	private int numQuadsPerRibbon;
	private float sphereSize;
	
	private Ribbon3D[] particles;

	private VerletPhysics physics;
	private VerletParticle head, tail;
	private int REST_LENGTH;
	
	private VerletParticle pivot;
	private VerletSpring[] pivotSprings;
	
	private ParticleConstraint sphereA, sphereB; 
	
	private boolean isPivoting = false;
	
	private int vertexCount = 0;
	
	private GLModel imageQuadModel;
	private GLSLShader imageShader; // should pe provided by mother class?
	private int cosDetail = 25;
	private float[] cosLUT = new float[cosDetail];
	
	private float seedSpeed = .01f;
	
	public RibbonGroupTest(PApplet parent, float sphereSize, int numRibbons, int numJointsPerRibbon, int REST_LENGTH) {
		this.parent = parent;
		
		this.seed				= parent.random(1000);
		this.numPhysicParticles	= numRibbons;
		this.numQuadsPerRibbon	= numJointsPerRibbon;
		this.sphereSize			= sphereSize;
		this.REST_LENGTH		= REST_LENGTH;

		// create drole particles
		particles = new Ribbon3D[numRibbons];

		// create particles
		for (int i = 0; i < numRibbons; i++) {			
//			PVector startPosition = new PVector(parent.random(-3.1414f, 3.1414f), parent.random(-3.1414f, 3.1414f), parent.random(-3.1414f, 3.1414f));
			PVector startPosition = new PVector(0, 0, 0);
			particles[i] = new Ribbon3D(parent, startPosition, numJointsPerRibbon);
			vertexCount += particles[i].getVertexCount();
		}

		// create collision sphere at origin, replace OUTSIDE with INSIDE to
		// keep particles inside the sphere
		ParticleConstraint sphereA = new SphereConstraint(new Sphere(new Vec3D(), sphereSize * .8f), SphereConstraint.OUTSIDE);
		ParticleConstraint sphereB = new SphereConstraint(new Sphere(new Vec3D(), sphereSize), SphereConstraint.INSIDE);

		physics = new VerletPhysics();

		// weak gravity along Y axis
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 0.01f, 0)));

		// set bounding box to 110% of sphere radius
		physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(sphereSize, sphereSize, sphereSize).scaleSelf(1.1f)));

		VerletParticle prev = null;

		for (int i = 0; i < numRibbons; i++) {
			// create particles at random positions outside sphere
			VerletParticle p = new VerletParticle(Vec3D.randomVector().scaleSelf(sphereSize * 2));

			// set sphere as particle constraint
			p.addConstraint(sphereA);
			p.addConstraint(sphereB);

			physics.addParticle(p);

			if(prev != null) {
				physics.addSpring(new VerletSpring(prev, p, REST_LENGTH * 1, 0.005f));
				physics.addSpring(new VerletSpring(physics.particles.get((int) parent.random(i)), p, REST_LENGTH * 2, 0.00001f + i * .0005f));
			}

			prev = p;
		}

		head = physics.particles.get(0);
		head.lock();
		
		tail = physics.particles.get(physics.particles.size() - 1);
		tail.lock();
		
		// create a model that uses quads
		imageQuadModel = new GLModel(parent, vertexCount*4, PApplet.QUADS, GLModel.DYNAMIC);
		imageQuadModel.initColors();
		imageQuadModel.initNormals();

		// load shader
		imageShader = new GLSLShader(parent, "data/shader/imageVert.glsl", "data/shader/imageFrag.glsl");
		
		// create cos lookup table
		for(int i=0;i<cosDetail;i++) {
			cosLUT[i] = parent.sin(((float)i/cosDetail)*parent.PI);
		}
	}

	public void update() {
		if(!isPivoting) {
			seed += seedSpeed;
			
			// update particle movement
			head.set(
					parent.noise(seed * (.0015f + PApplet.cos(seed * .001f) * .0015f)) * parent.width -parent. width / 2,
					parent.noise(seed * .0015f + PApplet.cos(seed * .001f) * .0015f) * parent.height - parent.height / 2,
					parent.noise(seed * .001f + 100) * parent.width - parent.width / 2);
			
			float tailSeed = seed + 10.0f;
			tail.set(
					parent.noise(tailSeed * (.0015f + PApplet.cos(tailSeed * .001f) * .0015f)) * parent.width - parent.width / 2,
					parent.noise(tailSeed * .0015f + PApplet.cos(tailSeed * .001f) * .0015f) * parent.height - parent.height / 2,
					parent.noise(tailSeed * .01f + 100) * parent.width - parent.width / 2);
	
			// also apply sphere constraint to head
			// this needs to be done manually because if this particle is locked
			// it won't be updated automatically
			head.applyConstraints();
			tail.applyConstraints();
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
			s.setRestLength(0.1f);
			s.setStrength(0.001f);
		}
		
		// Add springs between all joints and the pivot
		pivotSprings = new VerletSpring[numPhysicParticles];
		for(int i = 0; i < numPhysicParticles; i++) {
			pivotSprings[i] = new VerletSpring(head, pivot, 5, 0.000001f);
			pivotSprings[i].lockB(true);
			
			physics.addSpring(pivotSprings[i]);
		}
		
		head.unlock();
		
		isPivoting = true;
	}
	
	public void deletePivot() {
		// First remove all pivot prings
		for(int i = 0; i < numPhysicParticles; i++) {
			physics.removeSpring(pivotSprings[i]);
		}
		// Remove pivot
		physics.removeParticle(pivot);
		
		// Relax all other springs
		for(VerletSpring s : physics.springs) s.setRestLength(REST_LENGTH * 20);
		
		// Randomize all particle positions
		for(VerletParticle p : physics.particles) {
			p.lock();
			p.set(Vec3D.randomVector().scaleSelf(sphereSize * 2));
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
		// arrays for storing ribbon vertices
		float[] floatQuadVertices = new float[vertexCount*16];
		float[] floatQuadNormals = new float[vertexCount*16];
		float[] floatQuadColors = new float[vertexCount*16];
		int quadVertexIndex = 0;
		int quadNormalIndex = 0;
		int quadColorIndex = 0;
		
		float ribbonR = .1f;
		float ribbonG = .1f;
		float ribbonB = .1f;
		
		float quadHeight = .75f;
		
		for (int i = 0; i < numPhysicParticles; i++) {
			
			
			Ribbon3D agent = particles[i];
			// create quads from ribbons
			PVector[] agentsVertices = agent.getVertices();
			int agentVertexNum = agentsVertices.length;

			for(int j=0;j<agentVertexNum-1;j++) {
				
				// cosinus from lookup table
				float ratio = cosLUT[(int)(((float)j/agentVertexNum) * cosDetail)];
				
				PVector thisP = agentsVertices[j];
				PVector nextP = agentsVertices[j+1];
				//PVector thirdP = agentsVertices[j+1];

				// create quad from above vertices and save in glmodel, then add colors
				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y + quadHeight*ratio*2.0f;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y + quadHeight*ratio*2.0f;
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
		
		imageShader.start();
	    imageShader.setFloatUniform("zmin", 0.65f);
	    imageShader.setFloatUniform("zmax", 0.85f);
	    imageShader.setFloatUniform("shininess", 100.0f);
	    imageShader.setVecUniform("lightPos", 100.0f, -10.0f, 30.0f);
		
		// A model can be drawn through the GLGraphics renderer:
	    GLGraphics renderer = (GLGraphics)parent.g;
		renderer.model(imageQuadModel);

		imageShader.stop();
		
		parent.fill(255);
		parent.pushMatrix();
		parent.translate(head.x, head.y, head.z);
		parent.ellipse(0,0, 30, 30);
		parent.popMatrix();
	}

	public void drawAsLines() {
		for (int i = 0; i < numPhysicParticles; i++) {
			if(i != numPhysicParticles-1) particles[i].drawStrokeRibbon(parent.color(200, 200, 0), 5);
			else particles[i].drawStrokeRibbon(parent.color(200, 0, 0), 5);
		}
	}
	
	public PVector getHead() {
		return new PVector(head.x, head.y, head.z);
	}

}
