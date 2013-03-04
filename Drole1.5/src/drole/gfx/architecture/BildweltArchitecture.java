package drole.gfx.architecture;

import processing.core.PApplet;
import processing.core.PVector;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;
import drole.gfx.assoziation.BildweltAssoziationPensee;
import drole.settings.Settings;

public class BildweltArchitecture extends Drawable {

	private int MODE_VOID = 0;
	private int MODE_SHOWING = 1;
	private int MODE_HIDING = 2;
	private int CURRENT_MODE = MODE_VOID;
	private int wallID = 0;

	private BildweltAssoziationPensee back, bottom, top, left, right;
	private GLTexture backHighres, bottomHighres, topHighres, leftHighres, rightHighres;
	private PVector backPosition, bottomPosition, topPosition, leftPosition, rightPosition;
	private GLModel backModel, bottomModel, topModel, leftModel, rightModel;

	public BildweltArchitecture(Engine e, PVector position, PVector dimension) {
		super(e);
		position(position);
		dimension(dimension);

		// create walls as pensees
		float roomSize = 34.7f;
		backPosition = new PVector(0, 0, -Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM*2);//-dimension.x*scale.x*2.5f);
		bottomPosition = new PVector(0, -Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM*.95f, -Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM*.95f);//-dimension.x*scale.x*1.25f);
		topPosition = new PVector(0, -Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM*.76f, Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM*.95f);//dimension.x*scale.x*1.25f);
		leftPosition = new PVector(Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM*.84f, 0, -Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM);//dimension.x*scale.x*1.25f);
		rightPosition = new PVector(Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM*.84f, 0, Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM);//dimension.x*scale.x*1.25f);

		int introSteps = 100;
		back = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/floorHighres.png",
				dimension.x*scale.x*100.0f,
				roomSize*.985f,
				backPosition,
				new PVector(0, 0, 0),
				introSteps
		);

		bottom = new BildweltAssoziationPensee(
				e,
				"",
				dimension.x*scale.x*100.0f,
				roomSize,
				bottomPosition,
				new PVector(0, 0, 0),
				introSteps
		);

		top = new BildweltAssoziationPensee(
				e,
				"",
				dimension.x*scale.x*100.0f,
				roomSize,
				topPosition,
				new PVector(0, 0, 0),
				introSteps
		);

		left = new BildweltAssoziationPensee(
				e,
				"",
				dimension.x*scale.x*100.0f,
				roomSize,
				leftPosition,
				new PVector(0, 0, 0),
				introSteps
		);

		right = new BildweltAssoziationPensee(
				e,
				"",
				dimension.x*scale.x*100.0f,
				roomSize,
				rightPosition,
				new PVector(0, 0, 0),
				introSteps
		);

		back.setLooping(false);
		bottom.setLooping(false);
		top.setLooping(false);
		left.setLooping(false);
		right.setLooping(false);

		back.stop();
		bottom.stop();
		top.stop();
		left.stop();
		right.stop();

		back.noiseScale = 1400;
		bottom.noiseScale = 1400;
		top.noiseScale = 1400;
		left.noiseScale = 1400;
		right.noiseScale = 1400;

		// load highres images
		backHighres = new GLTexture(e.p, "data/room/architecture/backHighres.png");
		topHighres = new GLTexture(e.p, "data/room/architecture/ceilingHighres.png");
		leftHighres = new GLTexture(e.p, "data/room/architecture/rightHighres.png");
		rightHighres = new GLTexture(e.p, "data/room/architecture/leftHighres.png");
		bottomHighres = new GLTexture(e.p, "data/room/architecture/floorHighres.png");

