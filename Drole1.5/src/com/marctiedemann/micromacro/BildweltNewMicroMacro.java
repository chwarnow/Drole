package com.marctiedemann.micromacro;

import java.util.ArrayList;

import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.engine.drawable.Drawable;

public class BildweltNewMicroMacro extends Drawable {

	private GLTexture bigWorldTex;

	private float[][] greyLevels;
	
	
	private float mouseY;
	
	
	ArrayList<CellSystem> cellSystem;
	
	private int gridSize = 500;

	public BildweltNewMicroMacro(Engine e) {
		super(e);

		bigWorldTex = e.requestTexture("images/Mikro_Makro_BigWorld.png");

		g.imageMode(EngineApplet.CENTER);

		analyseGreyLevels();
	
		
	  cellSystem = new ArrayList<CellSystem>();
		
		int imageCenterX = bigWorldTex.width/2;
		int imageCenterY = bigWorldTex.height/2;
		
		int steps = 50;
		
		for(int i=-steps;i<steps+1;i++){
			for(int j=-steps;j<steps+1;j++){
				
				int xPos = imageCenterX + i;
				int yPos = imageCenterY + j;
						
				float greyLevel = greyLevels[xPos][yPos];
				
				CellSystem newSystem = new CellSystem(e,greyLevel,gridSize,i*gridSize,j*gridSize,0);
				cellSystem.add(newSystem);

				
			}
		}
		
	
		
	}
	
	
	
	

	void analyseGreyLevels() {
		bigWorldTex.loadPixels();

		greyLevels = new float[bigWorldTex.width][bigWorldTex.height];

		for (int i = 0; i < bigWorldTex.height; i++) {
			for (int j = 0; j < bigWorldTex.width; j++) {

				int getPixel = (i * bigWorldTex.width) + j;

				float greyLevel = e.p.red(bigWorldTex.pixels[getPixel]) / 255;

				greyLevels[j][i] = greyLevel;

			}
		}
	}

	@Override
	public void update() {

	}

	@Override
	public void draw() {

		
		
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y, position.z);
		g.scale(scale.x, scale.y, scale.z);
		g.rotateX(rotation.x);
		g.rotateY(rotation.y);
		g.rotateZ(rotation.z);


		float newScale = EngineApplet.map(mouseY, 0, e.p.height, 0, 3);
		
		g.scale(newScale);
	//	System.out.println(newScale);
		
		for(int i =0;i<cellSystem.size();i++){
		cellSystem.get(i).draw(e.g);
		}
		
		//g.box(400);
		
		
		g.popMatrix();
		g.popStyle();

	}
	
	public void setMouseY(float mouseY){
		this.mouseY = mouseY;
	}

}
