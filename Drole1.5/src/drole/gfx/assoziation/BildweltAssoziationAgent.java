package drole.gfx.assoziation;

import com.madsim.engine.Engine;

import processing.core.PApplet;
import processing.core.PVector;
import drole.gfx.ribbon.Ribbon3D;

class BildweltAssoziationAgent {
	Engine parent;
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
	BildweltAssoziationAgent(Engine e, PVector beginning, int thisColor, int positionSteps, float noiseScale, float noiseStrength, float beginX, float sphereConstraintRadius, float quadHeight, int pathLength) {
		p = new PVector(beginning.x, beginning.y, beginning.z);//new PVector(0, 0, 0);
		this.myColor = thisColor;
		this.beginning = beginning;
		this.positionSteps = positionSteps;

		positionsX = new float[positionSteps];
		positionsY = new float[positionSteps];
		positionsZ = new float[positionSteps];

		offset = 10000;
		stepSize = e.p.random(2, 3);
		// how many points has the ribbon
		pathLength = PApplet.max(1, pathLength);
		int ribbonAmount = (int)e.p.random(1*pathLength, 2*pathLength);
		if(ribbonAmount %2 != 0) ribbonAmount ++;
		ribbon = new Ribbon3D(parent, p, ribbonAmount);
		// precompute positions
		for (int i=0;i<positionSteps;i++) {
			PVector thisP = new PVector(p.x, p.y, p.z);
			if (i>1 && i > beginX) {
				thisP.x = positionsX[i-1];
				thisP.y = positionsY[i-1];
				thisP.z = positionsZ[i-1];


				float angleY = e.p.noise(thisP.x/noiseScale, thisP.y/noiseScale, thisP.z/noiseScale) * noiseStrength; 
				float angleZ = e.p.noise(thisP.x/noiseScale+offset, thisP.y/noiseScale, thisP.z/noiseScale) * noiseStrength;

				thisP.x += PApplet.cos(angleZ) * PApplet.cos(angleY) * stepSize;
				thisP.y += PApplet.sin(angleZ) * stepSize;
				thisP.z += PApplet.cos(angleZ) * PApplet.sin(angleY) * stepSize;

				// distance to center

				float centerDistance = thisP.x*thisP.x + thisP.y*thisP.y + thisP.z*thisP.z;

				// constrain to sphere
				if(centerDistance > sphereConstraintRadius*sphereConstraintRadius) {
					thisP.normalize();
					thisP.mult(sphereConstraintRadius);
				}

				positionsX[i] = thisP.x;
				positionsY[i] = thisP.y;
				positionsZ[i] = thisP.z;
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
			}
		}
		
		// set colors array
		colors = new int[getVertexCount()];
		for (int i=0;i<getVertexCount();i++) {
			colors[i] = myColor;
		}

		r = e.p.red(myColor)/255.0f;
		g = e.p.green(myColor)/255.0f;
		b = e.p.blue(myColor)/255.0f;
		a = e.p.alpha(myColor)/255.0f;
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

