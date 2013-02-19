package drole.tests.vsmshadow;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class ShadowTest extends PApplet {

	private static final long serialVersionUID = 1L;

	private float RENDER_WIDTH		=	1000.0f;
	private float RENDER_HEIGHT		=	 700.0f;
	private float SHADOW_MAP_COEF 	=	   2.0f;
	private float BLUR_COEF 		= 	  0.25f;

	private GLGraphics renderer;
	private GL gl;
	private GLU glu;
	private GLUT glut;

	//Camera movement circle radius
	float cam_mvnt = 35.0f;
	
	//Camera position
	float p_camera[] = {35, 25, 5, 1};

	//Camera lookAt
	float l_camera[] = {0, 0, -10};

	//Light position
	float p_light[] = {3, 18, 0, 1};

	//Light lookAt
	float l_light[] = {0, 0, -5};


	//Light movement circle radius
	float light_mvnt = 35.0f;

	// Hold id of the framebuffer for light POV rendering
	int[] fboId = { 0 };
	// Z values will be rendered to this texture when using fboId framebuffer
	int[] depthTextureId = { 0 };
	int[] colorTextureId = { 0 };

	/*
	// Use to activate/disable shadowShader
	GLhandleARB shadowShaderId;
	GLuint shadowMapUniform;
	GLuint shadowMapStepXUniform;
	GLuint shadowMapStepYUniform;

	// Used to store values during the first pass
	GLuint storeMomentsShader;
	*/
	
	// Bluring FBO
	int[] blurFboId = { 0 };
	// Z values will be rendered to this texture when using fboId framebuffer
	int[] blurFboIdColorTextureId = { 0 };

	/*
	// Used to blur the depth values
	GLuint blurShader;
	GLuint scaleUniform; // Used to pass blur horiz or vert
	GLuint textureSourceUniform;	
	*/
	
	private String dataPath = "drole/tests/vsmshadow/";
	
	private GLSLShader composeShader;
	private GLSLShader storeDepthShader;
	private GLSLShader blurShader;
	
	private double[] modelView=new double[16];
	private double[] projection=new double[16];
	private double[] bias = {	
	  0.5, 0.0, 0.0, 0.0, 
	  0.0, 0.5, 0.0, 0.0,
	  0.0, 0.0, 0.5, 0.0,
	  0.5, 0.5, 0.5, 1.0
	};	
	
	public void setup() {
		  size((int)RENDER_WIDTH, (int)RENDER_HEIGHT, GLConstants.GLGRAPHICS);

		  renderer = (GLGraphics)g;
		  gl=renderer.gl;
		  glu=renderer.glu;
		  glut=new GLUT();
		  
		  composeShader = new GLSLShader(this, dataPath+"VertexShader.glsl", dataPath+"FragmentShader.glsl");
		  storeDepthShader = new GLSLShader(this, dataPath+"StoreDepthVertexShader.glsl", dataPath+"StoreDepthFragmentShader.glsl");
		  blurShader = new GLSLShader(this, dataPath+"BlurVertexShader.glsl", dataPath+"BlurFragmentShader.glsl");
		  
		  renderer.beginGL();

		  generateShadowFBO();
		  
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glClearColor(0,0,0,1.0f);
			gl.glEnable(GL.GL_CULL_FACE);
			gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
			gl.glEnable(GL.GL_POLYGON_SMOOTH);
			gl.glEnable(GL.GL_LINE_SMOOTH);
			gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
			gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
			
		renderer.endGL();
	}
	
	/*
	public void draw() {
		  renderer.beginGL();
		  
		  	g.translate(width-300, height, -1500);
		  
		  	renderScene();
		  	
		  renderer.endGL();
	}
	
	private void renderScene() {
		g.background(0);
		
		g.pushStyle();
		g.pushMatrix();
		
			g.rotateX(radians(80));
			g.rotateZ(radians(50));
			
			g.stroke(100);
			g.fill(243, 243, 225);
			g.box(3000, 3000, 5);
			
		g.popMatrix();
		g.popStyle();
		
		g.pushStyle();
		g.pushMatrix();
		
			g.translate(-600, -100, 0);
			g.rotateX(radians(80));
			g.rotateZ(radians(50));
			
			g.noStroke();
			g.fill(200);
			g.box(200);
			
		g.popMatrix();
		g.popStyle();
		
		g.pushStyle();
		g.pushMatrix();
		
			g.translate(900, 700, 400);
			
			g.noStroke();
			g.fill(200);
			g.sphere(400);
			
		g.popMatrix();
		g.popStyle();		
	}
	*/
	
	// During translation, we also have to maintain the GL_TEXTURE8, used in the shadow shader
	// to determine if a vertex is in the shadow.
	void startTranslate(float x,float y,float z) {
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
		int shadowMapWidth = (int) (RENDER_WIDTH * SHADOW_MAP_COEF);
		int shadowMapHeight = (int) (RENDER_HEIGHT * SHADOW_MAP_COEF);
		
		//GLfloat borderColor[4] = {0,0,0,0};
		
		int FBOstatus;
		
		// Try to use a texture depth component
		gl.glGenTextures(1, depthTextureId, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, depthTextureId[0]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		
		// Remove artefact on the edges of the shadowmap
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP );
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP );
		
		// No need to force GL_DEPTH_COMPONENT24, drivers usually give you the max precision if available 
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		gl.glGenTextures(1, colorTextureId, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, colorTextureId[0]);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
		
//		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
		

		// Remove artefact on the edges of the shadowmap
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP );
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP );

		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB16F_ARB, shadowMapWidth, shadowMapHeight, 0, GL.GL_RGB, GL.GL_FLOAT, null);
		gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);



		// create a framebuffer object
		gl.glGenFramebuffersEXT(1, fboId, 0);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);
		
		// attach the texture to FBO depth attachment point
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT , GL.GL_TEXTURE_2D, depthTextureId[0], 0);
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, colorTextureId[0], 0);

		// check FBO status
		FBOstatus = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		if(FBOstatus != GL.GL_FRAMEBUFFER_COMPLETE_EXT) println("GL_FRAMEBUFFER_COMPLETE_EXT failed for shadowmap FBO, CANNOT use FBO\n");
		
		// switch back to window-system-provided framebuffer
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);


		// Creating the blur FBO
		gl.glGenFramebuffersEXT(1, blurFboId, 0);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, blurFboId[0]);

		gl.glGenTextures(1, blurFboIdColorTextureId, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, blurFboIdColorTextureId[0]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB16F_ARB, (int)(shadowMapWidth*BLUR_COEF), (int)(shadowMapHeight*BLUR_COEF), 0, GL.GL_RGB, GL.GL_FLOAT, null);

		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT,GL.GL_TEXTURE_2D, blurFboIdColorTextureId[0], 0);
		FBOstatus = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		if(FBOstatus != GL.GL_FRAMEBUFFER_COMPLETE_EXT) println("GL_FRAMEBUFFER_COMPLETE_EXT failed for blur FBO, CANNOT use FBO\n");

		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	}
	
	private void setupMatrices(float position_x,float position_y,float position_z,float lookAt_x,float lookAt_y,float lookAt_z) {
		  gl.glMatrixMode(GL.GL_PROJECTION);
		  gl.glLoadIdentity();
		  glu.gluPerspective(45, RENDER_WIDTH/(float)RENDER_HEIGHT, 10, 40000);
		  gl.glMatrixMode(GL.GL_MODELVIEW);
		  gl.glLoadIdentity();
		  glu.gluLookAt(position_x,position_y,position_z,lookAt_x,lookAt_y,lookAt_z,0,1,0);
	}

	private void setTextureMatrix() {
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
	
	private void drawObjects() {
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
		startTranslate(0 , 8, -40);
		glut.glutSolidCube(4);
		endTranslate();

		gl.glColor4f(0.0f, 0.8f, 0.8f, 1);
		
		startTranslate(-8, 4.1f, -16);
		glut.glutSolidCube(4);
		endTranslate();

		startTranslate(8,4.1f,-16);
		glut.glutSolidCube(4);
		endTranslate();
		
		gl.glColor4f(0.8f, 0.0f, 0.8f, 1);
		
		startTranslate(0, 8, -5);
		//glutSolidCube(4);
		glut.glutSolidSphere(5,5,5);
		endTranslate();
		
		startTranslate(20, 8, -50);
		//glutSolidCube(4);
		glut.glutSolidSphere(5,5,5);
		endTranslate();		
	}

	private void blurShadowMap() {
		//glDisable(GL_DEPTH_TEST);
		//glDisable(GL_CULL_FACE);
		
		// Bluring the shadow map  horinzontaly
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, blurFboId[0]);
		
		gl.glViewport(0,0, (int)(RENDER_WIDTH * SHADOW_MAP_COEF *BLUR_COEF), (int)(RENDER_HEIGHT* SHADOW_MAP_COEF*BLUR_COEF));
		
		blurShader.start();
		blurShader.setVecUniform("ScaleU", 1.0f / (RENDER_WIDTH * SHADOW_MAP_COEF * BLUR_COEF), 0.0f);
		blurShader.setIntUniform("textureSource", 0);
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, colorTextureId[0]);

			//Preparing to draw quad
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-RENDER_WIDTH/2,RENDER_WIDTH/2,-RENDER_HEIGHT/2,RENDER_HEIGHT/2,1,20);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

			 //Drawing quad 
		gl.glTranslated(0,0,-5);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(0,0); gl.glVertex3f(-RENDER_WIDTH/2,-RENDER_HEIGHT/2,0);
			gl.glTexCoord2d(1,0); gl.glVertex3f(RENDER_WIDTH/2,-RENDER_HEIGHT/2,0);
			gl.glTexCoord2d(1,1); gl.glVertex3f(RENDER_WIDTH/2,RENDER_HEIGHT/2,0);
			gl.glTexCoord2d(0,1); gl.glVertex3f(-RENDER_WIDTH/2,RENDER_HEIGHT/2,0);
		gl.glEnd();
