package drole.tests.shadow;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLSLShader;
import processing.core.PApplet;

public class ShadowTextureTest extends PApplet {

	private static final long serialVersionUID = 1L;

	int RENDER_WIDTH=1280;
	int RENDER_HEIGHT=960;

	int SHADOW_MAP_RATIO=2; // adjust the quality of shadow with this parameter

	int nw=3; // number of blocks in x direction
	int nh=3; // number of blocks in y direction
	int xspread=1800; //interval of block spread in x direction
	int yspread=2800; // interval of block spread in y direction
	int dw=xspread/nw;
	int dh=yspread/nh;

	Block[] blocks=new Block[nw*nh];

	GLSLShader shader;
	GLGraphics renderer;
	GL gl;
	GLU glu;
	GLUT glut;

	float angle;

	float[] p_camera= { // position of the camera
	  0,3000,5000
	};

	float[] l_camera= { // look vector of the camera
	  0,0,0
	}; 

	float[] p_light= {  // position of light
	  0,0,6000
	};
	float[] l_light= { // look vector of light
	  0,0,0
	};

	int[] fboId= {
	  0
	};
	int[] depthTextureId= {
	  0
	};

	float light_mvnt=5000.0f;
	int shadowMapUniform;

	double[] modelView=new double[16];
	double[] projection=new double[16];
	double[] bias = {	
	  0.5, 0.0, 0.0, 0.0, 
	  0.0, 0.5, 0.0, 0.0,
	  0.0, 0.0, 0.5, 0.0,
	  0.5, 0.5, 0.5, 1.0
	};	
	
	public void setup() {
		  size(RENDER_WIDTH, RENDER_HEIGHT, GLConstants.GLGRAPHICS);
		
		  noCursor();

		  shader = new GLSLShader(this, "drole/tests/shadow/data/VertexShader.glsl", "drole/tests/shadow/data/FragmentShader.glsl"); // load the shaders

		  renderer = (GLGraphics)g;
		  gl=renderer.gl;
		  glu=renderer.glu;
		  glut=new GLUT();
		  renderer.beginGL();
		  generateShadowFBO(gl);
		  gl.glEnable(GL.GL_DEPTH_TEST);
		  gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		  gl.glEnable(GL.GL_CULL_FACE);
		  gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		  //gl.glPolygonMode(GL.GL_FRONT,GL.GL_FILL);
		  //gl.glPolygonMode(GL.GL_BACK,GL.GL_FILL);
		  //gl.glDisable(GL.GL_CULL_FACE);
		  //gl.glFrontFace(GL.GL_CW);

		  renderer.endGL();

		  for (int i=0;i<nw*nh;i++) {
		    blocks[i]=new Block(dw*(i%nw)-xspread/2, dh*(i/nw)-yspread/2, 0, dw, gl, glut);
		  }
	}
	
	public void draw() {
		  angle += 0.005f;
		  update(angle);
		  renderer.beginGL();
		  renderScene(gl,glu);
		  renderer.endGL();
	}
	
	public void drawObjects(GL a) {
		  // Ground
		  //  a.glColor4f(0.9f, 0.9f, 0.9f, 1);
		  //  // Instead of calling glTranslatef, we need a custom function that also maintain the light matrix
		  //  startTranslate(0, 0, 0, a);
		  //  a.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
		  //  a.glBegin(GL.GL_POLYGON);
		  //  a.glVertex3f(-5000, -5000, 0);
		  //  a.glVertex3f(-5000, 5000, 0);
		  //  a.glVertex3f(5000, 5000, 0);
		  //  a.glVertex3f(5000, -5000, 0);
		  //  a.glEnd();
		  //  endTranslate(a);
		  for (int i=0;i<nw*nh;i++) {
		    blocks[i].thisObject();
		  }

		  //for (int i=0;i<10;i++) {
		  //    startTranslate(0,0,500,a);//random(1000), random(1000), random(1000), a);
		  //    glut.glutSolidSphere(100,100,100);
		  //    endTranslate(a);
		  //
		  //    startTranslate(0,300,500, a);
		  //    glut.glutSolidCube(300);
		  //    endTranslate(a);
		  //  //}
	}
	
