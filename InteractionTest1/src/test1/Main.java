package test1;

import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PMatrix3D;
import processing.core.PVector;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.source.MotionListener;
import xx.codeflower.spielraum.motion.source.kinect.KinectHardware;

public class Main extends PApplet implements MotionListener {

	private static final long serialVersionUID = 1L;
	
	private String executionPath = "";
	
	private long setupCallTime = 0; 
	
	private SceneConfigFile scene;
	
	private ArrayList<String> logs = new ArrayList<String>();
	private boolean drawLogs = true;
	
	private ArrayList<String> fixedLogs = new ArrayList<String>();
	private boolean drawFixedLogs = true;	
	
	private PFont logFont;
	
	public static float finalWidth 	= 1080;
	public static float finalHeight = 1080;
	
	public static float resRatio	= 1.0f;
	
	private boolean SHOW_DEPTH = false;
	
	private boolean drawSkeleton = false;
	
	private MotionDataSet mds = new MotionDataSet();
	
	private float sphereRadius = 1.0f;
	
	private KinectHardware motionSource;
	
	private float camX = 0, camY = 0, camZ = -10;
	
	private float fx = 0, fy = 0, fz = 0;
	
	private PVector[] cubes = new PVector[20];
	
	public void setup() {
		setupCallTime = System.currentTimeMillis();
		
		size((int)(finalWidth*resRatio), (int)(finalHeight*resRatio), OPENGL);
		
		logFont = createFont("Helvetica", 12);
		textFont(logFont);
		
		log("Setting up at "+setupCallTime+" ...");
		
		executionPath = System.getProperty("user.dir");
		log("Execution path: "+executionPath.replace("\\", "/"));
		
		if(!loadConfig()) exit();

		setScene();
		
		for(int i = 0; i < cubes.length; i++) {
			cubes[i] = new PVector((float)Math.random()*width, (float)Math.random()*height, ((float)(-1000*Math.random())));
		}
		
		initKinect();
		startKinect();
	}
	
	private void log(Object o) {
		if(logs.size() > 50) logs.clear();
		logs.add(o.toString());
		println(o);
	}
	
	private boolean loadConfig() {
		log("Loading config ...");
		String filename = executionPath+"/data/setup.ini";
		scene = new SceneConfigFile(filename);
		try {
			scene.parse();
		} catch(IOException e) {
			log("Couldn't read config file '"+filename+"'");
			return false;
		}
		
		return true;
	}
	
	private void setScene() {
		imageMode(CORNERS);
		noStroke();
		/*
		float fov = radians(60f);
		float cameraZ = (height/2.0f) / (float)Math.tan(fov/2.0f);
		perspective(fov, (float)width/(float)height, cameraZ/10.0f, cameraZ*10.0f);
		*/
	}
	
	private void drawLogs() {
		fill(255);
		
		for(int i = 0; i < logs.size(); i++) text(logs.get(i), 10, 200+(i*14));
	}
	
	private void drawSkeleton() {
		pushMatrix();
			translate((width/2.0f)-(motionSource.kinect.depthWidth()/2.0f), (height/2.0f)-(motionSource.kinect.depthHeight()/2.0f), 0);
			
			fill(255, 0, 0);
			ellipse(mds.HEAD.x, mds.HEAD.y, 20, 20);
			ellipse(mds.NECK.x, mds.NECK.y, 20, 20);
			ellipse(mds.LEFT_HIP.x, mds.LEFT_HIP.y, 20, 20);
			ellipse(mds.RIGHT_HIP.x, mds.RIGHT_HIP.y, 20, 20);
			ellipse(mds.LEFT_ELBOW.x, mds.LEFT_ELBOW.y, 20, 20);
			ellipse(mds.RIGHT_ELBOW.x, mds.RIGHT_ELBOW.y, 20, 20);
			ellipse(mds.RIGHT_HAND.x, mds.RIGHT_HAND.y, 20, 20);
			ellipse(mds.LEFT_HAND.x, mds.LEFT_HAND.y, 20, 20);
			ellipse(mds.LEFT_KNEE.x, mds.LEFT_KNEE.y, 20, 20);
			ellipse(mds.RIGHT_KNEE.x, mds.RIGHT_KNEE.y, 20, 20);
			ellipse(mds.RIGHT_FOOT.x, mds.RIGHT_FOOT.y, 20, 20);
			ellipse(mds.LEFT_FOOT.x, mds.LEFT_FOOT.y, 20, 20);
			
			fill(255, 255, 255);
			text(mds.HEAD.toString(), mds.HEAD.x, mds.HEAD.y);
		popMatrix();
	}
	
	public void draw() {
		if(motionSource != null) motionSource.update();
		
		background(0);
		
		/*
		setOffScreenCamera();
			if(SHOW_DEPTH && motionSource.isRunning()) drawDepth();
		stopCamera();
		*/
		
		drawScene();
		
		setHUDCamera();
		
			if(drawSkeleton && motionSource.isRunning()) drawSkeleton();
			
			if(drawLogs) drawLogs();
			
			//drawSmallDepth();
		
		stopCamera();
		
		if(frameCount%100 == 0) println(frameRate);
	}
	
