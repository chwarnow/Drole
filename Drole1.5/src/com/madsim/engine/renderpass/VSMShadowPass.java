package com.madsim.engine.renderpass;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;

import processing.core.PApplet;

import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.FilterSets;
import com.madsim.engine.optik.LookAt;
import com.madsim.engine.shader.Shader;
import com.madsim.engine.shader.VSMBlurShader;
import com.madsim.engine.shader.VSMComposeShader;
import com.madsim.engine.shader.VSMStoreDepthShader;

public class VSMShadowPass extends RenderPass implements KeyListener {

	private float SHADOW_MAP_COEF 	=	   2.0f;
	private float BLUR_COEF 		= 	  0.25f;

	//Camera movement circle radius
	float cam_mvnt = 35.0f;
	
	//Camera position
	float p_camera[] = {35, 25, 5, 1};

	//Camera lookAt
	float l_camera[] = {0, 0, -10};

	//Light position
	float p_light[] = {0, 0, 1000};

	//Light lookAt
	float l_light[] = {0, 0, -1000};

	//Light movement circle radius
	float light_mvnt = 35.0f;
	
	float light_angle = 45;

	// Hold id of the framebuffer for light POV rendering
	int[] fboId = { 0 };
	// Z values will be rendered to this texture when using fboId framebuffer
	public GLTexture depthTexture;
	public GLTexture colorTexture;
	
	// Bluring FBO
	int[] blurFboId = { 0 };
	
	// Z values will be rendered to this texture when using fboId framebuffer
	public GLTexture blurFboIdColorTexture;
	
	private double[] modelView = new double[16];
	private double[] projection = new double[16];
	private double[] bias = {	
		0.5, 0.0, 0.0, 0.0, 
		0.0, 0.5, 0.0, 0.0,
		0.0, 0.0, 0.5, 0.0,
		0.5, 0.5, 0.5, 1.0
	};	
	
	public VSMShadowPass(Engine e) {
		super(e);

		e.addShader("VSMCompose", new VSMComposeShader(e.p));
		e.addShader("VSMStoreDepth", new VSMStoreDepthShader(e.p));
		e.addShader("VSMBlur", new VSMBlurShader(e.p));
		
		generateShadowFBO();
		
		e.p.addKeyListener(this);
	}
	
	// During translation, we also have to maintain the GL_TEXTURE8, used in the shadow shader
	// to determine if a vertex is in the shadow.
	void startTranslate(float x, float y, float z) {
		gl.glPushMatrix();
		gl.glTranslatef(x,y,z);
		
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glActiveTexture(GL.GL_TEXTURE7);
		gl.glPushMatrix();
		gl.glTranslatef(x,y,z);
	}

	void endTranslate() {
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPopMatrix();
	}
	
