package drole.menu;


import processing.core.PApplet;
import processing.core.PVector;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;
import com.madsim.tracking.fake.MouseXY;
import com.madsim.tracking.kinect.Kinect;
import com.madsim.ui.kinetics.gestures.RipInterpreter;
import com.madsim.ui.kinetics.gestures.RipMotionListener;
import com.madsim.ui.kinetics.gestures.RotationInterpreter;

import drole.gfx.ribbon.RibbonGlobe;

public class Menu extends Drawable implements RipMotionListener {

	private Kinect kinect;
	
	private float NUM_WORLDS = 5;
	
	private int activeWorld;
	
	public boolean inWorld = false;
	
	private RibbonGlobe globe;
	
	private float a = 0.0f;
	private float radius;
	
	float distanceBetweenWorlds;

	private GLTexture[] tex = new GLTexture[(int)NUM_WORLDS];
	
	private float worldGravity = 0.1f;

	private MouseXY mouseXY;	
	
	private RotationInterpreter ri;

	private RipInterpreter ripi;
	
	public Menu(Engine e, Kinect kinect, PVector position, float radius) {
		super(e);
		
		this.kinect = kinect;
		
		this.radius = radius;
		
		position(position);
		
		dimension = new PVector(radius, radius, radius);
		
		a = e.p.random(0, PApplet.TWO_PI);
		
		mouseXY = new MouseXY();
		e.p.addMouseMotionListener(mouseXY);
		
		ri = new RotationInterpreter(mouseXY, 0);
		
		ripi = new RipInterpreter(this, mouseXY, 1, 1);
		
//		globe = new RibbonGlobe(e, position, dimension);
		
		calculateActiveWorld();
	}
	
	public int getActiveWorld() {
		return activeWorld;
	}

	private void calculateActiveWorld() {
		float dist = 99999999;
		int cWorld = 0;
		
		for(int i = 0; i < NUM_WORLDS; i++) {
			PVector pos = getPointOnCircleXZ(radius, a, i, 0, 1, 0);
	
			float cDist = pos.dist(new PVector(0, 0, 0));
			if(cDist < dist) {
				dist = cDist;
				cWorld = i;
			}
		}
		
		if(cWorld != activeWorld) {
			activeWorld = cWorld;
			e.p.pinLog("Active World", activeWorld);
			e.p.logLn("Active World: "+activeWorld);
		}
	}
	
	private float getWorldA(int world) {
		return PApplet.TWO_PI-((PApplet.TWO_PI/NUM_WORLDS) * world);
	}
	
	private float getActiveWorldA() {
		return getWorldA(activeWorld);
	}
	
	@Override
	public void update() {
		super.update();
		ripi.update();
		
		
//		a = (e.p.frameCount / 100.0f) % PApplet.TWO_PI;
		
		calculateActiveWorld();
		
		float goal = getActiveWorldA();
		
		a = ri.get()[0];
		
//		a = PApplet.lerp(a, goal, worldGravity);
		
		/*
		if(PApplet.abs(a - aa) > 0.001f) a = PApplet.lerp(a, aa, worldGravity);		
		a = getActiveWorldA();
		*/
	}
	
	@Override
	public void draw() {	
		g.pushMatrix();
	
			g.noStroke();
		
			for(int i = 0; i < NUM_WORLDS; i++) {
				if(i == activeWorld) g.fill(200, 0, 0);
				else g.fill(255);
				
				if(i == 0) g.fill(0, 0, 200);
				if(i == 3) g.fill(200, 200, 0);
				
				PVector p1 = getPointOnCircleXZ(radius, a, i, 0, 1, 0);
				g.pushMatrix();
					g.translate(p1.x, p1.y, p1.z);
					g.sphere(20);
				g.popMatrix();
			}
		
			g.fill(200);
			g.pushMatrix();
				g.translate(0, 0, 0);
				g.ellipse(0, 0, 10, 10);
			g.popMatrix();
	
		g.popMatrix();
		
		/*
		useLights();
		setPointLight(0, -800, 0, -1000, 255, 255, 255, 1.0f, 0.0001f, 0.0f);
		setPointLight(1,  700, 0,   0, 255, 255, 255, 1.0f, 0.0001f, 0.0f);
		
		setAmbient(1.0f, 1.0f, 1.0f);
		
		g.pushMatrix();
		g.pushStyle();
			
			g.translate(position.x, position.y, position.z);
			g.rotateX(PApplet.radians(-90));
		
			g.pushMatrix();
			g.rotateX(rotation.x);
			g.rotateY(rotation.y);
			g.rotateZ(rotation.z);
			
				for(int i = 0; i < NUM_WORLDS; i++) {
					e.setupModel(worlds[i]);
					worlds[i].render();
				}
			
				// GLOBE
				globe.draw();
				
			g.popMatrix();
			
		g.popStyle();
		g.popMatrix();
		*/
	}
	
	public PVector getPointOnCircleXZ(float r, float a, float world, float d, float xOff, float yOff) {
		float xca = a + (((PApplet.TWO_PI / NUM_WORLDS) + (d * xOff)) * world);
		float zca = a + ((PApplet.TWO_PI / NUM_WORLDS) * world);

		float x = r * PApplet.sin(xca);
		float y = yOff;
		float z = (r * PApplet.cos(zca)) - r;
		
		return new PVector(x, y, z);
	}

	@Override
	public void ripGestureFound() {
		e.p.logLn("M…GM…G");
	}
	
}
