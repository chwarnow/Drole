package drole.tests.vbo;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

public class VBO {
	final int STRIDE = Buffers.SIZEOF_FLOAT * 4;

	int numVertices;

	int[] vertID = new int[1];

	int[] normID = new int[1];

	private GL2 gl;

	VBO(int num, GL2 gl) {
		this.gl = gl;
		numVertices = num;
		
		initBuffer(vertID);
		initBuffer(normID);
	}

	void initBuffer(int[] bufferID) {
		gl.glGenBuffers(1, bufferID, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferID[0]);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numVertices * STRIDE, null, GL2.GL_DYNAMIC_DRAW);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	}

	void updateBuffer(int id, float[] data) {
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
		gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).asFloatBuffer().put(data);
		gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	}

	void updateVertices(float[] vertices) {
		updateBuffer(vertID[0], vertices);
	}

	void updateNormals(float[] normals) {
		updateBuffer(normID[0], normals);
	}

	void render(int drawMode) {
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

	void cleanup() {
		gl.glDeleteBuffers(1, vertID, 0);
		gl.glDeleteBuffers(1, normID, 0);
	}
}
