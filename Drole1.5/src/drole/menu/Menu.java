package drole.menu;

import processing.core.PApplet;
import codeanticode.glgraphics.GLModel;

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
			worlds[i] = new GLModel(e.p, 4, GLModel.QUADS, GLModel.STATIC);
			
			worlds[i].initTextures(1);
			worlds[i].setTexture(0, e.requestTexture("data/images/MenuTestTarget.png"));
			
			worlds[i].beginUpdateVertices();

					worlds[i].beginUpdateTexCoords(0);
					
						float l = ((r * PApplet.cos((PApplet.TWO_PI/NUM_WORLDS)+0.2f)) - (r * PApplet.cos((PApplet.TWO_PI/NUM_WORLDS)-0.2f)))/2f;
						
						float a = ((PApplet.TWO_PI/NUM_WORLDS)*i)-0.2f;
						float x = r * PApplet.cos(a);
						float y = r * PApplet.sin(a);				
						worlds[i].updateVertex(0, x, y, l);
						worlds[i].updateTexCoord(0, 0, 1);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*i)+0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);
						worlds[i].updateVertex(1, x, y, l);
						worlds[i].updateTexCoord(1, 1, 1);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*i)+0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);
						worlds[i].updateVertex(2, x, y, -l);
						worlds[i].updateTexCoord(2, 1, 0);
						
						a = ((PApplet.TWO_PI/NUM_WORLDS)*i)-0.2f;
						x = r * PApplet.cos(a);
						y = r * PApplet.sin(a);				
						worlds[i].updateVertex(3, x, y, -l);
						worlds[i].updateTexCoord(3, 0, 0);
					
					worlds[i].endUpdateTexCoords();

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
			
			g.translate(position.x, position.y, position.z);
			g.rotateX(PApplet.radians(-90));
		
			g.pushMatrix();
			g.rotateY(rotation.y);
			
				for(int i = 0; i < NUM_WORLDS; i++) {
					e.setupModel(worlds[i]);
					worlds[i].render();
				}
			
			g.popMatrix();
			
		g.popStyle();
		g.popMatrix();
	}
	
}
