package drole.gfx.assoziation;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import penner.easing.*;

/**
 * loads an image and converts every pixel into an agent, which wanders on a noise field trough the space
 *
 * @Author Christopher Warnow, hello@christopherwarnow.com
 *
 */
//TODO: implement a fine and stable state pattern and get rid of all those booleans
public class BildweltAssoziationPensee extends Drawable {
	Engine e;

	// ------ agents ------
	BildweltAssoziationAgent[] agents;
	int agentsCount;
	BildweltAssoziationDataItem dataItem;

	// model vars
	private float noiseScale = 250, noiseStrength = 20; 
	private int vertexCount = 0;
	private GLTexture content;
	private GLModel imageQuadModel;
	private float quadHeight = 1.0f;
	private boolean isAgents = false;
	
	// animation values
	public float currPosition = 0;
	private int positionSteps = 250;
	private int oldEasedIndex = 0;
	private int animationDirection = -1;
	private int delay = 0; // count up when delaying
	private int delayTime = 100; // wait for 100 frames until next one begins
	private int stopFrame = positionSteps;
	private float delaySteps = 1.0f;//.33f;
	private int easedPosition = 0;
	private boolean isLooping = true;
	private boolean isAnimationDone = false;
	private boolean isShowing = false;
	private boolean isRunning = true;
	private boolean isHiding = false;
	private boolean isVisible = false;
	
	// lookup table
	private int cosDetail = 25;
	private float[] cosLUT = new float[cosDetail];
	
	public BildweltAssoziationPensee(Engine e, String imagePath, float sphereConstraintRadius, float quadHeight, PVector penseeCenter, PVector constraintCenter) {
		super(e);
		this.e = e;
		this.quadHeight = quadHeight;
		e.p.logLn("[Assoziation]: Load Bildwelt Assoziation: " + imagePath);
		e.p.noiseSeed((long)e.p.random(1000));

		// create data
		dataItem = new BildweltAssoziationDataItem();
		dataItem.createPenseeData(e, new GLTexture(e.p, imagePath), sphereConstraintRadius, quadHeight, penseeCenter, constraintCenter, positionSteps, noiseScale, noiseStrength);
		
		// create cos lookup table
		for(int i=0;i<cosDetail;i++) {
			cosLUT[i] = PApplet.cos(((float)i/cosDetail)*PApplet.PI);
		}

	}

	public void update() {
		if(!isAgents) {
			if(dataItem.isAvailable()) {
				isAgents = true;
				isVisible = true;
				agents = dataItem.getAgentsData();
				agentsCount = dataItem.getAgentsCount();
				vertexCount = dataItem.getVertexCount();
				
				// clear existing glmodel
				if(imageQuadModel != null) imageQuadModel.delete();
				
				// create a model that uses quads
				imageQuadModel = new GLModel(e.p, vertexCount*4, PApplet.QUADS, GLModel.DYNAMIC);
				imageQuadModel.initColors();
				imageQuadModel.initNormals();
				
				// when beginning in the middle, update the first agent position
				if(currPosition != 0) {
					for(int i=0;i<agentsCount;i++) {
						BildweltAssoziationAgent agent = agents[i];
						agent.update((int)currPosition);
						agent.update((int)currPosition);
						agent.update((int)currPosition);
					}
				}
				isAnimationDone = false;
			}
		} else {
			if(isRunning) {
				// update playhead on precomputed noise path
				if(isHiding) {
					if(!isLooping && (int)currPosition == stopFrame) {
						isAnimationDone = true;
						isVisible = false;
					} else {
						currPosition += animationDirection*delaySteps;
						if(currPosition <= 0) currPosition = positionSteps-1;
					}
				} else {
					if (currPosition >= positionSteps-1) {
						if(isShowing) {
							isRunning = false;
						} else if ( delay++ == delayTime) {
							currPosition = 0;
							delay = 0;
						}
					} else if(!isLooping && (int)currPosition == stopFrame) {
						isAnimationDone = true;
					} else {
						currPosition -= animationDirection*delaySteps;
					}
				}
				// eased value out of currStep/positionSteps
				easedPosition = (int)currPosition;
			}
		}
	}

	public void loadPensee() {
		dataItem.start();
	}
	
	public void draw() {

		// update glmodel

		// extract agents vertices
		if(isAgents) {
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

		// cosinus from lookup table
		float ratio = cosLUT[(int)(e.p.min(cosDetail-1, (easedPosition/(positionSteps-positionSteps*.15f)) * cosDetail))];// * fade;
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
				//PVector thirdP = agentsVertices[j+1];

				// create quad from above vertices and save in glmodel, then add colors
				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = thisP.x;
				floatQuadVertices[quadVertexIndex++] = thisP.y + quadHeight*ratio;
				floatQuadVertices[quadVertexIndex++] = thisP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y + quadHeight*ratio;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				floatQuadVertices[quadVertexIndex++] = nextP.x;
				floatQuadVertices[quadVertexIndex++] = nextP.y;
				floatQuadVertices[quadVertexIndex++] = nextP.z;
				floatQuadVertices[quadVertexIndex++] = 1.0f;

				// compute face normal
				PVector v1 = new PVector(thisP.x - nextP.x, thisP.y - nextP.y, thisP.z - nextP.z);
				PVector v2 = new PVector(nextP.x - thisP.x, (nextP.y+quadHeight*ratio) - thisP.y, nextP.z - thisP.z);
				PVector v3 = v1.cross(v2);
				// PVector v3 = new PVector(thisP.x, thisP.y, thisP.z);
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

		imageQuadModel.render();
		}

	}
	
	public int positionSteps() {
		return this.positionSteps;
	}
	
	/**
	 * position between 0 and 1
	 * @return
	 */
	public void setPosition(float playHead) {
		playHead = e.p.constrain(playHead, 0, 1);
		currPosition = playHead*positionSteps;
		easedPosition = (int)currPosition;
		oldEasedIndex = (int)currPosition;
		stopFrame = (int)currPosition - 1;
	}
	
	public void setLooping(boolean isLooping) {
		this.isLooping = isLooping;
	}
	
	public boolean isAnimationDone() {
		return isAnimationDone;
	}
	
	public boolean isReady() {
		return isAgents;
	}
	
	public void loadNewImage(String imagePath, float sphereConstraintRadius, PVector penseeCenter, PVector constraintCenter) {
		e.p.logLn("[Assoziation]: Load Bildwelt Assoziation: " + imagePath);
		isAnimationDone = false;
		isAgents = false;
		dataItem = null;
		dataItem = new BildweltAssoziationDataItem();
		dataItem.createPenseeData(e, new GLTexture(e.p, imagePath), sphereConstraintRadius, quadHeight, penseeCenter, constraintCenter, positionSteps, noiseScale, noiseStrength);
		loadPensee();
	}
	
	public void showMe() {
		isShowing = true;
		isHiding = false;
		setPosition(.5f);
		isRunning = true;
		isVisible = true;
	}
	
	public void hideMe() {
		isShowing = false;
		isHiding = true;
		// setPosition(.5f);
		isRunning = true;
	}
	
	public void resume() {
		isHiding = false;
		setPosition(.5f);
		isRunning = true;
		isVisible = true;
	}
	
	public void stop() {
		isRunning = false;
	}
	
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	
	public boolean isVisible() {
		return this.isVisible;
	}
}