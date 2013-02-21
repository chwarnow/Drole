package drole.gfx.ribbon;

import codeanticode.glgraphics.GLModel;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import processing.core.PApplet;
import processing.core.PVector;

//M_1_6_01_TOOL.pde
//Agent.pde, GUI.pde, Ribbon3d.pde, TileSaver.pde
//
//Generative Gestaltung, ISBN: 978-3-87439-759-9
//First Edition, Hermann Schmidt, Mainz, 2009
//Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
//Copyright 2009 Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
//
//http://www.generative-gestaltung.de
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

public class Ribbon3D extends Drawable {

	private int numJoints; // how many points has the ribbon
	private int numVertices;
	
	private PVector[] joints;
	
	private GLModel model;
	
	public Ribbon3D(Engine e, PVector startPosition, int numJoints) {
		super(e);
		this.numJoints = numJoints;
		joints = new PVector[numJoints];
		for(int i = 0; i < numJoints; i++) joints[i] = new PVector(startPosition.x, startPosition.y, startPosition.z);
		
		numVertices = (numJoints-1)*2;
		
		model = new GLModel(e.p, numVertices, GLModel.QUAD_STRIP, GLModel.DYNAMIC);
		
		model.initColors();
		model.setColors(e.p.random(255), e.p.random(255));
		
		model.initNormals();
		
		model.initTextures(1);
		model.setTexture(0, e.requestTexture("data/textures/globe/transparent-noise.png"));
		
		update(startPosition.x, startPosition.y, startPosition.z);
	}

	public void update(float x, float y, float z) {
		// shift the values to the right side
		// simple queue
		for(int i = numJoints - 1; i > 0; i--) joints[i].set(joints[i - 1]);

		joints[0].set(new PVector(x, y, z));
		
		updateVertices();
	}
	
	private void updateVertices() {
		// draw the ribbons with meshes
		model.beginUpdateVertices();
		
			int vi = 0;
			for(int i = 0; i < numJoints - 1; i++) {
				PVector v1 = PVector.sub(joints[i], joints[i + 1]);
				PVector v2 = PVector.add(joints[i + 1], joints[i]);
				PVector v3 = v1.cross(v2);
				v2 = v1.cross(v3);
	
				float scaling = PApplet.max(.25f, (PApplet.sin(((float) i / numJoints) * PApplet.PI) * PApplet.cos(PApplet.atan2(v1.y, v1.x))));
//				float scaling = 3f;
	
				v1.normalize();
				v2.normalize();
				v3.normalize();
				v1.mult(dimension.x * scaling);
				v2.mult(dimension.x * scaling);
				v3.mult(dimension.x * scaling);
				
				model.updateVertex(vi++, joints[i].x + v3.x, joints[i].y + v3.y, joints[i].z + v3.z);
				model.updateVertex(vi++, joints[i].x - v3.x, joints[i].y - v3.y, joints[i].z - v3.z);
			}
			
		model.endUpdateVertices();
		
		// Calculate normals
		model.beginUpdateNormals();
			
			PVector n;
			for(int i = 0; i < numVertices; i++) {
				n = new PVector(model.vertices.get(i), model.vertices.get(i+1), model.vertices.get(i+2));
				n.normalize();
				model.updateNormal(i, n.x, n.y, n.z);
			}
			
		model.endUpdateNormals();
		
		// Calculate TextureCoords
		model.beginUpdateTexCoords(0);
			for(int i = 0; i < numVertices; i+=2) {
				model.updateTexCoord(i, (1.0f/numVertices)*i, 1);
				model.updateTexCoord(i+1, (1.0f/numVertices)*(i+1), 0);
			}
		model.endUpdateTexCoords();
	}

	public void drawStrokeRibbon(float color, float width) {
		// draw the ribbons with lines
		g.pushMatrix();
		g.pushStyle();
		
			g.noFill();
			g.strokeWeight(width);
			g.stroke(color);
			
			model.beginUpdateColors();
				model.setColors(color);
			model.endUpdateColors();
			e.setupModel(model);
			
			model.render();
			
		g.popMatrix();
		g.popStyle();
	}
	
	public int getVertexCount() {
		return numJoints;
	}
	
	public PVector[] getVertices() {
		return joints;
	}
	
	@Override
	public void draw() {
		e.setupModel(model);
		model.render();
	}
}
