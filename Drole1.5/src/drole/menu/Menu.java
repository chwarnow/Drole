package drole.menu;


import processing.core.PApplet;
import processing.core.PVector;
import codeanticode.glgraphics.GLTexture;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;
import com.madsim.tracking.kinect.Kinect;
import com.madsim.tracking.kinect.KinectUserEventListener;
import com.madsim.ui.kinetics.KinectInput;
import com.madsim.ui.kinetics.MouseXYInput;
import com.madsim.ui.kinetics.gestures.AngleDetection;
import com.madsim.ui.kinetics.gestures.AngleDetectionListener;
import com.madsim.ui.kinetics.gestures.RipInterpreter;
import com.madsim.ui.kinetics.gestures.RipMotionListener;
import com.madsim.ui.kinetics.gestures.RotationInterpreter;
import com.madsim.ui.kinetics.gestures.TwoHandPushInterpreter;
import com.madsim.ui.kinetics.gestures.TwoHandPushListener;


public class Menu implements RipMotionListener, AngleDetectionListener, TwoHandPushListener, KinectUserEventListener {

	private Engine e;
	
	private Kinect kinect;
	
	private Drawable[] worlds;
	
	private MenuGlobe menuGlobe;
	
	private float NUM_WORLDS = 5;
	
	private int activeWorld;
	
	public boolean inWorld = false;
	
	private float a = 0.0f;
	private float radius;
	
	float distanceBetweenWorlds;

	private GLTexture[] tex = new GLTexture[(int)NUM_WORLDS];
	
	private float worldGravity = 0.1f;

	private MouseXYInput mouseXY;
	
	private KinectInput kiLeftHand, kiRightHand;
	
	private RotationInterpreter ri;

	private RipInterpreter ripi;
	
	private TwoHandPushInterpreter pushi;
	
	private AngleDetection angleDetectionLeft, angleDetectionRight;
	
	private PVector bodyMovement = new PVector(0, 0, 0);
	private PVector lastBodyPosition = new PVector(0, 0, 0);
	private PVector bodyMovementThreshold = new PVector(30.0f, 30.0f, 30.0f);
	
	private boolean bodyLockdown = false;
	
