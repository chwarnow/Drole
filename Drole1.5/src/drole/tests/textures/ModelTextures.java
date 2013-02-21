package drole.tests.textures;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;

public class ModelTextures extends PApplet {

	private static final long serialVersionUID = 1L;

	private GLModel model;
	private GLTexture texture0, texture1;

	private GLSLShader shader;
	
	private int numPoints = 4;

	public void setup() {
	  size(640, 480, GLConstants.GLGRAPHICS);  

	  shader = new GLSLShader(this, "drole/tests/textures/vertex.glsl", "drole/tests/textures/fragment.glsl");
	  
	  // The model is dynamic, which means that its coordinates can be
	  // updating during the drawing loop.
	  model = new GLModel(this, numPoints, QUADS, GLModel.DYNAMIC);
	    
	  // Updating the vertices to their initial positions.
	  model.beginUpdateVertices();
	  model.updateVertex(0, -100, -100, 0);
	  model.updateVertex(1, 100, -100, 0);
	  model.updateVertex(2, 100, 100, 0);
	  model.updateVertex(3, -100, 100, 0);    
	  model.endUpdateVertices();

	  // Enabling the use of texturing...
	  model.initTextures(2);
	  // ... and loading and setting texture for this model.
	  texture0 = new GLTexture(this, "data/images/boxTexture.jpg");
	  model.setTexture(0, texture0);
	  shader.setTexUniform("texture0", texture0);

	  // Setting the texture coordinates.
	  model.beginUpdateTexCoords(0);
	  model.updateTexCoord(0, 0, 0);
	  model.updateTexCoord(1, 1, 0);    
	  model.updateTexCoord(2, 1, 1);
	  model.updateTexCoord(3, 0, 1);
	  model.endUpdateTexCoords();
	  
	  texture1 = new GLTexture(this, "data/images/fabricAngelA.png");
	  shader.setTexUniform("texture1", texture1);
	  model.setTexture(1, texture1);
	  
	  model.beginUpdateTexCoords(1);
	  model.updateTexCoord(0, 0, 0);
	  model.updateTexCoord(1, 1, 0);    
	  model.updateTexCoord(2, 1, 1);
	  model.updateTexCoord(3, 0, 1);
	  model.endUpdateTexCoords();

	  // Enabling colors.
	  model.initColors();
	  model.beginUpdateColors();
	  for (int i = 0; i < numPoints; i++) {
	    model.updateColor(i, random(0, 255), random(0, 255), random(0, 255), 225);
	  }
	  model.endUpdateColors();
	}

	public void draw() {    
	  GLGraphics renderer = (GLGraphics)g;
	  renderer.beginGL();   
	  
	  lights();
	  
	  background(0);
	  
	  pointLight(200, 0, 0, 0, 0, 100);
	  
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
	  	shader.setIntUniform("numTextures", model.getNumTextures());
//	  	shader.setIntUniform("numTextures", 0);
	  	
	  	renderer.model(model);
	  shader.stop();
	  
	  renderer.endGL();    
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[]{"drole.tests.textures.ModelTextures"});
	}
	
}
