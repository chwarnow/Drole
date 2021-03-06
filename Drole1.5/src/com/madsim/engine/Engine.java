package com.madsim.engine;


import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import penner.easing.Quad;
import processing.core.PApplet;
import processing.core.PFont;

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
	
	private float[][] lights		= 	new float[8][10];
	private int activeLights 		= 	0;
	private float[] ambientLight 	= 	new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	
	private float pointSize = 1.0f;
	private boolean usePoints = false;
	
	private GLTexture environmentMap;
	
	public boolean drawStarted = false;
	
	public boolean tweening = false, tweenedIn = false;
	private Drawable tweeningOut, tweeningIn;
	
	private GLModel mog;
	
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
		dl.setName(name);
		this.drawables.put(name, dl);
	}
	
	public void update(String name) {
		Drawable dl = drawables.get(name);
		
		p.pinLog("Drawbale "+dl.name(), dl.mode());
		
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
			activeShader().glsl().setIntUniform("numLights", activeLights);
			activeShader().glsl().setVecUniform("ambient", ambientLight[0], ambientLight[1], ambientLight[2], ambientLight[3]);
		}
	}
	
	public void usePoints() {
		usePoints = true;
	}
	
	public void setPointSize(float ps) {
		activeShader().glsl().setFloatUniform("pointSize", ps);
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
			activeShader().glsl().setFloatUniform("pointSize", 0.0f);
			if(usePoints) {
				gl.glEnable(GL.GL_POINT_SPRITE);
				gl.glEnable(GL.GL_VERTEX_PROGRAM_POINT_SIZE);
				gl.glDepthMask(false);
			} else {
				gl.glDisable(GL.GL_POINT_SPRITE);
				gl.glDisable(GL.GL_VERTEX_PROGRAM_POINT_SIZE);
				gl.glDepthMask(true);
			}
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
	
	private void resetLights() {
		lights = new float[8][10];
		lights[0] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		lights[1] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		lights[2] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		lights[3] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		lights[4] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		lights[5] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		lights[6] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		lights[7] = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}
	
	private void setupLights(Drawable d) {
		// TODO: set that globally
		/*
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
			
		*/
		/*
		g.pushMatrix();
				g.translate(basicLightPosition.x - 500, basicLightPosition.y + 100, basicLightPosition.z + 450);
				g.lightFalloff(0.5f, 0.01f, 0.0f);
				g.pointLight(255, 255, 255, 0, 0, 0);
				g.noStroke();
				g.fill(255, 255, 255);
				// g.sphere(10);
		g.popMatrix();
		*/
		
		updateTransition();
		
		g.noLights();
		
		if(tweening) {
			g.noLights();
			
			if(tweeningOut.mode() == Drawable.FADING_OUT) {
				p.pinLog("FADING OUT", ambientLight[0]);
					
				if(tweeningOut.usesLights()) lights = tweeningOut.getLights();
					
				ambientLight[0] = PApplet.map(tweeningOut.fade(), 1.0f, 0.0f, tweeningOut.ambient()[0], 0f);
				ambientLight[1] = PApplet.map(tweeningOut.fade(), 1.0f, 0.0f, tweeningOut.ambient()[1], 0f);
				ambientLight[2] = PApplet.map(tweeningOut.fade(), 1.0f, 0.0f, tweeningOut.ambient()[2], 0f);
				ambientLight[3] = PApplet.map(tweeningOut.fade(), 1.0f, 0.0f, 1.0f, 0f);
			}
			
			if(tweeningIn.mode() == Drawable.FADING_IN) {
				p.pinLog("FADING IN", ambientLight[0]);
					
				if(tweeningIn.usesLights()) lights = tweeningIn.getLights();
					
				ambientLight[0] = PApplet.map(tweeningIn.fade(), 0.0f, 1.0f, 0f, tweeningIn.ambient()[0]);
				ambientLight[1] = PApplet.map(tweeningIn.fade(), 0.0f, 1.0f, 0f, tweeningIn.ambient()[1]);
				ambientLight[2] = PApplet.map(tweeningIn.fade(), 0.0f, 1.0f, 0f, tweeningIn.ambient()[2]);
				ambientLight[3] = PApplet.map(tweeningIn.fade(), 0.0f, 1.0f, 0f, 1.0f);
			}
			
		} else {
			if(d.usesLights()) {
				lights = d.getLights();
				ambientLight = d.ambient();
				p.pinLog("UL:"+d.name()+":"+d.mode(), d.ambient()[0]+":"+d.ambient()[1]+":"+d.ambient()[2]);
			}
		}
		
		activeLights = 0;
		
		for(int i = 0; i < lights.length; i++) {
			if(lights[i][9] == 1) {
				g.pushMatrix();
					g.lightFalloff(lights[i][6], lights[i][7], lights[i][8]);
					g.pointLight(lights[i][3], lights[i][4], lights[i][5], lights[i][0], lights[i][1], lights[i][2]);
				g.popMatrix();
				
				activeLights++;
			}
		}
		p.pinLog("Active Lights "+d.name(), activeLights);
		
		g.lights();
		
		setLights();
	}
	
	private void updateTransition() {
		if(tweening) {
			if(tweeningOut.mode() == Drawable.OFF_SCREEN && !tweenedIn) {
				tweeningIn.fadeIn(30);
				tweenedIn = true;
			}
			if(tweeningOut.mode() == Drawable.OFF_SCREEN && tweeningIn.mode() == Drawable.ON_SCREEN) tweening = false;
		}
	}
	
	public void transitionBetweenDrawables(Drawable out, Drawable in) {
		tweening = true;
		tweenedIn = false;
		tweeningOut = out;
		tweeningOut.fadeOut(30);
		
		tweeningIn = in;
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
				
				setupLights(dl);
				
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
		
		ambient(ambientLight[0], ambientLight[1], ambientLight[2]);
		
		useOptik("OffCenter");
		activeOptik().calculate();
		activeOptik().set();
		
		/*
		startShader("JustColor");
		
		// TODO: set that globally
		float basicLightValueX = 586.0f - p.noise(p.frameCount*.005f)*250f;
		float basicLightValueY = 426.0f + p.noise(p.frameCount*.005f + 100)*150f;
		
		PVector basicLightPosition = new PVector(
				PApplet.map(basicLightValueX, 0, g.width, -2000, 2000),
				PApplet.map(basicLightValueY, 0, g.width, -2000, 2000),
				-1600 + p.noise(p.frameCount*.005f)*250f);
		
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
		*/

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

		startShader("RoomShader");
		
			drawContent();
			
		stopShader();
		
		// LOGGING
		if(Settings.DRAW_LOGS) drawLogs();
		
		endDraw();
	}
	
}
