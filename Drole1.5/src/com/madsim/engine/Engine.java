package com.madsim.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.drawable.Drawlist;
import com.madsim.engine.optik.Optik;
import com.madsim.engine.renderpass.ShadowMapPass;
import com.madsim.engine.renderpass.VSMShadowPass;
import com.madsim.engine.shader.Shader;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;

public class Engine {

	public EngineApplet p;
	
	public GLGraphics g;
	
	public GL gl;
	
	private ShadowMapPass shadowPass;
	
	private VSMShadowPass vsmShadowPass;
	
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
	
	private HashMap<String, GLTexture> textures = new HashMap<String, GLTexture>();
	
	private float[][] lights = new float[3][6];
	
	public boolean drawStarted = false;
	
	public Engine(EngineApplet p) {
		this.p = p;
		refreshGLG();
		
		maskTexture = new GLTexture(p, "data/images/drole-mask.png");
		
		p.logLn("[Engine]: Setting rendering defaults.");
		g.smooth();
		
		p.logLn("[Engine]: Initializing Framebuffers ("+g.width+":"+g.height+")");
		offG = new GLGraphicsOffScreen(p, g.width, g.height);
		
		shadowPass = new ShadowMapPass(this);
		
		vsmShadowPass = new VSMShadowPass(this);
	}
	
	public GLTexture requestTexture(String uri) {
		if(!textures.containsKey(uri)) textures.put(uri, new GLTexture(p, uri));
		return textures.get(uri);
	}
	
	/*
	public void model(GLModel model) {
		if(drawOffScreen) offG.model(model);
		else g.model(model);
	}
	*/
	
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
	
	public Shader activeShader() {
		return activeShader;
	}
	
	public void addDrawable(String name, Drawable dl) {
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
	
	public Optik getActiveOptik() {
		return optiks.get(activeOptik); 
	}
	
	private void setOptik(PGraphicsOpenGL cg) {
		getActiveOptik().setG(cg);
		getActiveOptik().calculate();
		getActiveOptik().set();
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
		gl = g.gl;
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
		
			drawContent();
			
		endDraw(g);
	}
	
	public float[] getLightPosition(int p) {
		return lights[p];
	}
	
	public void setLights() {
		// First kill all previously set lights!
		g.noLights();
		offG.noLights();
		
		// enable lights
		g.lights();
		g.lightFalloff(0.8f, 0.0f, 0.0f);

		lights[0] = new float[]{
			PApplet.sin(p.frameCount/100f)*500,
			0,
			PApplet.abs(PApplet.sin(p.frameCount/30f)*500),
			20, 0, 200
		};
		lights[1] = new float[]{200, 200, -400, 0, 120, 100};	
		lights[2] = new float[]{500, 300, -800, 200, 0, 30};

		for(int i = 0; i < lights.length; i++) {
			g.pointLight(lights[i][3], lights[i][4], lights[i][5], lights[i][0], lights[i][1], lights[i][2]);
		}
		
		activeShader.glsl().setVecUniform("ambient", 0.1f, 0.1f, 0.1f, 0.1f);
	}
	
	public boolean isInHintList(short hint, short[] list) {
		for(short s : list) if(s == hint) return true;
		return false;
	}
	
	public void setupModel(GLModel model) {
		// Set texture informations for the shader
		activeShader().glsl().setIntUniform("numTextures", model.getNumTextures());
		for(int i = 0; i < model.getNumTextures(); i++) {
			activeShader().glsl().setTexUniform("texture"+i, model.getTexture(i));
		}
	}
	
	public void drawContent() {
		drawContent(new short[]{ Drawable.CAST_SHADOW, Drawable.RECEIVE_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW });
	}
	
	public void drawContent(short[] shadowHintFilter) {
		g.background(255);

		for(Entry<String, Drawable> dle : drawables.entrySet()) {
			Drawable dl = dle.getValue();
			if(
				dl.mode() != Drawable.OFF_SCREEN && 
				isInHintList(dl.SHADOW_HINT, shadowHintFilter)
			) {
				// Draw
				g.pushStyle();
				g.pushMatrix();
					dl.draw();
				g.popMatrix();
				g.popStyle();
			}
		}
	}
	
	public void drawLights() {
		for(int i = 0; i < lights.length; i++) {
			g.pushStyle();
			g.pushMatrix();
				g.fill(200, 200, 0);
				g.translate(lights[i][0], lights[i][1], lights[i][2]);
				g.sphere(10);
			g.popMatrix();
			g.popStyle();
		}		
	}
	
	public void drawRenderToTexture(int textureID) {
		activateOptik("Ortho");
		setOptik(g);
		
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
		
		initialActiveOptik = activeOptik;
		
		// Force GLGraphics to start with a fresh matrix
		g.resetMatrix();
		
		g.beginGL();
		
//			shadowPass.beginRender();
//			drawRenderToTexture(shadowPass.depthTexture.getTextureID());
			
//			shadowPass.finalizeRender();
//			shadowPass.drawLightsView();
		
		/*
			startShader("PolyLightAndColor");
		
			setLights();
			
			drawOffScreen = true;
				drawFirstPass(offG);
				firstPassResult = offG.getTexture();
			drawOffScreen = false;
			
			stopShader();
			
			drawRenderToTexture(firstPassResult);
		*/
			activateOptik("OffCenter");
			setOptik(g);
			
			startShader("PolyLightAndTexture");
				drawContent(new short[]{ Drawable.CAST_SHADOW, Drawable.RECEIVE_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW });
			stopShader();
		
		g.endGL();
				
		activeOptik = initialActiveOptik;
	}
	
}