	private void generateShadowFBO() {
		int shadowMapWidth = (int) (g.width * SHADOW_MAP_COEF);
		int shadowMapHeight = (int) (g.height * SHADOW_MAP_COEF);
		
		int FBOstatus;
		
		// Try to use a texture depth component
		depthTexture = new GLTexture(e.p, shadowMapWidth, shadowMapHeight);
		gl.glBindTexture(GL.GL_TEXTURE_2D, depthTexture.getTextureID());
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		
		// Remove artefacts on the edges of the shadowmaps
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP );
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP );
		
		// No need to force GL_DEPTH_COMPONENT24, drivers usually give you the max precision if available 
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		// Enable MipMapping
		colorTexture = new GLTexture(e.p, shadowMapWidth, shadowMapHeight);
		gl.glBindTexture(GL.GL_TEXTURE_2D, colorTexture.getTextureID());
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);		

		// Remove artefact on the edges of the shadowmap
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP );
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP );

		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB16F_ARB, shadowMapWidth, shadowMapHeight, 0, GL.GL_RGB, GL.GL_FLOAT, null);

		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		// create a framebuffer object
		gl.glGenFramebuffersEXT(1, fboId, 0);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);
		
		// attach the texture to FBO depth attachment point
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT , GL.GL_TEXTURE_2D, depthTexture.getTextureID(), 0);
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, colorTexture.getTextureID(), 0);

		// check FBO status
		FBOstatus = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		if(FBOstatus != GL.GL_FRAMEBUFFER_COMPLETE_EXT) e.p.logLn("GL_FRAMEBUFFER_COMPLETE_EXT failed for shadowmap FBO, CANNOT use FBO\n");
		
		// switch back to window-system-provided framebuffer
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

		// Creating the blur FBO
		gl.glGenFramebuffersEXT(1, blurFboId, 0);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, blurFboId[0]);

		blurFboIdColorTexture = new GLTexture(e.p, shadowMapWidth, shadowMapHeight);
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, blurFboIdColorTexture.getTextureID());
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB16F_ARB, (int)(shadowMapWidth*BLUR_COEF), (int)(shadowMapHeight*BLUR_COEF), 0, GL.GL_RGB, GL.GL_FLOAT, null);

		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT,GL.GL_TEXTURE_2D, blurFboIdColorTexture.getTextureID(), 0);
		
		FBOstatus = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		if(FBOstatus != GL.GL_FRAMEBUFFER_COMPLETE_EXT) e.p.logLn("GL_FRAMEBUFFER_COMPLETE_EXT failed for blur FBO, CANNOT use FBO\n");

		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	}

	private void setTextureMatrix() {
		  gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelView, 0);
		  gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
		  gl.glMatrixMode(GL.GL_TEXTURE);
		  gl.glActiveTexture(GL.GL_TEXTURE0 + depthTexture.getTextureID());
		  gl.glLoadIdentity();	
		  gl.glLoadMatrixd(bias, 0);  
		  gl.glMultMatrixd (projection, 0);
		  gl.glMultMatrixd (modelView, 0);
		  gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	private void blurShadowMap() {
		// Bluring the shadow map  horinzontaly
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, blurFboId[0]);
		
		gl.glViewport(0,0, (int)(g.width * SHADOW_MAP_COEF * BLUR_COEF), (int)(g.height * SHADOW_MAP_COEF * BLUR_COEF));
		
		e.startShader("VSMBlur");
		
			e.activeShader().glsl().setVecUniform("ScaleU", 1.0f / (g.width * SHADOW_MAP_COEF * BLUR_COEF), 0.0f);
			e.activeShader().glsl().setIntUniform("textureSource", 0);
			
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, colorTexture.getTextureID());
	
				//Preparing to draw quad
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(-g.width/2, g.width/2, -g.height/2, g.height/2, 1, 20);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
	
			//Drawing quad 
			gl.glTranslated(0,0,-5);
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(0,0); gl.glVertex3f(-g.width/2, -g.height/2, 0);
				gl.glTexCoord2d(1,0); gl.glVertex3f(g.width/2, -g.height/2, 0);
				gl.glTexCoord2d(1,1); gl.glVertex3f(g.width/2, g.height/2, 0);
				gl.glTexCoord2d(0,1); gl.glVertex3f(-g.width/2, g.height/2, 0);
			gl.glEnd();
	
			// Bluring vertically
			gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);
			gl.glViewport(0,0,(int)(g.width * SHADOW_MAP_COEF), (int)(g.height * SHADOW_MAP_COEF));
			
			e.activeShader().glsl().setVecUniform("ScaleU", 0.0f, 1.0f / (g.height * SHADOW_MAP_COEF));	
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, blurFboIdColorTexture.getTextureID());
			
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(0,0); gl.glVertex3f(-g.width/2, -g.height/2, 0);
				gl.glTexCoord2d(1,0); gl.glVertex3f(g.width/2, -g.height/2, 0);
				gl.glTexCoord2d(1,1); gl.glVertex3f(g.width/2, g.height/2, 0);
				gl.glTexCoord2d(0,1); gl.glVertex3f(-g.width/2, g.height/2, 0);
			gl.glEnd();
		
		e.stopShader();
	}

	
	// This update only change the position of the light.
	//int elapsedTimeCounter = 0;
	private void update() {
		//printf("%d\n",glutGet(GLUT_ELAPSED_TIME));
//		p_light[0] = light_mvnt * PApplet.cos(e.p.frameCount/1000.0f);
//		p_light[2] = light_mvnt * PApplet.sin(e.p.frameCount/1000.0f);

//		p_camera[0] = -light_mvnt * cos(frameCount/1800.0f);
//		p_camera[2] = -light_mvnt * sin(frameCount/1800.0f);
		
		//p_light[0] = light_mvnt * cos(4000/1000.0);
		//p_light[2] = light_mvnt * sin(4000/1000.0);

		//p_light[0] = light_mvnt * cos(3000/1000.0);
		//p_light[2] = light_mvnt * sin(3000/1000.0);
	}

	private void drawObjects() {
		
		g.pushMatrix();
		g.pushStyle();
			g.translate(0, 0, 900);
			g.fill(200, 0, 0);
			g.sphere(10);
		g.popMatrix();
		g.popStyle();
		
		/*
		// Ground
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1);
		
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
			gl.glVertex3f(-100, 2, -100);
			gl.glVertex3f(100, 2,  -100);
			gl.glVertex3f(-100, 2,  0);
			gl.glVertex3f(100, 2, 0);
		gl.glEnd();
		
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1);
		
		// Instead of calling glTranslatef, we need a custom function that also maintain the light matrix
//		startTranslate(0 , 8, -40);
		glut.glutSolidCube(4);
//		endTranslate();

		gl.glColor4f(0.0f, 0.8f, 0.8f, 1);
		
//		startTranslate(-8, 4.1f, -16);
		glut.glutSolidCube(4);
//		endTranslate();

//		startTranslate(8,4.1f,-16);
		glut.glutSolidCube(4);
//		endTranslate();
		
		gl.glColor4f(0.8f, 0.0f, 0.8f, 1);
		
//		startTranslate(0, 8, -5);
		//glutSolidCube(4);
		glut.glutSolidSphere(5,5,5);
//		endTranslate();
		
//		startTranslate(20, 8, -50);
		//glutSolidCube(4);
		glut.glutSolidSphere(5,5,5);
//		endTranslate();		
 */
	}
	
	/*
	private void setupMatrices(float position_x,float position_y,float position_z,float lookAt_x,float lookAt_y,float lookAt_z) {
		  gl.glMatrixMode(GL.GL_PROJECTION);
		  gl.glLoadIdentity();
		  glu.gluPerspective(45, g.width/(float)g.height, 10, 40000);
		  gl.glMatrixMode(GL.GL_MODELVIEW);
		  gl.glLoadIdentity();
		  glu.gluLookAt(position_x,position_y,position_z,lookAt_x,lookAt_y,lookAt_z,0,1,0);
	}
	*/
	
	private void altRender() {
		update();

		//First step: Render from the light POV to a FBO, store depth and square depth in a 32F frameBuffer
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);	//Rendering offscreen
		
		// In the case we render the shadowmap to a higher resolution, the viewport must be modified accordingly.
		gl.glViewport(0, 0, (int)(g.width * SHADOW_MAP_COEF), (int)(g.height* SHADOW_MAP_COEF));
		
		// Clear previous frame values
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT |  GL.GL_DEPTH_BUFFER_BIT);
	
		e.activateOptik("LookAt");
		LookAt la = (LookAt)e.getActiveOptik();
		la.calculate(p_light[0], p_light[1], p_light[2], l_light[0], l_light[1], l_light[2], light_angle);
		la.set();
		
		//Using the custom shader to do so
		e.startShader("VSMStoreDepth");
			drawObjects();
			e.drawContent(FilterSets.All());
		e.stopShader();
		
