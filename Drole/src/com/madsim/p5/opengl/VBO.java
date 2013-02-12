package com.madsim.p5.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import processing.core.PImage;

import com.jogamp.common.nio.Buffers;

public class VBO {
	final int STRIDE = Buffers.SIZEOF_FLOAT * 4;

	private int numVertices;

	private int[] vertID = new int[1];
	private int[] normID = new int[1];
	private int[] textID = new int[1];
	
	private GL2 gl;
	
	public VBO(GL2 gl, int numVertices) {
		this.gl 			= gl;
		this.numVertices 	= numVertices;
		
		createVertexBuffer();
		createNormalsBuffer();
//		initBuffer(textID);
	}
	
	private void createVertexBuffer() {
		createBuffer(vertID);
	}
	
	private void createNormalsBuffer() {
		createBuffer(normID);
	}

	private void initTextureBuffer(int[] texture) {
	    gl.glGenBuffers(1, textID, 0);
	    gl.glBindBuffer(GL2.GL_TEXTURE_BUFFER, textID[0]);
	    gl.glBufferData(GL2.GL_TEXTURE_BUFFER, (texture.length * 4 * STRIDE), null, GL2.GL_DYNAMIC_DRAW);
	    
	    gl.glGenTextures(1, texture, 0);
	    gl.glBindTexture(GL2.GL_TEXTURE_BUFFER, textID[0]);
	    gl.glTexBuffer(GL2.GL_TEXTURE_BUFFER, GL2.GL_RGBA32F, textID[0]);
	    gl.glBindBuffer(GL2.GL_TEXTURE_BUFFER, 0);	
	}
	
	private void createBuffer(int[] bufferID) {
		gl.glGenBuffers(1, bufferID, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferID[0]);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numVertices * STRIDE, null, GL2.GL_DYNAMIC_DRAW);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	}

	private void updateBuffer(int id, float[] data) {
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
		gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).asFloatBuffer().put(data);
		gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	}

	/*
	public void texture(PImage texture) {
		initTextureBuffer(texture.pixels);
	}
	*/
	
	public void updateVertices(float[] vertices) {
		updateBuffer(vertID[0], vertices);
	}

	public void updateNormals(float[] normals) {
		updateBuffer(normID[0], normals);
	}

	public void render(int drawMode) {
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		if (normID != null) {
			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, normID[0]);
			gl.glNormalPointer(GL2.GL_FLOAT, STRIDE, 0);
		}
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertID[0]);
		gl.glVertexPointer(3, GL.GL_FLOAT, STRIDE, 0);
		gl.glDrawArrays(drawMode, 0, numVertices);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		if (normID != null)
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
	}

	public void cleanup() {
		gl.glDeleteBuffers(1, vertID, 0);
		gl.glDeleteBuffers(1, normID, 0);
	}
}
