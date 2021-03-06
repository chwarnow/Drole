package com.marctiedemann.spektakel;

import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletSpring;
import processing.core.PApplet;

import java.awt.List;
import java.util.ArrayList;

import com.madsim.engine.Engine;
import com.madsim.engine.shader.JustColorShader;

import codeanticode.glgraphics.*;
import drole.tests.spektakel.T_ShapeParticle;

import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;

public class ParticleSystem extends VerletParticle{

	protected boolean shockwave = false;

	protected float initalBoomPower = -2.0f;

	protected float initalSpringPower = 0.0001f;

	protected float boomFalloff = 0.005f;

	protected float springFallOff = 0.01f;
	
	
	protected float trailAlpha = 0.3f;

	
	private float boomPower = initalBoomPower;

	private float springPower = initalSpringPower;

	protected AttractionBehavior boomForce;

	protected boolean exploded = false;

	protected VerletPhysics physics;

	protected ArrayList<ShapedParticle> bigParticle;

	protected GLModel sprites, trails;

	private GLTexture tex;
	protected float[] coords;
	protected float[] colors;

	protected int numPoints = 0;

	private int myID;

	protected int trailLength = 5;

	
	protected float x, y, z;

	protected Engine e;

	public ParticleSystem(Engine e, VerletPhysics physics, float x, float y,
			float z) {
		
		super(x,y,z);
		
		this.e = e;
		this.physics = physics;

		this.x = x;
		this.y = y;
		this.z = z;

		bigParticle = new ArrayList<ShapedParticle>();

		myID = (e.p.frameCount);
	}

	protected void initSprites() {

		numPoints = bigParticle.size();

		sprites = new GLModel(e.p, numPoints * 4, GLModel.POINT_SPRITES,
				GLModel.DYNAMIC);

		tex = e.requestTexture("images/particle4.png");

		updateSpritePositions();
		sprites.initColors();
		updateSpriteColors();

		sprites.initTextures(1);
		sprites.setTexture(0, tex);
		// Setting the maximum sprite to the 90% of the maximum point size.
		// model.setMaxSpriteSize(0.9f * pmax);
		// Setting the distance attenuation function so that the sprite size
		// is 20 when the distance to the camera is 400.

		// sprites.setColors(255);
		sprites.setSpriteSize(50, 700);
		sprites.setBlendMode(PApplet.ADD);

	}

	protected void updateSpritePositions() {

		numPoints = bigParticle.size();

		coords = new float[4 * numPoints];

		// p.println("updatimng" + myID + " num " + numPoints + " size "
		// + bigParticle.size());

		for (int i = 0; i < numPoints; i++) {

			Particle oneParticle = bigParticle.get(i);

			coords[4 * i + 0] = oneParticle.x;
			coords[4 * i + 1] = oneParticle.y;
			coords[4 * i + 2] = oneParticle.z;
			coords[4 * i + 3] = 1.0f; 
		}
		
		sprites.updateVertices(coords);
	}

	private void updateSpriteColors() {

		colors = new float[4 * numPoints];

		for (int i = 0; i < numPoints; i++) {

			float newAlpha = (bigParticle.get(i).getTimeToLife() * 0.003921f)+ e.p.random(-0.1f, 0.1f);

	//		colors[4 * i + 0] = 1;
	//		colors[4 * i + 1] = 0.1f + newAlpha * 0.4f;
	//		colors[4 * i + 2] = newAlpha - 1;
			
			
			colors[4 * i + 0] = 1;
			colors[4 * i + 1] = 1;
			colors[4 * i + 2] = 1;
			
			
			colors[4 * i + 3] = newAlpha*bigParticle.get(i).myAlpha;
    // 	colors[4 * i + 3] = 1;

			
	//		System.out.println(colors[4 * i + 3]);

			// colors[4 * i + 3] = 1;

		}

	
		sprites.updateColors(colors);

	}



	void initTrails() {

		trails = new GLModel(e.p, numPoints * (trailLength + 1) * 4 * 2,
				GLModel.LINES, GLModel.DYNAMIC);

		updateTrailPositions();

		trails.initColors();
	//	trails.setColors(250, 20);
		updateTrailColors();

		trails.setLineWidth(2);

		trails.setBlendMode(PApplet.ADD);
		
		

	}

