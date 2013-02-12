package com.madsim.p5.opengl;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import processing.core.PApplet;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class DGShape {

	private PApplet p;
	
	private PGraphicsOpenGL pgl;
	private GL2 gl;	
	
	private PShader shader;
	
	private String fragShaderFilename = "../shader/std/frag_color.glsl", vertShaderFilename = "../shader/std/vert_poly.glsl";
	
	private int[] vertexID = new int[1];
	private int[] colorID = new int[1];
	
	private FloatBuffer vertexBuffer, colorBuffer, texCoordsBuffer;
	
	private HashMap<String, FloatBuffer> bos = new HashMap<String, FloatBuffer>();
	
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
	
	@SuppressWarnings("static-access")
	private void refreshGL() {
		pgl = (PGraphicsOpenGL) p.g; // g may change
		gl = pgl.beginPGL().gl.getGL2();
	}
	
	private void init() {
		refreshGL();
		
		createShader();
		createBuffers();
	}
	
	private void createShader() {
		shader = p.loadShader(fragShaderFilename, vertShaderFilename);
	}
	
	private void createBuffers() {
		vertexBufferSize 	= numVertices * 4;
		colorBufferSize		= numVertices * 4;
		texCoordsBufferSize	= numVertices * 2;
		
		vertexBuffer 	= PGLUtil.allocateDirectFloatBuffer(vertexBufferSize);
		colorBuffer 	= PGLUtil.allocateDirectFloatBuffer(colorBufferSize);
		texCoordsBuffer	= PGLUtil.allocateDirectFloatBuffer(texCoordsBufferSize);
	}
	
	private void fillBuffer(String name, int[] id, FloatBuffer buffer, float[] data) {
		buffer.clear();
		buffer.put(data); 
		buffer.rewind();
		
		refreshGL();
		pgl.beginPGL();
			gl.glGenBuffers(1, id, 0);
		    gl.glBindBuffer(PGL.ARRAY_BUFFER, id[0]);
		    gl.glBufferData(PGL.ARRAY_BUFFER, buffer.limit()*PGLUtil.SIZEOF_FLOAT, buffer, PGL.STATIC_DRAW);
		    gl.glBindBuffer(PGL.ARRAY_BUFFER, 0);
	    pgl.endPGL();
	}

	private void warnBufferSizeDifference(String buffer, String lessmore, int v1, int v2) {
		System.out.println("[DGShape] Warning: Setting "+lessmore+" "+buffer+" ("+v1+") than buffers size ("+v2+")");
	}
	
	private void bufferVertexData(float[] data) {
		if(data.length < vertexBuffer.limit()) warnBufferSizeDifference("vertices", "LESS", vertexBuffer.limit(), data.length);
		if(data.length > vertexBuffer.limit()) warnBufferSizeDifference("vertices", "MORE", vertexBuffer.limit(), data.length);
		
		fillBuffer("inVertex", vertexID, vertexBuffer, data);
	}

	private void bufferColorData(float[] data) {
		if(data.length < vertexBuffer.limit()) warnBufferSizeDifference("colors", "LESS", colorBuffer.limit(), data.length);
		if(data.length > vertexBuffer.limit()) warnBufferSizeDifference("colors", "MORE", colorBuffer.limit(), data.length);
		
		fillBuffer("inColor", colorID, colorBuffer, data);
	}
	
	public void createAttributeVBO(String name, int numVertices) {
		if(!bos.containsKey(name)) bos.put(name, PGLUtil.allocateDirectFloatBuffer(numVertices));
	}
	
	public void setAttributeVBOData(String name, float[] data) {
//		fillBuffer(name, bos.get(name), data);
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
		
		/*
		int vertLoc = gl.glGetAttribLocation(shader.glProgram, "inVertex");
		int colorLoc = gl.glGetAttribLocation(shader.glProgram, "inColor");

		int boLoc = gl.glGetAttribLocation(shader.glProgram, "inBO");
		
		gl.glEnableVertexAttribArray(vertLoc);
		gl.glEnableVertexAttribArray(colorLoc);
		
		gl.glEnableVertexAttribArray(boLoc);

		gl.glVertexAttribPointer(vertLoc, 3, GL2.GL_FLOAT, false, 0, vertexBuffer);
		
		gl.glBindBuffer(PGL.ARRAY_BUFFER, boLoc);
		gl.glVertexAttribPointer(vertLoc, 3, GL2.GL_FLOAT, false, 0, bos.get("inBO"));
		
		gl.glVertexAttribPointer(colorLoc, 4, GL2.GL_FLOAT, false, 0, colorBuffer);
		*/
		
		// Bind attribute vbos
		/*
		for(Map.Entry<String, FloatBuffer> bbos : bos.entrySet()) {
			int boLoc = gl.glGetAttribLocation(shader.glProgram, bbos.getKey());
			gl.glEnableVertexAttribArray(boLoc);
			gl.glVertexAttribPointer(colorLoc, 3, GL2.GL_FLOAT, false, 0, bbos.getValue());
		}
		*/
		 //gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		 gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexID[0]);
		 
		 int vertLoc = gl.glGetAttribLocation(shader.glProgram, "inVertex");
		 gl.glEnableVertexAttribArray(vertLoc);
		 gl.glVertexAttribPointer(vertLoc, 4, GL2.GL_FLOAT, false, 0, 0);
		 
		 gl.glDrawArrays(GL2.GL_TRIANGLES, 0, numVertices);
		 
		 gl.glDisableVertexAttribArray(vertLoc);
		 gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0);
		 //gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		 
		/*

		gl.glDisableVertexAttribArray(vertLoc);
		gl.glDisableVertexAttribArray(boLoc);
		gl.glDisableVertexAttribArray(colorLoc);
		 */
		
		// Disable additional attribute buffer
		/*
		for(Map.Entry<String, FloatBuffer> bbos : bos.entrySet()) {
			int boLoc = gl.glGetAttribLocation(shader.glProgram, bbos.getKey());
			gl.glDisableVertexAttribArray(boLoc);
		}
		*/
		
		shader.unbind();
	}
	
}
