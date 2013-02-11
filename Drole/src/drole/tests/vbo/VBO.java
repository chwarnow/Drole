import com.sun.opengl.util.BufferUtil;
import processing.opengl.*;
import javax.media.opengl.GL;

public class VBO {
	final int STRIDE = BufferUtil.SIZEOF_FLOAT * 4;

	int numVertices;

	int[] vertID = new int[1];

	int[] normID = new int[1];

	private GL gl;

	VBO(int num, GL gl) {
		this.gl = gl;
		numVertices = num;
		initBuffer(vertID);
		initBuffer(normID);
	}

	void initBuffer(int[] bufferID) {
		gl.glGenBuffersARB(1, bufferID, 0);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, bufferID[0]);
		gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, numVertices * STRIDE, null, GL.GL_DYNAMIC_DRAW_ARB);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	void updateBuffer(int id, float[] data) {
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, id);
		gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY).asFloatBuffer().put(data);
		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	void updateVertices(float[] vertices) {
		updateBuffer(vertID[0], vertices);
	}

	void updateNormals(float[] normals) {
		updateBuffer(normID[0], normals);
	}

	void render() {
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		if (normID != null) {
			gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, normID[0]);
			gl.glNormalPointer(GL.GL_FLOAT, STRIDE, 0);
		}
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vertID[0]);
		gl.glVertexPointer(3, GL.GL_FLOAT, STRIDE, 0);
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, numVertices);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		if (normID != null)
			gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
	}

	void cleanup() {
		gl.glDeleteBuffers(1, vertID, 0);
		gl.glDeleteBuffers(1, normID, 0);
	}
}