	public void setupMatrices(float position_x,float position_y,float position_z,float lookAt_x,float lookAt_y,float lookAt_z,GL a,GLU b) {
		  a.glMatrixMode(GL.GL_PROJECTION);
		  a.glLoadIdentity();
		  b.gluPerspective(45, RENDER_WIDTH/(float)RENDER_HEIGHT, 10, 40000);
		  a.glMatrixMode(GL.GL_MODELVIEW);
		  a.glLoadIdentity();
		  b.gluLookAt(position_x,position_y,position_z,lookAt_x,lookAt_y,lookAt_z,0,1,0);
	}

	public void renderScene(GL a, GLU b) {
		  a.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);	
		  a.glViewport(0, 0, RENDER_WIDTH * SHADOW_MAP_RATIO, RENDER_HEIGHT* SHADOW_MAP_RATIO);
		  a.glClear(GL.GL_DEPTH_BUFFER_BIT);
		  a.glColorMask(false, false, false, false); 
		  
		  setupMatrices(p_light[0], p_light[1], p_light[2], l_light[0], l_light[1], l_light[2], a, b);
		  
		  a.glCullFace(GL.GL_BACK);
		  
		  drawObjects(a);
		  setTextureMatrix(a, b);  
		  
		  a.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
		  a.glViewport(0, 0, RENDER_WIDTH, RENDER_HEIGHT);
		  a.glColorMask(true, true, true, true); 
		  a.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		  
		  shader.start();
		  shader.setFloatUniform("xPixelOffset", 1.0f/ (RENDER_WIDTH * SHADOW_MAP_RATIO));
		  shader.setFloatUniform("yPixelOffset", 1.0f/ (RENDER_WIDTH * SHADOW_MAP_RATIO));
		  shader.setTexUniform("ShadowMap", 7);
		  
		  a.glActiveTexture(GL.GL_TEXTURE7);
		  a.glBindTexture(GL.GL_TEXTURE_2D, depthTextureId[0]);
		  
		  setupMatrices(p_camera[0], p_camera[1], p_camera[2], l_camera[0], l_camera[1], l_camera[2], a, b);
		  
		  a.glCullFace(GL.GL_FRONT);
		  
		  drawObjects(a);
		  shader.stop();
	}

	public void setTextureMatrix(GL a, GLU b) {
		  a.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelView, 0);
		  a.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
		  a.glMatrixMode(GL.GL_TEXTURE);
		  a.glActiveTexture(GL.GL_TEXTURE7);
		  a.glLoadIdentity();	
		  a.glLoadMatrixd(bias, 0);  
		  a.glMultMatrixd (projection, 0);
		  a.glMultMatrixd (modelView, 0);
		  a.glMatrixMode(GL.GL_MODELVIEW);
	}

	public void generateShadowFBO(GL a) {
		int shadowMapWidth = RENDER_WIDTH * SHADOW_MAP_RATIO;
		int shadowMapHeight = RENDER_HEIGHT * SHADOW_MAP_RATIO;

		int FBOstatus;
		a.glGenTextures(1, depthTextureId, 0);
		a.glBindTexture(GL.GL_TEXTURE_2D, depthTextureId[0]);
		a.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		a.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		a.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		a.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		a.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_COMPARE_MODE, GL.GL_COMPARE_R_TO_TEXTURE);
		a.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);
		a.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_DEPTH_TEXTURE_MODE, GL.GL_INTENSITY);
		a.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
		a.glBindTexture(GL.GL_TEXTURE_2D, 0);
		a.glGenFramebuffersEXT(1, fboId, 0);
		a.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId[0]);
		a.glDrawBuffer(GL.GL_NONE);
		a.glReadBuffer(GL.GL_NONE);
		a.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_TEXTURE_2D, depthTextureId[0], 0);
		FBOstatus = a.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		if(FBOstatus != GL.GL_FRAMEBUFFER_COMPLETE_EXT) print("GL_FRAMEBUFFER_COMPLETE_EXT failed, CANNOT use FBO\n");

		a.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	}

	public void update(float angle) {
		  p_light[0] =(mouseX-width/2)*100; //light_mvnt * cos(angle);
		  p_light[1] = -(mouseY-height/2)*100;//light_mvnt * sin(angle);
		  for(int i=0;i<nw*nh;i++){
		   blocks[i].moveCenter(0,0,(int)random(-10,10)); 
		  }
		  p_camera[0] = light_mvnt *sin(angle);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
//			"--present",
			"--bgcolor=#000000",
			"--present-stop-color=#000000", 
//			"--display=0",
			"drole.tests.shadow.ShadowTest"
		});
	}
	
}
