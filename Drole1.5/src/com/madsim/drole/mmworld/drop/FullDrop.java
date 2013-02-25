package com.madsim.drole.mmworld.drop;


import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

import toxi.physics.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.volume.*;

public class FullDrop {
	
	private PApplet p;

	private float REST_LENGTH = 100;
	private int DIM = 200;

	private int GRID = 36;
	private Vec3D SCALE = new Vec3D(DIM, DIM, DIM).scale(2);
	private float isoThreshold = 1.0f;

	private int numP;
	private VerletPhysics physics;

	private VolumetricSpaceArray volume;
	private IsoSurface surface;

	private WETriangleMesh mesh = new WETriangleMesh("fluid");

	private boolean isClosed = true;

	public FullDrop(PApplet p) {
		this.p = p;
		
		initPhysics();
		volume = new VolumetricSpaceArray(SCALE, GRID, GRID, GRID);
		surface = new ArrayIsoSurface(volume);
	}

	public void draw(PGraphicsOpenGL g) {
		updateParticles();

		computeVolume();

		g.pushMatrix();
	
			g.noStroke();
	
			g.beginShape(PGraphicsOpenGL.TRIANGLES);
				drawFilledMesh(g);
			g.endShape();

		g.popMatrix();
	}

	private void computeVolume() {
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

	private void drawFilledMesh(PGraphicsOpenGL g) {
		int num = mesh.getNumFaces();
		mesh.computeVertexNormals();
		for (int i = 0; i < num; i++) {
			Face f = mesh.faces.get(i);

			normal(g, f.a.normal);
			vertex(g, f.a);
			
			normal(g, f.b.normal);
			vertex(g, f.b);
			
			normal(g, f.c.normal);
			vertex(g, f.c);
		}
	}

	private void normal(PGraphicsOpenGL g, Vec3D v) {
		g.normal(v.x, v.y, v.z);
	}

	private void vertex(PGraphicsOpenGL g, Vec3D v) {
		g.vertex(v.x, v.y, v.z);
	}

	private void initPhysics() {
		physics = new VerletPhysics();
		physics.setWorldBounds(new AABB(new Vec3D(), new Vec3D(DIM, DIM, DIM)));
		if (surface != null) {
			surface.reset();
			mesh.clear();
		}
	}

	private void updateParticles() {
		numP = physics.particles.size();
		if (numP < 12) {
			VerletParticle center = new VerletParticle(0, 0, 0);
			center.lock();
			physics.addParticle(center);
			int numFirstBranch = 12;
			for (int i = 0; i < numFirstBranch; i++) {
				VerletParticle s = new VerletParticle(p.random(-100, 100), p.random(-100, 100), p.random(-100, 100));
				physics.addParticle(s);
				physics.addSpring(new VerletSpring(center, s, REST_LENGTH, 0.0001f));
			}
		}

		physics.update();
	}

}
