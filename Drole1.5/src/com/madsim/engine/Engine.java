package com.madsim.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.drawable.FilterSets;
import com.madsim.engine.optik.Optik;
import com.madsim.engine.shader.Shader;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;
import drole.settings.Settings;

public class Engine {

	public EngineApplet p;
	
	public GLGraphics g;
	
	public GL gl;
	
//	private ShadowMapPass shadowPass;
	
//	private VSMShadowPass vsmShadowPass;
	
	private PFont logFont;
	
	private HashMap<String, Drawable> drawables = new HashMap<String, Drawable>();
	
	private HashMap<String, Optik> optiks = new HashMap<String, Optik>();
	private String activeOptik;

	private HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private Shader activeShader;
	
	private HashMap<String, GLTexture> textures = new HashMap<String, GLTexture>();
	
	private ArrayList<float[]> lights = new ArrayList<float[]>();
	
	private float[] ambientLight = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	
	private GLTexture environmentMap;
	
	public boolean drawStarted = false;
	
	public Engine(EngineApplet p) {
		this.p = p;
		refreshGLG();
		
		p.logLn("[Engine]: Setting rendering defaults.");
		g.smooth();
		
		logFont = p.createFont("Helvetica", 12);
		
		p.logLn("[Engine]: Initializing Render Passes.");
//		shadowPass = new ShadowMapPass(this);
		
//		vsmShadowPass = new VSMShadowPass(this);
	}
	
	public GLTexture requestTexture(String uri) {
		if(!textures.containsKey(uri)) textures.put(uri, new GLTexture(p, uri));
		return textures.get(uri);
	}
	
	public void addOptik(String name, Optik optik) {
		optiks.put(name, optik);
		p.logLn("[Engine]: New optik '"+name+"' added.");
	}
	
	public void useOptik(String name) {
		activeOptik = name;
	}

	public void addShader(String name, Shader shader) {
		shaders.put(name, shader);
		p.logLn("[Engine]: New shader '"+name+"' added.");
	}
	
	public Shader activeShader() {
		return activeShader;
	}
	
	public void addDrawable(String name, Drawable dl) {
		this.drawables.put(name, dl);
	}
	
	public void update(String name) {
		Drawable dl = drawables.get(name);
		if(
			dl.updateMode() == Drawable.ONANDOFFSCREEN ||
			(dl.updateMode() == Drawable.ONLY_ONSCREEN && dl.mode() != Drawable.OFF_SCREEN)
		) {
			dl.update();
		}
	}
	
	public void updateAll() {
		for(Entry<String, Drawable> dle : drawables.entrySet()) {
			update(dle.getKey());
		}
	}
	
	public Optik activeOptik() {
		return optiks.get(activeOptik); 
	}
	
	public void startShader(String name) {
		if(!shaders.containsKey(name)) p.logErr("[Engine]: Can't find shader '"+name+"'!");
		activeShader = shaders.get(name);
		activeShader.start();
		setLights();
	}
	
	public void stopShader() {
		activeShader.stop();
		activeShader = null;
	}
	
	public void refreshGLG() {
		g = (GLGraphics)p.g;
		gl = g.gl;
	}
	
	public void setLights() {
		if(activeShader() != null && activeShader().lightHint() == Shader.USE_LIGHTS) {
			activeShader().glsl().setIntUniform("numLights", lights.size());
			activeShader().glsl().setVecUniform("ambient", ambientLight[0], ambientLight[1], ambientLight[2], ambientLight[3]);
		}
	}
	
	public void setEnvironment() {
		if(activeShader() != null && activeShader().environmentMapHint() == Shader.NO_ENVIRONMENT_MAP && environmentMap != null) {
			
		}
	}
	
	public boolean isInHintList(short hint, short[] list) {
		for(short s : list) if(s == hint) return true;
		return false;
	}
	
	public void setPixelKnockOut(float k) {
		activeShader().glsl().setFloatUniform("usePixelKnockOut", k);
	}
	
	public void resetShader() {
		if(activeShader() != null) {
			// Set texture informations for the shader
			activeShader().glsl().setIntUniform("numTextures", 0);
		}
	}
	
	public void setupModel(GLModel model) {
		if(activeShader() != null && activeShader().textureHint() == Shader.USE_TEXTURES) {
			// Set texture informations for the shader
			activeShader().glsl().setIntUniform("numTextures", model.getNumTextures());
			for(int i = 0; i < model.getNumTextures(); i++) {
				activeShader().glsl().setTexUniform("texture"+i, model.getTexture(i));
			}
		}
	}
	
	public void pointLight(float r, float g, float b, float x, float y, float z) {
		lights.add(new float[]{r, g, b, x, y, z});
		this.g.pointLight(r, g, b, x, y, z);
	}
	
	public void ambient(float r, float g, float b) {
		ambientLight[0] = r;
		ambientLight[1] = g;
		ambientLight[2] = b;
	}
	
