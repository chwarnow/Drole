package drole.gfx.ribbon;

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

public class RibbonHandler {

	private static final long serialVersionUID = 1L;

	private PApplet parent;
	
	private int numRibbons;
	private int numJointsPerRibbon;
	private float sphereSize = 300;
	
	private Ribbon3D[] particles;

	private VerletPhysics physics;
	private VerletParticle head;
	private int REST_LENGTH = 10;

	public RibbonHandler(PApplet parent, int numRibbons, int numJointsPerRibbon) {
		this.parent				= parent;
		this.numRibbons			= numRibbons;
		this.numJointsPerRibbon	= numJointsPerRibbon;
		
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
			VerletParticle p = new VerletParticle(Vec3D.randomVector()
					.scaleSelf(sphereSize * 2));

			// set sphere as particle constraint
			p.addConstraint(sphereA);
			p.addConstraint(sphereB);

			physics.addParticle(p);

			if (prev != null) {
				physics.addSpring(new VerletSpring(prev, p, REST_LENGTH * 5, 0.005f));
				physics.addSpring(new VerletSpring(physics.particles.get((int) parent.random(i)), p, REST_LENGTH * 20, 0.00001f + i * .0005f));
			}

			prev = p;
		}

		head = physics.particles.get(0);
		head.lock();
	}

	public void draw() {
		// update particle movement
		head.set(parent.noise(parent.frameCount * (.005f + PApplet.cos(parent.frameCount * .001f) * .005f)) * parent.width -parent. width / 2, parent.noise(parent.frameCount * .005f + PApplet.cos(parent.frameCount * .001f) * .005f) * parent.height - parent.height / 2, parent.noise(parent.frameCount * .01f + 100) * parent.width - parent.width / 2);
		physics.particles.get(physics.particles.size() - 1).set(parent.noise(parent.frameCount * (.005f + PApplet.cos(parent.frameCount * .001f) * .005f)) * parent.width - parent.width / 2, parent.noise(parent.frameCount * .005f + PApplet.cos(parent.frameCount * .001f) * .005f) * parent.height - parent.height / 2, parent.noise(parent.frameCount * .01f + 100) * parent.width - parent.width / 2);

		// also apply sphere constraint to head
		// this needs to be done manually because if this particle is locked
		// it won't be updated automatically
		
		head.applyConstraints();
		
		// update sim
		physics.update();
		
		// then all particles as dots
		int index = 0;
		
		for (VerletParticle p : physics.particles) {
			particles[index++].update(p.x, p.y, p.z);
		}
		
		// draw drole particles
		for (int i = 0; i < numRibbons; i++) {
			particles[i].drawMeshRibbon(30);
		}

	}

}
