package drole.gfx.room;


import processing.core.PGraphics;

import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

public class Room extends Drawable {
	
	private GLModel model;
	
	private float p = 1800;
	private float m = p/2f;
	
	private float[] vertices = new float[]{
		// Left
		-m, -m,  0,
		-m, -m, -p,
		-m,  m, -p,
		-m,  m,  0,
		
		// Right
		 m, -m, -p,
		 m, -m,  0,
		 m,  m,  0,
		 m,  m, -p,
		 
		 // Top
		-m, -m,  0,
		 m, -m,  0,
		 m, -m, -p,
		-m, -m, -p,
		
		// Bottom
		-m,  m,  0,
		 m,  m,  0,
		 m,  m, -p,
		-m,  m, -p,
		
		// Back
		-m, -m, -p,
		 m, -m, -p,
		 m,  m, -p,
		-m,  m, -p
		
	};
	private float[] coords = new float[]{
		// Left
		0.00000000f, 0.33333333f,
		0.33333333f, 0.33333333f,
		0.33333333f, 0.66666666f,
		0.00000000f, 0.66666666f,
		
		// Right
		0.66666666f, 0.33333333f,
		1.00000000f, 0.33333333f,
		1.00000000f, 0.66666666f,
		0.66666666f, 0.66666666f,
		
		// Top
		0.33333333f, 0.00000000f,
		0.66666666f, 0.00000000f,
		0.66666666f, 0.33333333f,
		0.33333333f, 0.33333333f,
		
		// Bottom
		0.33333333f, 0.66666666f,
		0.66666666f, 0.66666666f,
		0.66666666f, 1.00000000f,
		0.33333333f, 1.00000000f,
		
		// Back
		0.33333333f, 0.33333333f,
		0.66666666f, 0.33333333f,
		0.66666666f, 0.66666666f,
		0.33333333f, 0.66666666f		
	};
	private float[] normals = new float[]{
		// Left
		  1,   0,  0,
		
		// Right
		 -1,  0,  0,
		
		// Top
		  0,   1,  0,
		 
		// Bottom
		  0,   -1,  0,
		 
		// Back
		  0,   0,  1
	};
	
	private int numSides = 5;
	
	public Room(Engine e, String cubeMapFilename) {
		super(e);
		
		SHADOW_HINT = Drawable.RECEIVE_SHADOW;
		
		model = new GLModel(e.p, numSides*(12), PGraphics.QUADS, GLModel.STATIC);
		
		model.initColors();
		model.initNormals();
		
		model.initTextures(1);
		model.setTexture(0, new GLTexture(e.p, cubeMapFilename));
		
		model.setColors(100);
		
		/*
		model.beginUpdateColors();
			// Left
			model.updateColor( 0, 255, 0, 0);
			model.updateColor( 1, 255, 0, 0);
			model.updateColor( 2, 255, 0, 0);
			model.updateColor( 3, 255, 0, 0);
			
			// Right
			model.updateColor( 4, 0, 255, 0);
			model.updateColor( 5, 0, 255, 0);
			model.updateColor( 6, 0, 255, 0);
			model.updateColor( 7, 0, 255, 0);
			
			// Top
			model.updateColor( 8, 0, 0, 255);
			model.updateColor( 9, 0, 0, 255);
			model.updateColor(10, 0, 0, 255);
			model.updateColor(11, 0, 0, 255);
			
			// Bottom
			model.updateColor(12, 255, 255, 0);
			model.updateColor(13, 255, 255, 0);
			model.updateColor(14, 255, 255, 0);
			model.updateColor(15, 255, 255, 0);
			
			// Back
			model.updateColor(16, 0, 255, 255);
			model.updateColor(17, 0, 255, 255);
			model.updateColor(18, 0, 255, 255);
			model.updateColor(19, 0, 255, 255);
		model.endUpdateColors();
		*/
		
		int nv = 0;
		model.beginUpdateVertices();
			for(int i = 0; i < numSides*(12); i+=3) {
				model.updateVertex(nv++, vertices[i], vertices[i+1], vertices[i+2]);
			}
		model.endUpdateVertices();
		
		nv = 0;
		model.beginUpdateTexCoords(0);
			for(int i = 0; i < numSides*(8); i+=2) {
				model.updateTexCoord(nv++, coords[i], coords[i+1]);
			}
		model.endUpdateTexCoords();
			
		nv = 0;
		model.beginUpdateNormals();
			model.updateNormal(0, normals[0], normals[1], normals[2]);
			model.updateNormal(1, normals[0], normals[1], normals[2]);
			model.updateNormal(2, normals[0], normals[1], normals[2]);
			model.updateNormal(3, normals[0], normals[1], normals[2]);
			
			model.updateNormal(4, normals[3], normals[4], normals[5]);
			model.updateNormal(5, normals[3], normals[4], normals[5]);
			model.updateNormal(6, normals[3], normals[4], normals[5]);
			model.updateNormal(7, normals[3], normals[4], normals[5]);
			
			model.updateNormal(8, normals[6], normals[7], normals[8]);
			model.updateNormal(9, normals[6], normals[7], normals[8]);
			model.updateNormal(10, normals[6], normals[7], normals[8]);
			model.updateNormal(11, normals[6], normals[7], normals[8]);
			
			model.updateNormal(12, normals[9], normals[10], normals[11]);
			model.updateNormal(13, normals[9], normals[10], normals[11]);
			model.updateNormal(14, normals[9], normals[10], normals[11]);
			model.updateNormal(15, normals[9], normals[10], normals[11]);
			
			model.updateNormal(16, normals[12], normals[13], normals[14]);
			model.updateNormal(17, normals[12], normals[13], normals[14]);
			model.updateNormal(18, normals[12], normals[13], normals[14]);
			model.updateNormal(19, normals[12], normals[13], normals[14]);
		model.endUpdateNormals();
	}
	
	@Override
	public void draw() {
		g.pushStyle();
		g.pushMatrix();
		
			g.noFill();
			g.noStroke();
			
			g.translate(position.x, position.y, position.z);
			
			g.fill(200);
			g.noStroke();
			g.tint(255, 255);
			
			e.setupModel(model);
			model.render();
			
		g.popMatrix();
		g.popStyle();
	}
	
}
