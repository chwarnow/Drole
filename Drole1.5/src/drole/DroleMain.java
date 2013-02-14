package drole;

import java.util.ArrayList;

import com.christopherwarnow.bildwelten.BildweltOptik;

import drole.engine.Drawable;
import drole.engine.Drawlist;
import drole.engine.Engine;
import drole.engine.optik.OffCenterOptik;
import drole.engine.optik.StdOptik;
import drole.gfx.room.Room;
import drole.settings.Settings;
import drole.tracking.PositionTarget;
import drole.tracking.PositionTargetListener;
import drole.tracking.TargetBox3D;
import drole.tracking.TargetDetection;
import drole.tracking.TargetSphere;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.core.PVector;
import SimpleOpenNI.*;

public class DroleMain extends PApplet implements PositionTargetListener {

	private static final long serialVersionUID = 1L;

	private String DEBUG 				= "DEBUG";
	private String FORCED_DEBUG 		= "FORCED_DEBUG";
	private String LOGO 				= "LOGO";
	private String LOGO2 				= "LOGO2";
	private String TRANSIT_TO_LIVE 		= "TRANSIT_TO_LIVE";
	private String TRANSIT_FROM_LIVE	= "TRANSIT_FROM_LIVE";
	private String LIVE 				= "LIVE";
	private String ZOOMING 				= "ZOOMING";
	private String ROTATING 			= "ROTATING";
	private String MODE 				= DEBUG;

	private boolean FREEMODE			= false;
	
	/* GUI */
	private Image logoGrey;
	private Image logoColor;
	private Ellipse logoBG;
	
	private int background = color(27);
//	private Image background;

	private SimpleOpenNI context;
	private float zoomF = 0.5f;
	private float rotX = radians(180); // by default rotate the hole scene
										// 180deg around the x-axis,

	// the data from openni comes upside down
	private float rotY = radians(0);
	private boolean autoCalib = true;

	private PVector bodyCenter = new PVector();
	private PVector bodyDir = new PVector();

	/* Users Head */
	private PVector head = new PVector(0, 0, 3000);
	
	/* Logging */
	private ArrayList<String> logs = new ArrayList<String>();
	private boolean newLine = true;
	
	/* Engine */
	private Engine engine;
	
	/* Optiks */
	private OffCenterOptik offCenterOptik;
	
	/* Drawlists */
	private Drawlist overlayDrawlist;
	private Drawlist roomDrawlist;
	private Drawlist menuDrawlist;
	private Drawlist optikWorldDrawlist;
	
	/* Skybox */
	private Room room;
	
	/* Globe */
	private PImage globeTexture;
	private PVector globePosition = new PVector(0, -900, 0);
	private PVector globeSize = new PVector(900, 100, 100);
	private RibbonGlobe globe;
	
	private float horizontalViewAlpha = 0.0f;
	private float verticalViewAlpha = 0.0f;

	private PFont mainFont;

	// Position and Gesture Detection
	private TargetDetection targetDetection = new TargetDetection();
	private PositionTarget holdingTarget;
	private PositionTarget rotationTarget;
	
	private float usersArmLength = 0;

	private float[] leftHandSampling 		= new float[10];
	private short leftHandSamplingIndex 	= 0;
	private float[] rightHandSampling 		= new float[10];
	private short rightHandSamplingIndex 	= 0;	
	
	private float rotationMapStart = 0, rotationMapEnd = 0;
	
	private BildweltOptik testOptik;
	
