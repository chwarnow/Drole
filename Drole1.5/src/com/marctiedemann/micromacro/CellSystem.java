package com.marctiedemann.micromacro;

import java.util.ArrayList;

import com.madsim.engine.Engine;

import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;

public class CellSystem extends VerletParticle{

	
	private GLModel cellModel;
	private GLTexture tex;
	
	private float greyLevel =0;
	
	private int maxCells = 15;
	private int gridSize = 0;
	private int numCells = 0;
	
	private ArrayList<Cell> cell;
	
	private ArrayList<PVector> vertices;
	private ArrayList<PVector> texCoords;
	
	private Engine e;
	
public CellSystem(Engine e, float greyLevel,int gridSize, float x, float y, float z){
	
	super(x,y,z);
	
	this.e = e;
	this.greyLevel = greyLevel;
	this.gridSize = gridSize;
	
	numCells = (int)((1-greyLevel) * maxCells);
	
	cell = new ArrayList<Cell>();
	texCoords = new ArrayList<PVector>();
	vertices = new ArrayList<PVector>();
	
	tex = e.requestTexture("images/Mikro_Makro_Medium.png");
	
	System.out.println("new Cell System with graylevel "+greyLevel+" and "+numCells+" cells. At x "+x+" y "+y);

	
	spawn();
	
}

public void spawn(){
	
	
	cellModel = new GLModel(e.p, numCells * 4, GLModel.QUADS,
			GLModel.DYNAMIC);
	
	
	for(int i=0;i<numCells;i++){
	Cell newCell = new Cell(e.p.random(-gridSize/2,gridSize/2),e.p.random(-gridSize/2,gridSize/2),-500,e.p.random(gridSize)*(1-greyLevel));
	
	buildQuad(newCell,i);
	
	cell.add(newCell);
	}
	
	
	cellModel.updateVertices(vertices);	

	
	cellModel.initTextures(1);
	cellModel.setTexture(0, tex);
	cellModel.updateTexCoords(0, texCoords);
	
	cellModel.initColors();
	cellModel.setColors(255*greyLevel);
	
	

}

public void draw(GLGraphics g){
		
	g.pushMatrix();
	//g.translate(x, y);
//	g.rectMode(g.CENTER);
//	g.stroke(255);
//	g.noFill();
	//g.fill(greyLevel*255);
	
	e.setupModel(cellModel);
	g.model(cellModel);
	
	g.translate(0, 0,-500);
//	g.rect(x,y,gridSize,gridSize);
	
	
	g.popMatrix();
	
}


private void buildQuad(Cell fromCell,int id){
	
	float size = fromCell.mySize;
	
//	cellModel.updateVertex(id+0,fromCell.x-size,fromCell.y-size,0);
//	cellModel.updateVertex(id+1,fromCell.x+size,fromCell.y-size,0);
//	cellModel.updateVertex(id+2,fromCell.x+size,fromCell.y+size,0);
//	cellModel.updateVertex(id+3,fromCell.x-size,fromCell.y+size,0);

	
	
	addVertex(x+fromCell.x-size,y+fromCell.y-size,fromCell.z,0,0);
	addVertex(x+fromCell.x+size,y+fromCell.y-size,fromCell.z,1,0);
	addVertex(x+fromCell.x+size,y+fromCell.y+size,fromCell.z,1,1);
	addVertex(x+fromCell.x-size,y+fromCell.y+size,fromCell.z,0,1);

}


private void addVertex(float x, float y, float z, float u, float v)
{
	PVector vert = new PVector(x,y,z);
	PVector texCoord = new PVector(u, v);
    vertices.add(vert);
    texCoords.add(texCoord);
 
}


	
	
}
