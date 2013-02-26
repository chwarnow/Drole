package drole.tests.spektakel;

import processing.core.PApplet;

public class Spark extends Particle {

	
	public Spark(PApplet p,float size, float x, float y , float z){
		super(p,size,x,y,z);
		
		lifeSpan = 600;
		setWeight(1.5f);
		
	}
	
public void draw(){	
		
		float alpha = PApplet.map(lifeSpan,0,600,0,255);
	
		p.pushMatrix();
		p.translate(x, y,z);
		p.noStroke();
		p.fill(0,200,255,alpha);
		p.ellipse(0,0,mySize,mySize);
		p.popMatrix();
		
	}
}
