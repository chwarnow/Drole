package drole.gfx.ribbon;

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

class Ribbon3D {

	private int numJoints; // how many points has the ribbon

	private PVector[] joints;

	private PApplet parent;

	public Ribbon3D(PApplet parent, PVector startPosition, int numJoints) {
		this.parent = parent;
		this.numJoints = numJoints;
		joints = new PVector[numJoints];
		for(int i = 0; i < numJoints; i++) joints[i] = new PVector(startPosition.x, startPosition.y, startPosition.z);
	}

	public void update(float x, float y, float z) {
		// shift the values to the right side
		// simple queue
		for (int i = numJoints - 1; i > 0; i--)
			joints[i].set(joints[i - 1]);

		joints[0].set(new PVector(x, y, z));
	}

	public void drawMeshRibbon(float width) {
		// draw the ribbons with meshes
		parent.beginShape(PApplet.QUAD_STRIP);
		
		for(int i = 0; i < numJoints - 1; i++) {
			PVector v1 = PVector.sub(joints[i], joints[i + 1]);
			PVector v2 = PVector.add(joints[i + 1], joints[i]);
			PVector v3 = v1.cross(v2);
			v2 = v1.cross(v3);

			float scaling = PApplet.max(.25f, (PApplet.sin(((float) i / numJoints) * PApplet.PI) * PApplet.cos(PApplet.atan2(v1.y, v1.x))));

			v1.normalize();
			v2.normalize();
			v3.normalize();
			v1.mult(width * scaling);
			v2.mult(width * scaling);
			v3.mult(width * scaling);
			parent.vertex(joints[i].x + v3.x, joints[i].y + v3.y, joints[i].z + v3.z);
			parent.vertex(joints[i].x - v3.x, joints[i].y - v3.y, joints[i].z - v3.z);
		}

		parent.endShape();
	}

	public void drawStrokeRibbon(float color, float width) {
		// draw the ribbons with lines
		parent.pushMatrix();
		parent.pushStyle();
		
			parent.noFill();
			parent.strokeWeight(width);
			parent.stroke(color);
			
			parent.beginShape(PApplet.LINES);
				for (int i = 0; i < numJoints; i++) {
					parent.vertex(joints[i].x, joints[i].y, joints[i].z);
				}
			parent.endShape();
			
		parent.popMatrix();
		parent.popStyle();
	}
	
	public int getVertexCount() {
		return numJoints;
	}

	// TODO: incorporate gaps
	public PVector[] getVertices() {
		return joints;
	}

}