	void updateTrailPositions() {
		
		numPoints = bigParticle.size();

		coords = new float[4 * numPoints * (trailLength + 1) * 2];

		// p.println("updatimng" + myID + " num " + numPoints + " size "
		// + bigParticle.size());

		int numSections = trailLength + 1;
		int pointsToMesh = 2;

		for (int i = 0; i < numPoints; i++) {

			ShapedParticle oneParticle = bigParticle.get(i);

			for (int j = 0; j < trailLength - 1; j++) {

				int step = (i * numSections * pointsToMesh * 4)
						+ (j * pointsToMesh * 4);

				coords[step + 0] = bigParticle.get(i).x;
				coords[step + 1] = bigParticle.get(i).y;
				coords[step + 2] = bigParticle.get(i).z;
				coords[step + 3] = 1.0f; // The W coordinate of each point

				Vec3D trailPoint = oneParticle.getTailPoint(0);

				coords[step + 4] = trailPoint.x;
				coords[step + 5] = trailPoint.y;
				coords[step + 6] = trailPoint.z;
				coords[step + 7] = 1.0f; // The W coordinate of each point

				for (int k = 0; k < pointsToMesh; k++) {

					step += (k * 4);

					trailPoint = oneParticle.getTailPoint(j + k);

					coords[step + 0] = trailPoint.x;
					coords[step + 1] = trailPoint.y;
					coords[step + 2] = trailPoint.z;
					coords[step + 3] = 1.0f; // The W coordinate of each point
												// must
												// be
				}

			}
		}

		// p.println(coords);

		trails.updateVertices(coords);
	}
	
	
void updateTrailColors() {
		
		numPoints = bigParticle.size();

		colors = new float[4 * numPoints * (trailLength + 1) * 2];

		// p.println("updatimng" + myID + " num " + numPoints + " size "
		// + bigParticle.size());

		int numSections = trailLength + 1;
		int pointsToMesh = 2;
		
		float alphaSteps = trailAlpha/(trailLength);

		for (int i = 0; i < numPoints; i++) {

			ShapedParticle oneParticle = bigParticle.get(i);

				
			for (int j = 0; j < trailLength - 1; j++) {

				int step = (i * numSections * pointsToMesh * 4)
						+ (j * pointsToMesh * 4);

				colors[step + 0] = 1.0f;
				colors[step + 1] = 1.0f;
				colors[step + 2] = 1.0f;
				colors[step + 3] = trailAlpha; // The W coordinate of each point

				Vec3D trailPoint = oneParticle.getTailPoint(0);

				colors[step + 4] = 1.0f;
				colors[step + 5] = 1.0f;
				colors[step + 6] = 1.0f;
				colors[step + 7] = trailAlpha; // The W coordinate of each point

				for (int k = 0; k < pointsToMesh; k++) {

					step += (k * 4);

					trailPoint = oneParticle.getTailPoint(j + k);

					colors[step + 0] = 1.0f;
					colors[step + 1] = 1.0f;
					colors[step + 2] = 1.0f;
					
					
					colors[step + 3] = trailAlpha-(alphaSteps*(j+1))+(alphaSteps*k); 
				//	System.out.println("step "+step+" alpha "+colors[step + 3]);
				//	colors[step + 3] = 1;
				}

			}
		}


		trails.updateColors(colors);
	}

	protected void updateForce() {

	

			boomPower *= 1 - boomFalloff;
			springPower *= 1 - springFallOff;

			boomForce.setStrength(boomPower * 0.5f);

			for (int i = 0; i < bigParticle.size(); i++) {
				// boomForce.setStrength(boomPower);
				bigParticle.get(i).setBehaviorStrenght(springPower);
			}

			if (boomPower > -0.00001) {

				/*
				 * boomPower=-5.0f;
				 * 
				 * boomForce.setStrength(boomPower*0.1f);
				 * 
				 * for(int i=0;i<bigParticle.size();i++){
				 * boomForce.setStrength(boomPower);
				 * bigParticle.get(i).setBehaviorStrenght(boomPower); }
				 */

				physics.removeBehavior(boomForce);
			}
		

		}



	public void update() {

		for (int i = 0; i < bigParticle.size(); i++) {

			Particle pa = bigParticle.get(i);

			if (pa.isDead()) {

				// as it's tricky to delete from VBO just make invisible.
				// sprites.updateColor(i, 0, 0);

			//	System.out.println("particle dead");
				cleanParticleForces(i);
				bigParticle.remove(i);
				
			}
		}

		updateSpritePositions();
		updateSpriteColors();
		
		updateTrailPositions();

		
		updateForce();

	}

	public void draw(GLGraphics renderer) {

		// drawGrid();
		// drawErmitter(renderer);

		e.setupModel(sprites);
		renderer.model(sprites);

		e.setupModel(trails);
		renderer.model(trails);

	}

	public void cleanSytstem() {

		physics.removeBehavior(boomForce);
		
		for(int i=0;i<bigParticle.size();i++){
			cleanParticleForces(i);
		}
		

		// model.delete();
	}
	
	private void cleanParticleForces(int num){
		physics.removeSpring(bigParticle.get(num).getSpring());
		
	}

	public boolean isEmpty() {
		if (bigParticle.size() < 1)
			return true;
		else
			return false;
	}

	
	public void resetPowers(){
		
		boomPower = initalBoomPower;
		springPower = initalSpringPower;
	}
	
	public void setBoomPower(float newPower) {
		boomPower = newPower;
		initalBoomPower = boomPower;
	}

	public void setSpringPower(float newPower) {
		springPower = newPower;
		 initalSpringPower = springPower;
	}
	
	public float getBoomPower(){
		return boomPower;	
	}
	
	public float getSpringPower(){
		return springPower;
	}
	

}