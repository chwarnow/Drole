package com.madsim.engine.renderpass;


import javax.media.opengl.GL;

import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.optik.LookAt;

public class ShadowMapPass extends RenderPass {
	
	private int SHADOW_MAP_RATIO = 2;
	
	private int[] fboId = { 0 };
	public GLTexture depthTexture;
	
	private double[] modelView = new double[16];
	private double[] projection = new double[16];
	private double[] bias = {	
			0.5, 0.0, 0.0, 0.0,
			0.0, 0.5, 0.0, 0.0,
			0.0, 0.0, 0.5, 0.0,
			0.5, 0.5, 0.5, 1.0
	};
	
	public ShadowMapPass(Engine e) {
		super(e);
		
		generateShadowFBO();
	}

	@Override
	public void beginRender() {
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);
		gl.glViewport(0, 0, g.width * SHADOW_MAP_RATIO, g.height * SHADOW_MAP_RATIO);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glColorMask(false, false, false, false);

		e.activateOptik("LookAt");
		LookAt la = (LookAt)e.getActiveOptik();
		la.calculate(e.getLightPosition(0)[0], e.getLightPosition(0)[1], e.getLightPosition(0)[2], 0, 0, -1000, 60);
		la.set();

		gl.glCullFace(GL.GL_BACK);

		e.startShader("JustColor");
			e.drawContent(new short[]{ Drawable.CAST_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW });
		e.stopShader();

		setTextureMatrix();
		
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
		gl.glViewport(0, 0, g.width, g.height);
		gl.glColorMask(true, true, true, true);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void finalizeRender() {
		e.activateOptik("OffCenter");
		e.getActiveOptik().calculate();
		e.getActiveOptik().set();		
		
		e.startShader("PolyLightAndTexture");
		e.setLights();
	
//			e.p.logLn(depthTexture.getTextureID());
		
//			e.activeShader().glsl().setFloatUniform("xPixelOffset", 1.0f / (g.width * SHADOW_MAP_RATIO));
//			e.activeShader().glsl().setFloatUniform("yPixelOffset", 1.0f / (g.width * SHADOW_MAP_RATIO));
//			e.activeShader().glsl().setTexUniform("ShadowMap", depthTexture.getTextureID());
	
//			gl.glActiveTexture(GL.GL_TEXTURE0+depthTexture.getTextureID());
//			gl.glBindTexture(GL.GL_TEXTURE_2D, depthTexture.getTextureID());
	
			gl.glCullFace(GL.GL_FRONT);
	
			e.drawContent(new short[]{ Drawable.RECEIVE_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW });
	
		e.stopShader();
	}

	public void drawLightsView() {
		e.activateOptik("LookAt");
		LookAt la = (LookAt)e.getActiveOptik();
		la.calculate(e.getLightPosition(0)[0], e.getLightPosition(0)[1], e.getLightPosition(0)[2], 0, 0, -1000, 60);
		la.set();

		e.startShader("PolyLightAndTexture");
		e.setLights();
			
			gl.glCullFace(GL.GL_FRONT);
			
			e.drawContent(new short[]{ Drawable.CAST_SHADOW, Drawable.CAST_AND_RECEIVE_SHADOW });
			
		e.stopShader();
	}
	
	public void generateShadowFBO() {
		int shadowMapWidth = g.width * SHADOW_MAP_RATIO;
		int shadowMapHeight = g.height * SHADOW_MAP_RATIO;

		int FBOstatus;
		
		depthTexture = new GLTexture(e.p, shadowMapWidth, shadowMapHeight);
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, depthTexture.getTextureID());
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_COMPARE_MODE, GL.GL_COMPARE_R_TO_TEXTURE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_DEPTH_TEXTURE_MODE, GL.GL_INTENSITY);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		
		gl.glGenFramebuffersEXT(1, fboId, 0);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);
		gl.glDrawBuffer(GL.GL_NONE);
		gl.glReadBuffer(GL.GL_NONE);
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_TEXTURE_2D, depthTexture.getTextureID(), 0);
		FBOstatus = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		if(FBOstatus != GL.GL_FRAMEBUFFER_COMPLETE_EXT) e.p.logErr("[ShadowPass]: GL_FRAMEBUFFER_COMPLETE_EXT failed, CANNOT use FBO\n");

		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	}

	public void setTextureMatrix() {
		  gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelView, 0);
		  gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
		  gl.glMatrixMode(GL.GL_TEXTURE);
		  gl.glActiveTexture(GL.GL_TEXTURE7);
		  gl.glLoadIdentity();	
		  gl.glLoadMatrixd(bias, 0);  
		  gl.glMultMatrixd (projection, 0);
		  gl.glMultMatrixd (modelView, 0);
		  gl.glMatrixMode(GL.GL_MODELVIEW);
	}	
	
}
