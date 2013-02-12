package com.madsim.p5.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.media.opengl.GL;

import processing.core.PApplet;

public class PGLUtil {

	public static final boolean USE_DIRECT_BUFFERS     = true;
	
	public static final int MIN_DIRECT_BUFFER_SIZE     = 1;
	
	public static final int SIZEOF_SHORT = Short.SIZE / 8;
	public static final int SIZEOF_INT = Integer.SIZE / 8;
	public static final int SIZEOF_FLOAT = Float.SIZE / 8;
	public static final int SIZEOF_BYTE = Byte.SIZE / 8;
	public static final int SIZEOF_INDEX = SIZEOF_SHORT;
	public static final int INDEX_TYPE = GL.GL_UNSIGNED_SHORT;
	
	public static ByteBuffer allocateDirectByteBuffer(int size) {
		int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_BYTE;
		return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder());
	}

	public static ByteBuffer allocateByteBuffer(int size) {
		if (USE_DIRECT_BUFFERS) {
			return allocateDirectByteBuffer(size);
		} else {
			return ByteBuffer.allocate(size);
		}
	}

	public static ByteBuffer allocateByteBuffer(byte[] arr) {
		if (USE_DIRECT_BUFFERS) {
			return PGLUtil.allocateDirectByteBuffer(arr.length);
		} else {
			return ByteBuffer.wrap(arr);
		}
	}

	public static ByteBuffer updateByteBuffer(ByteBuffer buf, byte[] arr,
			boolean wrap) {
		if (USE_DIRECT_BUFFERS) {
			if (buf == null || buf.capacity() < arr.length) {
				buf = PGLUtil.allocateDirectByteBuffer(arr.length);
			}
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		} else {
			if (wrap) {
				buf = ByteBuffer.wrap(arr);
			} else {
				if (buf == null || buf.capacity() < arr.length) {
					buf = ByteBuffer.allocate(arr.length);
				}
				buf.position(0);
				buf.put(arr);
				buf.rewind();
			}
		}
		return buf;
	}

	public static void getByteArray(ByteBuffer buf, byte[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.get(arr);
			buf.rewind();
		}
	}

	public static void putByteArray(ByteBuffer buf, byte[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		}
	}

	public static void fillByteBuffer(ByteBuffer buf, int i0, int i1,
			byte val) {
		int n = i1 - i0;
		byte[] temp = new byte[n];
		Arrays.fill(temp, 0, n, val);
		buf.position(i0);
		buf.put(temp, 0, n);
		buf.rewind();
	}

	public static ShortBuffer allocateDirectShortBuffer(int size) {
		int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_SHORT;
		return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
				.asShortBuffer();
	}

	public static ShortBuffer allocateShortBuffer(int size) {
		if (USE_DIRECT_BUFFERS) {
			return allocateDirectShortBuffer(size);
		} else {
			return ShortBuffer.allocate(size);
		}
	}

	public static ShortBuffer allocateShortBuffer(short[] arr) {
		if (USE_DIRECT_BUFFERS) {
			return PGLUtil.allocateDirectShortBuffer(arr.length);
		} else {
			return ShortBuffer.wrap(arr);
		}
	}

	public static ShortBuffer updateShortBuffer(ShortBuffer buf,
			short[] arr, boolean wrap) {
		if (USE_DIRECT_BUFFERS) {
			if (buf == null || buf.capacity() < arr.length) {
				buf = PGLUtil.allocateDirectShortBuffer(arr.length);
			}
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		} else {
			if (wrap) {
				buf = ShortBuffer.wrap(arr);
			} else {
				if (buf == null || buf.capacity() < arr.length) {
					buf = ShortBuffer.allocate(arr.length);
				}
				buf.position(0);
				buf.put(arr);
				buf.rewind();
			}
		}
		return buf;
	}

	public static void getShortArray(ShortBuffer buf, short[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.get(arr);
			buf.rewind();
		}
	}

	public static void putShortArray(ShortBuffer buf, short[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		}
	}

	public static void fillShortBuffer(ShortBuffer buf, int i0, int i1,
			short val) {
		int n = i1 - i0;
		short[] temp = new short[n];
		Arrays.fill(temp, 0, n, val);
		buf.position(i0);
		buf.put(temp, 0, n);
		buf.rewind();
	}

	public static IntBuffer allocateDirectIntBuffer(int size) {
		int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_INT;
		return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
				.asIntBuffer();
	}

	public static IntBuffer allocateIntBuffer(int size) {
		if (USE_DIRECT_BUFFERS) {
			return allocateDirectIntBuffer(size);
		} else {
			return IntBuffer.allocate(size);
		}
	}

	public static IntBuffer allocateIntBuffer(int[] arr) {
		if (USE_DIRECT_BUFFERS) {
			return PGLUtil.allocateDirectIntBuffer(arr.length);
		} else {
			return IntBuffer.wrap(arr);
		}
	}

	public static IntBuffer updateIntBuffer(IntBuffer buf, int[] arr,
			boolean wrap) {
		if (USE_DIRECT_BUFFERS) {
			if (buf == null || buf.capacity() < arr.length) {
				buf = PGLUtil.allocateDirectIntBuffer(arr.length);
			}
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		} else {
			if (wrap) {
				buf = IntBuffer.wrap(arr);
			} else {
				if (buf == null || buf.capacity() < arr.length) {
					buf = IntBuffer.allocate(arr.length);
				}
				buf.position(0);
				buf.put(arr);
				buf.rewind();
			}
		}
		return buf;
	}

	public static void getIntArray(IntBuffer buf, int[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.get(arr);
			buf.rewind();
		}
	}

	public static void putIntArray(IntBuffer buf, int[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		}
	}

	public static void fillIntBuffer(IntBuffer buf, int i0, int i1, int val) {
		int n = i1 - i0;
		int[] temp = new int[n];
		Arrays.fill(temp, 0, n, val);
		buf.position(i0);
		buf.put(temp, 0, n);
		buf.rewind();
	}

	public static FloatBuffer allocateDirectFloatBuffer(int size) {
		int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_FLOAT;
		return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
	}

	public static FloatBuffer allocateFloatBuffer(int size) {
		if (USE_DIRECT_BUFFERS) {
			return allocateDirectFloatBuffer(size);
		} else {
			return FloatBuffer.allocate(size);
		}
	}

	public static FloatBuffer allocateFloatBuffer(float[] arr) {
		if (USE_DIRECT_BUFFERS) {
			return PGLUtil.allocateDirectFloatBuffer(arr.length);
		} else {
			return FloatBuffer.wrap(arr);
		}
	}

	public static FloatBuffer updateFloatBuffer(FloatBuffer buf,
			float[] arr, boolean wrap) {
		if (USE_DIRECT_BUFFERS) {
			if (buf == null || buf.capacity() < arr.length) {
				buf = PGLUtil.allocateDirectFloatBuffer(arr.length);
			}
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		} else {
			if (wrap) {
				buf = FloatBuffer.wrap(arr);
			} else {
				if (buf == null || buf.capacity() < arr.length) {
					buf = FloatBuffer.allocate(arr.length);
				}
				buf.position(0);
				buf.put(arr);
				buf.rewind();
			}
		}
		return buf;
	}

	public static void getFloatArray(FloatBuffer buf, float[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.get(arr);
			buf.rewind();
		}
	}

	public static void putFloatArray(FloatBuffer buf, float[] arr) {
		if (!buf.hasArray() || buf.array() != arr) {
			buf.position(0);
			buf.put(arr);
			buf.rewind();
		}
	}

	public static void fillFloatBuffer(FloatBuffer buf, int i0, int i1,
			float val) {
		int n = i1 - i0;
		float[] temp = new float[n];
		Arrays.fill(temp, 0, n, val);
		buf.position(i0);
		buf.put(temp, 0, n);
		buf.rewind();
	}
}
