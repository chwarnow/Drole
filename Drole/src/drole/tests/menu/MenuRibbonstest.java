package drole.tests.menu;

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

public class MenuRibbonstest extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int particleAmount = 100;
	float sphereSize = 300;
	TestDrole[] particles;
	
	VerletPhysics physics;
	VerletParticle head;
	int REST_LENGTH=10;
	
	public void setup() {
		size(1200, 720, P3D);
		
		// create drole particles
		  particles = new TestDrole[particleAmount];

		  // create particles
		  for (int i=0;i<particleAmount;i++) {
		    particles[i] = new TestDrole(this, new PVector(random(-3.1414f, 3.1414f), random(-3.1414f, 3.1414f), random(-3.1414f, 3.1414f)), sphereSize, i, color(160));
		  }

		  // create collision sphere at origin, replace OUTSIDE with INSIDE to keep particles inside the sphere
		  ParticleConstraint sphereA=new SphereConstraint(new Sphere(new Vec3D(), sphereSize*.8f), SphereConstraint.OUTSIDE);
		  ParticleConstraint sphereB=new SphereConstraint(new Sphere(new Vec3D(), sphereSize), SphereConstraint.INSIDE);
		  physics=new VerletPhysics();
		  // weak gravity along Y axis
		  physics.addBehavior(new GravityBehavior(new Vec3D(0, 0.01f, 0)));
		  // set bounding box to 110% of sphere radius
		  physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(sphereSize, sphereSize, sphereSize).scaleSelf(1.1f)));
		  VerletParticle prev=null;
		  for (int i=0; i<particleAmount; i++) {
		    // create particles at random positions outside sphere
		    VerletParticle p=new VerletParticle(Vec3D.randomVector().scaleSelf(sphereSize*2));
		    // set sphere as particle constraint
		    p.addConstraint(sphereA);
		    p.addConstraint(sphereB);
		    physics.addParticle(p);
		    if (prev!=null) {
		      physics.addSpring(new VerletSpring(prev, p, REST_LENGTH*5, 0.005f));
		      physics.addSpring(new VerletSpring(physics.particles.get((int)random(i)), p, REST_LENGTH*20, 0.00001f + i*.0005f));
		    }
		    prev=p;
		  }
		  head=physics.particles.get(0);
		  head.lock();
	}
	
	public void draw() {
		
		// update particle movement
	    head.set(noise(frameCount*(.005f + cos(frameCount*.001f)*.005f))*width-width/2, noise(frameCount*.005f + cos(frameCount*.001f)*.005f)*height-height/2, noise(frameCount*.01f + 100)*width-width/2);
	    physics.particles.get(10).set(noise(frameCount*(.005f + cos(frameCount*.001f)*.005f))*width-width/2, noise(frameCount*.005f + cos(frameCount*.001f)*.005f)*height-height/2, noise(frameCount*.01f + 100)*width-width/2);
	    // also apply sphere constraint to head
	    // this needs to be done manually because if this particle is locked
	    // it won't be updated automatically
	    head.applyConstraints();
	    // update sim
	    physics.update();
	    // then all particles as dots
	    int index=0;
	    // for (Iterator i=physics.particles.iterator(); i.hasNext();) {
	    for(VerletParticle p:physics.particles) {
	      // VerletParticle p=(VerletParticle)i.next();
	      particles[index++].addPosition(p.x, p.y, p.z);
	    }
	    
		background(255);
		
		pushMatrix();
		  translate(width/2, height/2, 0);
		  
		  rotateY(radians(mouseX));
		  rotateX(radians(mouseY));
		  
		  lights();
		  
		  float dirY = (mouseY / (float)height - 0.5f) * 2;
		  float dirX = (mouseX / (float)width - 0.5f) * 2;
		  directionalLight(204, 204, 204, -dirX, -dirY, -1);
		  
		  // draw drole particles
		  for (int i=0;i<particleAmount;i++) {
		    particles[i].draw();
		  }
		  
		  // end evironment mapping shader
		  //resetShader();
		  
		  popMatrix();
	
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"--bgcolor=#000000",
			"drole.tests.menu.MenuRibbonstest"
		});
	}
}
