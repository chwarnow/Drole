package fhp.fassade.spielraum;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import fhp.fassade.spielraum.scene.Scene;
import fhp.fassade.spielraum.scene.SceneList;
import fhp.fassade.spielraum.scene.SceneParallax;
import fhp.fassade.spielraum.scene.ScenenDoesNotExistsException;

import SimpleOpenNI.*;
import processing.core.*;

public class Spielraum extends PApplet {

	private static final long serialVersionUID = 1L;

	public static final short STARTING 				=  0;
	public static final short INIT_KINECT 			= 10;
	public static final short WAITING_FOR_USER 		= 20;
	public static final short WAITING_FOR_DISTANCE 	= 30;
	public static final short DRAWING_CLOUD_P1		= 40;
	public static final short DRAWING_CLOUD_P2		= 50;
	public static final short DRAWING_WINDOW		= 60;
	
	public SimpleOpenNI context;
	
	private ArrayList<String> logs = new ArrayList<String>();
	
	private boolean drawLogs = true;
	
	private short state = STARTING;
	
	private PFont mainFont, cloudFont, cloudFont2;
	private int mainFontSize = 14, cloudFontSize = 200, cloudFontSize2 = 60;
	
	private PVector headJoint = new PVector(0, 0, -1);
	
	private SceneList sceneList;
	
	public void setup() {
		smooth();
		
		log("Setting size");
		size(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, P3D);
		
		log("Creating fonts");
		hint(ENABLE_NATIVE_FONTS);
		
		mainFont = createFont("DIN", mainFontSize);
		cloudFont = createFont("DIN-Black", cloudFontSize);
		cloudFont2 = createFont("DIN-Medium", cloudFontSize2);
		
		textFont(mainFont);
		
		log("Loading Scenes");
		
		sceneList = new SceneList();
		
		try {
			sceneList.add(this, "Blank");
			sceneList.add(this, "CloudP1");
			sceneList.add(this, "CloudP2");
			sceneList.add(this, "Tapete1");
			sceneList.add(this, "Window");
			
			sceneList.setActiveScene("Blank");
			
			sceneList.startDrawing();
		} catch (ScenenDoesNotExistsException e) {
			e.printStackTrace();
			log("Error while loading Scene!!!");
			stop();
			System.exit(0);
		}
		
		log("Entering main loop");
	}
	
	private void startKinect() {
		log("Starting Kinect Hardware ...");
		context = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
		
		log("Initializing Depth Map");
		context.enableDepth();
		
		log("Enable User Tracking");
		context.enableUser(SimpleOpenNI.SKEL_PROFILE_HEAD_HANDS);
		
		log("Kinect Ready!");
	}
	
	public void draw() {
		updateUserData();
		
		switch(state) {
			case STARTING: drawBackground(0); drawStartScreen(); break;
			case INIT_KINECT: drawBackground(0); drawStartScreen(); break;
			case WAITING_FOR_USER: drawBackground(0); drawDepth(); break;
			case WAITING_FOR_DISTANCE: drawBackground(255); drawDepth(); sceneList.draw(g); drawUser(1); break;
			case DRAWING_CLOUD_P1: drawBackground(255); drawDepth(); sceneList.draw(g); drawUser(1); break;
			case DRAWING_CLOUD_P2: drawBackground(255); drawDepth(); sceneList.draw(g); drawUser(1); break;
			case DRAWING_WINDOW: drawBackground(255); drawDepth(); sceneList.draw(g); drawUser(1); break;
		}
		
		if(drawLogs) {
			if(state == DRAWING_CLOUD_P1 || state == DRAWING_CLOUD_P2 || state == WAITING_FOR_DISTANCE) drawLogs(0);
			else drawLogs(255);
		}
	}
	
	private void drawBackground(int color) {
		fill(color);
		noStroke();
		rect(0, 0, width, height);
	}
	
	private void drawLogs(int color) {
		fill(color);
		noStroke();
		textFont(mainFont);
		try {
			int i = 0;
			for(String s : logs) text("--> "+s, 10, 10+(++i*mainFontSize));
		} catch(ConcurrentModificationException e) {}
	}
	
	private void drawStartScreen() {
		text("STARTING", (width/2)-50, (height/2)-20);
	}

	public void drawDepth() {
		if(context != null) {
			context.update();		
			image(context.depthImage(), width-220, 100, 200, 200);
		}
	}

	private void drawCloudP1() {
		/*
		fill(60);
		textFont(cloudFont);
		text("NR. 2", (width/2), height-120);
		text("PLATTE", (width/2), height-220);
		*/
		
		drawUser(1);
	}

	private void drawCloudP2() {
		/*
		fill(60);
		textFont(cloudFont2);
		text("NR. 2", (width/2), height-120);
		text("PLATTE", (width/2), height-220);
		*/
		
		drawUser(1);
	}
	
