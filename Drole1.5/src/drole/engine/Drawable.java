package drole.engine;

import penner.easing.Quad;
import processing.core.PApplet;
import processing.core.PVector;

public abstract class Drawable {

	public static String OFF_SCREEN 	= "OFF_SCREEN";
	public static String ON_SCREEN 		= "ON_SCREEN";
	public static String FADING_IN 		= "FADING_IN";
	public static String FADING_OUT 	= "FADING_OUT";
	
	protected String MODE = ON_SCREEN;
	
	private float fadeTime = 1;
	private float currentFadeTime = 1;

	protected PApplet parent;

	protected float fade = 1;
	public boolean 	visible = true;
	
	protected PVector 	position 			= 	new PVector(0, 0, 0);
	protected PVector 	dimension 			= 	new PVector(0, 0, 0);
	protected PVector 	scale 				= 	new PVector(1, 1, 1);
	
	private PVector 	targetPosition 		= 	new PVector(0, 0, 0);
	private PVector 	targetDimension		= 	new PVector(0, 0, 0);
	private PVector 	targetScale 		= 	new PVector(1, 1, 1);
	
	private long 		positionEaseMillis	=	0;
	private long 		positionEaseTime	=	0;
	
	private long 		dimensionEaseMillis	=	0;
	private long 		dimensionEaseTime	=	0;
	
	private long 		scaleEaseMillis		=	0;
	private long 		scaleEaseTime		=	0;

	public Drawable(PApplet parent) {
		this.parent = parent;
	}

	public String mode() {
		return MODE;
	}

	public void mode(String MODE) {
		this.MODE = MODE;
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
		scale 				= new PVector(x, y, z);
		targetScale 		= scale.get();
		
		return scale();
	}
	
	public PVector scale(PVector scale) {
		this.scale	 		= scale.get();
		this.targetScale 	= this.scale.get();
		
		return scale();
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
	    	if(position.dist(targetPosition) > 1) {
	    		long time = System.currentTimeMillis();
	    		position.x = Quad.easeInOut(time-positionEaseTime, 0, 1, positionEaseMillis)*(position.x-targetPosition.x);
	    		position.y = Quad.easeInOut(time-positionEaseTime, 0, 1, positionEaseMillis)*(position.y-targetPosition.y);
	    		position.z = Quad.easeInOut(time-positionEaseTime, 0, 1, positionEaseMillis)*(position.z-targetPosition.z);
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
	    	if(scale.dist(targetScale) > 0.0001) {
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
