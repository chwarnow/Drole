package drole.tests.spektakel;

import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.AttractionBehavior;

public class DudeSystem extends ParticleSystem {

	PImage dudeImage;

	float[][] pixelValues;
	
	
	public DudeSystem(PApplet p,VerletPhysics _physics, float mySize, float x,float y, float z) {

		super(p,_physics, mySize, x, y, z);
		loadImage();
		

		spawnNew();
			
	
	}
	
	void loadImage(){
		
		dudeImage = p.loadImage("images/flyingDude.png");
		dudeImage.loadPixels();
		
		pixelValues = new float[dudeImage.width][dudeImage.height];
		
		for(int i=0;i<dudeImage.height;i++){
			for(int j=0;j<dudeImage.width;j++){
				
				int getPixel = (i*dudeImage.width)+j;
				
				float color = p.red(dudeImage.pixels[getPixel])/255;
		
			    pixelValues[j][i] = color;
				
			    
			}
		}
	}
	
	public void spawnNew() {

		bigParticle.clear();
		clean();

		shockwave = true;
		boomPower = initalBoomPower;
		boomForce = new AttractionBehavior(this, 2000, boomPower * 0.3f, 0.1f);
		physics.addBehavior(boomForce);

	

		for (int i =0;i<pixelValues.length;i++) {
			for (int j=0;j<pixelValues[i].length;j++){
			
			
/*
			ShapeParticle newPart = new ShapeParticle(p, mySize, x() + face.a.x
					- targetAngle.x / 2, y() + face.a.y - targetAngle.y / 2,
					z() );

			// p.println("x "+f.a.x+" y "+f.a.y+" z "+f.a.z);

		*/
	//		bigParticle.add(newPart);
	//		physics.addParticle(newPart);

			/*
			 * newPart = new Particle(p, mySize / 2, f.c.x+x, f.c.y+y, f.c.z+z);
			 * bigParticle.add(newPart); physics.addParticle(newPart);
			 */
		}
		}

		initSprites();

	}
	

}
