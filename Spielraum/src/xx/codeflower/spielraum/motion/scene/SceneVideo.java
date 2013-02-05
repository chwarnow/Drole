package xx.codeflower.spielraum.motion.scene;

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
		movieFileName = setup[10];
		
		movie = new Movie(parent, contentFolder+movieFileName);
		movie.play();
		movie.stop();
	}
	
	protected String getConfigString() {
		return "video="+name+","+pos[0]+","+pos[1]+","+pos[2]+","+pos[3]+","+pos[4]+","+pos[5]+","+pos[6]+","+pos[7]+","+(isEditable ? "editable" : "locked")+","+movieFileName+"\n";
	}
	
	public void drawContent(PGraphics g) {
		if(movie.isLooping()) {
			
			if(movie.available()) movie.read();

			g.noStroke();
			
			g.beginShape();			
			g.fill(255);
			
			if(alpha  < 250) g.tint(255, alpha);
			else g.noTint();
		
			g.texture(movie);
				g.vertex(pos[0], pos[1]);
				g.vertex(pos[2], pos[3]);
				g.vertex(pos[4], pos[5]);
				g.vertex(pos[6], pos[7]);
			g.endShape();
			
			g.beginShape();			
				g.noFill();
				
				if(alpha  < 250) g.tint(255, alpha);
				else g.noTint();
			
				g.texture(movie);
					g.vertex(pos[0], pos[1], 0, 0);
					g.vertex(pos[2], pos[3], movie.width, 0);
					g.vertex(pos[4], pos[5], movie.width, movie.height);
					g.vertex(pos[6], pos[7], 0, movie.height);
			g.endShape();
			
			g.noTint();
		}
	}

	@Override
	public void start() {
		movie.loop();
	}

	@Override
	public void stop() {
		movie.stop();
	}
	
}
