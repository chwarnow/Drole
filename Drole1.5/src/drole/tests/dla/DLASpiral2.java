package drole.tests.dla;

/**
 * <p>DLASpiral shows the general usage pattern for the current implementation of the
 * Diffusion-limited Aggregation process. Unlike the standard unguided DLA growth,
 * this implementation is utilizing line segments to guide & control the growth process.
 * The guidelines in this demo are forming a spiral extruded along the Z-axis and along
 * which the growth will happen.</p>
 * <p>The DLA process also emits a number of different events to which a client application
 * can subscribe to. The package provides an event adapter class (see Adapter design pattern)
 * which is also used in this demo to trigger the automatic saving of all particles when the
 * spiral has grown to full size/is complete.</p>
 * <p>The last key feature of the demo deals with the visualization of the octree structure
 * underlying the DLA simulation. Both the tree structure and particle contents are shown.</p>
 *
 * <p><em>Please note that DLA is an extremely slow & resource intensive process and can take
 * several hours to complete. You should also increase your max. available memory setting in
 * Processing to be able to store the possibly several million particles.</em></p>
 * 
 * <p><strong>Usage:</strong><ul>
 * <li>move mouse to rotate view</li>
 * <li>- / = : adjust zoom</li>
 * <li>o : toggle octree display</li>
 * <li>s : save current particles</li>
 * <li>r : restart simulation</li>
 * </ul></p>
 */

/* 
 * Copyright (c) 2010 Karsten Schmidt
 * 
 * This demo & library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import java.util.ArrayList;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;

import toxi.sim.dla.*;
import toxi.geom.*;

import processing.core.PApplet;
import processing.core.PVector;

public class DLASpiral2 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	GLGraphics glg;
	
	float rotationX = 0;
	float rotationY = 0;
	float velocityX = 0;
	float velocityY = 0;

	ArrayList vertices;
	ArrayList texCoords;
	ArrayList normals;

	int globeDetail = 35;                 // Sphere detail setting.
	float globeRadius = 450;              // Sphere radius.
	String globeMapName = "data/images/world32k.jpg"; // Image of the earth.

	GLModel earth;
	GLTexture tex;

	float distance = 30000; // Distance of camera from origin.
	float sensitivity = 1.0f;
	
	DLA dla;
	DLAListener listener;

	float currScale = 20;
	boolean isOctreeVisible=true;

	public void setup() {
	  size(1200, 1000, GLGraphics.GLGRAPHICS);
	  smooth();
	  
	    // This funtion calculates the vertices, texture coordinates and normals for the earth model.
	    calculateEarthCoords();

	    earth = new GLModel(this, vertices.size(), TRIANGLE_STRIP, GLModel.STATIC);
	    
	    // Sets the coordinates.
	    earth.updateVertices(vertices);
	    
	    // Sets the texture map.
	    /*
	    tex = new GLTexture(this, globeMapName);
	    earth.initTextures(1);
	    earth.setTexture(0, tex);
	    earth.updateTexCoords(0, texCoords);
	     */
	    
	    // Sets the normals.
	    earth.initNormals();
	    earth.updateNormals(normals);
	    
	    // Sets the colors of all the vertices to white.
	    earth.initColors();
//	    earth.setColors(200, 100);
	    earth.setColors(200);
	  
	  // compute spiral key points (every 45 degrees)
	  ArrayList points = new ArrayList();
	  for(float theta =- TWO_PI, r = 20; theta < 0.2f * TWO_PI; theta += QUARTER_PI) {
	    Vec3D p = Vec3D.fromXYTheta(theta).scale(r);
	    p.z = theta*4;
	    points.add(p);
	    r *= 0.92;
	  }
	  // use points to compute a spline and
	  // use resulting segments as DLA guidelines
	  DLAGuideLines guides = new DLAGuideLines();
	  
