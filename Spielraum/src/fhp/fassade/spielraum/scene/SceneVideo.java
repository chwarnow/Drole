package fhp.fassade.spielraum.scene;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.video.Movie;

public class SceneVideo extends SceneContent {

	private Movie movie; 
	private String movieFileName;
	
	public SceneVideo(PApplet parent, String contentFolder, String[] setup) {
		super(parent, contentFolder, setup);
	}
	
	protected void parseTypeRelatedSetup(String[] setup) {
		movieFileName = setup[9];
		
		movie = new Movie(parent, contentFolder+setup[9]);
		movie.loop();
	}
	
	protected String getConfigString() {
		return "video="+name+","+pos[0]+","+pos[1]+","+pos[2]+","+pos[3]+","+pos[4]+","+pos[5]+","+pos[6]+","+pos[7]+","+movieFileName+"\n";
	}
	
	public void drawContent(PGraphics g) {
		if(movie.isLooping()) {
			movie.read();
			g.beginShape();
				g.tint(255, alpha);
				g.texture(movie);
					g.vertex(pos[0], pos[1], 0, 0);
					g.vertex(pos[2], pos[3], movie.width, 0);
					g.vertex(pos[4], pos[5], movie.width, movie.height);
					g.vertex(pos[6], pos[7], 0, movie.height);
				g.tint(255, 255);
			g.endShape();
		}
	}
	
}
