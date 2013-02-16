package com.madsim.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.drawable.Drawlist;
import com.madsim.engine.optik.Optik;
import com.madsim.engine.shader.Shader;

import codeanticode.glgraphics.GLGraphics;

public class Engine {

	public EngineApplet p;
	
	public GLGraphics g;
	
	private HashMap<String, Drawable> drawables = new HashMap<String, Drawable>();
	
	private HashMap<String, Optik> optiks = new HashMap<String, Optik>();
	private String activeOptik;

	private HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private String activeShader;
	
	public boolean drawStarted = false;
	
	public Engine(EngineApplet p) {
		this.p = p;
		refreshGLG();
		
		p.logLn("[Engine]: Setting rendering defaults.");
		g.smooth();
	}
	
	public void addOptik(String name, Optik optik) {
		optiks.put(name, optik);
		p.logLn("[Engine]: New optik '"+name+"' added.");
	}
	
	public void activateOptik(String name) {
		activeOptik = name;
	}

	public void addShader(String name, Shader shader) {
		shaders.put(name, shader);
		p.logLn("[Engine]: New shader '"+name+"' added.");
	}
	
	public void addDrawlist(String name, Drawlist dl) {
		this.drawables.put(name, dl);
	}
	
	public void update(String name) {
		Drawable dl = drawables.get(name);
		if(
			dl.updateMode() == Drawable.ONANDOFFSCREEN ||
			(dl.updateMode() == Drawable.ONLY_ONSCREEN && dl.mode() != Drawable.OFF_SCREEN)
		) {
			dl.setG(g);
			dl.update();
		}
	}
	
	public void updateAll() {
		for(Entry<String, Drawable> dle : drawables.entrySet()) {
			update(dle.getKey());
		}
	}
	
	private void setOptik() {
		optiks.get(activeOptik).setG(g);
		optiks.get(activeOptik).calculate();
		optiks.get(activeOptik).set();
	}
	
	public void startShader(String name) {
		activeShader = name;
		shaders.get(activeShader).start();
	}
	
	public void stopShader() {
		shaders.get(activeShader).stop();
	}
	
	public void refreshGLG() {
		g = (GLGraphics)p.g;
	}
	
	public void beginDraw() {
		if(!drawStarted) {
			drawStarted = true;
			
//			refreshGLG();
			
			// GLGraphics will copy the old matrix to the new context, we don't want the old matrix
			g.resetMatrix();
			
			g.beginGL();
			
			// Setting up clean Matrix again to wipe out GLGraphics std. settings
			g.resetMatrix();
			
			// Set current Optik
			setOptik();
			
			// Refresh background color
			g.background(0, 0, 0);
		}
	}

	public void endDraw() {
		if(drawStarted) {
			g.endGL();
			drawStarted = false;
		}
	}
	
	public void draw() {
		beginDraw();
		
		updateAll();
		
			for(Entry<String, Drawable> dle : drawables.entrySet()) {
				Drawable dl = dle.getValue();
				if(dl.mode() != Drawable.OFF_SCREEN) {
					g.pushStyle();
					g.pushMatrix();
						dl.draw();
					g.popMatrix();
					g.popStyle();
				}
			}
		
		endDraw();
	}
	
}
