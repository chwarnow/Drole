package kinect_debug;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PMatrix3D;
import processing.core.PVector;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.source.MotionListener;
import xx.codeflower.spielraum.motion.source.kinect.KinectHardware;

public class PureKinect extends PApplet implements MotionListener {

	private static final long serialVersionUID = 1L;
	
	private String executionPath = "";
	
	private long setupCallTime = 0;
	
	private ArrayList<String> logs = new ArrayList<String>();
	private boolean drawLogs = true;	
	
	private PFont logFont;
	
	public static float finalWidth 	= 1080;
	public static float finalHeight = 1080;
	
	public static float resRatio	= 1.0f;
	
	private boolean SHOW_DEPTH = false;
	
	private boolean drawSkeleton = false;
	
	private MotionDataSet mds = new MotionDataSet();
	
	private KinectHardware motionSource;
	
	public void setup() {
		setupCallTime = System.currentTimeMillis();
		
		size((int)(finalWidth*resRatio), (int)(finalHeight*resRatio), OPENGL);
		
		smooth();
		noStroke();
		
		logFont = createFont("Helvetica", 12);
		textFont(logFont);
		textAlign(LEFT);
		
		log("Setting up at "+setupCallTime+" ...");
		
		executionPath = System.getProperty("user.dir");
		log("Execution path: "+executionPath.replace("\\", "/"));
		
		initKinect();
		startKinect();
	}
	
	private void log(Object o) {
		if(logs.size() > 50) logs.clear();
		logs.add(o.toString());
		println(o);
	}
	
	private void drawLogs() {
		fill(0, 200, 0);
		
		for(int i = 0; i < logs.size(); i++) text(logs.get(i), 100, 100+(i*14));
		
		text(mds.HEAD.toString(), (width/2), (height/2));
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
		popMatrix();
	}
	
	public void draw() {
		if(motionSource != null) motionSource.update();
		
		background(0);
		drawScene();
	}
	
	private void drawScene() {
		drawDepth();
		drawSkeleton();
		drawLogs();
	}
	
	private void drawDepth() {
		if(motionSource.isRunning()) {
			image(motionSource.kinect.depthImage(), (width/2)-(motionSource.kinect.depthWidth()/2), (height/2)-(motionSource.kinect.depthHeight()/2));			
		}
	}
	
	public void keyPressed() {
		log(keyCode);
		if(keyCode == 32) startKinect();
		if(keyCode == 68) SHOW_DEPTH = !SHOW_DEPTH;
		
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
	}

	@Override
	public void onLostUser(int userid) {
		log("Lost user "+userid);
	}

	@Override
	public void onNewUserData(int userid, MotionDataSet mds) {
		this.mds = mds;
		log(mds.HEAD);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { 
		"--present",
		"--bgcolor=#000000",
		"--present-stop-color=#000000",
		"--display=0",
		"kinect_debug.PureKinect"
	    });
	}
	
}
