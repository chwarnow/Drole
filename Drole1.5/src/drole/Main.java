package drole;


import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import codeanticode.glgraphics.GLConstants;

import com.christopherwarnow.bildwelten.BildweltOptik;
import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.drawable.file.Image;
import com.madsim.engine.drawable.geom.Ellipse;
import com.madsim.engine.optik.LookAt;
import com.madsim.engine.optik.OffCenterOptik;
import com.madsim.engine.optik.OrthoOptik;
import com.madsim.engine.optik.StdOptik;
import com.madsim.engine.shader.JustColorShader;
import com.madsim.engine.shader.PolyLightAndColorShader;
import com.madsim.engine.shader.PolyLightAndTextureShader;
import com.madsim.tracking.kinect.PositionTarget;
import com.madsim.tracking.kinect.PositionTargetListener;
import com.madsim.tracking.kinect.TargetBox3D;
import com.madsim.tracking.kinect.TargetDetection;
import com.madsim.tracking.kinect.TargetSphere;

import drole.gfx.assoziation.BildweltAssoziation;
import drole.gfx.fabric.BildweltFabric;
import drole.gfx.ribbon.RibbonGlobe;
import drole.gfx.room.Room;
import drole.settings.Settings;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PMatrix3D;
import processing.core.PVector;
import SimpleOpenNI.*;

public class Main extends EngineApplet implements PositionTargetListener, MouseWheelListener {

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

	private boolean FREEMODE			= !Settings.USE_KINECT;
	
	/* GUI */
	private Image logoGrey;
	private Image logoColor;
	private Ellipse logoBG;

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
	private PVector mouseHead = new PVector(0, 0, 3000);
	
	/* Engine */
	private Engine engine;
	
	/* Optiks */
	private OffCenterOptik offCenterOptik;
	private StdOptik stdOptik;
	private OrthoOptik orthoOptik;
	
	/* Shader */
	private JustColorShader justColorShader;
	private PolyLightAndColorShader polyLightAndColorShader;
	private PolyLightAndTextureShader polyLightAndTextureShader;
	
	/* Skybox */
	private Room room;
	
	/* Globe */
	private PVector globePosition = new PVector(0, 0, -1000);
	private PVector globeSize = new PVector(600, 0, 0);
	private RibbonGlobe globe;

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
	
	/* Bildwelten */
	private BildweltAssoziation bildweltAssoziation;
	private BildweltOptik bildweltOptik;
	private BildweltFabric bildweltFabric;
	
	public void setup() {
		size(Settings.VIRTUAL_SCREEN_WIDTH, Settings.VIRTUAL_SCREEN_HEIGHT, GLConstants.GLGRAPHICS);
		
		logLn("Starting Drole!");
		logLn("Executing at : '"+System.getProperty("user.dir").replace("\\", "/")+"'");
		
		addMouseWheelListener(this);
		
		logLn("Initializing Engine ...");
			engine = new Engine(this);
			
			justColorShader = new JustColorShader(this);
			engine.addShader("JustColor", justColorShader);
			
			polyLightAndColorShader = new PolyLightAndColorShader(this);
			engine.addShader("PolyLightAndColor", polyLightAndColorShader);
			
			polyLightAndTextureShader = new PolyLightAndTextureShader(this);
			engine.addShader("PolyLightAndTexture", polyLightAndTextureShader);
			
			stdOptik = new StdOptik(engine);
			engine.addOptik("Std", stdOptik);

			orthoOptik = new OrthoOptik(engine);
			engine.addOptik("Ortho", orthoOptik);
			
			offCenterOptik = new OffCenterOptik(
				engine,
				Settings.REAL_SCREEN_DIMENSIONS_WIDTH_MM,
				Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM,
				Settings.REAL_SCREEN_DIMENSIONS_DEPTH_MM,
				Settings.REAL_SCREEN_POSITION_X_MM,
				Settings.REAL_SCREEN_POSITION_Y_MM,
				Settings.REAL_SCREEN_POSITION_Z_MM,
				head
			);
			
			engine.addOptik("OffCenter", offCenterOptik);
			
			engine.useOptik("Std");
			
			engine.addOptik("LookAt", new LookAt(engine));
			
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
//		setupLogo();
		
		setupRoom();
		
		setupMenu();
		
//		setupOptikWorld();
		
//		setupAssoziationWorld();
		
//		setupFabricWorld();
		
		/* START */
		if(FREEMODE) {
//			globe.fadeIn(500);
			switchMode(LIVE);
		}
		
		switchMode(LIVE);
	}
	