	public Menu(Engine e, Kinect kinect, MenuGlobe menuGlobe, PVector position, float radius, Drawable[] worlds) {
		this.e = e;
		
		this.worlds = worlds;
		NUM_WORLDS = worlds.length;
		
		this.kinect = kinect;
		kinect.addUserEventListener(this);
		
		this.menuGlobe = menuGlobe;
		
		this.radius = radius;
		
		a = e.p.random(0, PApplet.TWO_PI);
		
//		mouseXY = new MouseXYInput();
//		e.p.addMouseMotionListener(mouseXY);
		
		kiRightHand = new KinectInput(kinect, Kinect.SKEL_RIGHT_HAND);
		kiLeftHand = new KinectInput(kinect, Kinect.SKEL_LEFT_HAND);
		
		ri = new RotationInterpreter(kiRightHand, 0);
		ri.lock();
		
		ripi = new RipInterpreter(this, kiLeftHand, -1, 2);
		ripi.lock();
		
		pushi = new TwoHandPushInterpreter(this, kiLeftHand, kiRightHand, 1, 2);
		pushi.lock();
		
		angleDetectionLeft = new AngleDetection("LEFT_HAND", kinect, Kinect.SKEL_LEFT_HAND, Kinect.SKEL_LEFT_SHOULDER, -1400f, 1);
		angleDetectionLeft.addListener(this);
		
		angleDetectionRight = new AngleDetection("RIGHT_HAND", kinect, Kinect.SKEL_RIGHT_HAND, Kinect.SKEL_RIGHT_SHOULDER, -1400f, 1);
		angleDetectionRight.addListener(this);
		
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
	
	private void updateBodyMovement() {
		bodyMovement = PVector.sub(lastBodyPosition, kinect.getJoint(Kinect.SKEL_TORSO));
		lastBodyPosition = kinect.getJoint(Kinect.SKEL_TORSO);
		e.p.pinLog("Body Movement", bodyMovement);
	}
	
	private void checkBodyMovement() {
		if(
			Math.abs(bodyMovement.x) > bodyMovementThreshold.x || 
			Math.abs(bodyMovement.y) > bodyMovementThreshold.y ||
			Math.abs(bodyMovement.z) > bodyMovementThreshold.z
		) {
			e.p.pinLog("BODY LOCK DOWN", "ON");
			bodyLockdown = true;
//			ri.lock();
//			ripi.lock();
		} else {
			e.p.pinLog("BODY LOCK DOWN", "OFF");
			bodyLockdown = false;
		}
	}
	
	public void update() {
		updateBodyMovement();
		checkBodyMovement();
		
		ripi.update();
		pushi.update();
		angleDetectionLeft.update();
		angleDetectionRight.update();

		
//		a = (e.p.frameCount / 100.0f) % PApplet.TWO_PI;
		
		calculateActiveWorld();
		
		float goal = getActiveWorldA();
		
		a = ri.get()[0];
		
//		a = PApplet.lerp(a, goal, worldGravity);
		
		/*
		if(PApplet.abs(a - aa) > 0.001f) a = PApplet.lerp(a, aa, worldGravity);		
		a = getActiveWorldA();
		*/
		
		menuGlobe.setGestureRotation(a);
	}
	
	public void draw() {
		if(menuGlobe.mode() == Drawable.ON_SCREEN) {
			e.g.pushMatrix();
		
				e.g.translate(0, 0, -1500);
			
				e.g.noStroke();
			
				for(int i = 0; i < NUM_WORLDS; i++) {
					if(i == activeWorld) e.g.fill(200, 0, 0);
					else e.g.fill(255);
					
					PVector p1 = getPointOnCircleXZ(radius, a, i, 0, 1, 0);
					e.g.pushMatrix();
						e.g.translate(p1.x, p1.y, p1.z);
						e.g.sphere(20);
					e.g.popMatrix();
				}
			
				e.g.fill(200);
				e.g.pushMatrix();
					e.g.translate(0, 0, 0);
					e.g.ellipse(0, 0, 10, 10);
				e.g.popMatrix();
		
			e.g.popMatrix();
		}
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
		if(angleDetectionLeft.status() == AngleDetection.IN_ANGLE && !inWorld) {
			e.p.logLn("Pull Gesture found");
			e.transitionBetweenDrawables(menuGlobe, worlds[activeWorld]);
			inWorld = true;
		}
	}

	@Override
	public void inAngle(String id) {
		e.p.pinLog("Angle "+id, "IN");
		
		if(id == "LEFT_HAND" && !bodyLockdown) {
			ripi.unlock();
			pushi.unlock();
		}
		
		if(id == "RIGHT_HAND" && !bodyLockdown) {
			ri.unlock();
			pushi.unlock();
		}
	}

	@Override
	public void lostAngle(String id) {
		e.p.pinLog("Angle "+id, "OUT");
		
		if(id == "LEFT_HAND") {
			ripi.lock();
			pushi.lock();
		}
		
		if(id == "RIGHT_HAND") {
			ri.lock();
			pushi.lock();
		}
	}

	@Override
	public void pushGestureFound() {
		if(angleDetectionLeft.status() == AngleDetection.IN_ANGLE && angleDetectionRight.status() == AngleDetection.IN_ANGLE && inWorld) {
			e.p.logLn("Two hand push occured!");
			ripi.forceCooldown();
			e.transitionBetweenDrawables(worlds[activeWorld], menuGlobe);
			inWorld = false;
		}
	}

	@Override
	public void trackingUser() {
		ripi.lock();
		pushi.lock();
		angleDetectionLeft.unlock();
		angleDetectionRight.unlock();
	}

	@Override
	public void lostUser() {
		ripi.lock();
		pushi.lock();
		angleDetectionLeft.lock();
		angleDetectionRight.lock();
	}
	
}
