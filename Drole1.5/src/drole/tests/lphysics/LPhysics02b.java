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

import processing.core.PApplet;

import toxi.physics.*;
import toxi.physics.behaviors.*;
import toxi.physics.constraints.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.volume.*;

public class LPhysics02b extends PApplet {

	private static final long serialVersionUID = 1L;

	int NUM_PARTICLES = 100;
	float REST_LENGTH = 100;
	int DIM = 200;

	int GRID = 36;
	float VS = 2 * DIM / GRID;
	Vec3D SCALE = new Vec3D(DIM, DIM, DIM).scale(2);
	float isoThreshold = 1.0f;

	int numP;
	VerletPhysics physics;
	ParticleConstraint boundingSphere;
	GravityBehavior gravity;

	VolumetricSpaceArray volume;
	IsoSurface surface;

	WETriangleMesh mesh = new WETriangleMesh("fluid");

	boolean showPhysics = false;
	boolean isWireFrame = false;

	boolean isClosed = true;
	boolean useBoundary = true;

	Vec3D colAmp = new Vec3D(400, 200, 200);

	public void setup() {
		size(1280, 720, OPENGL);
		smooth();
		initPhysics();
		volume = new VolumetricSpaceArray(SCALE, GRID, GRID, GRID);
		surface = new ArrayIsoSurface(volume);
	}

	public void draw() {
		updateParticles();

		computeVolume();

		background(224);

		pushMatrix();

			translate(width/2, height * 0.5f, 0);
			rotateX(mouseY * 0.01f);
			rotateY(mouseX * 0.01f);
	
			ambientLight(216, 216, 216);
			directionalLight(255, 255, 255, 0, 1, 0);
			directionalLight(96, 96, 96, 1, 1, -1);
	
			noStroke();
	
			beginShape(TRIANGLES);
				drawFilledMesh();
			endShape();

		popMatrix();
	}

	public void computeVolume() {
		float cellSize = (float) DIM * 2 / GRID;
		Vec3D pos = new Vec3D();
		Vec3D offset = physics.getWorldBounds().getMin();
		float[] volumeData = volume.getData();
		for (int z = 0, index = 0; z < GRID; z++) {
			pos.z = z * cellSize + offset.z;
			for (int y = 0; y < GRID; y++) {
				pos.y = y * cellSize + offset.y;
				for (int x = 0; x < GRID; x++) {
					pos.x = x * cellSize + offset.x;
					float val = 0;
					for (int i = 0; i < numP; i++) {
						Vec3D p = (Vec3D) physics.particles.get(i);
						float mag = pos.distanceToSquared(p) + 0.00001f;
						val += 1 / mag;
					}
					volumeData[index++] = val;
				}
			}
		}
		if (isClosed) {
			volume.closeSides();
		}
		surface.reset();
		surface.computeSurfaceMesh(mesh, isoThreshold * 0.001f);
	}

	public void drawFilledMesh() {
		int num = mesh.getNumFaces();
		mesh.computeVertexNormals();
		for (int i = 0; i < num; i++) {
			Face f = mesh.faces.get(i);
			Vec3D col = f.a.add(colAmp).scaleSelf(0.5f);
			fill(col.x, col.y, col.z);
			normal(f.a.normal);
			vertex(f.a);
			col = f.b.add(colAmp).scaleSelf(0.5f);
			fill(col.x, col.y, col.z);
			normal(f.b.normal);
			vertex(f.b);
			col = f.c.add(colAmp).scaleSelf(0.5f);
			fill(col.x, col.y, col.z);
			normal(f.c.normal);
			vertex(f.c);
		}
	}

	public void normal(Vec3D v) {
		normal(v.x, v.y, v.z);
	}

	public void vertex(Vec3D v) {
		vertex(v.x, v.y, v.z);
	}

	public void initPhysics() {
		physics = new VerletPhysics();
		physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(DIM, DIM, DIM)));
		if (surface != null) {
			surface.reset();
			mesh.clear();
		}
	}

	public void updateParticles() {
		numP = physics.particles.size();
		if (numP < 12) {
			VerletParticle center = new VerletParticle(0, 0, 0);
			center.lock();
			physics.addParticle(center);
			int numFirstBranch = 12;
			for (int i = 0; i < numFirstBranch; i++) {
				VerletParticle s = new VerletParticle(random(-100, 100),
						random(-100, 100), random(-100, 100));
				physics.addParticle(s);
				physics.addSpring(new VerletSpring(center, s, REST_LENGTH,
						0.0001f));
			}
		}

		physics.update();
	}

	public void keyPressed() {
		if (key == 'r')
			initPhysics();
		if (key == 'w')
			isWireFrame = !isWireFrame;
		if (key == 'p')
			showPhysics = !showPhysics;
		if (key == 'c')
			isClosed = !isClosed;
		if (key == '-' || key == '_') {
			isoThreshold -= 0.001;
		}
		if (key == '=' || key == '+') {
			isoThreshold += 0.001;
		}
		if (key == 's') {
			mesh.saveAsOBJ(sketchPath(mesh.name + ".obj"));
			mesh.saveAsSTL(sketchPath(mesh.name + ".stl"));
		}
	}

}
