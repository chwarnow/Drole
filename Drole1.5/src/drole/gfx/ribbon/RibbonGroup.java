package drole.gfx.ribbon;

import drole.DroleMain;
import drole.engine.Drawable;
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

public class RibbonGroup extends Drawable {

	private static final long serialVersionUID = 1L;

	private float seed;
	private int numPhysicParticles;
	private int numQuadsPerRibbon;
	private float sphereSize;
	
	private Ribbon3D[] particles;

	private VerletPhysics physics;
	private VerletParticle head;
	private int REST_LENGTH;
	
	private VerletParticle pivot;
	private VerletSpring[] pivotSprings;
	
	private ParticleConstraint sphereA, sphereB; 
	
	private boolean isPivoting = false;
	
	public RibbonGroup(DroleMain parent, float sphereSize, int numRibbons, int numJointsPerRibbon, int REST_LENGTH) {
		super(parent);
		
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
				physics.addSpring(new VerletSpring(prev, p, REST_LENGTH * 5, 0.005f));
				physics.addSpring(new VerletSpring(physics.particles.get((int) parent.random(i)), p, REST_LENGTH * 20, 0.00001f + i * .0005f));
			}

			prev = p;
		}

		head = physics.particles.get(0);
		head.lock();
	}

	public void update() {
		super.update();
		if(!isPivoting) {
			seed++;
			
			// update particle movement
			head.set(parent.noise(seed * (.005f + PApplet.cos(seed * .001f) * .005f)) * parent.width -parent. width / 2, parent.noise(seed * .005f + PApplet.cos(seed * .001f) * .005f) * parent.height - parent.height / 2, parent.noise(seed * .01f + 100) * parent.width - parent.width / 2);
			physics.particles.get(physics.particles.size() - 1).set(parent.noise(seed * (.005f + PApplet.cos(seed * .001f) * .005f)) * parent.width - parent.width / 2, parent.noise(seed * .005f + PApplet.cos(seed * .001f) * .005f) * parent.height - parent.height / 2, parent.noise(seed * .01f + 100) * parent.width - parent.width / 2);
	
			// also apply sphere constraint to head
			// this needs to be done manually because if this particle is locked
			// it won't be updated automatically
			head.applyConstraints();
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
		parent.g.pushStyle();
		parent.g.pushMatrix();

			parent.startShader("JustColor");
		
			parent.g.translate(position.x, position.y, position.z);
			parent.g.scale(scale.x, scale.y, scale.z);
			
			parent.fill(200, 200, 200, fade*255);
			parent.noStroke();
			
			for (int i = 0; i < numPhysicParticles; i++) {
				particles[i].drawMeshRibbon(30);
			}
		
			parent.stopShader();
			
		parent.g.popMatrix();
		parent.g.popStyle();
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
