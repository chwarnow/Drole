package drole.tests.textures;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;

public class ModelTextures extends PApplet {

	private static final long serialVersionUID = 1L;

	private GLModel texquad;
	private GLTexture texture0, texture1;

	private GLSLShader shader;
	
	private int numPoints = 4;

	public void setup() {
	  size(640, 480, GLConstants.GLGRAPHICS);  

	  shader = new GLSLShader(this, "drole/tests/textures/vertex.glsl", "drole/tests/textures/fragment.glsl");
	  
	  // The model is dynamic, which means that its coordinates can be
	  // updating during the drawing loop.
	  texquad = new GLModel(this, numPoints, QUADS, GLModel.DYNAMIC);
	    
	  // Updating the vertices to their initial positions.
	  texquad.beginUpdateVertices();
	  texquad.updateVertex(0, -100, -100, 0);
	  texquad.updateVertex(1, 100, -100, 0);
	  texquad.updateVertex(2, 100, 100, 0);
	  texquad.updateVertex(3, -100, 100, 0);    
	  texquad.endUpdateVertices();

	  // Enabling the use of texturing...
	  texquad.initTextures(2);
	  // ... and loading and setting texture for this model.
	  texture0 = new GLTexture(this, "data/images/boxTexture.jpg");
	  texquad.setTexture(0, texture0);
	  shader.setTexUniform("texture0", texture0);
	  
	  texture1 = new GLTexture(this, "data/images/fabricAngelA.png");
	  shader.setTexUniform("texture1", texture1);
	  texquad.setTexture(1, texture1);
	  
	  // Setting the texture coordinates.
	  texquad.beginUpdateTexCoords(0);
	  texquad.updateTexCoord(0, 0, 0);
	  texquad.updateTexCoord(1, 1, 0);    
	  texquad.updateTexCoord(2, 1, 1);
	  texquad.updateTexCoord(3, 0, 1);
	  texquad.endUpdateTexCoords();
	  
	  texquad.beginUpdateTexCoords(1);
	  texquad.updateTexCoord(0, 0, 0);
	  texquad.updateTexCoord(1, 1, 0);    
	  texquad.updateTexCoord(2, 1, 1);
	  texquad.updateTexCoord(3, 0, 1);
	  texquad.endUpdateTexCoords();

	  // Enabling colors.
	  texquad.initColors();
	  texquad.beginUpdateColors();
	  for (int i = 0; i < numPoints; i++) {
	    texquad.updateColor(i, random(0, 255), random(0, 255), random(0, 255), 225);
	  }
	  texquad.endUpdateColors();    
	}

	public void draw() {    
	  GLGraphics renderer = (GLGraphics)g;
	  renderer.beginGL();   
	  
	  background(0);
	  
	  translate(width/2, height/2, 200);        
	  rotateY(frameCount * 0.01f);   
	    
	  // Randomizing the vertices.
	  /*
	  texquad.beginUpdateVertices();
	  for (int i = 0; i < numPoints; i++) { 
	    texquad.displaceVertex(i, random(-1.0f, 1.0f), random(-1.0f, 1.0f), random(-1.0f, 1.0f));   
	  }
	  texquad.endUpdateVertices();    
	   */
	  shader.start();
	  	shader.setIntUniform("w00t", 2);
	  	
	  	renderer.model(texquad);
	  shader.stop();
	  
	  renderer.endGL();    
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[]{"drole.tests.textures.ModelTextures"});
	}
	
}