		// create glmodels for each texture
		// this way textures are shown
		float quadSize = roomSize*.5f;
		backModel = createQuad(backHighres, backHighres.width*quadSize, backHighres.height*quadSize);
		topModel = createQuad(topHighres, topHighres.width*quadSize, topHighres.height*quadSize*.75f);
		bottomModel = createQuad(bottomHighres, bottomHighres.width*quadSize, bottomHighres.height*quadSize*.85f);
		leftModel = createQuad(leftHighres, leftHighres.width*quadSize*.8f, leftHighres.height*quadSize*1.01f);
		rightModel = createQuad(rightHighres, rightHighres.width*quadSize*.8f, rightHighres.height*quadSize);

	}

	@Override
	public void fadeIn(float time) {
		super.fadeIn(time);

		wallID = 0;
		CURRENT_MODE = MODE_SHOWING;

		if(!back.isReady()) back.loadNewImage("data/room/architecture/back.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, 0),
				new PVector(0, 0, 0));
		if(!bottom.isReady()) bottom.loadNewImage("data/room/architecture/floor.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, 0),
				new PVector(0, 0, 0));
		if(!top.isReady()) top.loadNewImage("data/room/architecture/ceiling.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, 0),
				new PVector(0, 0, 0));
		if(!left.isReady()) left.loadNewImage("data/room/architecture/right.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, 0),
				new PVector(0, 0, 0));
		if(!right.isReady()) right.loadNewImage("data/room/architecture/left.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, 0),
				new PVector(0, 0, 0));

		back.stop();
		bottom.stop();
		top.stop();
		left.stop();
		right.stop();
	}

	@Override
	public void fadeOut(float time) {
		super.fadeOut(time);
		/*
		back.currPosition = 0;
		bottom.currPosition = 0;
		top.currPosition = 0;
		left.currPosition = 0;
		right.currPosition = 0;
		*/
		
		if(back.isShowing()) back.hideMe();
		if(bottom.isShowing()) bottom.hideMe();
		if(top.isShowing()) top.hideMe();
		if(left.isShowing()) left.hideMe();
		if(right.isShowing()) right.hideMe();

		// TODO: fade out highres quads via alpha
		// CURRENT_MODE = MODE_VOID;
	}

	@Override
	public void update() {
		super.update();

		back.update();
		bottom.update();
		top.update();
		left.update();
		right.update();

		if(CURRENT_MODE == MODE_SHOWING) {
			// go through planes and show them one after another
			
			// back
			if(wallID == 0) {
				if(!back.isShowing()) {
					back.setPosition(.5f);
					back.showMe();
				}
				else if(!back.isRunning()) wallID++;

			}
			// left
			if(wallID == 1) {
				if(!left.isShowing()) {
					left.setPosition(.5f);
					left.showMe();
				}
				else if(!left.isRunning()) wallID++;
			}
			// bottom
			if(wallID == 2) {
				if(!bottom.isShowing()) {
					bottom.setPosition(.5f);
					bottom.showMe();
				}
				else if(!bottom.isRunning()) wallID++;
			}
			// right
			if(wallID == 3) {
				if(!right.isShowing()) {
					right.setPosition(.5f);
					right.showMe();
				}
				else if(!right.isRunning()) wallID++;
			}
			// top
			if(wallID == 4) {
				if(!top.isShowing()) {
					top.setPosition(.5f);
					top.showMe();
				}
				else if(!top.isRunning()) wallID++;
			}
		}

		// unload wall pensees when faded out
		if(fade == 0) {
			if(!back.isCleared()) back.clear();
			if(!bottom.isCleared()) bottom.clear();
			if(!top.isCleared()) bottom.clear();
			if(!left.isCleared()) left.clear();
			if(!right.isCleared()) right.clear();
		}
	}

	@Override
	public void draw() {
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z);
		g.scale(scale.x*.5f, scale.y*.5f, scale.z*.5f);
		// g.rotateY(e.p.frameCount*.01f);

		// set shader for pensees
		e.startShader("JustColor");

		if(wallID == 0) if(back.isShowing())  {
			g.pushMatrix();
			g.translate(backPosition.x, backPosition.y, backPosition.z);
			back.draw();
			g.popMatrix();
		}

		if(wallID == 1) if(left.isShowing())  {
			g.pushMatrix();
			g.rotateY(3.1414f/2);
			g.translate(leftPosition.x, leftPosition.y, leftPosition.z);
			left.draw();
			g.popMatrix();
		}
		// bottom
		if(wallID == 2) if(bottom.isShowing())  {
			g.pushMatrix();
			g.rotateX(3.1414f/2);
			g.translate(bottomPosition.x, bottomPosition.y, bottomPosition.z);
			bottom.draw();
			g.popMatrix();
		}

		// right
		if(wallID == 3) if(right.isShowing())  {
			g.pushMatrix();
			g.rotateY(3.1414f/2);
			g.translate(rightPosition.x, rightPosition.y, rightPosition.z);
			right.draw();
			g.popMatrix();
		}

		// top
		if(wallID == 4) if(top.isShowing())  {
			g.pushMatrix();
			g.rotateX(3.1414f/2);
			g.translate(topPosition.x, topPosition.y, topPosition.z);
			top.draw();
			g.popMatrix();
		}

		// draw highres images

		// todo: light settings
		disableLights();

		// set shader for highres images
		e.startShader("PolyLightAndTexture");

		float introThreshold = .75f;
		// back
		// if(wallID == 0) {
		System.out.println(back.currPosition);
		if(back.currPosition > 0) {
			float alpha = (back.currPosition < (back.positionSteps()*(1-introThreshold))) ? 0 : (back.currPosition - back.positionSteps()*(introThreshold)) / (back.positionSteps()*((1-introThreshold)));
			g.pushMatrix();
			g.translate(backPosition.x, backPosition.y, backPosition.z);
			e.setupModel(backModel);
			backModel.setColors(255, alpha*255*fade);
			backModel.render();
			g.popMatrix();
		}
		// left
		if(left.currPosition > 0) {
			float alpha = (left.currPosition < (left.positionSteps()*(1-introThreshold))) ? 0 : (left.currPosition - left.positionSteps()*(introThreshold)) / (left.positionSteps()*((1-introThreshold)));
			g.pushMatrix();
			g.rotateY(3.1414f/2);
			g.translate(leftPosition.x, leftPosition.y, leftPosition.z);
			leftModel.setColors(255, alpha*255*fade);
			leftModel.render();
			g.popMatrix();
		}

		// bottom
		if(bottom.currPosition > 0) {
			float alpha = (bottom.currPosition < (bottom.positionSteps()*(1-introThreshold))) ? 0 : (bottom.currPosition - bottom.positionSteps()*(introThreshold)) / (bottom.positionSteps()*((1-introThreshold)));
			g.pushMatrix();
			g.rotateX(3.1414f/2);
			g.translate(bottomPosition.x, bottomPosition.y, bottomPosition.z);
			bottomModel.setColors(255, alpha*255*fade);
			bottomModel.render();
			g.popMatrix();
		}

		// right
		if(right.currPosition > 0) {
			float alpha = (right.currPosition < (right.positionSteps()*(1-introThreshold))) ? 0 : (right.currPosition - right.positionSteps()*(introThreshold)) / (right.positionSteps()*((1-introThreshold)));
			g.pushMatrix();
			g.rotateY(3.1414f/2);
			g.translate(rightPosition.x, rightPosition.y, rightPosition.z);
			rightModel.setColors(255, alpha*255*fade);
			rightModel.render();
			g.popMatrix();
		}

		// top
		if(top.currPosition > 0) {
			float alpha = (top.currPosition < (top.positionSteps()*(1-introThreshold))) ? 0 : (top.currPosition - top.positionSteps()*(introThreshold)) / (top.positionSteps()*((1-introThreshold)));
			g.pushMatrix();
			g.rotateX(3.1414f/2);
			g.translate(topPosition.x, topPosition.y, topPosition.z);
			topModel.setColors(255, alpha*255*fade);
			topModel.render();
			g.popMatrix();
		}

		g.popStyle();
		g.popMatrix();
	}

	private GLModel createQuad(GLTexture tex, float width, float height) {
		GLModel model = new GLModel(e.p, 4, PApplet.QUADS, GLModel.STATIC);

		model.beginUpdateVertices();
		model.updateVertex(0, -.5f*width, -.5f*height, 0);
		model.updateVertex(1, .5f*width, -.5f*height, 0);
		model.updateVertex(2, .5f*width, .5f*height, 0);
		model.updateVertex(3, -.5f*width, .5f*height, 0);  

		model.endUpdateVertices();

		model.initColors();
		model.setColors(255);

		model.initNormals();
		model.beginUpdateNormals();
		model.updateNormal(0, 0, 0, -1);
		model.updateNormal(1, 0, 0, -1);
		model.updateNormal(2, 0, 0, -1);
		model.updateNormal(3, 0, 0, -1);
		model.endUpdateNormals();

		model.initTextures(1);
		// ... and loading and setting texture for this model.  
		model.setTexture(0, tex);

		// Setting the texture coordinates.
		model.beginUpdateTexCoords(0);
		model.updateTexCoord(0, 0, 0);
		model.updateTexCoord(1, 1, 0);    
		model.updateTexCoord(2, 1, 1);
		model.updateTexCoord(3, 0, 1);
		model.endUpdateTexCoords();

		return model;
	}
}
