package com.madsim.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.drawable.Drawlist;
import com.madsim.engine.optik.Optik;
import com.madsim.engine.shader.Shader;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;

public class Engine {

	public EngineApplet p;
	
	public GLGraphics g;
	
	private GLGraphicsOffScreen offG;
	
	private GLTexture firstPassResult;
	
	private GLTexture maskTexture;
	
	private boolean drawOffScreen = false;
	
	private HashMap<String, Drawable> drawables = new HashMap<String, Drawable>();
	
	private HashMap<String, Optik> optiks = new HashMap<String, Optik>();
	private String activeOptik;
	private String initialActiveOptik;

	private HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private Shader activeShader;
	
	public boolean drawStarted = false;
	
	public Engine(EngineApplet p) {
		this.p = p;
		refreshGLG();
		
		maskTexture = new GLTexture(p, "data/images/drole-mask.png");
		
		p.logLn("[Engine]: Setting rendering defaults.");
		g.smooth();
		
		p.logLn("[Engine]: Initializing Framebuffers ("+g.width+":"+g.height+")");
		offG = new GLGraphicsOffScreen(p, g.width, g.height);
	}
	
	public void model(GLModel model) {
		if(drawOffScreen) offG.model(model);
		else g.model(model);
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
	
	public void update(String name, PGraphicsOpenGL cg) {
		Drawable dl = drawables.get(name);
		if(
			dl.updateMode() == Drawable.ONANDOFFSCREEN ||
			(dl.updateMode() == Drawable.ONLY_ONSCREEN && dl.mode() != Drawable.OFF_SCREEN)
		) {
			dl.setG(cg);
			dl.update();
		}
	}
	
	public void updateAll(PGraphicsOpenGL cg) {
		for(Entry<String, Drawable> dle : drawables.entrySet()) {
			update(dle.getKey(), cg);
		}
	}
	
	private void setOptik(PGraphicsOpenGL cg) {
		optiks.get(activeOptik).setG(cg);
		optiks.get(activeOptik).calculate();
		optiks.get(activeOptik).set();
	}
	
	public void startShader(String name) {
		if(!shaders.containsKey(name)) p.logErr("[Engine]: Can't find shader '"+name+"'!");
		activeShader = shaders.get(name);
		activeShader.start();
	}
	
	public void stopShader() {
		activeShader.stop();
	}
	
	public void refreshGLG() {
		g = (GLGraphics)p.g;
	}
	
	public void beginDraw() {
		if(!drawStarted) {
			drawStarted = true;
			
			// GLGraphics will copy the old matrix to the new context, we don't want the old matrix!
			g.resetMatrix();
			
			g.beginGL();
			
			// Set current Optik
			setOptik(g);
		}
	}

	public void beginDraw(GLGraphicsOffScreen cg) {
		if(!drawStarted) {
			drawStarted = true;
			
			cg.beginDraw();
			
			// GLGraphics will copy the old matrix to the new context, we don't want the old matrix!
			cg.resetMatrix();			
			
			cg.beginGL();
			
			setOptik(g);
			
			// Flip x-axis back as this is already done in GLGraphicsOffScreen 
			offG.gl.glScalef(1.0f, -1.0f, 1.0f);
		}
	}
	
	public void endDraw() {
		endDraw(g);
	}
	
	public void endDraw(GLGraphics cg) {
		if(drawStarted) {
			cg.endGL();
			drawStarted = false;
		}
	}

	public void endDraw(GLGraphicsOffScreen cg) {
		if(drawStarted) {
			cg.endGL();
			cg.endDraw();
			drawStarted = false;
		}
	}
	
	private void drawFirstPass(GLGraphicsOffScreen cg) {
		activateOptik("OffCenter");

		beginDraw(cg);
		
			updateAll((PGraphicsOpenGL) cg);
		
			cg.background(0);

			for(Entry<String, Drawable> dle : drawables.entrySet()) {
				Drawable dl = dle.getValue();
				if(dl.mode() != Drawable.OFF_SCREEN) {
					cg.pushStyle();
					cg.pushMatrix();
						dl.draw();
					cg.popMatrix();
					cg.popStyle();
				}
			}
			
		endDraw(cg);
	}
	
	private void drawOnScreenFirstPass() {
		activateOptik("OffCenter");
		
		beginDraw();
		
			updateAll((PGraphicsOpenGL) g);
		
			g.background(200, 0, 100);

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
			
		endDraw(g);
	}
	
	private void setLights() {
		// First kill all previously set lights!
		g.noLights();
		offG.noLights();
		
		// enable lights
		g.lights();
		g.lightFalloff(1.0f, 0.0f, 0.0f);
//		offG.ambient(200, 0, 0);
		g.pointLight(200*PApplet.sin(p.frameCount/20f), 0, 100*PApplet.sin(p.frameCount/20f), -200, -200, -400);
		g.pointLight(0, 255*PApplet.sin(p.frameCount/110f), 200*PApplet.sin(p.frameCount/110f), 200, 200, -400);
		g.pointLight(0, 0, 170*PApplet.sin(p.frameCount/60f), 500, 300, -800);
//		activeShader.glsl().setVecAttribute("ambientGlobal", 0.6f, 0.0f, 0.0f, 1.0f);
		activeShader.glsl().setVecUniform("ambient", 0.6f, 0.6f, 0.6f, 1.0f);
	}
	
	public void draw() {
		refreshGLG();
		
		initialActiveOptik = activeOptik;
		
		// Force GLGraphics to start with a fresh matrix
		g.resetMatrix();
		
		g.beginGL();
		
			startShader("PolyLightAndTexture");
		
			setLights();
			
			drawOffScreen = true;
				drawFirstPass(offG);
				firstPassResult = offG.getTexture();
			drawOffScreen = false;
			
			stopShader();
			
			activateOptik("Ortho");
			setOptik(g);
			
			g.noLights();
			
			g.background(0);
			
			g.beginShape(PApplet.QUADS);
				g.texture(firstPassResult);
				g.vertex(-(g.width/2), -(g.height/2), -1, 0, 0);
				g.vertex(g.width/2, -(g.height/2), -1, 1, 0);
				g.vertex(g.width/2, g.height/2, -1, 1, 1);
				g.vertex(-(g.width/2), g.height/2, -1, 0, 1);
			g.endShape();

		g.endGL();
				
		activeOptik = initialActiveOptik;
	}
	
}