	public void setup() {
		size(1080, 1080, PGraphics.OPENGL);
		
		logLn("Starting Drole!");
		logLn("Executing at : '"+System.getProperty("user.dir").replace("\\", "/")+"'");
		
		logLn("Initializing Engine ...");
			engine = new Engine(this);
			
			engine.addOptik("std", new StdOptik(this));
			engine.activateOptik("std");
			
			offCenterOptik = new OffCenterOptik(
				this,
				Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM,
				Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM,
				Settings.REAL_SCREEN_DIMENSIONS_DEPTH_MM,
				Settings.REAL_SCREEN_POSITION_X_MM,
				Settings.REAL_SCREEN_POSITION_Y_MM,
				Settings.REAL_SCREEN_POSITION_Z_MM
			);
			
			overlayDrawlist = new Drawlist(this);
			engine.addDrawlist("Overlay", overlayDrawlist);
		logLn("Engine is setup!");
			
		/* FONTS */
		mainFont = createFont("Helvetica", 12);
		textFont(mainFont);
		
		/* KINECT */
		if(FREEMODE) logLn("We are using forced FREEMODE!");
		else logLn("FREEMODE was NOT forced, checking the kinect!");
		
		if(!FREEMODE) context = new SimpleOpenNI(this);

		// enable depthMap generation
		if(!FREEMODE) {
			if(context.enableDepth() == false) {
				logLn("Can't open the depthMap, maybe the camera is not connected ... switching to free mode!");
				FREEMODE = true;
			}
		}
		
		// enable skeleton generation for all joints
		if(!FREEMODE) context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
		
		if(!FREEMODE) setupGestureDetection();

		/* CONTENT */
		setupLogo();
		
		setupRoom();
		
		setupMenu();
		
		setupOptikWorld();
		
		/* START */
		if(FREEMODE) {
			menuDrawlist.fadeAllIn(500);
			switchMode(LIVE);
		}
		
	}
	
	private void truncateLog() {
		if(logs.size() > Settings.MAX_LOG_ENTRYS) logs.remove(0);
	}
	
	public void logLn(String msg) {
		logs.add(msg);
		println(msg);
		newLine = true;
		truncateLog();
	}

	public void log(String msg) {
		if(newLine) {
			logs.add(msg);
		} else {
			String newLog = logs.get(logs.size()-1)+msg;
			logs.set(logs.size()-1, newLog);
		}
		
		print(msg);
		newLine = false;
		truncateLog();
	}
	
	private void switchMode(String MODE) {
		if(this.MODE != FORCED_DEBUG) {
			logLn("Switching MODE from '" + this.MODE + "' to '" + MODE + "'");
			this.MODE = MODE;
		} else {
			logLn("Switching MODE from '" + this.MODE + "' to '" + MODE + "' DENIED!");
		}
	}

	private void setupGestureDetection() {
		TargetBox3D holdingTargetShapeBox = new TargetBox3D(0, -200, -800, 400, 400, 1000);
		holdingTarget = new PositionTarget(this, "HOLDING_TARGET", context, holdingTargetShapeBox, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_TORSO);
		targetDetection.targets.add(holdingTarget);
		
		TargetSphere rotationTargetShapeSphere = new TargetSphere(0, 200, 0, 700);
		rotationTarget = new PositionTarget(this, "ROTATION_TARGET", context, rotationTargetShapeSphere, SimpleOpenNI.SKEL_LEFT_HAND, SimpleOpenNI.SKEL_RIGHT_HAND);
		targetDetection.targets.add(rotationTarget);

		/*
		TargetBox3D rotationTargetShapeBox = new TargetBox3D(0, 200, 0, 1200, 1000, 1000);
		rotationTarget = new PositionTarget(this, "ROTATION_TARGET", context, rotationTargetShapeBox, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_TORSO);
		targetDetection.targets.add(rotationTarget);
		*/
	}

	private void setupRoom() {
		logLn("Initializing Room ...");
		roomDrawlist = new Drawlist(this);
		
		room = new Room(this, "data/room/drolebox2/panorama03.");
		room.position(0, -950, 0);
		
		roomDrawlist.add(room);
		
		engine.addDrawlist("Room", roomDrawlist);
	}
	
	private void setupMenu() {
		logLn("Initializing Menu ...");
		menuDrawlist = new Drawlist(this);
		
		globeTexture = loadImage("data/images/Karte_1.jpg");
		globe = new RibbonGlobe(this, globePosition, globeSize, globeTexture);
		globe.scale(.5f, .5f, .5f);
		
		menuDrawlist.add(globe);
		
		engine.addDrawlist("Menu", menuDrawlist);
	}	
	
