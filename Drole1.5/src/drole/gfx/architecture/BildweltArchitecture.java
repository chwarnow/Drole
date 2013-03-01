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
		float roomSize = 37;
		backPosition = new PVector(0, 0, -Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM*2);//-dimension.x*scale.x*2.5f);
		bottomPosition = new PVector(0, -Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM, -Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM);//-dimension.x*scale.x*1.25f);
		topPosition = new PVector(0, -Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM, Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM);//dimension.x*scale.x*1.25f);
		leftPosition = new PVector(Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM, 0, -Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM);//dimension.x*scale.x*1.25f);
		rightPosition = new PVector(Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM, 0, Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM);//dimension.x*scale.x*1.25f);
		
		back = new BildweltAssoziationPensee(
			e,
			"data/room/architecture/back.png",
			dimension.x*scale.x*100.0f,
			roomSize,
			backPosition,
			new PVector(0, 0, 0)
		);
		
		bottom = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/floor.png",
				dimension.x*scale.x*100.0f,
				roomSize,
				bottomPosition,
				new PVector(0, 0, 0)
			);
		
		top = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/ceiling.png",
				dimension.x*scale.x*100.0f,
				roomSize,
				topPosition,
				new PVector(0, 0, 0)
			);
		
		left = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/left.png",
				dimension.x*scale.x*100.0f,
				roomSize,
				leftPosition,
				new PVector(0, 0, 0)
			);
		
		right = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/right.png",
				dimension.x*scale.x*100.0f,
				roomSize,
				rightPosition,
				new PVector(0, 0, 0)
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
		
		// load highres images
		backHighres = new GLTexture(e.p, "data/room/architecture/backHighres.png");
		topHighres = new GLTexture(e.p, "data/room/architecture/ceilingHighres.png");
		leftHighres = new GLTexture(e.p, "data/room/architecture/leftHighres.png");
		rightHighres = new GLTexture(e.p, "data/room/architecture/rightHighres.png");
		bottomHighres = new GLTexture(e.p, "data/room/architecture/floorHighres.png");
		
		// create glmodels for each texture
		// this way textures are shown
		float quadSize = roomSize*.5f;
		backModel = createQuad(backHighres, backHighres.width*quadSize, backHighres.height*quadSize);
		topModel = createQuad(topHighres, topHighres.width*quadSize, topHighres.height*quadSize);
		bottomModel = createQuad(bottomHighres, bottomHighres.width*quadSize, bottomHighres.height*quadSize);
		leftModel = createQuad(leftHighres, leftHighres.width*quadSize, leftHighres.height*quadSize);
		rightModel = createQuad(rightHighres, rightHighres.width*quadSize, rightHighres.height*quadSize);
		
	}
	
	@Override
	public void fadeIn(float time) {
		super.fadeIn(time);
		
		wallID = 0;
		CURRENT_MODE = MODE_SHOWING;
		
		if(!back.isReady()) back.loadNewImage("data/room/architecture/back.png",
				dimension.x*scale.x*10.0f,
				backPosition,
				new PVector(0, 0, 0));
		if(!bottom.isReady()) bottom.loadNewImage("data/room/architecture/floor.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, -dimension.x*scale.x*1.25f),
				new PVector(0, 0, 0));
		if(!top.isReady()) top.loadNewImage("data/room/architecture/ceiling.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, dimension.x*scale.x*.0f, dimension.x*scale.x*1.25f),
				new PVector(0, 0, 0));
		if(!left.isReady()) left.loadNewImage("data/room/architecture/left.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, -dimension.x*scale.x*1.25f),
				new PVector(0, 0, 0));
		if(!right.isReady()) right.loadNewImage("data/room/architecture/right.png",
				dimension.x*scale.x*10.0f,
				new PVector(0, 0, dimension.x*scale.x*1.25f),
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
		
		back.hideMe();
		bottom.hideMe();
		top.hideMe();
		left.hideMe();
		right.hideMe();
		
		// TODO: fade out highres quads via alpha
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
		
		g.translate(position.x, position.y + e.p.cos(e.p.frameCount*.02f)*0, position.z);
		g.scale(scale.x*.5f, scale.y*.5f, scale.z*.5f);
		// g.rotateY(e.p.frameCount*.01f);
		
		// set shader for pensees
		e.startShader("PolyLightAndColor");
		
		if(wallID == 0) if(back.isShowing()) back.draw();
		
		g.pushMatrix();
		g.rotateX(3.1414f/2);
		if(wallID == 2) if(bottom.isShowing()) bottom.draw();
		if(wallID == 4) if(top.isShowing()) top.draw();
		g.popMatrix();
		
		g.pushMatrix();
		g.rotateY(3.1414f/2);
		if(wallID == 1) if(left.isShowing()) left.draw();
		if(wallID == 3) if(right.isShowing()) right.draw();
		g.popMatrix();
		
		
		// draw highres images
		
		// set shader for highres images
		e.startShader("PolyLightAndTexture");
		
		if(mode() == ON_SCREEN) {
			// back
			if(wallID > 0) {
				g.pushMatrix();
				g.translate(backPosition.x, backPosition.y, backPosition.z);
				e.setupModel(backModel);
				backModel.render();
				g.popMatrix();
			}
			// left
			if(wallID > 1) {
				g.pushMatrix();
				g.rotateY(3.1414f/2);
				g.translate(leftPosition.x, leftPosition.y, leftPosition.z);
				leftModel.render();
				g.popMatrix();
			}
			// bottom
			if(wallID > 2) {
				g.pushMatrix();
				g.rotateX(3.1414f/2);
				g.translate(bottomPosition.x, bottomPosition.y, bottomPosition.z);
				bottomModel.render();
				g.popMatrix();
			}
			// right
			if(wallID > 3) {
				g.pushMatrix();
				g.rotateY(3.1414f/2);
				g.translate(rightPosition.x, rightPosition.y, rightPosition.z);
				rightModel.render();
				g.popMatrix();
			}
			// top
			if(wallID > 4) {
				g.pushMatrix();
				g.rotateX(3.1414f/2);
				g.translate(topPosition.x, topPosition.y, topPosition.z);
				topModel.render();
				g.popMatrix();
			}
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
