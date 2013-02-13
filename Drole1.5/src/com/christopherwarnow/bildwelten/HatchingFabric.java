package com.christopherwarnow.bildwelten;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;

public class HatchingFabric {
	private PApplet parent;
	private VerletPhysics physics;
	private TriangleMesh mesh;

	// amounts
	private int detail=30;
	private float springLength=20;
	private float STRENGTH=1;

	// glmodel vars
	private GLModel fabricMesh;
	private GLSLShader shader;
	private GLTexture tex0, tex1, tex2, tex3, tex4, tex5, imageTex;

	private String contentTexturePath;
	
	private float scalingX, scalingY;
	public HatchingFabric(PApplet parent, String contentTexturePath, int detail, float springLength, float scalingX, float scalingY) {
		this.parent = parent;
		this.detail = detail;
		this.springLength = springLength;
		this.scalingX = scalingX;
		this.scalingY = scalingY;
		
		this.contentTexturePath = contentTexturePath;

		// init physics
		initPhysics();
		// init toxic mesh
		updateMesh();
		// init glmodel
		initGLModel();

	}

	public void draw() {
		
		//-------------- updating simulation
		
		// update simulation
		physics.update();
		// update cloth mesh
		updateMesh();

		// convert to glmodel
		updateGLModel();

		//-------------- drawing part
		
		parent.pushMatrix();

		GLGraphics renderer = (GLGraphics)parent.g;
		renderer.beginGL();
		
		renderer.gl.glEnable(GL.GL_TEXTURE_2D);
		
		shader.start(); // Enabling shader.
		shader.setIntUniform("Hatch0", 0);
		shader.setIntUniform("Hatch1", 1);
		shader.setIntUniform("Hatch2", 2);
		shader.setIntUniform("Hatch3", 3);
		shader.setIntUniform("Hatch4", 4);
		shader.setIntUniform("Hatch5", 5);
		shader.setIntUniform("imageTex", 6);
		shader.setVecUniform("lightDir", -.5f, 1.0f, 1.0f);

		// render the fabric mesh
		renderer.model(fabricMesh);

		shader.stop(); // Disabling shader.
		
		renderer.endGL();
		parent.popMatrix();
	}

	// iterates over all particles in the grid order
	// they were created and constructs triangles
	private void updateMesh() {

		// create toxiclibs mesh
		mesh=new TriangleMesh();
		for (int y=0; y<detail-1; y++) {
			for (int x=0; x<detail-1; x++) {
				int i=y*detail+x;
				VerletParticle a=physics.particles.get(i);
				VerletParticle b=physics.particles.get(i+1);
				VerletParticle c=physics.particles.get(i+1+detail);
				VerletParticle d=physics.particles.get(i+detail);
				mesh.addFace(a, d, c);
				mesh.addFace(a, c, b);
			}
		}
	}

	private void updateGLModel() {
		// convert to glmodel mesh
		mesh.computeVertexNormals();
		float[] verts = mesh.getMeshAsVertexArray();
		int numV = verts.length / 4; // The vertices array from the mesh object has a spacing of 4.
		float[] norms = mesh.getVertexNormalsAsArray();

		// TODO: just copy vertex arrays, its faster
		fabricMesh.beginUpdateVertices();
		for (int i = 0; i < numV; i++) fabricMesh.updateVertex(i, verts[4 * i], verts[4 * i + 1], verts[4 * i + 2]);
		fabricMesh.endUpdateVertices(); 

		fabricMesh.initNormals();
		fabricMesh.beginUpdateNormals();
		for (int i = 0; i < numV; i++) fabricMesh.updateNormal(i, norms[4 * i], norms[4 * i + 1], norms[4 * i + 2]);
		fabricMesh.endUpdateNormals();
	}

	private void initPhysics() {
		// create the physical world by constructing all
		// obstacles/constraints, particles and connecting them
		// in the correct order using springs

		physics=new VerletPhysics();
		physics.addBehavior(new GravityBehavior(new Vec3D(0,0.1f,0)));

		for(int y=0,idx=0; y<detail; y++) {
			for(int x=0; x<detail; x++) {
				VerletParticle p=new VerletParticle(x*springLength*scalingX-(detail*springLength*scalingX)/2,-200,y*springLength*scalingY-(detail*springLength*scalingY)/2);
				if(y == 0 && x == 0) p.lock();
				if(y == 0 && x == detail-1) p.lock();
				// TODO: move upper line on a sine
				physics.addParticle(p);
				if (x>0) {
					VerletSpring s=new VerletSpring(p,physics.particles.get(idx-1),springLength*scalingX,STRENGTH);
					physics.addSpring(s);
				}
				if (y>0) {
					VerletSpring s=new VerletSpring(p,physics.particles.get(idx-detail),springLength*scalingY,STRENGTH);
					physics.addSpring(s);
				}
				idx++;
			}
		}
	}

	private void initGLModel() {
		float[] verts = mesh.getMeshAsVertexArray();
		int numV = verts.length / 4; // The vertices array from the mesh object has a spacing of 4.

		fabricMesh = new GLModel(parent, numV, parent.TRIANGLES, GLModel.STATIC);

		// load tone map

		// Enabling the use of texturing...
		fabricMesh.initTextures(7);
		// ... and loading and setting texture for this model.
		tex0 = new GLTexture(parent, "data/images/tonemap_d0.png");
		tex1 = new GLTexture(parent, "data/images/tonemap_d1.png");
		tex2 = new GLTexture(parent, "data/images/tonemap_d2.png");
		tex3 = new GLTexture(parent, "data/images/tonemap_d3.png");
		tex4 = new GLTexture(parent, "data/images/tonemap_d4.png");
		tex5 = new GLTexture(parent, "data/images/tonemap_d5.png");
		imageTex = new GLTexture(parent, contentTexturePath);

		fabricMesh.setTexture(0, tex0);
		fabricMesh.setTexture(1, tex1);
		fabricMesh.setTexture(2, tex2);
		fabricMesh.setTexture(3, tex3);
		fabricMesh.setTexture(4, tex4);
		fabricMesh.setTexture(5, tex5);
		fabricMesh.setTexture(6, imageTex);
		
		// texture coords
		// Setting the texture coordinates.
		for (int texID=0;texID<7;texID++) {
			fabricMesh.beginUpdateTexCoords(texID);
			int index = 0;
			for (int y=0; y<detail-1; y++) {
				for (int x=0; x<detail-1; x++) {
					int i=y*detail+x;

					float ua = (float) x / (detail-1); // i
					float va = (float) y / (detail-1); // i

					float ub = (float) (x+1) / (detail-1);
					float vb = (float) (y) / (detail-1);

					float uc = (float) (x+1) / (detail-1);
					float vc = (float) (y+1) / (detail-1);

					float ud = (float) (x) / (detail-1);
					float vd = (float) (y+1) / (detail-1);

					// texture coord
					fabricMesh.updateTexCoord(index++, ua, va);
					fabricMesh.updateTexCoord(index++, ud, vd);
					fabricMesh.updateTexCoord(index++, uc, vc);

					fabricMesh.updateTexCoord(index++, ua, va);
					fabricMesh.updateTexCoord(index++, uc, vc);
					fabricMesh.updateTexCoord(index++, ub, vb);
				}
			}
			fabricMesh.endUpdateTexCoords();
		}

		// shader
		shader = new GLSLShader(parent, "data/shader/hatch_vert.glsl", "data/shader/hatch_frag.glsl");
	}
}