//		gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
			
			 
			 // Bluring vertically
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);	
		gl.glViewport(0,0,(int)(RENDER_WIDTH * SHADOW_MAP_COEF), (int)(RENDER_HEIGHT* SHADOW_MAP_COEF));
		
		blurShader.setVecUniform("ScaleU", 0.0f, 1.0f / (RENDER_HEIGHT * SHADOW_MAP_COEF ));	
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, blurFboIdColorTextureId[0]);
		
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(0,0); gl.glVertex3f(-RENDER_WIDTH/2,-RENDER_HEIGHT/2,0);
			gl.glTexCoord2d(1,0); gl.glVertex3f(RENDER_WIDTH/2,-RENDER_HEIGHT/2,0);
			gl.glTexCoord2d(1,1); gl.glVertex3f(RENDER_WIDTH/2,RENDER_HEIGHT/2,0);
			gl.glTexCoord2d(0,1); gl.glVertex3f(-RENDER_WIDTH/2,RENDER_HEIGHT/2,0);
		gl.glEnd();
			
	}

	
	// This update only change the position of the light.
	//int elapsedTimeCounter = 0;
	private void update() {
		//printf("%d\n",glutGet(GLUT_ELAPSED_TIME));
		p_light[0] = light_mvnt * cos(frameCount/1000.0f);
		p_light[2] = light_mvnt * sin(frameCount/1000.0f);

//		p_camera[0] = -light_mvnt * cos(frameCount/1800.0f);
//		p_camera[2] = -light_mvnt * sin(frameCount/1800.0f);
		
		//p_light[0] = light_mvnt * cos(4000/1000.0);
		//p_light[2] = light_mvnt * sin(4000/1000.0);

		//p_light[0] = light_mvnt * cos(3000/1000.0);
		//p_light[2] = light_mvnt * sin(3000/1000.0);
	}
	
	public void draw() {
		update();
		
		renderer.beginGL();
		
		/*
		setupMatrices(p_light[0],p_light[1],p_light[2],l_light[0],l_light[1],l_light[2]);
		
		drawObjects();
		*/
		
		//First step: Render from the light POV to a FBO, store depth and square depth in a 32F frameBuffer
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);	//Rendering offscreen
		
		//Using the custom shader to do so
		storeDepthShader.start();
		
		// In the case we render the shadowmap to a higher resolution, the viewport must be modified accordingly.
		gl.glViewport(0, 0, (int)(RENDER_WIDTH * SHADOW_MAP_COEF), (int)(RENDER_HEIGHT* SHADOW_MAP_COEF));
		
		// Clear previous frame values
		gl.glClear(GL.GL_COLOR_BUFFER_BIT |  GL.GL_DEPTH_BUFFER_BIT);
			
		setupMatrices(p_light[0],p_light[1],p_light[2],l_light[0],l_light[1],l_light[2]);
		
		// Culling switching, rendering only backface, this is done to avoid self-shadowing
