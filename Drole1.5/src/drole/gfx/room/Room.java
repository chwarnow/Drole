package drole.gfx.room;

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
		 1,  0,  0,
		
		// Right
		-1,  0,  0,
		
		// Top
		 0,  1,  0,
		 
		// Bottom
		 0, -1,  0,
		 
		// Back
		 0,  0, -1
	};
	
	private int numSides = 5;
	
	public Room(Engine e, String cubeMapFilename) {
		super(e);
		
		SHADOW_HINT = Drawable.RECEIVE_SHADOW;
		
		model = new GLModel(e.p, numSides*(12), GLModel.QUADS, GLModel.STATIC);
		
		model.initColors();
		model.beginUpdateColors();
			for(int i = 0; i < numSides*4; i++) {
//				model.updateColor(i, e.p.random(0, 255), e.p.random(0, 255), e.p.random(0, 255), 225);
//				model.updateColor(i, e.p.random(100, 255));
				model.updateColor(i, 255);
			}
		model.endUpdateColors();
		
		model.initNormals();
		
		model.initTextures(1);
		model.setTexture(0, new GLTexture(e.p, cubeMapFilename));
		
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
			for(int i = 0; i < numSides*(3); i+=3) {
				model.updateNormal(nv++, normals[i], normals[i+1], normals[i+2]);
			}
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
