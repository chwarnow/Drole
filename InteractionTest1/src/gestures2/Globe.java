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


import com.christopherwarnow.bildwelten.DroleWelt;
import processing.core.PApplet;
import processing.core.PImage;
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
	public float rotationSpeed 	= 0.04f;

	public PImage globeTexture;

	private int sDetail = 35;  // Sphere detail setting
	private float pushBack = 0;

	private float[] cx, cz, sphereX, sphereY, sphereZ;
	private float sinLUT[];
	private float cosLUT[];
	private float SINCOS_PRECISION = 0.5f;
	private int SINCOS_LENGTH = (int)(360.0f / SINCOS_PRECISION);

	
	// ------ drole particles on sphere ------
	private int particleAmount = 500;
	private Drole[] particles;

	private int NUM_PARTICLES = 100;
	private int REST_LENGTH=3;

	private VerletPhysics physics;
	private VerletParticle head;
	private int droleAmount = 5;
	private int drolesPerWelt = 50;
	private DroleWelt[] droles;
	private DroleWelt droleA, droleB;

	public Globe(PApplet parent, PVector position, PVector dimension, PImage globeTexture) {
		super(parent);

		position(position);
		dimension(dimension);
		
		initMenuSphere();
		
		this.globeTexture = globeTexture;
		initializeSphere(sDetail);

		// generate drole swarms
		droles = new DroleWelt[droleAmount];
		for(int i=0;i<droleAmount;i++) {
			droles[i] = new DroleWelt(parent, drolesPerWelt, dimension.x);
		}
	}

	@Override
	public void update() {
		super.update();
		// rotation += rotationSpeed;

		for(DroleWelt droleWelt:droles) {
			droleWelt.update();
		}
	}

	@Override
	public void draw() {
		parent.g.pushStyle();
//		parent.g.lights();

		//parent.g.tint(255, PApplet.map(fade, 0, 1, 0, 255));	

		parent.g.pushMatrix();
		// position, scale, rotation and dimension must be respected!
		parent.g.translate(position.x, position.y+PApplet.map(fade, 0, 1, 0, 0), position.z);
		parent.g.scale(scale.x, scale.y, scale.z);
		parent.g.rotateY(rotation);

		/* ACTUAL APPEARANCE OF THE OBJECT */
//		parent.g.pointLight(255, 255, 255, position.x+500, position.y+1000, position.z+500);
//		parent.g.pointLight(255, 255, 255, position.x-500, position.y-1000, position.z+200);

		parent.g.noStroke();
		parent.g.fill(200);

		// dimension must be respected!
		parent.g.tint(255, 255);
		texturedSphere(dimension.x, globeTexture);

		parent.g.stroke(120);
		parent.g.noFill();

		// parent.g.sphere(dimension.x+2);
		
		/* END APPEARANCE */

		// draw the droles

		for(DroleWelt droleWelt:droles) {
			droleWelt.draw();
		}

		parent.g.popMatrix();

//		parent.g.noLights();

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
							dimension.x,
							i);
		}

		// create collision sphere at origin, replace OUTSIDE with INSIDE to keep particles inside the sphere
		ParticleConstraint sphereA=new SphereConstraint(new Sphere(new Vec3D(), dimension.x), SphereConstraint.OUTSIDE);
		ParticleConstraint sphereB=new SphereConstraint(new Sphere(new Vec3D(), dimension.x*1.1f), SphereConstraint.INSIDE);
		physics=new VerletPhysics();
		// weak gravity along Y axis
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 0.01f, 0)));
		// set bounding box to 110% of sphere radius
		physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(dimension.x, dimension.x, dimension.x).scaleSelf(1.1f)));
		VerletParticle prev=null;
		for (int i=0; i<NUM_PARTICLES; i++) {
			// create particles at random positions outside sphere
			VerletParticle p=new VerletParticle(Vec3D.randomVector().scaleSelf(dimension.x*2));
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

	private void initializeSphere(int res)
	{
	  sinLUT = new float[SINCOS_LENGTH];
	  cosLUT = new float[SINCOS_LENGTH];

	  for (int i = 0; i < SINCOS_LENGTH; i++) {
	    sinLUT[i] = (float) Math.sin(i * PApplet.DEG_TO_RAD * SINCOS_PRECISION);
	    cosLUT[i] = (float) Math.cos(i * PApplet.DEG_TO_RAD * SINCOS_PRECISION);
	  }

	  float delta = (float)SINCOS_LENGTH/res;
	  float[] cx = new float[res];
	  float[] cz = new float[res];
	  
	  // Calc unit circle in XZ plane
	  for (int i = 0; i < res; i++) {
	    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
	    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
	  }
	  
	  // Computing vertexlist vertexlist starts at south pole
	  int vertCount = res * (res-1) + 2;
	  int currVert = 0;
	  
	  // Re-init arrays to store vertices
	  sphereX = new float[vertCount];
	  sphereY = new float[vertCount];
	  sphereZ = new float[vertCount];
	  float angle_step = (SINCOS_LENGTH*0.5f)/res;
	  float angle = angle_step;
	  
	  // Step along Y axis
	  for (int i = 1; i < res; i++) {
	    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
	    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
	    for (int j = 0; j < res; j++) {
	      sphereX[currVert] = cx[j] * curradius;
	      sphereY[currVert] = currY;
	      sphereZ[currVert++] = cz[j] * curradius;
	    }
	    angle += angle_step;
	  }
	  sDetail = res;
	}

	// Generic routine to draw textured sphere
	void texturedSphere(float r, PImage t) {
		int v1,v11,v2;
		r = (r + 240f ) * 0.33f;
		parent.g.beginShape(PApplet.TRIANGLE_STRIP);
		parent.g.texture(t);
		float iu=(float)(t.width-1)/(sDetail);
		float iv=(float)(t.height-1)/(sDetail);
		float u=0,v=iv;
		for(int i = 0; i < sDetail; i++) {
			parent.g.vertex(0, -r, 0,u,0);
			parent.g.vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
		    u+=iu;
		}
		parent.g.vertex(0, -r, 0,u,0);
		parent.g.vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
		parent.g.endShape();   
		  
		  // Middle rings
		  int voff = 0;
		  for(int i = 2; i < sDetail; i++) {
		    v1=v11=voff;
		    voff += sDetail;
		    v2=voff;
		    u=0;
		    parent.g.beginShape(PApplet.TRIANGLE_STRIP);
		    parent.g.texture(t);
		    for (int j = 0; j < sDetail; j++) {
		    	parent.g.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
		    	parent.g.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
		      u+=iu;
		    }
		  
		    // Close each ring
		    v1=v11;
		    v2=voff;
		    parent.g.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
		    parent.g.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
		    parent.g.endShape();
		    v+=iv;
		  }
		  u=0;
		  
		  // Add the northern cap
		  parent.g.beginShape(PApplet.TRIANGLE_STRIP);
		  parent.g.texture(t);
		  for (int i = 0; i < sDetail; i++) {
		    v2 = voff + i;
		    parent.g.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
		    parent.g.vertex(0, r, 0,u,v+iv);    
		    u+=iu;
		  }
		  parent.g.vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
		  parent.g.endShape();
	}
	
}