//		gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
		//Save modelview/projection matrice into texture7, also add a biais
		setTextureMatrix();
		
		blurShadowMap();

		// Now rendering from the camera POV, using the FBO to generate shadows
		
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
		
		gl.glViewport(0, 0, (int)g.width, (int)g.height);
			
		// Clear previous frame values
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		/*
		// DEBUG only. this piece of code draw the depth buffer onscreen
		 gl.glMatrixMode(GL.GL_PROJECTION);
		 gl.glLoadIdentity();
		 gl.glOrtho(0, g.width, g.height, 0, 10 , -10);
		 gl.glMatrixMode(GL.GL_MODELVIEW);
		 gl.glLoadIdentity();
		 gl.glColor4f(1,1,1,1);
		 gl.glActiveTexture(GL.GL_TEXTURE0);
		 gl.glBindTexture(GL.GL_TEXTURE_2D, colorTexture.getTextureID());
		 gl.glEnable(GL.GL_TEXTURE_2D);
		 gl.glTranslated(0, 0, -1);
		 gl.glBegin(GL.GL_TRIANGLE_STRIP);
		 gl.glTexCoord2d(1,1); gl.glVertex3f(g.width, 0, 0);
		 gl.glTexCoord2d(1,0); gl.glVertex3f(g.width, g.height, 0);
		 gl.glTexCoord2d(0,1); gl.glVertex3f(0, 0, 0);
		 gl.glTexCoord2d(0,0); gl.glVertex3f(0, g.height, 0);
		 gl.glEnd();
		 gl.glDisable(GL.GL_TEXTURE_2D);
		*/
		
		gl.glActiveTexture(GL.GL_TEXTURE0 + depthTexture.getTextureID());
		gl.glBindTexture(GL.GL_TEXTURE_2D, depthTexture.getTextureID());
		
		e.activateOptik("OffCenter");
		e.getActiveOptik().calculate();
		e.getActiveOptik().set();
		
//		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, p_light, 0);

//		gl.glCullFace(GL.GL_BACK);
		
		//Using the shadow shader
		e.startShader("VSMCompose");
		
			e.activeShader().glsl().setTexUniform("ShadowMap", depthTexture.getTextureID());
			e.activeShader().glsl().setIntUniform("shadowTexCoord", depthTexture.getTextureID());
			e.activeShader().glsl().setFloatUniform("xPixelOffset", 1.0f / (g.width * SHADOW_MAP_COEF));
			e.activeShader().glsl().setFloatUniform("yPixelOffset", 1.0f / (g.height * SHADOW_MAP_COEF));
			
			e.drawContent(FilterSets.All());
		
		e.stopShader();
	}
	
	@Override
	public void beginRender() {
		gl.glEnable(GL.GL_CULL_FACE);
		
		altRender();
	}

	@Override
	public void finalizeRender() {}

	@Override
	public void keyPressed(KeyEvent ee) {
		if(ee.getKeyCode() == 38) {
			p_light[2] -= 10;
			l_light[2] -= 10;
		}
		if(ee.getKeyCode() == 40) {
			p_light[2] += 10;
			l_light[2] += 10;
		}
		if(ee.getKeyCode() == 37) {
			light_angle += 10;
		}
		if(ee.getKeyCode() == 39) {
			light_angle -= 10;
		}		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
