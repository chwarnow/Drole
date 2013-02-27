package drole.menu;

import processing.core.PApplet;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLworlds[i];

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

public class Menu extends Drawable {

	private short NUM_WORLDS = 5;
	
	private float r = 500;
	
	private short acriveWorld = -1;
	
	private GLModel[] worlds = new GLModel[NUM_WORLDS];
	
	public Menu(Engine e) {
		super(e);
		
		for(int i = 0; i < NUM_WORLDS; i++) {
			worlds[i] = new GLModel(e.p, 4*NUM_WORLDS, GLModel.QUADS, GLModel.STATIC);
			
			worlds[i].initTextures(NUM_WORLDS);
			worlds[i].setTexture(0, e.requestTexture("data/images/MenuTestTarget.png"));
			worlds[i].setTexture(1, e.requestTexture("data/images/MenuTestTarget.png"));
			worlds[i].setTexture(2, e.requestTexture("data/images/MenuTestTarget.png"));
			worlds[i].setTexture(3, e.requestTexture("data/images/MenuTestTarget.png"));
			worlds[i].setTexture(4, e.requestTexture("data/images/MenuTestTarget.png"));
			
			worlds[i].beginUpdateVertices();
				int world = 0;
				for(int i = 0; i < 4*NUM_WORLDS; i+=4) {
					worlds[i].beginUpdateTexCoords(world);
					
						float l = ((r * PApplet.cos((PApplet.TWO_PI/NUM_WORLDS)+0.2f)) - (r * PApplet.cos((PApplet.TWO_PI/NUM_WORLDS)-0.2f)))/2f;
						
						float a = ((PApplet.TWO_PI/NUM_WORLDS)*world)-0.2f;
						float x = r * PApplet.cos(a);
						float y = r * PApplet.sin(a);				
						worlds[i].updateVertex(i, x, y, l);
						worlds[i].updateTexCoord(i, 0, 1);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*world)+0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);
						worlds[i].updateVertex(i+1, x, y, l);
						worlds[i].updateTexCoord(i+1, 1, 1);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*world)+0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);
						worlds[i].updateVertex(i+2, x, y, -l);
						worlds[i].updateTexCoord(i+2, 1, 0);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*world)-0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);				
						worlds[i].updateVertex(i+3, x, y, -l);
						worlds[i].updateTexCoord(i+3, 0, 0);
						
						world++;
					
					worlds[i].endUpdateTexCoords();
				}
			worlds[i].endUpdateVertices();
			
			worlds[i].initColors();
			worlds[i].setColors(200);
			
	//		worlds[i].initNormals();
		}
	}

	@Override
	public void draw() {
		g.pushMatrix();
		g.pushStyle();
		
			g.rotateX(PApplet.radians(-90));
		
			for(int i = 0; i < NUM_WORLDS; i++) {
				e.setupModel(worlds[i]);
				worlds[i].render();
			}
			
		g.popStyle();
		g.popMatrix();
	}
	
}