	private void setupOptikWorld() {
		// testwise optik scene
		testOptik = new BildweltOptik(this);
		optikWorldDrawlist = new Drawlist(this);
		optikWorldDrawlist.add(testOptik);
		
		optikWorldDrawlist.hideAll();
		engine.addDrawlist("OptikWorld", optikWorldDrawlist);
	}

	private void setupLogo() {
		logoGrey = new Image(this, "images/logo-grey.png");
		logoColor = new Image(this, "images/logo-color.png");
		logoBG = new Ellipse(this, 250, 250, 90);
	}

	private void setViewAlpha() {
		horizontalViewAlpha = atan(abs(head.x - globePosition.x) / abs(head.z - globePosition.z));
		horizontalViewAlpha = (head.x <= globePosition.x) ? horizontalViewAlpha : -horizontalViewAlpha;

		verticalViewAlpha = atan(abs(head.x - globePosition.x) / abs(head.y - globePosition.y));
		verticalViewAlpha = (head.y <= globePosition.y) ? verticalViewAlpha : -verticalViewAlpha;
	}

	private void updateHead() {
		if(context.isTrackingSkeleton(1)) {
			PVector thead = new PVector();
			context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_HEAD, thead);
			head = offCenterOptik.updateHeadPosition(thead);
		}
	}

	public void draw() {
		// update the cam
		if(!FREEMODE) context.update();

		if(!FREEMODE) updateHead();

		setViewAlpha();

		// set the scene pos
		translate(width / 2, height / 2, 0);

		if(MODE == DEBUG || MODE == FORCED_DEBUG) {
			resetMatrix();

			perspective(radians(45), (float) width / (float) height, 10, 150000);

			background(0, 0, 0);

			translate(0, 0, -1000); // set the rotation center of the scene 1000
									// in front of the camera

			rotateX(rotX);
			rotateY(rotY);
			scale(zoomF);
			
			drawRealWorldScreen();
			
			drawMainScene();
			
			if(!FREEMODE) { 
				int[] depthMap = context.depthMap();
				int steps = 3; // to speed up the drawing, draw every third point
				int index;
				PVector realWorldPoint;
	
				strokeWeight(1);
				stroke(100);
				for (int y = 0; y < context.depthHeight(); y += steps) {
					for (int x = 0; x < context.depthWidth(); x += steps) {
						index = x + y * context.depthWidth();
						if (depthMap[index] > 0) {
							// draw the projected point
							realWorldPoint = context.depthMapRealWorld()[index];
							point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
						}
					}
				}
	
				// draw the skeleton if it's available
				int[] userList = context.getUsers();
				for(int i = 0; i < userList.length; i++) {
					if(context.isTrackingSkeleton(userList[i])) drawSkeleton(userList[i]);
				}
	
				targetDetection.check();
				
				pushStyle();
				stroke(0, 200, 0);
				noFill();
					for(PositionTarget pt : targetDetection.targets) pt.drawTarget(g);
				popStyle();
			}
			
			/*
			if(rotationTarget.inTarget()) {
				noFill();
				stroke(200, 200, 0);
				strokeWeight(3);
				
				PVector leftHand = new PVector(0, 0, 0);
				PVector rightHand = new PVector(0, 0, 0);
				context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
				context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);				

				println(PVector.angleBetween(leftHand, rightHand));
				globe.rotation = PVector.angleBetween(leftHand, rightHand);
			}
			*/
			
			// draw the kinect cam
			if(!FREEMODE) context.drawCamFrustum();
		}

		if (MODE == LOGO || MODE == LOGO2 || MODE == TRANSIT_TO_LIVE) drawLogo();
		
		if (MODE == TRANSIT_TO_LIVE && logoBG.mode() == Drawable.OFF_SCREEN) switchMode(LIVE);
		
		if (MODE == TRANSIT_FROM_LIVE && globe.mode() == Drawable.OFF_SCREEN) backToLogo();

		if (MODE == LIVE || MODE == TRANSIT_FROM_LIVE || MODE == ZOOMING || MODE == ROTATING) {
			background(background);

			engine.activateOptik("OffCenter");
			if(!FREEMODE) offCenterOptik.updateHeadPosition(head);
			offCenterOptik.calculate();
			offCenterOptik.set();

			//drawOffCenterVectors(head);

			// Draw our holo object
			drawMainScene();

			// Draw Real World Screen
			// drawRealWorldScreen();

			if(!FREEMODE) {
				targetDetection.check();
				
				if(holdingTarget.inTarget()) {
					// If the arm wasn't mesured yet!
					// Lets mesure the users arm length to map it to the scaling of our globe
					PVector leftHand 		= new PVector(0, 0, 0);
					PVector rightHand 		= new PVector(0, 0, 0);
					PVector leftElbow 		= new PVector(0, 0, 0);
					PVector leftShoulder 	= new PVector(0, 0, 0);
					PVector torso		 	= new PVector(0, 0, 0);
					context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_HAND, leftHand);
					context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_LEFT_HAND, rightHand);
					context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_ELBOW, leftElbow);
					context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_SHOULDER, leftShoulder);
					context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_TORSO, torso);
					
					if(usersArmLength == 0) {
						usersArmLength = leftHand.dist(leftElbow)+leftElbow.dist(leftShoulder);
						System.out.println("Users arm length is: "+usersArmLength+" mm.");
						
						for(int i = 0; i < leftHandSampling.length; i++) leftHandSampling[i] = 0;
					}
					
					leftHandSampling[leftHandSamplingIndex++] = abs(leftHand.z-leftShoulder.z);
					if(leftHandSamplingIndex == leftHandSampling.length) leftHandSamplingIndex = 0;				
					float dHandZ = 0;
					for(int i = 0; i < leftHandSampling.length; i++) dHandZ += leftHandSampling[i];
					dHandZ /= leftHandSampling.length;
					
	//				println(dHandZ+" : "+usersArmLength);
					
					float newScale = map(dHandZ, 0, usersArmLength, 3f, 0.1f);
					globe.easeToScale(new PVector(newScale, newScale, newScale), 300);
					
					if(rotationTarget.inTarget()) {
						rightHandSampling[rightHandSamplingIndex++] = rightHand.x;
						if(rightHandSamplingIndex == rightHandSampling.length) rightHandSamplingIndex = 0;				
						float dHandA = 0;
						for(int i = 0; i < rightHandSampling.length; i++) dHandA += rightHandSampling[i];
						dHandA /= rightHandSampling.length;
							
	//					println(dHandA);
						float rot = map(dHandA, rotationMapStart, rotationMapEnd, -PI, PI);
						if(!Float.isInfinite(rot) && !Float.isNaN(rot)) globe.rotation = rot;  
					}
				}
			}
		}
	}

	private void drawLogo() {
		resetMatrix();
		perspective(radians(45), (float) width / (float) height, 0.1f, 3000);

		background(background);

		pushMatrix();
		translate(0, 0, -1400);

		logoBG.update();
		logoBG.draw();
		logoGrey.update();
		logoGrey.draw();
		logoColor.update();
		logoColor.draw();
		popMatrix();
	}

	// draw the skeleton with the selected joints
	public void drawSkeleton(int userId) {
		strokeWeight(3);

		// to get the 3d joint data
		drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

		drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

		drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

		drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);

		// draw body direction
		getBodyDirection(userId, bodyCenter, bodyDir);

		bodyDir.mult(200); // 200mm length
		bodyDir.add(bodyCenter);

		stroke(255, 200, 200);
		line(bodyCenter.x, bodyCenter.y, bodyCenter.z, bodyDir.x, bodyDir.y,
				bodyDir.z);

		strokeWeight(1);
	}

	public void drawLimb(int userId, int jointType1, int jointType2) {
		PVector jointPos1 = new PVector();
		PVector jointPos2 = new PVector();
		float confidence;

		// draw the joint position
		confidence = context.getJointPositionSkeleton(userId, jointType1, jointPos1);
		confidence = context.getJointPositionSkeleton(userId, jointType2, jointPos2);

		stroke(255, 0, 0, confidence * 200 + 55);
		line(jointPos1.x, jointPos1.y, jointPos1.z, jointPos2.x, jointPos2.y, jointPos2.z);

		drawJointOrientation(userId, jointType1, jointPos1, 50);
	}

	public void drawJointOrientation(int userId, int jointType, PVector pos,
			float length) {
		// draw the joint orientation
		PMatrix3D orientation = new PMatrix3D();
		float confidence = context.getJointOrientationSkeleton(userId, jointType, orientation);
		if(confidence < 0.001f)
			// nothing to draw, orientation data is useless
		return;

		pushMatrix();
		translate(pos.x, pos.y, pos.z);

		// set the local coordsys
		applyMatrix(orientation);

		// coordsys lines are 100mm long
		// x - r
		stroke(255, 0, 0, confidence * 200 + 55);
		line(0, 0, 0, length, 0, 0);
		// y - g
		stroke(0, 255, 0, confidence * 200 + 55);
		line(0, 0, 0, 0, length, 0);
		// z - b
		stroke(0, 0, 255, confidence * 200 + 55);
		line(0, 0, 0, 0, 0, length);
		popMatrix();
	}

	// -----------------------------------------------------------------
	// SimpleOpenNI user events

	public void onNewUser(int userId) {
		println("onNewUser - userId: " + userId);
		println("  start pose detection");

		if (autoCalib) {
			context.requestCalibrationSkeleton(userId, true);
			if (userId == 1)
				switchToLogo2();
		} else {
			context.startPoseDetection("Psi", userId);
		}
	}

	public void onLostUser(int userId) {
		println("onLostUser - userId: " + userId);
		if (userId == 1)
			backToLogo();
	}

	public void onExitUser(int userId) {
		println("onExitUser - userId: " + userId);
		if(userId == 1) backToLogo();
	}

	public void onReEnterUser(int userId) {
		println("onReEnterUser - userId: " + userId);
		if(userId == 1) switchToLive();
	}

	public void onStartCalibration(int userId) {
		println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);

		if (successfull) {
			println("  User calibrated !!!");
			context.startTrackingSkeleton(userId);
			
			if(userId == 1) switchToLive();
		} else {
			println("  Failed to calibrate user !!!");
			println("  Start pose detection");
			context.startPoseDetection("Psi", userId);
		}
	}

	public void onStartPose(String pose, int userId) {
		println("onStartdPose - userId: " + userId + ", pose: " + pose);
		println(" stop pose detection");

		context.stopPoseDetection(userId);
		context.requestCalibrationSkeleton(userId, true);

	}

	public void onEndPose(String pose, int userId) {
		println("onEndPose - userId: " + userId + ", pose: " + pose);
	}

	private void switchToLogo() {
		logoColor.hide();
		logoBG.fadeIn(50);
		logoGrey.fadeIn(50);

		switchMode(LOGO);
	}

	private void switchToLogo2() {
		logoGrey.fadeOut(50);
		logoColor.fadeIn(50);

		switchMode(LOGO2);
	}

	private void backToLogo() {
		if (MODE == LOGO2 || MODE == TRANSIT_TO_LIVE) {

			logoColor.fadeOut(50);
			logoGrey.fadeIn(50);
			switchMode(LOGO);

		} else if (MODE == LIVE) {

			globe.fadeOut(50);
			switchMode(TRANSIT_FROM_LIVE);

		} else if (MODE == TRANSIT_FROM_LIVE) {
			logoBG.fadeIn(50);
			logoColor.fadeIn(50);
			switchMode(LOGO2);
		}

	}

	private void switchToLive() {
		logoColor.fadeOut(50);
		logoGrey.fadeOut(50);
		logoBG.fadeOut(50);

		globe.fadeIn(100);

		switchMode(TRANSIT_TO_LIVE);
	}

	// -----------------------------------------------------------------
	// Keyboard events

	public void keyPressed() {
		switch (key) {
		case ' ':
			context.setMirror(!context.mirror());
			break;
		case 'd':
//			switchToDebug();
			switchMode(FORCED_DEBUG);
			break;
		case 'l':
			if (MODE == LOGO) {
				switchToLogo2();
			}
			if (MODE == LOGO2) {
				switchToLive();
			}
			if (MODE == LIVE || MODE == DEBUG) {
				switchToLogo();
			}
			break;
		case 'g':
			switchToLive();
			break;
		case 'r': 
			if(!globe.getRibbons().get(0).isPivoting()) {
				globe.getRibbons().get(0).createPivotAt(0, 0, -200);
				
				menuDrawlist.fadeAllOut(100);
				optikWorldDrawlist.fadeAllIn(100);
			} else {
				globe.getRibbons().get(0).deletePivot();
				
				menuDrawlist.fadeAllIn(100);
				optikWorldDrawlist.fadeAllOut(100);
			}
		}
		
		switch (keyCode) {
		case LEFT:
			rotY += 0.1f;
			break;
		case RIGHT:
			// zoom out
			rotY -= 0.1f;
			break;
		case UP:
			if (keyEvent.isShiftDown())
				zoomF += 0.1f;
			else
				rotX += 0.1f;
			break;
		case DOWN:
			if (keyEvent.isShiftDown()) {
				zoomF -= 0.01f;
				if (zoomF < 0.01f)
					zoomF = 0.01f;
			} else
				rotX -= 0.1f;
			break;
		}
	}

	public void getBodyDirection(int userId, PVector centerPoint, PVector dir) {
		PVector jointL = new PVector();
		PVector jointH = new PVector();
		PVector jointR = new PVector();

		// draw the joint position
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, jointL);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, jointH);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, jointR);

		// take the neck as the center point
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, centerPoint);

		/*
		 * // manually calc the centerPoint PVector shoulderDist =
		 * PVector.sub(jointL,jointR);
		 * centerPoint.set(PVector.mult(shoulderDist,.5));
		 * centerPoint.add(jointR);
		 */

		PVector up = new PVector();
		PVector left = new PVector();

		up.set(PVector.sub(jointH, centerPoint));
		left.set(PVector.sub(jointR, centerPoint));

		dir.set(up.cross(left));
		dir.normalize();
	}

	public void drawMainScene() {
		pushMatrix();
		pushStyle();
		
			engine.update("Room");
			engine.draw("Room");
		
			engine.update("Menu");
			engine.draw("Menu");

			engine.update("OptikWorld");
			engine.draw("OptikWorld");
			
		popMatrix();
		popStyle();
	}

	private void drawRealWorldScreen() {
		offCenterOptik.drawRealWorldScreen();
	}
	
	@Override
	public void jointEnteredTarget(String name) {
		println("Joint in " + name);
		if(name == "ROTATION_TARGET" && holdingTarget.inTarget()) {
			switchMode(ROTATING);
			globe.rotationSpeed = 0.0f;
			PVector rightHand = new PVector(0, 0, 0);
			context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
			rotationMapStart = rightHand.x - 600;
			rotationMapStart = rightHand.x + 600;
		}
	}
	
	@Override
	public void jointLeftTarget(String name) {
		println("Joint left " + name);
		if(name == "HOLDING_TARGET") {
			globe.easeToScale(new PVector(1, 1, 1), 300);
			globe.rotationSpeed = 0.04f;
			switchMode(LIVE);
		}
		if(name == "ROTATION_TARGET" && holdingTarget.inTarget()) {
			switchMode(ZOOMING);
			globe.rotationSpeed = 0.04f;
		}
	}	
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
//			"--present",
			"--bgcolor=#000000",
			"--present-stop-color=#000000", 
			"--display=1",
			"drole.DroleMain"
		});
	}

}