	public void setOffCenterCamera() {
		beginCamera();
		
		 camera(fx, fy, fz, 
		         fx, fy, 0, 
		         0, 1, 0); 
		  float near = fz; 

		float left, right, top, bottom; 
		  float angle = radians(60.0f); 
		  float facd = (width/2.0f) / tan(angle / 2.0f); 
		  near = 20.0f; 
		  left = -(width/2.0f) + fx; 
		  right = (width/2.0f) + fx; 
		  top = -(height/2.0f) + fy; 
		  bottom = (height/2.0f) + fy; 
		  left /= facd / fz; 
		  right /= facd / fz; 
		  top /= facd / fz; 
		  bottom /= facd / fz; 
		  left *= near / fz; 
		  right *= near / fz; 
		  top *= near / fz; 
		  bottom *= near / fz; 
		  frustum(left, right, top, bottom, near, 60000); 
	}
	
	private void setSceneCamera() {
		beginCamera();
		camera(camX, camY, camZ, width/2f, height/2f, -1000, 0, 1f, 0);
	}
	
	private void setHUDCamera() {
		beginCamera();
		camera(width/2f, height/2f, 1000, width/2f, height/2f, 0, 0, 1f, 0);
	}
	
	private void stopCamera() {
		endCamera();
	}
	
	private void drawScene() {
		lights();
		
//		camX = map(mds.HEAD.x, 220, 550, 50, -50);
		camX = mds.HEAD.x;
		camY = mds.HEAD.y;
		//camZ = map(mds.HEAD.z, 2500, 1100, 200, -200);
		camZ = mds.HEAD.z;
		
		pointLight(200, 200, 200, width-100f, height/2f, 0);
		
		/*
		pushMatrix();
			translate(width/2.0f, height/2.0f, 0);
			
			fill(255);
			ellipse(0, 0, width, height);
		popMatrix();
		*/
		
		/*
		fill(0, 0, 200);
		pushMatrix();
			translate(0, 0, -20);
			box(20);
		popMatrix();
		
		pushMatrix();
			translate(width-20, 0, -20);
			box(20);
		popMatrix();
			
		pushMatrix();
			translate(width-20, height-20, -20);
			box(20);
		popMatrix();
			
		pushMatrix();
			translate(0, height-20, -20);
			box(20);
		popMatrix();
		*/
		
		setOffCenterCamera();
			/*
			fill(200);
			for(int i = 0; i < cubes.length; i++) {
				pushMatrix();
//					translate(cubes[i].x+camX, cubes[i].y+camY, cubes[i].z+camZ);
					translate(cubes[i].x, cubes[i].y, cubes[i].z);
					
					box(20.0f);
				popMatrix();
			}
			*/
			
			fill(200);
			pushMatrix();
				translate((width/2f)-150, (height/2f)-150, -40000);
				box(300);
			popMatrix();
			
		stopCamera();
	}
	
	private void drawDepth() {
		if(motionSource.isRunning()) {
			image(motionSource.kinect.depthImage(), (width/2)-(motionSource.kinect.depthWidth()/2), (height/2)-(motionSource.kinect.depthHeight()/2));			
		}
	}

	private void drawSmallDepth() {
		if(motionSource.isRunning()) {
			image(motionSource.kinect.depthImage(), 0, 0, 200, 100);
		}
	}	
	
	public void keyPressed() {
		log(keyCode);
		if(keyCode == 32) startKinect();
		if(keyCode == 68) SHOW_DEPTH = !SHOW_DEPTH;
		
//		if(keyCode == 38) camZ--;
//		if(keyCode == 40) camZ++;
		
		if(keyCode == 76) drawLogs = !drawLogs;
	}

	private void initKinect() {
		motionSource = new KinectHardware(this);
		motionSource.addListener(this);
		
	  	log("Kinect initialized ...");
	}	
	
	private void startKinect() {
	  	motionSource.start(width, height);
	  	motionSource.changeDataMapping(new PVector(1, 1, 1));
	  	motionSource.kinect.setMirror(true);
		
	  	SHOW_DEPTH = true;
	  	
	  	log("Kinect started ...");
	}
	
	public void dispose() {
		log("Shutting down ...");
		if(motionSource != null) motionSource.stop();
		super.stop();
	}
	
	@Override
	public void onNewUser(int userid) {
		log("New user "+userid+" found!");
		SHOW_DEPTH = false;
	}

	@Override
	public void onLostUser(int userid) {
		log("Lost user "+userid);
	}

	@Override
	public void onNewUserData(int userid, MotionDataSet mds) {
		this.mds = mds;
		log(mds.HEAD);
		recalcFrustum();
	}
	
	private void recalcFrustum() {
		// This is values are more or less guessed try and error values to make it work more or less 
	    float headDist = mds.HEAD.z/10; 
	    float mx = mds.HEAD.x; 
	    float my = mds.HEAD.y; 
	    float fov = (45f*PI / 180.0f); 
	    float rx = sin( map(mx,-500f,500f,0f,PI)) * headDist*0.5f; 
	    float ry = sin(map(my,-500f,500f,0f,PI)) *headDist*1.5f; 

	  //  println(rx); 
	    fx = rx; 
	    fy = ry; 
	    fz = headDist;		
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { 
		"--present",
		"--bgcolor=#000000",
		"--present-stop-color=#000000",
		"--display=1",
		"test1.Main"
	    });
	}
	
}