	public void drawLogs() {
		String ao = activeOptik;
		useOptik("Ortho");
		activeOptik().calculate();
		activeOptik().set();
		
		g.noLights();
		g.noStroke();
		g.fill(255, 200);
		g.textFont(logFont);
		
		g.pushMatrix();
		g.translate(-(g.width/2)+30, -(g.height/2)+35, -1);
			for(int i = 0; i < p.logs.size(); i++) {
				g.text(p.logs.get(i), 0, 17*i);
			}
		g.popMatrix();
			
		g.pushMatrix();
		g.translate(200, -(g.height/2)+35, -1);
			int i = 0;
			for(Entry<String, String> es : p.pinLog.entrySet()) {
				g.text(es.getKey(), 0, 17*i);
				g.text(es.getValue(), 250, 17*i);
				i++;
			}
		g.popMatrix();		
		
		useOptik(ao);
	}
	
	public void drawContent() {
		drawContent(FilterSets.All());
	}
	
	public void drawContent(short[] shadowHintFilter) {
		for(Entry<String, Drawable> dle : drawables.entrySet()) {
			Drawable dl = dle.getValue();
			if(
				dl.mode() != Drawable.OFF_SCREEN && 
				isInHintList(dl.SHADOW_HINT, shadowHintFilter)
			) {
				resetShader();
				
				// Draw
				g.pushStyle();
				g.pushMatrix();
					dl.draw();
				g.popMatrix();
				g.popStyle();
			}
		}
	}
	
	public void beginDraw() {
		if(!drawStarted) {
			lights.clear();
			g.beginGL();
			drawStarted = true;
		}
	}
	
	public void endDraw() {
		if(drawStarted) {
			g.endGL();
			drawStarted = false;
		}
	}
	
	public void drawRenderToTexture(int textureID) {
		useOptik("Ortho");
		activeOptik().calculate();
		activeOptik().set();
		
		 gl.glMatrixMode(GL.GL_PROJECTION);
		 gl.glLoadIdentity();
		 gl.glOrtho(0, g.width, g.height, 0, 10, -10);
		 gl.glMatrixMode(GL.GL_MODELVIEW);
		 gl.glLoadIdentity();
			 gl.glColor4f(1,1,1,1);
			 gl.glActiveTexture(GL.GL_TEXTURE0);
			 gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
			 gl.glEnable(GL.GL_TEXTURE_2D);
			 gl.glTranslated(0, 0, -1);
			 gl.glBegin(GL.GL_TRIANGLE_STRIP);
				 gl.glTexCoord2d(1,1); gl.glVertex3f(g.width, 0, 0);
				 gl.glTexCoord2d(1,0); gl.glVertex3f(g.width, g.height, 0);
				 gl.glTexCoord2d(0,1); gl.glVertex3f(0, 0, 0);
				 gl.glTexCoord2d(0,0); gl.glVertex3f(0, g.height, 0);
			 gl.glEnd();
			 gl.glDisable(GL.GL_TEXTURE_2D);
	}
	
	public void draw() {
		refreshGLG();
		
		updateAll();
		
		// Force GLGraphics to start with a fresh matrix
		g.resetMatrix();
		
		beginDraw();
		
		g.background(0);
		
		ambient(0.8f, 0.8f, 0.8f);
		
		useOptik("OffCenter");
		activeOptik().calculate();
		activeOptik().set();
		
		startShader("JustColor");
		
		// TODO: set that globally
		float basicLightValueX = 586.0f - p.noise(p.frameCount*.005f)*250f;
		float basicLightValueY = 426.0f + p.noise(p.frameCount*.005f + 100)*150f;
		
		PVector basicLightPosition = new PVector(
				PApplet.map(basicLightValueX, 0, g.width, -2000, 2000),
				PApplet.map(basicLightValueY, 0, g.width, -2000, 2000),
				-1600 + p.noise(p.frameCount*.005f)*550f);
		
			g.pushMatrix();
				g.translate(basicLightPosition.x, basicLightPosition.y, basicLightPosition.z);
				g.lightFalloff(0.5f, 0.01f, 0.0f);
				pointLight(255, 255, 255, 0, 0, 0);
				g.noStroke();
				g.fill(255, 255, 255);
				// g.sphere(10);
			g.popMatrix();
		
			g.pushMatrix();
				g.translate(basicLightPosition.x, basicLightPosition.y + 700, basicLightPosition.z + 450);
				g.lightFalloff(0.5f, 0.01f, 0.0f);
				pointLight(255, 255, 255, 0, 0, 0);
				g.noStroke();
				g.fill(255, 255, 255);
				//  g.sphere(10);
			g.popMatrix();
			
			g.pushMatrix();
				g.translate(basicLightPosition.x - 500, basicLightPosition.y + 100, basicLightPosition.z + 450);
				g.lightFalloff(0.5f, 0.01f, 0.0f);
				pointLight(255, 255, 255, 0, 0, 0);
				g.noStroke();
				g.fill(255, 255, 255);
				// g.sphere(10);
		g.popMatrix();
		
		stopShader();

		/*
		startShader("PolyLightAndTextureAndEM");
		
			g.pushMatrix();
				g.translate(0, 0, -1000);
				g.noStroke();
				g.fill(200);
				
				g.sphere(200);
			g.popMatrix();
		
		stopShader();
		*/
		
		// edit by chris, testing a shader spinoff
		startShader("PolyLightAndTexture");
//		startShader("JustTexture");
//		startShader("RoomShader");
			
			drawContent();
			
		stopShader();
		
		// LOGGING
		if(Settings.DRAW_LOGS) drawLogs();
		
		endDraw();
	}
	
}