	public void startShader(String name) {
		engine.startShader(name);
	}
	
	public void stopShader() {
		engine.stopShader();
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
		room = new Room(engine, "data/room/drolebox3/drolebox-cubemap.jpg");
		room.position(0, 0, 0);
		
		engine.addDrawable("Room", room);
	}
	
	private void setupMenu() {
		logLn("Initializing Menu ...");
		globe = new RibbonGlobe(engine, globePosition, globeSize);
		
		engine.addDrawable("Globe", globe);
	}	
	
	private void setupOptikWorld() {
		logLn("Initializing world 'Optik' ...");
		
		// testwise optik scene
		bildweltOptik = new BildweltOptik(engine);
		bildweltOptik.hide();
		
		engine.addDrawable("OptikWorld", bildweltOptik);
	}
	
	private void setupAssoziationWorld() {
		bildweltAssoziation = new BildweltAssoziation(engine);
		bildweltAssoziation.hide();
		
		engine.addDrawable("AssoziationWorld", bildweltAssoziation);
	}
	
	private void setupFabricWorld() {
		bildweltFabric = new BildweltFabric(engine);
		bildweltFabric.hide();
		
		engine.addDrawable("FabricWorld", bildweltFabric);
	}

	private void setupLogo() {
		logoGrey = new Image(engine, "images/logo-grey.png");
		logoColor = new Image(engine, "images/logo-color.png");
		logoBG = new Ellipse(engine, 250, 250, 90);
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

		if(MODE == DEBUG || MODE == FORCED_DEBUG) {
			engine.useOptik("Std");
			
			engine.beginDraw();

			engine.g.background(0, 0, 0);

			engine.g.translate(0, 0, -3000); // set the rotation center of the scene 1000
									// in front of the camera

			engine.g.rotateX(rotX);
			engine.g.rotateY(rotY);
			engine.g.scale(zoomF);
			
			drawRealWorldScreen();
			
			drawMainScene();
			
			if(!FREEMODE) { 
				int[] depthMap = context.depthMap();
				int steps = 3; // to speed up the drawing, draw every third point
				int index;
				PVector realWorldPoint;
	
				engine.g.strokeWeight(1);
				engine.g.stroke(100);
				for (int y = 0; y < context.depthHeight(); y += steps) {
					for (int x = 0; x < context.depthWidth(); x += steps) {
						index = x + y * context.depthWidth();
						if (depthMap[index] > 0) {
							// draw the projected point
							realWorldPoint = context.depthMapRealWorld()[index];
							engine.g.point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
						}
					}
				}
	
				// draw the skeleton if it's available
				int[] userList = context.getUsers();
				for(int i = 0; i < userList.length; i++) {
					if(context.isTrackingSkeleton(userList[i])) drawSkeleton(userList[i]);
				}
	
				targetDetection.check();
				
				engine.g.pushStyle();
				engine.g.stroke(0, 200, 0);
				engine.g.noFill();
					for(PositionTarget pt : targetDetection.targets) pt.drawTarget(engine.g);
				engine.g.popStyle();
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
			
			engine.endDraw();
		}

		if (MODE == LOGO || MODE == LOGO2 || MODE == TRANSIT_TO_LIVE) drawLogo();
		
		if (MODE == TRANSIT_TO_LIVE && logoBG.mode() == Drawable.OFF_SCREEN) switchMode(LIVE);
		
		if (MODE == TRANSIT_FROM_LIVE && globe.mode() == Drawable.OFF_SCREEN) backToLogo();

		if (MODE == LIVE || MODE == TRANSIT_FROM_LIVE || MODE == ZOOMING || MODE == ROTATING) {
			engine.useOptik("OffCenter");

			//drawOffCenterVectors(head);

			// Draw our holo object
			drawMainScene();

			// Draw Real World Screen
			// drawRealWorldScreen();
			
			if(!FREEMODE) {
				targetDetection.check();
				
				if(holdingTarget.inTarget()) {
					// If the arm wasn't measured yet!
					// Lets measure the users arm length to map it to the scaling of our globe
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
						
						// add rotation to assoziation bildwelt
						if(!Float.isInfinite(rot) && !Float.isNaN(rot)) bildweltAssoziation.rotation = rot;
						
						// add rotation to fabric bildwelt
						if(!Float.isInfinite(rot) && !Float.isNaN(rot)) bildweltFabric.rotation = rot;
					}
				}
			}
		}
	}

