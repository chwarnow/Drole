package com.madsim.engine.drawable;


import com.madsim.engine.Engine;

import penner.easing.Quad;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public abstract class Drawable {

	protected Engine e;
	protected PGraphicsOpenGL g;
	
	public static String OFF_SCREEN 			= 	"OFF_SCREEN";
	public static String ON_SCREEN 				= 	"ON_SCREEN";
	public static String FADING_IN 				= 	"FADING_IN";
	public static String FADING_OUT 			= 	"FADING_OUT";
	public static String ONLY_ONSCREEN 			= 	"ONLY_ONSCREEN";
	public static String ONANDOFFSCREEN 		= 	"ONANDOFFSCREEN";
	
	protected String MODE 						= 	ON_SCREEN;
	protected String UPDATE_MODE				= 	ONLY_ONSCREEN;
	
	public static short CAST_SHADOW				=	10;
	public static short RECEIVE_SHADOW			=	20;
	public static short CAST_AND_RECEIVE_SHADOW	=	30;
	public short SHADOW_HINT					= 	CAST_AND_RECEIVE_SHADOW;
	
	public String shader						=	"";
	public boolean useShader					= 	false;
	
	private float fadeTime 						= 	1;
	private float currentFadeTime 				= 	1;

	protected float fade 						= 	1;
	public boolean 	visible 					= 	true;
	
	protected PVector 	position 				= 	new PVector(0, 0, 0);
	protected PVector 	dimension 				= 	new PVector(0, 0, 0);
	protected PVector 	scale 					= 	new PVector(1, 1, 1);
	protected PVector 	rotation 				= 	new PVector(0, 0, 0);
	
	private PVector 	targetPosition 			= 	new PVector(0, 0, 0);
	private PVector 	targetDimension			= 	new PVector(0, 0, 0);
	private PVector 	targetScale 			= 	new PVector(1, 1, 1);
	
	private long 		positionEaseMillis		=	0;
	private long 		positionEaseTime		=	0;
	
	private long 		dimensionEaseMillis		=	0;
	private long 		dimensionEaseTime		=	0;
	
	private long 		scaleEaseMillis			=	0;
	private long 		scaleEaseTime			=	0;

	
	public Drawable(Engine e) {
		this.e = e;
		setG(e.g);
	}

	public void setG(PGraphicsOpenGL g) {
		this.g = g;
	}
	
	public String mode() {
		return MODE;
	}

	public void mode(String MODE) {
		this.MODE = MODE;
	}

	public String updateMode() {
		return UPDATE_MODE;
	}

	public void updateMode(String UPDATE_MODE) {
		this.UPDATE_MODE = UPDATE_MODE;
	}
	
	public PVector position() {
		return position;
	}
	
	public PVector position(float x, float y, float z) {
		position 		= new PVector(x, y, z);
		targetPosition 	= position.get();
		
		return position();
	}
	
	public PVector position(PVector position) {
		this.position	 		= position.get();
		this.targetPosition 	= this.position.get();
		
		return position();
	}
	
	public PVector dimension() {
		return dimension;
	}
	
	public PVector dimension(float x, float y, float z) {
		dimension 			= new PVector(x, y, z);
		targetDimension 	= dimension.get();
		
		return dimension();
	}
	
	public PVector dimension(PVector dimension) {
		this.dimension	 		= dimension.get();
		this.targetDimension 	= this.dimension.get();
		
		return dimension();
	}
	
	public PVector scale() {
		return scale;
	}
	
	public PVector scale(float x, float y, float z) {
		return scale(new PVector(x, y, z));
	}
	
	public PVector scale(PVector scale) {
		this.scale	 		= scale.get();
		this.targetScale 	= this.scale.get();
		
		return scale();
	}	
	
	public PVector rotation() {
		return rotation;
	}
	
	public PVector rotation(float x, float y, float z) {
		rotation 		= new PVector(x, y, z);

		return rotation();
	}
	
	public PVector rotation(PVector rotation) {
		this.rotation	 		= rotation.get();

		return rotation();
	}
	
	public void easeToPosition(float x, float y, float z, long millis) {
		targetPosition 				= new PVector(x, y, z);
		positionEaseMillis			= millis;
		positionEaseTime			= System.currentTimeMillis();
	}
	
	public void easeToDimension(PVector targetDimension, long millis) {
		this.targetDimension 		= targetDimension.get();
		dimensionEaseMillis			= millis;
		dimensionEaseTime			= System.currentTimeMillis();
	}
	
	public void easeToScale(PVector targetScale, long millis) {
		this.targetScale 			= targetScale.get();
		scaleEaseMillis				= millis;
		scaleEaseTime				= System.currentTimeMillis();
	}	
	
	public void hide() {
		visible = false;
		mode(OFF_SCREEN);
	}

	public void show() {
		visible = true;
		mode(ON_SCREEN);
	}

	public void fadeIn(float time) {
		fadeTime = time;
		currentFadeTime = 0;
		fade = 0;
		show();
		mode(FADING_IN);
	}

	public void fadeOut(float time) {
		fadeTime = time;
		currentFadeTime = time;
		fade = 1;
		mode(FADING_OUT);
	}

	public void update() {
	    if(mode() == FADING_IN && currentFadeTime == fadeTime) mode(ON_SCREEN);
	    if(mode() == FADING_OUT && currentFadeTime == 0) mode(OFF_SCREEN);
	    
	    if(mode() == FADING_IN) currentFadeTime++;
	    if(mode() == FADING_OUT) currentFadeTime--;
	    
	    if(mode() == FADING_IN || mode() == FADING_OUT) {      
	      fade = Quad.easeInOut(currentFadeTime, 0, 1, fadeTime);
	    }
	    
	    if(!position.equals(targetPosition)) {
	    	if(position.dist(targetPosition) > 0.001f) {
	    		/*
	    		long time = System.currentTimeMillis();
	    		position.x = Quad.easeInOut(time-positionEaseTime, 0, 1, positionEaseMillis)*(position.x-targetPosition.x);
	    		position.y = Quad.easeInOut(time-positionEaseTime, 0, 1, positionEaseMillis)*(position.y-targetPosition.y);
	    		position.z = Quad.easeInOut(time-positionEaseTime, 0, 1, positionEaseMillis)*(position.z-targetPosition.z);
	    		*/
	    		
	    		position.x = PApplet.lerp(position.x, targetPosition.x, 0.02f);
	    		position.y = PApplet.lerp(position.y, targetPosition.y, 0.02f);
	    		position.z = PApplet.lerp(position.z, targetPosition.z, 0.02f);
	    	} else {
	    		position = targetPosition.get();
	    	}
	    }
	    
	    if(!dimension.equals(targetDimension)) {
	    	if(dimension.dist(targetDimension) > 1) {
	    		long time = System.currentTimeMillis();
	    		dimension.x = Quad.easeInOut(time-dimensionEaseTime, 0, 1, dimensionEaseMillis)*(dimension.x-targetDimension.x);
	    		dimension.y = Quad.easeInOut(time-dimensionEaseTime, 0, 1, dimensionEaseMillis)*(dimension.y-targetDimension.y);
	    		dimension.z = Quad.easeInOut(time-dimensionEaseTime, 0, 1, dimensionEaseMillis)*(dimension.z-targetDimension.z);
	    	} else {
	    		dimension = targetDimension.get();
	    	}
	    }
	    
	    if(!scale.equals(targetScale)) {
	    	if(scale.dist(targetScale) > 0.001) {
	    		/*
	    		long time = System.currentTimeMillis();
	    		System.out.println((time-scaleEaseTime)+" : "+scaleEaseMillis);
	    		scale.x = Quad.easeInOut(time-scaleEaseTime, 0, 1, scaleEaseMillis)*(scale.x-targetScale.x);
	    		scale.y = Quad.easeInOut(time-scaleEaseTime, 0, 1, scaleEaseMillis)*(scale.y-targetScale.y);
	    		scale.z = Quad.easeInOut(time-scaleEaseTime, 0, 1, scaleEaseMillis)*(scale.z-targetScale.z);
	    		System.out.println(scale);
	    		*/
	    		scale.x = PApplet.lerp(scale.x, targetScale.x, 0.02f);
	    		scale.y = PApplet.lerp(scale.y, targetScale.y, 0.02f);
	    		scale.z = PApplet.lerp(scale.z, targetScale.z, 0.02f);
	    	} else {
	    		scale = targetScale.get();
	    	}
	    }	    
	}

	public abstract void draw();

}
