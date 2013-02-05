package xx.codeflower.spielraum.motion.scene;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class SceneParallax extends SceneContent {

	private PImage[] images;
	private PGraphics offG;
	public String[] imagePaths;
	private float[] pFactors;
	private float offsetX = 0, offsetY = 0;
	
	public SceneParallax(PApplet parent, String contentFolder, String[] setup) {
		super(parent, contentFolder, setup);
	}
	
	protected void parseTypeRelatedSetup(String[] setup) {
		int num = (setup.length-9)/2;
		images = new PImage[num];
		imagePaths = new String[num];
		pFactors = new float[num];
		
		int ii = 0;
		for(int i = 9; i < setup.length; i+=2) {
			imagePaths[ii] = setup[i];
			images[ii] = parent.loadImage(contentFolder+setup[i]);
			pFactors[ii] = Float.parseFloat(setup[i+1]);
			ii++;
		}
		
		offG = parent.createGraphics(600, 600);
	}
	
	public void moveTo(float x, float y) {
		offsetX = x;
		offsetY = y;
	}
	
	public void moveLeft(float x, float y) {
		offsetX -= x;
		offsetY -= y;
	}
	
	public void moveRight(float x, float y) {
		offsetX += x;
		offsetY += y;
	}
	
	protected String getConfigString() {
		String c = "parallax="+name+","+pos[0]+","+pos[1]+","+pos[2]+","+pos[3]+","+pos[4]+","+pos[5]+","+pos[6]+","+pos[7];
		for(int i = 0; i < imagePaths.length; i++) c = c+","+imagePaths[i]+","+pFactors[i];
		return c+"\n";
	}
	
	public void drawContent(PGraphics g) {
		offG.beginDraw();
			for(int i = 0; i < images.length; i++) {
				offG.image(images[i], offsetX*pFactors[i], offsetY*pFactors[i], offG.width, offG.height);
			}
		offG.endDraw();
		
		g.tint(255, alpha);
		g.noFill();
		
		g.beginShape();
			g.tint(255, alpha);
			g.noFill();
			
			g.texture(offG);
				g.vertex(pos[0], pos[1], 0, 0);
				g.vertex(pos[2], pos[3], offG.width, 0);
				g.vertex(pos[4], pos[5], offG.width, offG.height);
				g.vertex(pos[6], pos[7], 0, offG.height);
			g.tint(255, 255);
		g.endShape();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
}
