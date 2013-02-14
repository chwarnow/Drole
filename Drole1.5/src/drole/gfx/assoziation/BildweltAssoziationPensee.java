package drole.gfx.assoziation;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;
import processing.core.PVector;
import penner.easing.*;

/**
 * loads an image and converts every pixel into an agent, wich wanders on a noise field trough the space
 *
 * @Author Christopher Warnow, hello@christopherwarnow.com
 *
 */
public class BildweltAssoziationPensee {
	PApplet parent;

	// ------ agents ------
	BildweltAssoziationAgent[] agents;
	int agentsCount;

	float noiseScale = 150, noiseStrength = 20; 
	int vertexCount = 0;

	GLTexture content;
	GLModel imageModel, imageQuadModel;
	GLSLShader imageShader; // should pe provided by mother class?

	// animation values

	int positionSteps = 200;
	int currPosition = 0;
	int animationDirection = -1;
	int oldEasedIndex = 0;
	float easedPosition = 0;
	float quadHeight = 1.0f;

	public BildweltAssoziationPensee(PApplet parent, String imagePath, float sphereConstraintRadius) {
		this.parent = parent;
		parent.noiseSeed((long)parent.random(1000));
		// load image
		content = new GLTexture(parent, imagePath);

		// init agents pased on images pixels
		agentsCount = content.width*content.height;
		agents = new BildweltAssoziationAgent[agentsCount];

		int i=0;
		for (int x=0;x<content.width;x++) {
			for (int y=0;y<content.height;y++) {
				float starterThreshold = content.width/2 - parent.dist(x, y, content.width/2, content.height/2) * parent.noise(x*.1f, y*.1f);//x*.5;
				starterThreshold *= quadHeight;
				agents[i++]=new BildweltAssoziationAgent(
						parent,
						new PVector((x-content.width/2)*quadHeight, (y-content.height/2)*quadHeight, 0),
						content.get(x, y),
						positionSteps,
						noiseScale,
						noiseStrength,
						starterThreshold,
						sphereConstraintRadius,
						quadHeight,
						10
				);
				vertexCount += agents[i-1].getVertexCount();
			}
		}

		// extract agents vertices
		PVector[] vertices = new PVector[vertexCount];
		int vertexIndex = 0;
		for (BildweltAssoziationAgent agent:agents) {
			for (PVector p:agent.getVertices()) {
				vertices[vertexIndex++] = new PVector(p.x, p.y, p.z);
			}
		}

		// create a model that uses quads
		imageQuadModel = new GLModel(parent, vertexCount*4, PApplet.QUADS, GLModel.DYNAMIC);
		imageQuadModel.initColors();
		imageQuadModel.initNormals();

		// load shader
		imageShader = new GLSLShader(parent, "data/shader/imageVert.glsl", "data/shader/imageFrag.glsl");
	}

	public void update() {
		// update playhead on precomputed noise path
		if (currPosition == positionSteps-1) {
			animationDirection *= -1;
		}
		if (currPosition == 0) {
			// if (frameCount%200==0) {
			animationDirection *= -1;
			currPosition += animationDirection;
			// }
		}
		else {
			currPosition += animationDirection;
		}

		// eased value out of currStep/positionSteps
		easedPosition = Sine.easeInOut (currPosition, 0, positionSteps-1, positionSteps);

	}

	public void draw(GLGraphics renderer) {
		// renderer.lights();

		// update glmodel

		// extract agents vertices

		//if(frameCount%100==0) {
		float[] floatQuadVertices = new float[vertexCount*16];
		float[] floatQuadNormals = new float[vertexCount*16];
		float[] floatQuadColors = new float[vertexCount*16];
		int quadVertexIndex = 0;
		int quadNormalIndex = 0;
		int quadColorIndex = 0;
		int easedIndex = (int)easedPosition;
		boolean isUpdate = false;
		if(oldEasedIndex != easedIndex) isUpdate = true;
		oldEasedIndex = easedIndex;

		// for (Agent agent:agents) {
		for(int i=0;i<agentsCount;i++) {
			BildweltAssoziationAgent agent = agents[i];
			// set agents position
			// TODO: improve updating performance
			if(isUpdate) agent.update(easedIndex);

			// create quads from ribbons
			PVector[] agentsVertices = agent.getVertices();
			int agentVertexNum = agentsVertices.length;

			for(int j=0;j<agentVertexNum-1;j++) {
				PVector thisP = agentsVertices[j];
				PVector nextP = agentsVertices[j+1];
				PVector thirdP = agentsVertices[j+1];

				// create quad from above vertices and save in glmodel, then add colors
				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y + quadHeight;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y + quadHeight;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				// compute face normal
				PVector v1 = new PVector(thisP.x - nextP.x, thisP.y - nextP.y, thisP.z - nextP.z);
				PVector v2 = new PVector(nextP.x - thisP.x, (nextP.y+quadHeight) - thisP.y, nextP.z - thisP.z);
				PVector v3 = v1.cross(v2);
				v3.normalize();

				float nX = v3.x;
				float nY = v3.y;
				float nZ = v3.z;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;

				floatQuadNormals[quadNormalIndex++] = nX;
				floatQuadNormals[quadNormalIndex++] = nY;
				floatQuadNormals[quadNormalIndex++] = nZ;
				floatQuadNormals[quadNormalIndex++] = 1.0f;

				// add colors
				float theAlpha = agent.a;// * ((!gaps[gapIndex++]) ? 1.0f : 0.0f);

				floatQuadColors[quadColorIndex++] = agent.r;
				floatQuadColors[quadColorIndex++] = agent.g;
				floatQuadColors[quadColorIndex++] = agent.b;
				floatQuadColors[quadColorIndex++] = theAlpha;

				floatQuadColors[quadColorIndex++] = agent.r;
				floatQuadColors[quadColorIndex++] = agent.g;
				floatQuadColors[quadColorIndex++] = agent.b;
				floatQuadColors[quadColorIndex++] = theAlpha;

				floatQuadColors[quadColorIndex++] = agent.r;
				floatQuadColors[quadColorIndex++] = agent.g;
				floatQuadColors[quadColorIndex++] = agent.b;
				floatQuadColors[quadColorIndex++] = theAlpha;

				floatQuadColors[quadColorIndex++] = agent.r;
				floatQuadColors[quadColorIndex++] = agent.g;
				floatQuadColors[quadColorIndex++] = agent.b;
				floatQuadColors[quadColorIndex++] = theAlpha;        
			}

		}

		imageQuadModel.updateVertices(floatQuadVertices);
		imageQuadModel.updateColors(floatQuadColors);
		imageQuadModel.updateNormals(floatQuadNormals);

		// renderer.beginGL();  
		/*
	    imageShader.start();
	    imageShader.setFloatUniform("zmin", 0.65f);
	    imageShader.setFloatUniform("zmax", 0.85f);
	    imageShader.setFloatUniform("shininess", 100.0f);
	    imageShader.setVecUniform("lightPos", 00.0f, 00.0f, 500.0f);
		*/
		// A model can be drawn through the GLGraphics renderer:
		renderer.model(imageQuadModel);

		// imageShader.stop();

		// renderer.endGL();
	}
}