//	  guides.addCurveStrip(new Spline3D(points).computeVertices(8));
	  
	  guides.addPoint(new Vec3D(0, 0, 0));
	  
	  // create DLA 3D simulation space 128 units wide (cubic)
	  dla = new DLA(128);
	  // use default configuration
	  dla.setConfig(new DLAConfiguration());
	  // add guide lines
	  dla.setGuidelines(guides);
	  // set leaf size of octree
	  dla.getParticleOctree().setMinNodeSize(1);
	  // add a listener for simulation events
//	  listener=new DLAListener();
//	  dla.addListener(listener);
	  textFont(createFont("SansSerif", 12));
	}

	float SINCOS_PRECISION = 0.5f;
	int SINCOS_LENGTH = (int) ((int)360.0f / SINCOS_PRECISION);  

	void calculateEarthCoords()
	{
	    float[] cx, cz, sphereX, sphereY, sphereZ;
	    float sinLUT[];
	    float cosLUT[];
	    float delta, angle_step, angle;
	    int vertCount, currVert;
	    float r, u, v;
	    int v1, v11, v2, voff;
	    float iu, iv;
	      
	    sinLUT = new float[SINCOS_LENGTH];
	    cosLUT = new float[SINCOS_LENGTH];

	    for (int i = 0; i < SINCOS_LENGTH; i++) 
	    {
	        sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
	        cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
	    }  
	  
	    delta = (float) SINCOS_LENGTH / globeDetail;
	    cx = new float[globeDetail];
	    cz = new float[globeDetail];

	    // Calc unit circle in XZ plane
	    for (int i = 0; i < globeDetail; i++) 
	    {
	        cx[i] = -cosLUT[(int) (i * delta) % SINCOS_LENGTH];
	        cz[i] = sinLUT[(int) (i * delta) % SINCOS_LENGTH];
	    }

	    // Computing vertexlist vertexlist starts at south pole
	    vertCount = globeDetail * (globeDetail - 1) + 2;
	    currVert = 0;
	  
	    // Re-init arrays to store vertices
	    sphereX = new float[vertCount];
	    sphereY = new float[vertCount];
	    sphereZ = new float[vertCount];
	    angle_step = (SINCOS_LENGTH * 0.5f) / globeDetail;
	    angle = angle_step;
	  
	    // Step along Y axis
	    for (int i = 1; i < globeDetail; i++) 
	    {
	        float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
	        float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
	        for (int j = 0; j < globeDetail; j++) 
	        {
	            sphereX[currVert] = cx[j] * curradius;
	            sphereY[currVert] = currY;
	            sphereZ[currVert++] = cz[j] * curradius;
	        }
	        angle += angle_step;
	    }

	    vertices = new ArrayList();
	    texCoords = new ArrayList();
	    normals = new ArrayList();

	    r = globeRadius;
	    r = (r + 240 ) * 0.33f;

	    iu = (float) (1.0f / (globeDetail));
	    iv = (float) (1.0f / (globeDetail));
	    
	    // Add the southern cap    
	    u = 0;
	    v = iv;
	    for (int i = 0; i < globeDetail; i++) 
	    {
	        addVertex(0.0f, -r, 0.0f, u, 0);
	        addVertex(sphereX[i] * r, sphereY[i] * r, sphereZ[i] * r, u, v);        
	        u += iu;
	    }
	    addVertex(0.0f, -r, 0.0f, u, 0);
	    addVertex(sphereX[0] * r, sphereY[0] * r, sphereZ[0] * r, u, v);
	  
	    // Middle rings
	    voff = 0;
	    for (int i = 2; i < globeDetail; i++) 
	    {
	        v1 = v11 = voff;
	        voff += globeDetail;
	        v2 = voff;
	        u = 0;    
	        for (int j = 0; j < globeDetail; j++) 
	        {
	            addVertex(sphereX[v1] * r, sphereY[v1] * r, sphereZ[v1++] * r, u, v);
	            addVertex(sphereX[v2] * r, sphereY[v2] * r, sphereZ[v2++] * r, u, v + iv);
	            u += iu;
	        }
	  
	        // Close each ring
	        v1 = v11;
	        v2 = voff;
	        addVertex(sphereX[v1] * r, sphereY[v1] * r, sphereZ[v1] * r, u, v);
	        addVertex(sphereX[v2] * r, sphereY[v2] * r, sphereZ[v2] * r, u, v + iv);
	        
	        v += iv;
	    }
	    u=0;
	  
	    // Add the northern cap
	    for (int i = 0; i < globeDetail; i++) 
	    {
	        v2 = voff + i;
	        
	        addVertex(sphereX[v2] * r, sphereY[v2] * r, sphereZ[v2] * r, u, v);
	        addVertex(0, r, 0, u, v + iv);
	   
	        u+=iu;
	    }
	    addVertex(sphereX[voff] * r, sphereY[voff] * r, sphereZ[voff] * r, u, v);
	}

	void addVertex(float x, float y, float z, float u, float v)
	{
	    PVector vert = new PVector(x, y, z);
	    PVector texCoord = new PVector(u, v);
	    PVector vertNorm = PVector.div(vert, vert.mag()); 
	    vertices.add(vert);
	    texCoords.add(texCoord);
	    normals.add(vertNorm);
	}	
	
	public void draw() {
		  glg = (GLGraphics)g;
		  glg.beginGL();
		  
		  background(0);
		  
		  lights();
		  
		  
	  
	  // DLA is a *VERY* slow process so we need to
	  // compute a large number of iterations each frame
	  dla.update(10000);
	  fill(255);
	  text("particles: " + dla.getNumParticles(), 20, 20);
	  translate(width / 2, height / 2, 0);
	  rotateX(mouseY * 0.01f);
	  rotateY(mouseX * 0.01f);
	  scale(currScale);
	  // draw growth progress and guide particles
	  drawOctree(dla.getParticleOctree(), isOctreeVisible, 0xffff0000);
	  drawOctree(dla.getGuideOctree(), false, 0xff0000ff);
	  
	  glg.endGL();
	}

	// this method recursively paints an entire octree structure
	public void drawOctree(PointOctree node, boolean doShowGrid, int col) {
	  if (doShowGrid) {
	    drawBox(node);
	  }
	  if (node.getNumChildren() > 0) {
	    PointOctree[] children = node.getChildren();
	    for (int i = 0; i < 8; i++) {
	      if (children[i] != null) {
	        drawOctree(children[i], doShowGrid, col);
	      }
	    }
	  } 
	  else {
		    java.util.List points = node.getPoints();
		    if (points != null) {
		      int numP = points.size();
		      for (int i = 0; i < numP; i += 10) {
		        Vec3D p = (Vec3D)points.get(i);
		        pushMatrix();
		        	translate(p.x(), p.y(), p.z());
		        	scale(0.001f, 0.001f, 0.001f);
		        	glg.model(earth);
		        popMatrix();
		      }
		    }
		   
		  /*
	    java.util.List points = node.getPoints();
	    if (points != null) {
	      stroke(col);
	      beginShape(POINTS);
	      int numP = points.size();
	      for (int i = 0; i < numP; i += 10) {
	        Vec3D p = (Vec3D)points.get(i);
	        vertex(p.x, p.y, p.z);
	      }
	      endShape();
	    }
	    */
	  }
	}

	public void drawBox(PointOctree node) {
	  noFill();
	  stroke(0, 24);
	  pushMatrix();
	  translate(node.x, node.y, node.z);
	  box(node.getSize());
	  popMatrix();
	}

	public void keyPressed() {
	  if (key == '-') {
	    currScale -= 0.25f;
	  }
	  if (key == '=') {
	    currScale += 0.25f;
	  }
	  if (key=='o') {
	    isOctreeVisible=!isOctreeVisible;
	  }
	  if (key == 's') {
	    listener.save();
	  }
	}

	class DLAListener extends DLAEventAdapter {

	  // this method will be called when all guide segments
	  // have been processed
	  public void dlaAllSegmentsProcessed(DLA dla) {
	    println("all done, saving...");
	    save();
	  }

	  public void save() {
	    dla.save(sketchPath("spiral.dla"), false);
	  }
	}

	public static void main(String args[]) {
		PApplet.main(new String[] {
			"drole.tests.dla.DLASpiral2"
		});
	}
}
