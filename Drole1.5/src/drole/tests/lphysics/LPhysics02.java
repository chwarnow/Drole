package drole.tests.lphysics;

/**
 * <p>BoxFLuid demo combining 3D physics particles with the IsoSurface class to
 * create an animated mesh with a fluid behaviour. The mesh is optionally created
 * within a boundary sphere, but other forms can be created using a custom
 * ParticleConstraint class.</p>
 * 
 * <p>Dependencies:</p>
 * <ul>
 * <li>toxiclibscore-0015 or newer package from <a href="http://toxiclibs.org">toxiclibs.org</a></li>
 * <li>verletphysics-0004 or newer package from <a href="http://toxiclibs.org">toxiclibs.org</a></li>
 * <li>volumeutils-0002 or newer package from <a href="http://toxiclibs.org">toxiclibs.org</a></li>
 * <li>controlP5 GUI library from <a href="http://sojamo.de">sojamo.de</a></li>
 * </ul>
 * 
 * <p>Key controls:</p>
 * <ul>
 * <li>w : wireframe on/off</li>
 * <li>c : close sides on/off</li>
 * <li>p : show particles only on/off</li>
 * <li>b : turn bounding sphere on/off</li>
 * <li>r : reset particles</li>
 * <li>s : save current mesh as OBJ & STL format</li>
 * <li>- / = : decrease/increase surface threshold/tightness</li>
 * </ul>
 */