	private void drawScene() {
		fill(0);
		noStroke();
		rect(0, 0, width, height);
	}

	private void log(Object msg) {
		if(logs.size() > 40) logs.clear();
		logs.add(msg.toString());
		println("--> "+msg.toString());
	}
	
	// draw the skeleton with the selected joints
	public void drawUser(int userId) {
		fill(200, 0, 30);
		noStroke();
//		ellipse(map(headJoint.x, -750, 750, 0, width), map(headJoint.y, 0, context.depthHeight(), height, 0), headJoint.z/80, headJoint.z/80);
		
		textFont(mainFont);
		fill(0);
		noStroke();
		text(headJoint.x+" "+headJoint.y+" "+headJoint.z, width-400, 30);
		
		if(state == DRAWING_WINDOW) {
			Scene s = sceneList.get("Window");
			SceneParallax sp = (SceneParallax) sceneList.get("Window").getElement("Para1");
			sp.moveTo(map(headJoint.x, 1000, -1000, 20, -20), 0);
		}
	}
	
	private void updateUserData() {
		if(context != null && context.isTrackingSkeleton(1)) {
			context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_HEAD, headJoint);
			checkUserDistance();
		} else {
			headJoint.set(0, 0, -1);
		}
	}

	private void checkUserDistance() {
		boolean found = false;
		if(
				(headJoint.x <= 620 && headJoint.z >= 4500) &&  
				(headJoint.z <= -270 && headJoint.z >= 4000) 
				
		) {
			if(state != DRAWING_CLOUD_P1) {
				state = DRAWING_CLOUD_P1;
				log("Drawing Cloud One");
				
				sceneList.blendTo("CloudP1", 30, 30);
			}
			found = true;
		} else if(
				(headJoint.x <= -270 && headJoint.z >= 0) &&  
				(headJoint.z <= 4000 && headJoint.z >= 3600)
			) {
			if(state != DRAWING_CLOUD_P2) {
				state = DRAWING_CLOUD_P2;
				log("Drawing Cloud Two");
				
				sceneList.blendTo("CloudP2", 30, 30);
			}
			found = true;
		}
		if(!found && (state == DRAWING_CLOUD_P1 || state == DRAWING_CLOUD_P2)) {
			state = WAITING_FOR_DISTANCE;
			sceneList.blendTo("Blank", 30, 30);
		}
	}
	
	// -----------------------------------------------------------------
	// SimpleOpenNI events

	public void onNewUser(int userId) {
		log("New User found: " + userId);
		log("Start pose detection ...");

		context.startPoseDetection("Psi", userId);
	}

	public void onLostUser(int userId) {
		log("Lost User: " + userId);
		if(userId == 1) state = WAITING_FOR_USER;
	}

	public void onStartCalibration(int userId) {
		log("Start calibration for user: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		log("End calibration for user: " + userId + ", successfull: "
				+ successfull);

		if (successfull) {
			log("User calibrated!");
			context.startTrackingSkeleton(userId);
			state = WAITING_FOR_DISTANCE;
		} else {
			log("Failed to calibrate user: "+userId);
			log("Start pose detection ...");
			context.startPoseDetection("Psi", userId);
		}
	}

	public void onStartPose(String pose, int userId) {
		log("Start pose for user: " + userId + ", pose: " + pose);
		log("Stop pose detection ...");

		context.stopPoseDetection(userId);
		context.requestCalibrationSkeleton(userId, true);

	}

	public void onEndPose(String pose, int userId) {
		log("End pose for user: " + userId + ", pose: " + pose);
	}

	public void keyPressed() {
		println(keyCode);
		if(keyCode == 69) {
			sceneList.startEditMode();
		}
		if(keyCode == 83) {
			sceneList.saveScene();
			sceneList.endEditMode();
		}
		if(keyCode == 73) {
			state = INIT_KINECT;
			startKinect();
			state = WAITING_FOR_USER;
		}
		if(keyCode == 32) {
			sceneList.blendTo("Tapete1", 50, 50);
		}
		if(keyCode == 49) {
			sceneList.blendTo("CloudP1", 50, 50);
			state = DRAWING_CLOUD_P1;
		}
		if(keyCode == 50) {
			sceneList.blendTo("CloudP2", 50, 50);
			state = DRAWING_CLOUD_P2;
		}
		if(keyCode == 51) {
			sceneList.blendTo("Window", 50, 50);
			state = DRAWING_WINDOW;
		}
	}

	// WHY BUT WHYYYY?
	@Override
	public void mouseDragged(MouseEvent e) {
		sceneList.mouseDragged(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		sceneList.mousePressed(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		sceneList.mouseReleased(e);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {"--display=2", "--full-screen", "fhp.fassade.spielraum.Spielraum"});
//		PApplet.main(new String[] {"--display=1", "fhp.fassade.spielraum.Spielraum"});
	}
}
