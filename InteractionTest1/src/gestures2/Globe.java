package gestures2;

/**
 * 
 * Initial Class by Denny Koch
 * 
 * particles that float around an invisible sphere
 * using toxiclibs verlet physics
 * being the menu (Christopher Warnow)
 * 
 */

import java.util.Iterator;

import com.christopherwarnow.bildwelten.Drole;
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

public class Globe extends Drawable {

	public float rotation 		= 0;
	public float rotationSpeed = 0.04f;


	// ------ drole particles on sphere ------
	private int particleAmount = 500;
	private Drole[] particles;

	private float sphereSize = 120;
	private int NUM_PARTICLES = 100;
	private int REST_LENGTH=3;

	private VerletPhysics physics;
	private VerletParticle head;

	public Globe(PApplet parent) {
		super(parent);

		initMenuSphere();
	}

	@Override
	public void update() {
		super.update();
		// rotation += rotationSpeed;

		updateMenuSphere();
	}

	@Override
	public void draw() {
		parent.g.pushStyle();
		parent.g.lights();

		parent.g.tint(255, PApplet.map(fade, 0, 1, 0, 255));	

		parent.g.pushMatrix();
		// position, scale, rotation and dimension must be respected!
		parent.g.translate(position.x, position.y+PApplet.map(fade, 0, 1, 190, 0), position.z);
		parent.g.scale(scale.x, scale.y, scale.z);
		parent.g.rotateY(rotation);

		/* ACTUAL APPEARANCE OF THE OBJECT */
		parent.g.pointLight(255, 255, 255, position.x+500, position.y+1000, position.z+500);
		parent.g.pointLight(255, 255, 255, position.x-500, position.y-1000, position.z+200);

		parent.g.noStroke();
		parent.g.fill(200);

		// dimension must be respected!
		parent.g.sphere(dimension.x*.5f);

		parent.g.stroke(120);
		parent.g.noFill();

		// parent.g.sphere(dimension.x+2);
		
		/* END APPEARANCE */

		// draw the droles
		drawMenuSphere();

		parent.g.popMatrix();

		parent.g.noLights();

		parent.g.popStyle();
	}

	/**
	 * create a swarm of spring-connected verlet physics particles that are constrained on a sphere
	 */
	private void initMenuSphere() {
		
		// create drole particles
		particles = new Drole[particleAmount];

		// create particles
		for (int i=0;i<NUM_PARTICLES;i++) {
			particles[i] = new Drole( parent,
					new PVector(
							parent.random(-3.1414f, 3.1414f),
							parent.random(-3.1414f, 3.1414f),
							parent.random(-3.1414f, 3.1414f)),
							sphereSize,
							i);
		}

		// create collision sphere at origin, replace OUTSIDE with INSIDE to keep particles inside the sphere
		ParticleConstraint sphereA=new SphereConstraint(new Sphere(new Vec3D(), sphereSize), SphereConstraint.OUTSIDE);
		ParticleConstraint sphereB=new SphereConstraint(new Sphere(new Vec3D(), sphereSize*1.1f), SphereConstraint.INSIDE);
		physics=new VerletPhysics();
		// weak gravity along Y axis
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 0.01f, 0)));
		// set bounding box to 110% of sphere radius
		physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(sphereSize, sphereSize, sphereSize).scaleSelf(1.1f)));
		VerletParticle prev=null;
		for (int i=0; i<NUM_PARTICLES; i++) {
			// create particles at random positions outside sphere
			VerletParticle p=new VerletParticle(Vec3D.randomVector().scaleSelf(sphereSize*2));
			// set sphere as particle constraint
			p.addConstraint(sphereA);
			p.addConstraint(sphereB);
			physics.addParticle(p);
			if (prev!=null) {
				physics.addSpring(new VerletSpring(prev, p, REST_LENGTH*5, 0.005f));
				physics.addSpring(new VerletSpring(physics.particles.get((int)parent.random(i)), p, REST_LENGTH*20, 0.00001f + i*.0005f));
			}
			prev=p;
		}
		head=physics.particles.get(0);
		head.lock();
	}

	/**
	 * let the particles wander by setting position of certain particles
	 */
	private void updateMenuSphere() {
		// update particle movement
		head.set(
				parent.noise(parent.frameCount*(.005f + parent.cos(parent.frameCount*.01f)*.00f))*parent.width-parent.width/2,
				parent.noise(parent.frameCount*.005f + parent.cos(parent.frameCount*.01f)*.05f)*parent.height-parent.height/2,
				parent.noise(parent.frameCount*.01f + 100)*parent.width-parent.width/2
		);
		physics.particles.get(10).set(
				parent.noise(-parent.frameCount*(.005f + parent.cos(parent.frameCount*.001f)*.005f))*parent.width-parent.width/2,
				parent.noise(-parent.frameCount*.005f + parent.cos(parent.frameCount*.001f)*.005f)*parent.height-parent.height/2,
				parent.noise(-parent.frameCount*.01f + 100)*parent.width-parent.width/2);

		// also apply sphere constraint to head
		// this needs to be done manually because if this particle is locked
		// it won't be updated automatically
		head.applyConstraints();
		// physics.particles.get(10).applyConstraints();

		// update sim
		physics.update();
		// then all particles as dots
		int index=0;
		for (Iterator i=physics.particles.iterator(); i.hasNext();) {
			VerletParticle p=(VerletParticle)i.next();
			particles[index++].addPosition(p.x, p.y, p.z);
		}
	}

	private void drawMenuSphere() {
		parent.g.pushMatrix();
		parent.g.stroke(0);
		for (int i=0;i<NUM_PARTICLES;i++) {
			particles[i].draw();
		}
		parent.g.popMatrix();
	}
}
