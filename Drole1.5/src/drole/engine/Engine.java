package drole.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLTexture;

import drole.DroleMain;
import drole.engine.optik.Optik;
import drole.engine.shader.Shader;

public class Engine {

	private DroleMain p;
	
	private HashMap<String, Drawlist> drawlists = new HashMap<String, Drawlist>();
	
	private HashMap<String, Optik> optiks = new HashMap<String, Optik>();
	private String activeOptik;

	private HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private String activeShader;
	
	private GLTexture mask;
	
	public Engine(DroleMain p) {
		this.p = p;
		p.logLn("[Engine]: Setting rendering defaults.");
		mask = new GLTexture(p, "data/images/drole-mask.png");
		p.smooth();
	}
	
	public void addOptik(String name, Optik optik) {
		optiks.put(name, optik);
		p.logLn("[Engine]: New optik '"+name+"' added.");
	}
	
	public void activateOptik(String name) {
		activeOptik = name;
		setOptik();
	}

	public void addShader(String name, Shader shader) {
		shaders.put(name, shader);
		p.logLn("[Engine]: New shader '"+name+"' added.");
	}
	
	public void addDrawlist(String name, Drawlist dl) {
		this.drawlists.put(name, dl);
	}
	
	public void update(String name) {
		Drawlist dl = drawlists.get(name);
		if(dl.mode() != Drawable.OFF_SCREEN) dl.update();
	}
	
	public void updateAll() {
		for(Entry<String, Drawlist> dle : drawlists.entrySet()) {
			Drawlist dl = dle.getValue();
			if(dl.mode() != Drawable.OFF_SCREEN) dl.update();
		}
	}
	
	private void setOptik() {
		optiks.get(activeOptik).calculate();
		optiks.get(activeOptik).set();		
	}
	
	public void startShader(String name) {
		activeShader = name;
		shaders.get(activeShader).start();
		
		GLGraphics renderer = (GLGraphics) p.g;
		GL gl = renderer.gl;
		
		/*
		// First set active texture unit
		gl.glClientActiveTexture(GL.GL_mask.getTextureID());
		// Then enable it!
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.glBindTexture(GL.GL_TEXTURE_2D, mask.getTextureID());
		
		gl.glUniform1iARB(shaders.get(activeShader).glsl().getUniformLocation("maskTexture"), 0);
		*/
	}
	
	public void stopShader() {
		shaders.get(activeShader).stop();
	}
	
	public void draw(String name) {
		setOptik();
		
		Drawlist dl = drawlists.get(name);
		if(dl.mode() != Drawable.OFF_SCREEN) dl.draw();
	}	
	
	public void drawAll() {
		setOptik();
		
		for(Entry<String, Drawlist> dle : drawlists.entrySet()) {
			Drawlist dl = dle.getValue();
			if(dl.mode() != Drawable.OFF_SCREEN) dl.draw();
		}
	}
	
}
