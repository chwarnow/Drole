package com.madsim.p5.opengl;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class DGShape {

	private PApplet p;
	
	private PShader shader;
	
	private String fragShaderFilename = "../shader/std/frag_color.glsl", vertShaderFilename = "../shader/std/vert_poly.glsl";
	
	private FloatBuffer vertexBuffer, colorBuffer, texCoordsBuffer;
	
	private int numVertices;
	private int vertexBufferSize, colorBufferSize, texCoordsBufferSize;

	public DGShape(PApplet p, int numVertices) {
		this.p						= p;
		this.numVertices			= numVertices;
		
		init();
	}
	
	public DGShape(PApplet p, int numVertices, String fragShaderFilename, String vertShaderFilename) {
		this(p, numVertices);
		
		this.fragShaderFilename		= fragShaderFilename;
		this.vertShaderFilename		= vertShaderFilename;
		
		init();
	}
	
	private void init() {
		createShader();
		createBuffers();
	}
	
	private void createShader() {
		shader = p.loadShader(fragShaderFilename, vertShaderFilename);
	}
	
	private void createBuffers() {
		vertexBufferSize 	= numVertices * 3;
		colorBufferSize		= numVertices * 4;
		texCoordsBufferSize	= numVertices * 2;
		
		vertexBuffer 	= PGLUtil.allocateDirectFloatBuffer(vertexBufferSize);
		colorBuffer 	= PGLUtil.allocateDirectFloatBuffer(colorBufferSize);
		texCoordsBuffer	= PGLUtil.allocateDirectFloatBuffer(texCoordsBufferSize);
	}
	
	private void fillBuffer(FloatBuffer buffer, float[] data) {
		buffer.clear();
		buffer.put(data); 
		buffer.rewind();
	}

	private void warnBufferSizeDifference(String buffer, String lessmore, int v1, int v2) {
		System.out.println("[DGShape] Warning: Setting "+lessmore+" "+buffer+" ("+v1+") than buffers size ("+v2+")");
	}
	
	private void bufferVertexData(float[] data) {
		if(data.length < vertexBuffer.limit()) warnBufferSizeDifference("vertices", "LESS", vertexBuffer.limit(), data.length);
		if(data.length > vertexBuffer.limit()) warnBufferSizeDifference("vertices", "MORE", vertexBuffer.limit(), data.length);
		
		fillBuffer(vertexBuffer, data);
	}

	private void bufferColorData(float[] data) {
		if(data.length < vertexBuffer.limit()) warnBufferSizeDifference("colors", "LESS", colorBuffer.limit(), data.length);
		if(data.length > vertexBuffer.limit()) warnBufferSizeDifference("colors", "MORE", colorBuffer.limit(), data.length);
		
		fillBuffer(colorBuffer, data);
	}
	
	public void setVertices(float[] vertices) {
		bufferVertexData(vertices);
	}

	public void setRandomVertices(float dim) {
		float[] vertices = new float[vertexBufferSize];
		for(int i = 0; i < vertexBufferSize; i++) vertices[i] = p.random(-dim, dim);
		bufferVertexData(vertices);
	}
	
	public void setColors(float[] colors) {
		bufferColorData(colors);
	}

	public void setRandomColors() {
		float[] colors = new float[colorBufferSize];
		for(int i = 0; i < colors.length; i+=4) {
			colors[i]	= p.random(1);
			colors[i+1]	= p.random(1);
			colors[i+2]	= p.random(1);
			colors[i+3]	= p.random(1);
		}
		setColors(colors);
	}
	
	public void setColor(float r, float g, float b, float a) {
		float[] colors = new float[colorBufferSize];
		for(int i = 0; i < colors.length; i+=4) {
			colors[i]	= r;
			colors[i+1]	= g;
			colors[i+2]	= b;
			colors[i+3]	= a;
		}
		setColors(colors);
	}
	
	public void bindShader() {
		shader.bind();
	}
	
	public void unbindShader() {
		shader.unbind();
	}
	
	public void draw(GL2 gl, int mode) {
		shader.bind();
		
		int vertLoc = gl.glGetAttribLocation(shader.glProgram, "inVertex");
		int colorLoc = gl.glGetAttribLocation(shader.glProgram, "inColor");

		gl.glEnableVertexAttribArray(vertLoc);
		gl.glEnableVertexAttribArray(colorLoc);

		gl.glVertexAttribPointer(vertLoc, 3, GL2.GL_FLOAT, false, 0, vertexBuffer);
		gl.glVertexAttribPointer(colorLoc, 4, GL2.GL_FLOAT, false, 0, colorBuffer);

		gl.glDrawArrays(mode, 0, numVertices);

		gl.glDisableVertexAttribArray(vertLoc);
		gl.glDisableVertexAttribArray(colorLoc);

		shader.unbind();
	}
	
}
