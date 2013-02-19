package drole.gfx.assoziation;

import penner.easing.Cubic;
import processing.core.PApplet;
import processing.core.PVector;
import drole.gfx.ribbon.Ribbon3D;

class BildweltAssoziationAgent {
	PApplet parent;
	PVector p, beginning;
	float offset, stepSize, angleY, angleZ;
	Ribbon3D ribbon;
	float spaceSize = 250;
	int myColor;
	int positionSteps;

	float[] positionsX = new float[positionSteps];
	float[] positionsY = new float[positionSteps];
	float[] positionsZ = new float[positionSteps];
	int[] colors;
	float r, g, b, a;

	int pathPosition = 0;
	BildweltAssoziationAgent(PApplet parent, PVector beginning, int thisColor, int positionSteps, float noiseScale, float noiseStrength, float beginX, float sphereConstraintRadius, float quadHeight, int pathLength, PVector constraintCenter) {
		this.parent = parent;
		p = new PVector(beginning.x, beginning.y, beginning.z);//new PVector(0, 0, 0);
		this.myColor = thisColor;
		this.beginning = beginning;
		this.positionSteps = positionSteps;

		positionsX = new float[positionSteps];
		positionsY = new float[positionSteps];
		positionsZ = new float[positionSteps];

		offset = 10000;
		stepSize = parent.random(2, 4);
		// how many points has the ribbon
		pathLength = parent.max(1, pathLength);
		int ribbonAmount = (int)parent.random(1*pathLength, 2*pathLength);
		if(ribbonAmount %2 != 0) ribbonAmount ++;
		ribbon = new Ribbon3D(parent, p, ribbonAmount);
		int startPos = 0;
		// precompute positions
		for (int i=0;i<positionSteps;i++) {
			PVector thisP = new PVector(p.x, p.y, p.z);
			if (i>1 && i > beginX) {
				float ratio = (float)(i-startPos)/(positionSteps-startPos);
				float angle = ratio*parent.PI;
				float distanceRatio = parent.sin(angle);//Sine.easeInOut ((float)(i-startPos)/(positionSteps-startPos), 0, 1.0f, 1.0f);//parent.cos((float)i/(positionSteps-startPos)*parent.TWO_PI);
				
				
				thisP.x = positionsX[i-1];
				thisP.y = positionsY[i-1];
				thisP.z = positionsZ[i-1];


				float angleY = parent.noise(thisP.x/noiseScale, thisP.y/noiseScale, thisP.z/noiseScale) * noiseStrength; 
				float angleZ = parent.noise(thisP.x/noiseScale+offset, thisP.y/noiseScale, thisP.z/noiseScale) * noiseStrength;

				if(ratio < .75) {
					thisP.x += parent.cos(angleZ) * parent.cos(angleY) * stepSize;
					thisP.y += parent.sin(angleZ) * stepSize;
					thisP.z += parent.cos(angleZ) * parent.sin(angleY) * stepSize;
				}
				
				// distance to center
				float dx = thisP.x - constraintCenter.x;
				float dy = thisP.y - constraintCenter.y;
				float dz = thisP.z - constraintCenter.z;
				
				float centerDistance = dx*dx + dy*dy + dz*dz;//thisP.x*thisP.x + thisP.y*thisP.y + thisP.z*thisP.z;

				// constrain to sphere
				if(centerDistance > sphereConstraintRadius*sphereConstraintRadius) {
					thisP.normalize();
					thisP.mult(sphereConstraintRadius);
				}

				if(ratio > .5f) {
					float thisRatio = Cubic.easeIn(ratio-.5f, 0f, 1.0f, .5f);
					if(i%2==0) {
						thisP.x += (positionsX[0] - thisP.x)*thisRatio;
						thisP.y += (positionsY[0] - thisP.y)*thisRatio;
						thisP.z += (positionsZ[0] - thisP.z)*thisRatio;
					} else {
						thisP.x += (positionsX[1] - thisP.x)*thisRatio;
						thisP.y += (positionsY[1] - thisP.y)*thisRatio;
						thisP.z += (positionsZ[1] - thisP.z)*thisRatio;
					}
				}
				
				if(ratio > .81) {
					if(i%2==0) {
						thisP.x += (positionsX[0] - thisP.x)*1.0;
						thisP.y += (positionsY[0] - thisP.y)*1.0;
						thisP.z += (positionsZ[0] - thisP.z)*1.0;
					} else {
						thisP.x += (positionsX[1] - thisP.x)*1.0;
						thisP.y += (positionsY[1] - thisP.y)*1.0;
						thisP.z += (positionsZ[1] - thisP.z)*1.0;
					}
				}
				/*
				if(i == positionSteps-2) {
					thisP.x = positionsX[1];
					thisP.y = positionsY[1];
					thisP.z = positionsZ[1];
				} else if(i==positionSteps-1) {
					thisP.x = positionsX[0];
					thisP.y = positionsY[0];
					thisP.z = positionsZ[0];
				}
				*/
				
				float newX = thisP.x;//positionsX[startPos] + parent.cos(angle)*distanceRatio*100.0f + (parent.cos(angleZ) * parent.cos(angleY))*10.0f*angle;
				float newY = thisP.y;//positionsY[startPos] + parent.sin(angle)*distanceRatio*100.0f + (parent.sin(angleZ))*10.0f*angle;
				float newZ = thisP.z;//positionsZ[startPos] + parent.sin(angle)*distanceRatio*100.0f + (parent.cos(angleZ) * parent.sin(angleY))*10.0f*angle;
				positionsX[i] = newX;
				positionsY[i] = newY;
				positionsZ[i] = newZ;
			} 
			else {

				// distance to center
				float centerDistance = p.x*p.x + p.y*p.y + p.z*p.z;

				// constrain to sphere
				if(centerDistance > sphereConstraintRadius*sphereConstraintRadius) {
					p.normalize();
					p.mult(sphereConstraintRadius);
				}

				positionsX[i] = p.x;
				positionsY[i] = p.y;
				positionsZ[i] = p.z;
				//TODO: how to let pixels stay on place? (do not update ribbons)
				p.x += quadHeight;//(i%2==0) ? quadHeight : -quadHeight;
				
				startPos = i;
			}
		}
		
		// set colors array
		colors = new int[getVertexCount()];
		for (int i=0;i<getVertexCount();i++) {
			colors[i] = myColor;
		}

		r = parent.red(myColor)/255.0f;
		g = parent.green(myColor)/255.0f;
		b = parent.blue(myColor)/255.0f;
		a = parent.alpha(myColor)/255.0f;
	}

	public void update(int pathPosition) {
		// TODO: error handling / constrain to available positions

		// set curr position
		p.x = positionsX[pathPosition];
		p.y = positionsY[pathPosition];
		p.z = positionsZ[pathPosition];

		// create ribbons
		ribbon.update(p.x, p.y, p.z);//, false);
	}

	public void draw() {
		ribbon.drawStrokeRibbon(myColor, 1.0f);
	}

	/**
	 * return an array of vertices
	 */
	public PVector[] getVertices() {
		return ribbon.getVertices();
	}

	public int[] getColors() {
		return colors;
	}

	public int getVertexCount() {
		return ribbon.getVertexCount();
	}
	/*
	public boolean[] getGaps() {
		return ribbon.getGaps();
	}
	*/
}

