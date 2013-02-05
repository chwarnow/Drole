package fhp.fassade.spielraum.scene;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class SceneImage extends SceneContent {

	private PImage image; 
	public String imagePath;
	
	public SceneImage(PApplet parent, String contentFolder, String[] setup) {
		super(parent, contentFolder, setup);
	}
	
	protected void parseTypeRelatedSetup(String[] setup) {
		imagePath = setup[9];
		
		image = parent.loadImage(contentFolder+setup[9]);
	}
	
	protected String getConfigString() {
		return "image="+name+","+pos[0]+","+pos[1]+","+pos[2]+","+pos[3]+","+pos[4]+","+pos[5]+","+pos[6]+","+pos[7]+","+imagePath+"\n";
	}
	
	public void drawContent(PGraphics g) {
		g.beginShape();
			g.tint(255, alpha);
			g.noFill();
			g.texture(image);
				g.vertex(pos[0], pos[1], 0, 0);
				g.vertex(pos[2], pos[3], image.width, 0);
				g.vertex(pos[4], pos[5], image.width, image.height);
				g.vertex(pos[6], pos[7], 0, image.height);
			g.tint(255, 255);
		g.endShape(g.CLOSE);
	}
	
}
