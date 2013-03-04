package drole.menu;


import processing.core.PApplet;
import processing.core.PVector;
import codeanticode.glgraphics.GLModel;
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

import drole.gfx.ribbon.RibbonGlobe;

public class Menu extends Drawable implements RipMotionListener, AngleDetectionListener, TwoHandPushListener, KinectUserEventListener {

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
	
	public Menu(Engine e, Kinect kinect, PVector position, float radius) {
		super(e);
		
		this.kinect = kinect;
		kinect.addUserEventListener(this);
		
		this.radius = radius;
		
		position(position);
		
		dimension = new PVector(radius, radius, radius);
		
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
	
	@Override
	public void update() {
		super.update();
		
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
	}
	
	@Override
	public void draw() {	
		g.pushMatrix();
	
			g.noStroke();
		
			for(int i = 0; i < NUM_WORLDS; i++) {
				if(i == activeWorld) g.fill(200, 0, 0);
				else g.fill(255);
				
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
		
		e.p.pinLog("Left Hand", kiLeftHand.getPosition()[0]+":"+kiLeftHand.getPosition()[1]+":"+kiLeftHand.getPosition()[2]);
		e.p.pinLog("Right Hand", kiRightHand.getPosition()[0]+":"+kiRightHand.getPosition()[1]+":"+kiRightHand.getPosition()[2]);
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
		if(angleDetectionLeft.status() == AngleDetection.IN_ANGLE) {
			e.p.logLn("Pull Gesture found");
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
		if(angleDetectionLeft.status() == AngleDetection.IN_ANGLE && angleDetectionRight.status() == angleDetectionLeft.IN_ANGLE) {
			e.p.logLn("Two hand push occured!");
			ripi.forceCooldown();
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
