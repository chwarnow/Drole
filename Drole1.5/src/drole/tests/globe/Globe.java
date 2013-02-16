package drole.tests.globe;

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
import com.christopherwarnow.bildwelten.SpherePrimitive;
import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import drole.Main;


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
	private int drolesPerWelt = 100;
	private DroleWelt[] droles;
	
	// ------ hotspots for a menu to choose bildwelten
	private float menuRotation = 0;
	
	// ------ cube sphere ------
	PImage cubeTex;
	SpherePrimitive cubeEnvironment;
	
	// ------ circular mask ------
	PImage circularMask;

	public Globe(Engine e, PVector position, PVector dimension, PImage globeTexture) {
		super(e);

		position(position);
		dimension(dimension);
		
		this.globeTexture = globeTexture;
		initializeSphere(sDetail);

		// generate drole swarms
		droles = new DroleWelt[droleAmount];
		for(int i=0;i<droleAmount;i++) {
			droles[i] = new DroleWelt(e.p, drolesPerWelt, dimension.x);
		}
		
		// cube environment
		cubeTex = e.p.loadImage("data/images/boxTexture.jpg");
		cubeEnvironment = new SpherePrimitive(e.p, new PVector(), 1500, cubeTex, 32);
		
		// circular mask
		circularMask = e.p.loadImage("data/images/circularMaskWhite.png");
	}

	@Override
	public void update() {
		super.update();
		//rotation += rotationSpeed;
		smoothedRotation += (rotation - smoothedRotation) * smoothedRotationSpeed;
		
		for(DroleWelt droleWelt:droles) {
			droleWelt.update();
		}
	}

	@Override
	public void draw() {		
		g.pushStyle();
		
//		g.lights();

		//g.tint(255, PApplet.map(fade, 0, 1, 0, 255));	

		g.pushMatrix();
		// position, scale, rotation and dimension must be respected!
		/*
		System.out.println(position.toString());
		System.out.println(scale.toString());
		System.out.println(rotation+" : "+smoothedRotation);
		*/
		
		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateY(smoothedRotation);//rotation);
//		g.rotateY(rotation);

		/* ACTUAL APPEARANCE OF THE OBJECT */
//		g.pointLight(255, 255, 255, position.x+500, position.y+1000, position.z+500);
//		g.pointLight(255, 255, 255, position.x-500, position.y-1000, position.z+200);

		g.noStroke();
		g.fill(200);

		// render the cube scene
		g.tint(255, 255);
		
		// box
		
		// front
		float mainSize = 3000;
		/*
		g.pushMatrix();
		g.translate(-mainSize/2, -mainSize/2, mainSize/2);
		e.p.image(circularMask, 0, 0, mainSize, mainSize);
		g.popMatrix();
		*/
		
		// back
		g.pushStyle();
		g.imageMode(e.p.CORNER);
		
		g.pushMatrix();
		
		g.translate(0, 0, -mainSize*.75f);
		
		g.pushMatrix();
			g.translate(-mainSize/2, -mainSize/2, -mainSize/2);
			g.image(cubeTex, 0, 0, mainSize, mainSize);
		g.popMatrix();
		
		//left
		g.pushMatrix();
			g.translate(-mainSize/2, -mainSize/2, mainSize/2);
			g.rotateY(e.p.HALF_PI);
			g.image(cubeTex, 0, 0, mainSize, mainSize);
		g.popMatrix();
		
		//right
		g.pushMatrix();
		g.translate(mainSize/2, -mainSize/2, mainSize/2);
		g.rotateY(e.p.HALF_PI);
		e.p.image(cubeTex, 0, 0, mainSize, mainSize);
		g.popMatrix();
		
		//top
		g.pushMatrix();
		g.translate(-mainSize/2, -mainSize/2, -mainSize/2);
		g.rotateX(e.p.HALF_PI);
		e.p.image(cubeTex, 0, 0, mainSize, mainSize);
		g.popMatrix();
		
		//bottom
		g.pushMatrix();
		g.translate(-mainSize/2, mainSize/2, -mainSize/2);
		g.rotateX(e.p.HALF_PI);
		e.p.image(cubeTex, 0, 0, mainSize, mainSize);
		g.popMatrix();
		
		g.popMatrix();
		
		g.popStyle();
		
		//e.p.hint(e.p.ENABLE_DEPTH_TEST);
		/*
		g.pushMatrix();
		g.translate(0, 0, -1500);
		cubeEnvironment.draw();
		g.popMatrix();
		*/
		// dimension must be respected!
		g.tint(255, 255);
		texturedSphere(dimension.x, globeTexture);

		g.stroke(120);
		g.noFill();

		// g.sphere(dimension.x+2);
		
		/* END APPEARANCE */

		// draw the droles
		/*
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
			
//			System.out.println(i + " " + e.p.abs((startRotation + (droleAmount-i)*.15f) - smoothedRotation));

			if(e.p.abs((startRotation + (droleAmount-i)*.15f) - smoothedRotation) < .05f) {
				g.fill(55,0,0);
				droles[i].isActive(true);
			} else {
				g.noFill();
				droles[i].isActive(false);
			}
			g.stroke(50,0,0);
			g.strokeWeight(1);
			
			g.pushMatrix();
			g.rotateY(myRotation);
			g.translate(0, 0, dimension.x*.5f);
			g.ellipse(0,0, 100, 100);
			g.popMatrix();
		}
	*/
		g.popMatrix();

//		g.noLights();

		g.popStyle();
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
		e.g.beginShape(PApplet.TRIANGLE_STRIP);
		e.g.texture(t);
		float iu=(float)(t.width-1)/(sDetail);
		float iv=(float)(t.height-1)/(sDetail);
		float u=0,v=iv;
		for(int i = 0; i < sDetail; i++) {
			e.g.vertex(0, -r, 0,u,0);
			e.g.vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
		    u+=iu;
		}
		e.g.vertex(0, -r, 0,u,0);
		e.g.vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
		e.g.endShape();   
		  
		  // Middle rings
		  int voff = 0;
		  for(int i = 2; i < sDetail; i++) {
		    v1=v11=voff;
		    voff += sDetail;
		    v2=voff;
		    u=0;
		    e.g.beginShape(PApplet.TRIANGLE_STRIP);
		    e.g.texture(t);
		    for (int j = 0; j < sDetail; j++) {
		    	e.g.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
		    	e.g.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
		      u+=iu;
		    }
		  
		    // Close each ring
		    v1=v11;
		    v2=voff;
		    e.g.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
		    e.g.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
		    e.g.endShape();
		    v+=iv;
		  }
		  u=0;
		  
		  // Add the northern cap
		  e.g.beginShape(PApplet.TRIANGLE_STRIP);
		  e.g.texture(t);
		  for (int i = 0; i < sDetail; i++) {
		    v2 = voff + i;
		    e.g.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
		    e.g.vertex(0, r, 0,u,v+iv);    
		    u+=iu;
		  }
		  e.g.vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
		  e.g.endShape();
	}
	
}
