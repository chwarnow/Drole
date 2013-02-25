package com.madsim.drole.mmworld.drop;

import processing.opengl.PGraphicsOpenGL;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;
import toxi.physics.VerletPhysics;
import toxi.volume.ArrayIsoSurface;
import toxi.volume.IsoSurface;
import toxi.volume.VolumetricSpaceArray;

public class DropMesh {

	private int DIM = 200;

	private int GRID = 36;
	private Vec3D SCALE = new Vec3D(DIM, DIM, DIM).scale(2);
	private float isoThreshold = 1.0f;

	private int numP;

	private VolumetricSpaceArray volume;
	private IsoSurface surface;

	private WETriangleMesh mesh = new WETriangleMesh("fluid");

	private boolean isClosed = true;

	private Vec3D colAmp = new Vec3D(400, 200, 200);

	public DropMesh(int DIM) {
		this.DIM = DIM;
		
		volume = new VolumetricSpaceArray(SCALE, GRID, GRID, GRID);
		surface = new ArrayIsoSurface(volume);
	}

	public void update(VerletPhysics physics) {
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
		mesh.computeVertexNormals();
	}
	
	public void draw(PGraphicsOpenGL g) {
		g.noStroke();
		g.fill(200, 100);
		
		g.beginShape(PGraphicsOpenGL.TRIANGLES);
		
			int num = mesh.getNumFaces();
			
			for (int i = 0; i < num; i++) {
				Face f = mesh.faces.get(i);
				
				Vec3D col = f.a.add(colAmp).scaleSelf(0.5f);
				g.fill(col.x, col.y, col.z);
				normal(g, f.a.normal);
				vertex(g, f.a);
				
				col = f.b.add(colAmp).scaleSelf(0.5f);
				g.fill(col.x, col.y, col.z);
				normal(g, f.b.normal);
				vertex(g, f.b);
				
				col = f.c.add(colAmp).scaleSelf(0.5f);
				g.fill(col.x, col.y, col.z);
				normal(g, f.c.normal);
				vertex(g, f.c);
			}
			
		g.endShape();
	}
	
	public void normal(PGraphicsOpenGL g, Vec3D v) {
		g.normal(v.x, v.y, v.z);
	}

	public void vertex(PGraphicsOpenGL g, Vec3D v) {
		g.vertex(v.x, v.y, v.z);
	}
	
}
