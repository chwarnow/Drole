package drole.menu;

import processing.core.PApplet;
import codeanticode.glgraphics.GLModel;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

public class Menu extends Drawable {

	private short NUM_WORLDS = 5;
	
	private float r = 500;
	
	public static int NO_ACTIVE_WORLD = -1;
	
	private int activeWorld = NO_ACTIVE_WORLD;
	
	private GLModel[] worlds = new GLModel[NUM_WORLDS];
	
	private float[] worldAngles = new float[NUM_WORLDS];
	
	public boolean inWorld = false;
	
	public Menu(Engine e) {
		super(e);
		
		useLights();
		setPointLight(0, -800, 0, -1000, 255, 255, 255, 1.0f, 0.0001f, 0.0f);
		setPointLight(1,  700, 0,   -800, 255, 255, 255, 1.0f, 0.0001f, 0.0f);
		
		for(int i = 0; i < NUM_WORLDS; i++) {
			worlds[i] = new GLModel(e.p, 4, GLModel.QUADS, GLModel.STATIC);
			
//			worlds[i].initTextures(1);
//			worlds[i].setTexture(0, e.requestTexture("data/images/MenuTestTarget.png"));
			
			worlds[i].beginUpdateVertices();

//					worlds[i].beginUpdateTexCoords(0);
					
						float l = ((r * PApplet.cos((PApplet.TWO_PI/NUM_WORLDS)+0.2f)) - (r * PApplet.cos((PApplet.TWO_PI/NUM_WORLDS)-0.2f)))/2f;
						
						worldAngles[i] = ((PApplet.TWO_PI/NUM_WORLDS)*i);
						
						float a = ((PApplet.TWO_PI/NUM_WORLDS)*i)-0.2f;
						float x = r * PApplet.cos(a);
						float y = r * PApplet.sin(a);				
						worlds[i].updateVertex(0, x, y, l);
//						worlds[i].updateTexCoord(0, 0, 1);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*i)+0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);
						worlds[i].updateVertex(1, x, y, l);
//						worlds[i].updateTexCoord(1, 1, 1);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*i)+0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);
						worlds[i].updateVertex(2, x, y, -l);
//						worlds[i].updateTexCoord(2, 1, 0);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*i)-0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);				
						worlds[i].updateVertex(3, x, y, -l);
//						worlds[i].updateTexCoord(3, 0, 0);
					
//					worlds[i].endUpdateTexCoords();

			worlds[i].endUpdateVertices();
			
			worlds[i].initColors();
			worlds[i].setColors(200);
			
	//		worlds[i].initNormals();
		}
	}
	
	public int getActiveWorld() {
		return activeWorld;
	}

	@Override
	public void update() {
		super.update();
		
		if(!inWorld) {
			int tmpActiveWorld = -1;
			float curY = (((rotation.z+PApplet.HALF_PI)%PApplet.TWO_PI))*-1;
			for(int i = 0; i < NUM_WORLDS; i++) {
				if(worldAngles[i] >= curY-0.2f &&  worldAngles[i] <= curY+0.2f) {
					
					worlds[i].setColors(200, 0, 0);
					
					tmpActiveWorld = i;
				} else {
					worlds[i].setColors(200*(1.0f/i));
				}
				e.p.pinLog("World"+i, worldAngles[i]);
			}
			
			e.p.pinLog("RotationY", curY);
			
			activeWorld = tmpActiveWorld;
		}
	}
	
	@Override
	public void draw() {
		g.pushMatrix();
		g.pushStyle();
			
			g.translate(position.x, position.y, position.z);
			g.rotateX(PApplet.radians(-90));
		
			g.pushMatrix();
			g.rotateX(rotation.x);
			g.rotateY(rotation.y);
			g.rotateZ(rotation.z);
			
				for(int i = 0; i < NUM_WORLDS; i++) {
					e.setupModel(worlds[i]);
					worlds[i].render();
				}
			
			g.popMatrix();
			
		g.popStyle();
		g.popMatrix();
	}
	
}