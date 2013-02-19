package com.madsim.engine.renderpass;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import codeanticode.glgraphics.GLGraphics;

import com.madsim.engine.Engine;
import com.sun.opengl.util.GLUT;

public abstract class RenderPass {

	protected Engine e;
	protected GLGraphics g;
	protected GL gl;
	protected GLU glu;
	protected GLUT glut;
	
	public RenderPass(Engine e) {
		this.e		= e;
		this.g 		= e.g;
		this.gl		= g.gl;
		this.glu	= g.glu;
		this.glut	= new GLUT();
	}
	
	public abstract void beginRender();
	
	public abstract void finalizeRender();
	
}
