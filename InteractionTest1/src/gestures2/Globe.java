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

import com.christopherwarnow.bildwelten.DroleWelt;
import com.christopherwarnow.bildwelten.Drole;
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
	public float rotationSpeed = 0.04f;
	private float smoothedRotation = 0;
	private float smoothedRotationSpeed = .1f;

	public PImage globeTexture;

	private int sDetail = 35;  // Sphere detail setting
	private float pushBack = 0;

	private float[] cx, cz, sphereX, sphereY, sphereZ;
	private float sinLUT[];
	private float cosLUT[];
	private float SINCOS_PRECISION = 0.5f;
	private int SINCOS_LENGTH = (int)(360.0f / SINCOS_PRECISION);

	
	// ------ drole particles on sphere ------
	private int droleAmount = 5;
	private int drolesPerWelt = 50;
	private DroleWelt[] droles;
	
	// ------ hotspots for a menu to choose bildwelten
	private float menuRotation = 0;

	public Globe(PApplet parent, PVector position, PVector dimension, PImage globeTexture) {
		super(parent);

		position(position);
		dimension(dimension);
		
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

		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
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
		parent.g.translate(position.x, position.y, position.z);
		parent.g.scale(scale.x, scale.y, scale.z);
		parent.g.rotateY(smoothedRotation);//rotation);
//		parent.g.rotateY(rotation);

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
		
		// draw a menu item circle
		//System.out.println(rotation);
		// about .9 <>1.2
		
		float startRotation = .9f;
		float endRotation = 1.2f;
		for(int i=0;i<droleAmount;i++) {
			float myRotation = menuRotation - 3.1414f*.5f + i*.3f;
//			System.out.println(i + " " + parent.abs((startRotation + (droleAmount-i)*.15f) - smoothedRotation));
			
			if(parent.abs((startRotation + (droleAmount-i)*.15f) - smoothedRotation) < .05f) {
				parent.g.fill(55,0,0);
				droles[i].isActive(true);
			} else {
				parent.g.noFill();
				droles[i].isActive(false);
			}
			parent.g.stroke(50,0,0);
			parent.g.strokeWeight(1);
			
			parent.g.pushMatrix();
			parent.g.rotateY(myRotation);
			parent.g.translate(0, 0, dimension.x*.5f);
			parent.g.ellipse(0,0, 100, 100);
			parent.g.popMatrix();
		}
	
		parent.g.popMatrix();

//		parent.g.noLights();

		parent.g.popStyle();
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