//		gl.glCullFace(GL.GL_FRONT);
		
		drawObjects();
		
//		gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
		//Save modelview/projection matrice into texture7, also add a biais
		setTextureMatrix();
		
		storeDepthShader.stop();
		
		blurShadowMap();

		// Now rendering from the camera POV, using the FBO to generate shadows
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
		
		gl.glViewport(0, 0, (int)RENDER_WIDTH, (int)RENDER_HEIGHT);
			
		// Clear previous frame values
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		
		// DEBUG only. this piece of code draw the depth buffer onscreen
				 gl.glMatrixMode(GL.GL_PROJECTION);
				 gl.glLoadIdentity();
				 gl.glOrtho(0, RENDER_WIDTH, RENDER_HEIGHT, 0, 10 , -10);
				 gl.glMatrixMode(GL.GL_MODELVIEW);
				 gl.glLoadIdentity();
				 gl.glColor4f(1,1,1,1);
				 gl.glActiveTexture(GL.GL_TEXTURE0);
				 gl.glBindTexture(GL.GL_TEXTURE_2D, depthTextureId[0]);
				 gl.glEnable(GL.GL_TEXTURE_2D);
				 gl.glTranslated(0, 0, -1);
				 gl.glBegin(GL.GL_TRIANGLE_STRIP);
				 gl.glTexCoord2d(1,1); gl.glVertex3f(RENDER_WIDTH, 0, 0);
				 gl.glTexCoord2d(1,0); gl.glVertex3f(RENDER_WIDTH, RENDER_HEIGHT, 0);
				 gl.glTexCoord2d(0,1); gl.glVertex3f(0, 0, 0);
				 gl.glTexCoord2d(0,0); gl.glVertex3f(0, RENDER_HEIGHT, 0);
				 gl.glEnd();
				 gl.glDisable(GL.GL_TEXTURE_2D);
		
		/*
		//Using the shadow shader
		composeShader.start();
		
		composeShader.setTexUniform("ShadowMap", 7);
		composeShader.setFloatUniform("xPixelOffset", 1.0f / (RENDER_WIDTH * SHADOW_MAP_COEF));
		composeShader.setFloatUniform("yPixelOffset", 1.0f / (RENDER_HEIGHT * SHADOW_MAP_COEF));
		
		gl.glActiveTexture(GL.GL_TEXTURE7);
		gl.glBindTexture(GL.GL_TEXTURE_2D, depthTextureId[0]);
		
		setupMatrices(p_camera[0],p_camera[1],p_camera[2],l_camera[0],l_camera[1],l_camera[2]);

		
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, p_light, 0);

//		gl.glCullFace(GL.GL_BACK);
		
		drawObjects();
		
		
		composeShader.stop();
		*/
		
		renderer.endGL();
	}
	
	public void mouseDragged(MouseEvent e) {
		p_camera[0] = 100f*sin(map(e.getX(), 0, width, -1, 1));
		p_camera[1] = 100f*sin(map(e.getY(), 0, height, -1, 1));
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
			"drole.tests.vsmshadow.ShadowTest"
		});
	}
	
}
