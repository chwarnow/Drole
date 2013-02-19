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
	GLModel imageQuadModel;
	GLSLShader imageShader; // should pe provided by mother class?

	// animation values

	int positionSteps = 250;
	public int currPosition = 0;
	int animationDirection = -1;
	int oldEasedIndex = 0;
	float easedPosition = 0;
	float quadHeight = 1.0f;
	
	private int delay = 0; // count up when delaying
	private int delayTime = 1; // wait for 100 frames until next one begins
	int cosDetail = 25;
	float[] cosLUT = new float[cosDetail];

	public BildweltAssoziationPensee(PApplet parent, String imagePath, float sphereConstraintRadius, float quadHeight, PVector penseeCenter, PVector constraintCenter) {
		this.parent = parent;
		this.quadHeight = quadHeight;
		parent.noiseSeed((long)parent.random(1000));
		// load image
		content = new GLTexture(parent, imagePath);

		// init agents pased on images pixels
		agentsCount = 0;//content.width*content.height;
		// count visible pixels
		for (int x=0;x<content.width;x++) {
			for (int y=0;y<content.height;y++) {
				if(parent.alpha(content.get(x, y)) != 0.0) {
					agentsCount++;
				}
			}
		}
		agents = new BildweltAssoziationAgent[agentsCount];

		int i=0;
		for (int x=0;x<content.width;x++) {
			for (int y=0;y<content.height;y++) {
				if(parent.alpha(content.get(x, y)) != 0.0) {
				float starterThreshold = content.width/2 - parent.dist(x, y, content.width/2, content.height/2);// * parent.noise(x*.1f, y*.1f);//x*.5;
				starterThreshold *= .25f;
				agents[i++]=new BildweltAssoziationAgent(
						parent,
						new PVector((x-content.width/2)*quadHeight + penseeCenter.x, (y-content.height/2)*quadHeight + penseeCenter.y, 0 + penseeCenter.z),
						content.get(x, y),
						positionSteps,
						noiseScale,
						noiseStrength,
						starterThreshold,
						sphereConstraintRadius,
						quadHeight,
						1,
						constraintCenter
				);
				vertexCount += agents[i-1].getVertexCount();
				}
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
		
		// create cos lookup table
		for(i=0;i<cosDetail;i++) {
			cosLUT[i] = parent.cos(((float)i/cosDetail)*parent.PI);
		}
	}

	public void update() {
		// update playhead on precomputed noise path
		if (currPosition == positionSteps-1) {
			if (delay++==delayTime) {
				//animationDirection *= -1;
				//currPosition += animationDirection;
				currPosition = 0;
				delay = 0;
			}
		}
		/*
		else if (currPosition == 0) {
			if (delay++==delayTime) {
				//animationDirection *= -1;
				currPosition += animationDirection;
				delay = 0;
			}
		}
		*/
		else {
			// currPosition += animationDirection;
			currPosition -= animationDirection;
		}

		// eased value out of currStep/positionSteps
		easedPosition = currPosition;//Sine.easeInOut (currPosition, 0, positionSteps-1, positionSteps);

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
				
				// cosinus from lookup table
				float ratio = cosLUT[(int)(((float)j/agentVertexNum) * cosDetail)];
				
				PVector thisP = agentsVertices[j];
				PVector nextP = agentsVertices[j+1];
				//PVector thirdP = agentsVertices[j+1];

				// create quad from above vertices and save in glmodel, then add colors
				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y + quadHeight*ratio*2.0f;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y + quadHeight*ratio*2.0f;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				// compute face normal
				// PVector v1 = new PVector(thisP.x - nextP.x, thisP.y - nextP.y, thisP.z - nextP.z);
				// PVector v2 = new PVector(nextP.x - thisP.x, (nextP.y+quadHeight) - thisP.y, nextP.z - thisP.z);
				PVector v3 = new PVector(thisP.x, thisP.y, thisP.z);//v1.cross(v2);
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
		imageQuadModel.updateNormals(floatQuadVertices);

		// renderer.beginGL();  
		
	    imageShader.start();
	    imageShader.setFloatUniform("zmin", 0.65f);
	    imageShader.setFloatUniform("zmax", 0.85f);
	    imageShader.setFloatUniform("shininess", 100.0f);
	    imageShader.setVecUniform("lightPos", 100.0f, 150.0f, 30.0f);
		
		// A model can be drawn through the GLGraphics renderer:
		renderer.model(imageQuadModel);

		imageShader.stop();

		// renderer.endGL();
	}
}