	private void drawLogo() {
		engine.useOptik("Std");
		
		engine.beginDraw();	
		
			engine.g.pushStyle();
			engine.g.pushMatrix();
			
				engine.g.translate(0, 0, -1400);
		
					logoBG.update();
					logoBG.draw();
					logoGrey.update();
					logoGrey.draw();
					logoColor.update();
					logoColor.draw();
					
			engine.g.popMatrix();
			engine.g.pushStyle();
			
		engine.endDraw();
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
//			if (userId == 1) switchToLogo2();
		} else {
			context.startPoseDetection("Psi", userId);
		}
	}

	public void onLostUser(int userId) {
		println("onLostUser - userId: " + userId);
//		if (userId == 1) backToLogo();
	}

	public void onExitUser(int userId) {
		println("onExitUser - userId: " + userId);
//		if(userId == 1) backToLogo();
	}

	public void onReEnterUser(int userId) {
		println("onReEnterUser - userId: " + userId);
//		if(userId == 1) switchToLive();
	}

	public void onStartCalibration(int userId) {
		println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);

		if (successfull) {
			println("  User calibrated !!!");
			context.startTrackingSkeleton(userId);
			
//			if(userId == 1) switchToLive();
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
			if(globe.menuMode() == RibbonGlobe.MENU) {
//				globe.switchToLights();
				globe.menuMode(RibbonGlobe.LIGHTS);
				
				globe.fadeAllOut(100);
				globe.fadeOut(100);
				
				bildweltOptik.fadeIn(100);
//				assoziationWorldDrawlist.fadeAllIn(100);
//				fabricWorldDrawlist.fadeAllIn(100);
//				optikWorldDrawlist.fadeAllIn(100);
//				assoziationWorldDrawlist.fadeAllIn(100);
			} else {
//				globe.switchToMenu();
				
				globe.menuMode(RibbonGlobe.MENU);
				
				globe.fadeAllIn(100);
				globe.fadeIn(100);

				bildweltOptik.fadeOut(100);
//				assoziationWorldDrawlist.fadeAllOut(100);
//				fabricWorldDrawlist.fadeAllOut(100);

//				optikWorldDrawlist.fadeAllOut(100);
//				assoziationWorldDrawlist.fadeAllOut(100);
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
		engine.draw();
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
	
	public void mouseMoved(MouseEvent e) {
		if(FREEMODE) {
			mouseHead.x = map(e.getX(), width, 0, -offCenterOptik.realScreenDim.x/2f, offCenterOptik.realScreenDim.x/2f);
			mouseHead.y = map(e.getY(), 0, height, offCenterOptik.realScreenPos.y, offCenterOptik.realScreenPos.y+offCenterOptik.realScreenDim.y);
			
			offCenterOptik.updateHeadPosition(mouseHead);
//			println(mouseHead);
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(FREEMODE) {
			mouseHead.z += e.getWheelRotation()*30f;
			
//			println(mouseHead);
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] {
//			"--present",
			"--bgcolor=#000000",
			"--present-stop-color=#000000", 
//			"--display=0",
			"drole.Main"
		});
	}

}