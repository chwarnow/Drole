package com.christopherwarnow.bildwelten;

import processing.core.PApplet;
import processing.core.PVector;

public class Drole {
	private PVector p;
	private float sphereSize;
	private int id;
	private int stepsAmount = 30;
	private PVector[] oldPositions;
	private PVector xyzPos = new PVector();
	private PApplet parent;
	public Drole(PApplet parent, PVector p, float sphereSize, int id) {
		this.parent = parent;
		this.p = p;
		this.sphereSize = sphereSize;
		this.id = id;

		oldPositions = new PVector[stepsAmount];
		for (int i=0;i<stepsAmount;i++) {
			oldPositions[i] = new PVector();
		}
	}

	public void update() {

		// wandering
		p.x += parent.noise(p.x*100f)*.02;//cos(i+frameCount*.005)*.001;//noise(particles[i].x*10.1)*(noise(frameCount*.1 + i)*.01);
		p.y += parent.noise(p.y*10f)*.01;//cos(i+frameCount*.01)*.001;//cos(noise(particles[i].y*10.1))*(noise(frameCount*.1 + i)*.01);

		if (p.y<-1||p.y>1)
		{
			p.y*=-1;
		}

		// new xyz position
		xyzPos.x = parent.sin(p.x)*parent.sqrt(1 - (p.y*p.y))*sphereSize;
		xyzPos.y = parent.cos(p.x)*parent.sqrt(1 - (p.y*p.y))*sphereSize; 
		xyzPos.z = p.y*sphereSize;

		oldPositions[0].x = xyzPos.x;
		oldPositions[0].y = xyzPos.y;
		oldPositions[0].z = xyzPos.z;

		// save old positions
		for (int i=stepsAmount-1;i>0;i--) {
			oldPositions[i].x = oldPositions[i-1].x;
			oldPositions[i].y = oldPositions[i-1].y;
			oldPositions[i].z = oldPositions[i-1].z;
		}
	}

	public void draw() {
		parent.g.noFill();
		// parent.g.stroke(100, 0, 0, 100);
		for (int i=1;i<stepsAmount;i++) {
			// parent.g.stroke(255);
			float strokeSize= parent.noise(i*.05f)*4;//parent.noise(oldPositions[i].y*.05f)*4;//atan2(oldPositions[i-1].y - oldPositions[i].y, oldPositions[i-1].x - oldPositions[i].y);
/*
			float particleZ = parent.modelZ(oldPositions[i].x, oldPositions[i].y, oldPositions[i].z);		
			parent.g.stroke(
					255,
					parent.max(100, (parent.map(particleZ, -1000, -3000, 0, 255))));
			*/
			parent.g.strokeWeight(parent.abs(strokeSize));
			parent.g.beginShape();
			parent.g.vertex(oldPositions[i-1].x, oldPositions[i-1].y, oldPositions[i-1].z);
			parent.g.vertex(oldPositions[i].x, oldPositions[i].y, oldPositions[i].z);
			parent.g.endShape();
			/*
	      stroke(100 + abs(strokeSize)*5, 100, 100, max(10, (modelZ(oldPositions[i].x, oldPositions[i].y, oldPositions[i].z) + sphereSize))*.08);
	      strokeWeight(abs(strokeSize)*3);
	      beginShape();
	      vertex(oldPositions[i-1].x, oldPositions[i-1].y, oldPositions[i-1].z);
	      vertex(oldPositions[i].x, oldPositions[i].y, oldPositions[i].z);
	      endShape();
			 */

		}
	}
/*
	public void drawShadow() {
		pushMatrix();
		// rotateX(HALF_PI);
		translate(0, sphereSize, 0);
		scale(.9);
		for (int i=1;i<stepsAmount/2;i++) {

			float strokeSize= noise(oldPositions[i].y*.1)*2;//atan2(oldPositions[i-1].y - oldPositions[i].y, oldPositions[i-1].x - oldPositions[i].y);
			stroke(0, max(0, (modelZ(oldPositions[i].x, oldPositions[i].y, oldPositions[i].z) + sphereSize)*.05 -  (sphereSize/2 - oldPositions[i].y)*.03));

			strokeWeight(abs(strokeSize)*3);
			beginShape();
			vertex(oldPositions[i-1].x, 0, oldPositions[i-1].z);
			vertex(oldPositions[i].x, 0, oldPositions[i].z);
			endShape();

		}
		popMatrix();
	}
*/
	public void addPosition(float x, float y, float z) {
		xyzPos.x = x;
		xyzPos.y = y;
		xyzPos.z = z;

		oldPositions[0].x = xyzPos.x;
		oldPositions[0].y = xyzPos.y;
		oldPositions[0].z = xyzPos.z;

		// save old positions
		for (int i=stepsAmount-1;i>0;i--) {
			oldPositions[i].x = oldPositions[i-1].x;
			oldPositions[i].y = oldPositions[i-1].y;
			oldPositions[i].z = oldPositions[i-1].z;
		}
	}
}
