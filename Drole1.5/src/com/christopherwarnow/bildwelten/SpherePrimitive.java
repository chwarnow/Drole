package com.christopherwarnow.bildwelten;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class SpherePrimitive {
	
	private PApplet parent;
	private PImage texture;
	
	private float[] cx, cz, sphereX, sphereY, sphereZ;
	private float sinLUT[];
	private float cosLUT[];
	private float SINCOS_PRECISION = 0.5f;
	private int SINCOS_LENGTH = (int)(360.0f / SINCOS_PRECISION);
	private PVector position;
	private int sphereDetail;
	private float radius;
	
	public SpherePrimitive(PApplet parent, PVector position, float radius, PImage texture, int sphereDetail) {
		this.parent = parent;
		this.position = position;
		this.radius = radius;
		this.texture = texture;
		this.sphereDetail = sphereDetail;
		initializeSphere(sphereDetail);
	}
	
	public void draw() {
		texturedSphere(radius, texture);
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
	}

	// Generic routine to draw textured sphere
	void texturedSphere(float r, PImage t) {
		int v1,v11,v2;
		r = (r + 240f ) * 0.33f;
		parent.g.beginShape(PApplet.TRIANGLE_STRIP);
		parent.g.texture(t);
		float iu=(float)(t.width-1)/(sphereDetail);
		float iv=(float)(t.height-1)/(sphereDetail);
		float u=0,v=iv;
		for(int i = 0; i < sphereDetail; i++) {
			parent.g.vertex(0, -r, 0,u,0);
			parent.g.vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
		    u+=iu;
		}
		parent.g.vertex(0, -r, 0,u,0);
		parent.g.vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
		parent.g.endShape();   
		  
		  // Middle rings
		  int voff = 0;
		  for(int i = 2; i < sphereDetail; i++) {
		    v1=v11=voff;
		    voff += sphereDetail;
		    v2=voff;
		    u=0;
		    parent.g.beginShape(PApplet.TRIANGLE_STRIP);
		    parent.g.texture(t);
		    for (int j = 0; j < sphereDetail; j++) {
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
		  for (int i = 0; i < sphereDetail; i++) {
		    v2 = voff + i;
		    parent.g.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
		    parent.g.vertex(0, r, 0,u,v+iv);    
		    u+=iu;
		  }
		  parent.g.vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
		  parent.g.endShape();
	}
}
