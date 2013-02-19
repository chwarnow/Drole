package drole.tests.shadow;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

public class Block {

	int cx;
	int cy;
	int cz;
	int w;
	GL a;
	GLUT glut;

	Block(int _cx, int _cy, int _cz, int _w, GL _a, GLUT glut) {
		cx = _cx;
		cy = _cy;
		cz = _cz;
		w = _w;
		a = _a;
		this.glut = glut;
	}

	void thisObject() {
		startTranslate(cx, cy, cz, a);
		glut.glutSolidCube(w);
		endTranslate(a);
	}

	void moveCenter(int dx, int dy, int dz) {
		cx += dx;
		cy += dy;
		cz += dz;
	}

	public void startTranslate(float x, float y, float z, GL a) {
		a.glPushMatrix();
		a.glTranslatef(x, y, z);
		a.glMatrixMode(GL.GL_TEXTURE);
		a.glActiveTexture(GL.GL_TEXTURE7);
		a.glPushMatrix();
		a.glTranslatef(x, y, z);
	}

	public void endTranslate(GL a) {
		a.glPopMatrix();
		a.glMatrixMode(GL.GL_MODELVIEW);
		a.glPopMatrix();
	}

}
