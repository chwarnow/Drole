package drole.tests.menu;

import processing.core.PApplet;
import processing.core.PShape;
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

class Ribbon3d {
	int count; // how many points has the ribbon
	PVector[] p;
	boolean[] isGap;
	PShape ribbonShape;
	PApplet parent;
	Ribbon3d (PApplet parent, PVector theP, int theCount) {
		this.parent = parent;
		count = theCount; 
		p = new PVector[count];
		isGap = new boolean[count];
		for (int i=0; i<count; i++) {
			p[i] = new PVector(theP.x, theP.y, theP.z);
			isGap[i] = false;
		}
	}

	void update(PVector theP, boolean theIsGap) {
		// shift the values to the right side
		// simple queue
		for (int i=count-1; i>0; i--) {
			p[i].set(p[i-1]);
			isGap[i] = isGap[i-1];
		}
		p[0].set(theP);
		isGap[0] = theIsGap;
	}

	void drawMeshRibbon(int theMeshCol, float theWidth) {
		// draw the ribbons with meshes
		parent.fill(theMeshCol);
		parent.noStroke();
		// noFill();
		parent.beginShape(parent.QUAD_STRIP);
		for (int i=0; i<count-1; i++) {
			// if the point was wraped -> finish the mesh an start a new one
			/*
   if (isGap[i] == true) {
    vertex(p[i].x, p[i].y, p[i].z);
    vertex(p[i].x, p[i].y, p[i].z);
    endShape();
    beginShape(QUAD_STRIP);
    } 
    else { 
			 */
			PVector v1 = PVector.sub(p[i], p[i+1]);
			PVector v2 = PVector.add(p[i+1], p[i]);
			PVector v3 = v1.cross(v2);      
			v2 = v1.cross(v3);

			float scaling = parent.max(.25f, (parent.sin(((float)i/count)*parent.PI) * parent.cos(parent.atan2(v1.y, v1.x))));

			v1.normalize();
			v2.normalize();
			v3.normalize();
			v1.mult(theWidth*scaling);
			v2.mult(theWidth*scaling);
			v3.mult(theWidth*scaling);
			parent.vertex(p[i].x+v3.x, p[i].y+v3.y, p[i].z+v3.z);
			parent.vertex(p[i].x-v3.x, p[i].y-v3.y, p[i].z-v3.z);
		}

		parent.endShape();
	}


	void drawLineRibbon(int theStrokeCol, float theWidth) {
		// draw the ribbons with lines
		parent.noFill();
		parent.strokeWeight(theWidth);
		parent.stroke(theStrokeCol);
		parent.beginShape();
		for (int i=0; i<count; i++) {
			parent.vertex(p[i].x, p[i].y, p[i].z);
			// if the point was wraped -> finish the line an start a new one
			if (isGap[i] == true) {
				parent.endShape();
				parent.beginShape();
			}
		}
		parent.endShape();
	}

	int getVertexCount() {
		return count;
	}

	// TODO: incorporate gaps
	PVector[] getVertices() {
		return p;
	}

	boolean[] getGaps() {
		return isGap;
	}
}