/* 
 * Copyright (c) 2009 Karsten Schmidt
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

import java.util.Iterator;

import codeanticode.glgraphics.GLGraphics;

import processing.core.PApplet;
import processing.opengl.*;

import toxi.physics.*;
import toxi.physics.behaviors.*;
import toxi.physics.constraints.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.math.*;
import toxi.volume.*;

import controlP5.*;

public class LPhysics02 extends PApplet {

	private static final long serialVersionUID = 1L;
	
	int NUM_PARTICLES = 100;
	float REST_LENGTH= 300;
	int DIM=200;
	
	int GRID=18;
	float VS=2*DIM/GRID;
	Vec3D SCALE=new Vec3D(DIM,DIM,DIM).scale(2);
	float isoThreshold=3;
	
	int numP;
	VerletPhysics physics;
	ParticleConstraint boundingSphere;
	GravityBehavior gravity;
	
	VolumetricSpaceArray volume;
	IsoSurface surface;
	
	WETriangleMesh mesh = new WETriangleMesh("fluid");
	
	ControlP5 ui;
	
	boolean showPhysics=false;
	boolean isWireFrame=false;
	boolean isClosed=true;
	boolean useBoundary=true;
	
	Vec3D colAmp=new Vec3D(400, 200, 200);
	
	public void setup() {
	  size(1280,720, GLGraphics.GLGRAPHICS);
	  // Y u ignore me?????
	  frameRate(2000);
	  smooth();
	  initPhysics();
	  initGUI();
	  volume=new VolumetricSpaceArray(SCALE, GRID, GRID, GRID);
	  surface=new ArrayIsoSurface(volume);
	  textFont(createFont("SansSerif",12));
	}

	public void initGUI() {
	  ui = new ControlP5(this);
	  ui.addSlider("isoThreshold",1,12,isoThreshold,20,20,100,14).setLabel("iso threshold");

	  ui.addToggle("showPhysics",showPhysics,20,60,14,14).setLabel("show particles");
	  ui.addToggle("isWireFrame",isWireFrame,20,100,14,14).setLabel("wireframe");
	  ui.addToggle("isClosed",isClosed,20,140,14,14).setLabel("closed mesh");
	  ui.addToggle("toggleBoundary",useBoundary,20,180,14,14).setLabel("use boundary");

	  ui.addBang("initPhysics",20,240,28,28).setLabel("restart");
	}

	
	
	public void draw() {
		background(40);

		updateParticles();
	  computeVolume();
	  
	  pushMatrix();
	  translate(width/2,height*0.5f,0);
	  rotateX(mouseY*0.01f);
	  rotateY(mouseX*0.01f);
	  
	  fill(200, 200, 0);
	  drawFilledMesh();
	  
//	  drawPhysicSystem();
	  
	  noFill();
	  stroke(255,192);
	  strokeWeight(1);
	  box(physics.getWorldBounds().getExtent().x*2);
	  if (showPhysics) {
	    strokeWeight(4);
	    stroke(0);
	    for(Iterator i=physics.particles.iterator(); i.hasNext();) {
	      VerletParticle p=(VerletParticle)i.next();
	      Vec3D col=p.add(colAmp).scaleSelf(0.5f);
	      stroke(col.x,col.y,col.z);
	      point(p.x,p.y,p.z);
	    }
	  } 
	  else {
	    ambientLight(216, 216, 216);
	    directionalLight(255, 255, 255, 0, 1, 0);
	    directionalLight(96, 96, 96, 1, 1, -1);
	    if (isWireFrame) {
	      stroke(255);
	      noFill();
	    } 
	    else {
	    	noStroke();
	    	fill(200, 100);
	    }
	    beginShape(TRIANGLES);
	    if (!isWireFrame) {
	      drawFilledMesh();
	    } 
	    else {
	      drawWireMesh();
	    }
	    endShape();
	  }
	  
	  popMatrix();
	  
	  noLights();
	  fill(0);
	  text("faces: "+mesh.getNumFaces(),20,600);
	  text("vertices: "+mesh.getNumVertices(),20,615);
	  text("particles: "+physics.particles.size(),20,630);
	  text("springs: "+physics.springs.size(),20,645);
	  text("fps: "+frameRate, 20, 690);
	}
	
	public void drawPhysicSystem() {
		noStroke();
		fill(200, 200, 0);
		for(int i = 0; i < physics.particles.size(); i++) {
			g.pushMatrix();
				g.translate(physics.particles.get(i).x(), physics.particles.get(i).y(), physics.particles.get(i).z());
				g.sphere(5);
			g.popMatrix();
		}
		
		strokeWeight(1);
		stroke(200);
		noFill();
		for(int i = 0; i < physics.springs.size(); i++) {
			g.line(
					physics.springs.get(i).a.x(), 
					physics.springs.get(i).a.y(), 
					physics.springs.get(i).a.z(), 
					physics.springs.get(i).b.x(), 
					physics.springs.get(i).b.y(), 
					physics.springs.get(i).b.z()
			);
		}		
	}
	
	public void computeVolume() {
		  float cellSize=(float)DIM*2/GRID;
		  Vec3D pos=new Vec3D();
		  Vec3D offset=physics.getWorldBounds().getMin();
		  float[] volumeData=volume.getData();
		  for(int z=0,index=0; z<GRID; z++) {
		    pos.z=z*cellSize+offset.z;
		    for(int y=0; y<GRID; y++) {
		      pos.y=y*cellSize+offset.y;
		      for(int x=0; x<GRID; x++) {
		        pos.x=x*cellSize+offset.x;
		        float val=0;
		        for(int i=0; i<numP; i++) {
		          Vec3D p=(Vec3D)physics.particles.get(i);
		          float mag=pos.distanceToSquared(p)+0.00001f;
		          val+=1/mag;
		        }
		        volumeData[index++]=val;
		      }
		    }
		  }
		  if (isClosed) {
		    volume.closeSides();
		  }
		  surface.reset();
		  surface.computeSurfaceMesh(mesh, isoThreshold*0.001f);
		}

	public void drawFilledMesh() {
		  int num=mesh.getNumFaces();
		  mesh.computeVertexNormals();
//		  new LaplacianSmooth().filter(mesh, 2);
		  for(int i=0; i<num; i++) {
		    Face f=mesh.faces.get(i);
		    normal(f.a.normal);
		    vertex(f.a);
		    normal(f.b.normal);
		    vertex(f.b);
		    normal(f.c.normal);
		    vertex(f.c);
		  }
		}

	public void drawWireMesh() {
		  noFill();
		  int num=mesh.getNumFaces();
		  for(int i=0; i<num; i++) {
		    Face f=mesh.faces.get(i);
		    Vec3D col=f.a.add(colAmp).scaleSelf(0.5f);
		    stroke(col.x,col.y,col.z);
		    vertex(f.a);
		    col=f.b.add(colAmp).scaleSelf(0.5f);
		    stroke(col.x,col.y,col.z);
		    vertex(f.b);
		    col=f.c.add(colAmp).scaleSelf(0.5f);
		    stroke(col.x,col.y,col.z);
		    vertex(f.c);
		  }
		}

	public void normal(Vec3D v) {
		  normal(v.x,v.y,v.z);
		}

	public void vertex(Vec3D v) {
		  vertex(v.x,v.y,v.z);
		}	
	
	public void initPhysics() {
		  physics=new VerletPhysics();
		  physics.setWorldBounds(new AABB(new Vec3D(),new Vec3D(DIM,DIM,DIM)));
		  if (surface!=null) {
		    surface.reset();
		    mesh.clear();
		  }
		  boundingSphere=new SphereConstraint(new Sphere(new Vec3D(),DIM),SphereConstraint.INSIDE);
		  gravity=new GravityBehavior(new Vec3D(0,0,0));
		  physics.addBehavior(gravity);
		  
		  VerletParticle center = new VerletParticle(0, 0, 0);
		  physics.addParticle(center);
		  int numFirstBranch = 5;
		  for(int i = 0; i < numFirstBranch; i++) {
//			  VerletParticle s = new VerletParticle(center.x(), center.y(), center.z());
			  VerletParticle s = new VerletParticle(random(-100, 100), random(-100, 100), random(-100, 100));
			  physics.addParticle(s);
			  physics.addSpring(new VerletSpring(center, s, 100, 0.001f));
			  
			  for(int ii = 0; ii < 10; ii++) {
//				  VerletParticle s = new VerletParticle(center.x(), center.y(), center.z());
				  VerletParticle ss = new VerletParticle(random(-100, 100), random(-100, 100), random(-100, 100));
				  physics.addParticle(ss);
				  physics.addSpring(new VerletSpring(s, ss, 10, 0.001f));
			  }
		  }
		}

	public void updateParticles() {
		/*
		  Vec3D grav=Vec3D.Y_AXIS.copy();
		  grav.rotateX(mouseY*0.01f);
		  grav.rotateY(mouseX*0.01f);
		  gravity.setForce(grav.scaleSelf(2));
		  */
		  /*
		  numP=physics.particles.size();
		  if (random(1)<0.8 && numP<NUM_PARTICLES) {
		    VerletParticle p=new VerletParticle(new Vec3D(random(-1,1)*10,-DIM,random(-1,1)*10));
		    if (useBoundary) p.addConstraint(boundingSphere);
		    physics.addParticle(p);
		  }
		  if (numP>10 && physics.springs.size()<1400) {
		    for(int i=0; i<60; i++) {
		      if (random(1)<0.04) {
		        VerletParticle q=physics.particles.get((int)random(numP));
		        VerletParticle r=q;
		        while(q==r) {
		          r=physics.particles.get((int)random(numP));
		        }
		        physics.addSpring(new VerletSpring(q,r,REST_LENGTH, 0.0002f));
		      }
		    }
		  }
		  float len=(float)numP/NUM_PARTICLES*REST_LENGTH;
		  for(Iterator i=physics.springs.iterator(); i.hasNext();) {
		    VerletSpring s=(VerletSpring)i.next();
		    s.setRestLength(random(0.9f,1.1f)*len);
		  }
		  */
		  physics.update();
		}	
	
	public void keyPressed() {
	  if (key=='r') initPhysics();
	  if (key=='w') isWireFrame=!isWireFrame;
	  if (key=='p') showPhysics=!showPhysics;
	  if (key=='c') isClosed=!isClosed;
	  if (key=='b') {
	    toggleBoundary();
	  }
	  if (key=='-' || key=='_') {
	    isoThreshold-=0.001;
	  }
	  if (key=='=' || key=='+') {
	    isoThreshold+=0.001;
	  }
	  if (key=='s') {
	    mesh.saveAsOBJ(sketchPath(mesh.name+".obj"));
	    mesh.saveAsSTL(sketchPath(mesh.name+".stl"));
	  }
	}
	
	public void toggleBoundary() {
	  useBoundary=!useBoundary;
	  initPhysics();
	}

}
