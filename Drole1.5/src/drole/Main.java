package drole;


import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import codeanticode.glgraphics.GLConstants;

import com.christopherwarnow.bildwelten.BildweltOptik;
import com.madsim.engine.Engine;
import com.madsim.engine.EngineApplet;
import com.madsim.engine.drawable.Drawable;
import com.madsim.engine.optik.LookAt;
import com.madsim.engine.optik.OffCenterOptik;
import com.madsim.engine.optik.OrthoOptik;
import com.madsim.engine.optik.StdOptik;
import com.madsim.engine.shader.JustColorShader;
import com.madsim.engine.shader.PolyLightAndColorShader;
import com.madsim.engine.shader.PolyLightAndTextureAndEMShader;
import com.madsim.engine.shader.PolyLightAndTextureShader;
import com.madsim.engine.shader.RoomShader;
import com.madsim.fakebildwelten.BildweltFabric;
import com.madsim.fakebildwelten.BildweltMicroMacro;
import com.madsim.tracking.kinect.Kinect;
import com.madsim.tracking.kinect.KinectGFXUtils;
import com.marctiedemann.spektakel.Spektakel;

import drole.gfx.assoziation.BildweltAssoziation;
import drole.gfx.room.Room;
import drole.menu.Menu;
import drole.settings.Settings;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class Main extends EngineApplet implements MouseWheelListener {

	private static final long serialVersionUID = 1L;

	private String DEBUG 				= "DEBUG";
	private String FORCED_DEBUG 		= "FORCED_DEBUG";
	private String LIVE 				= "LIVE";
	private String ZOOMING 				= "ZOOMING";
	private String ROTATING 			= "ROTATING";
	private String MODE 				= DEBUG;

	private boolean FREEMODE			= !Settings.USE_KINECT;
	
	/* GUI */
	private float zoomF = 0.5f;
	private float rotX = radians(180); // by default rotate the hole scene
										// 180deg around the x-axis,

	// Kinect
	private Kinect kinect;
	
	// the data from openni comes upside down
	private float rotY = radians(0);

	/* Users Head */
	private PVector stdHeadPosition = new PVector(0, 0, 3000);
	private PVector head = new PVector(0, 0, 0);
	private PVector mouseHead = new PVector(0, 0, 1500);
	
	/* Engine */
	private Engine engine;
	
	/* Optiks */
	private OffCenterOptik offCenterOptik;
	private StdOptik stdOptik;
	private OrthoOptik orthoOptik;
	
	/* Skybox */
	private Room room;
	
	/* Menu */
	private Menu menu;

	private int fakeActiveWorld = 0;
	
	private float rotationSpeedY = 0.0f;
	private float lastHandsZL = 0, lastHandsZR = 0;
	private PVector lastRightHand = new PVector(0, 0, 0);
	private PVector lastLeftHand = new PVector(0, 0, 0);
	private PVector rightHandSpeedDir = new PVector(0, 0, 0);
	private boolean isRotating = false;
	private boolean isScaling = false;
	private float scaling = 0.0f;
	private boolean backRotationBlock = false;
	private boolean isInGoBackGesture = false;
	private int ticksInGoBackGesture = 0;
	
	private PFont mainFont;
	
	/* Bildwelten */
	private Drawable[] worlds = new Drawable[5];
	
//	private MMWorld bildweltMicroMacro;
	private BildweltMicroMacro bildweltMicroMacro;
	private BildweltAssoziation bildweltAssoziation;
	private BildweltOptik bildweltOptik;
	private BildweltFabric bildweltFabric;
	private Spektakel bildweltSpektakel;
	
	public void setup() {
		size(Settings.VIRTUAL_SCREEN_WIDTH, Settings.VIRTUAL_SCREEN_HEIGHT, GLConstants.GLGRAPHICS);
		
		logLn("Starting Drole!");
		logLn("Executing at : '"+System.getProperty("user.dir").replace("\\", "/")+"'");
		
		addMouseWheelListener(this);
		
		logLn("Initializing Engine ...");
			engine = new Engine(this);
			
			engine.addShader("JustColor", new JustColorShader(this));
			engine.addShader("JustTexture", new JustColorShader(this));
			engine.addShader("PolyLightAndColor", new PolyLightAndColorShader(this));
			engine.addShader("PolyLightAndTexture", new PolyLightAndTextureShader(this));
			engine.addShader("PolyLightAndTextureAndEM", new PolyLightAndTextureAndEMShader(this));
			
			// a spinoff shader based on PolyLightAndTexture by chris
			engine.addShader("RoomShader", new RoomShader(this));
			
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
		
		kinect = new Kinect(this, Kinect.VERBOSE, FREEMODE);

		/* CONTENT */
		setupRoom();
		
//		setupMenu();
		
		setupSpektakel();
		
		setupMicroMacroWorld();
		
		setupOptikWorld();
		
		setupAssoziationWorld();
		
		setupFabricWorld();
		
		setupWorlds();
		
		/* START */
		switchMode(LIVE);
	}
	
	private void setupWorlds() {
		worlds[0] = bildweltFabric;
		worlds[1] = bildweltAssoziation;
		worlds[2] = bildweltMicroMacro;
		worlds[3] = bildweltOptik;
		worlds[4] = bildweltSpektakel;
	}
	
	private void switchMode(String MODE) {
		if(this.MODE != FORCED_DEBUG) {
			logLn("Switching MODE from '" + this.MODE + "' to '" + MODE + "'");
			this.MODE = MODE;
		} else {
			logLn("Switching MODE from '" + this.MODE + "' to '" + MODE + "' DENIED!");
		}
	}
	
	private void setupRoom() {
		logLn("Initializing Room ...");
		room = new Room(engine, "data/room/drolebox3/drolebox-cubemap-cw.jpg");
		room.position(0, 0, 0);
		
		engine.addDrawable("Room", room);
	}
	
	private void setupMenu() {
		logLn("Initializing Menu ...");
		menu = new Menu(engine, new PVector(Settings.MENU_GLOBE_POSITION_X, Settings.MENU_GLOBE_POSITION_Y, Settings.MENU_GLOBE_POSITION_Z), Settings.MENU_GLOBE_RADIUS_MM);
		engine.addDrawable("Menu", menu);
	}
	
	private void setupSpektakel(){
		logLn("Initializing world 'Spektakel' ...");
		
		bildweltSpektakel = new Spektakel(engine);
	//	bildweltSpektakel.hide();
		
		engine.addDrawable("Spektakel", bildweltSpektakel);
	}


	private void setupMicroMacroWorld() {
		logLn("Initializing world 'MicroMacro' ...");
//		bildweltMicroMacro = new MMWorld(engine);
		bildweltMicroMacro = new BildweltMicroMacro(engine);
		bildweltMicroMacro.hide();
		
		engine.addDrawable("MicroMacro", bildweltMicroMacro);
//		engine.addDrawable("MicroMacro", new Drop(engine));
	}	
	
	private void setupOptikWorld() {
		logLn("Initializing world 'Optik' ...");
		
		// testwise optik scene
		bildweltOptik = new BildweltOptik(engine, new PVector(Settings.MENU_GLOBE_POSITION_X, Settings.MENU_GLOBE_POSITION_Y, Settings.MENU_GLOBE_POSITION_Z), new PVector(Settings.MENU_GLOBE_RADIUS_MM, Settings.MENU_GLOBE_RADIUS_MM, Settings.MENU_GLOBE_RADIUS_MM));
		bildweltOptik.hide();
		
		engine.addDrawable("OptikWorld", bildweltOptik);
	}
	
	private void setupAssoziationWorld() {
		logLn("Initializing world 'Assoziation' ...");
		
		bildweltAssoziation = new BildweltAssoziation(engine, new PVector(Settings.MENU_GLOBE_POSITION_X, Settings.MENU_GLOBE_POSITION_Y, Settings.MENU_GLOBE_POSITION_Z), new PVector(Settings.MENU_GLOBE_RADIUS_MM, Settings.MENU_GLOBE_RADIUS_MM, Settings.MENU_GLOBE_RADIUS_MM));
		bildweltAssoziation.hide();
		
		engine.addDrawable("AssoziationWorld", bildweltAssoziation);
	}
	
	private void setupFabricWorld() {
		logLn("Initializing world 'Fabrik' ...");
		
		bildweltFabric = new BildweltFabric(engine);
		bildweltFabric.hide();
		
		engine.addDrawable("FabricWorld", bildweltFabric);
	}

	private void updateHead() {
		PVector thead = kinect.getJoint(Kinect.SKEL_HEAD, stdHeadPosition);
		head = offCenterOptik.updateHeadPosition(thead);
	}

	private void transitToWorld(int worldID) {
		logLn("Transition from Menu to World no. "+worldID);
		engine.transitionBetweenDrawables(menu, worlds[worldID]);
	}
	
	private void transitToMenu(int worldID) {
		logLn("Transition from World no. "+worldID+" to menu");
		engine.transitionBetweenDrawables(worlds[worldID], menu);
	}
	
	public void draw() {
		// Update the cam
		kinect.update();

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
				int[] depthMap = kinect.getDepthMap();
				int steps = 3; // to speed up the drawing, draw every third point
				int index;
				PVector realWorldPoint;
	
				engine.g.strokeWeight(1);
				engine.g.stroke(100);
				for (int y = 0; y < kinect.depthHeight(); y += steps) {
					for (int x = 0; x < kinect.depthWidth(); x += steps) {
						index = x + y * kinect.depthWidth();
						if (depthMap[index] > 0) {
							// draw the projected point
							realWorldPoint = kinect.depthMapRealWorld()[index];
							engine.g.point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
						}
					}
				}
	
				// draw the skeleton if it's available
				KinectGFXUtils.drawSkeleton(kinect.getCurrentUserID());
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
			
			engine.endDraw();
		}

		if (MODE == LIVE) {
			
			
			engine.useOptik("OffCenter");

			//drawOffCenterVectors(head);

			// Draw our holo object
			drawMainScene();

			// Draw Real World Screen
			// drawRealWorldScreen();
			
			if(!FREEMODE) {/*
				pinLog("Head", kinect.getJoint(Kinect.SKEL_HEAD));
				pinLog("Left Hand", kinect.getJoint(Kinect.SKEL_LEFT_HAND));
				pinLog("Left Shoulder", kinect.getJoint(Kinect.SKEL_LEFT_SHOULDER));
				pinLog("Angle (Hand & Shoulder)", PVector.angleBetween(kinect.getJoint(Kinect.SKEL_LEFT_HAND), kinect.getJoint(Kinect.SKEL_LEFT_SHOULDER)));
				pinLog("Angle (Hand & Hip)", PVector.angleBetween(kinect.getJoint(Kinect.SKEL_LEFT_HAND), kinect.getJoint(Kinect.SKEL_LEFT_HIP)));
				pinLog("Angle (Hand & Elbow)", PVector.angleBetween(kinect.getJoint(Kinect.SKEL_LEFT_HAND), kinect.getJoint(Kinect.SKEL_LEFT_ELBOW)));
				pinLog("Angle (Elbow & Hip)", PVector.angleBetween(kinect.getJoint(Kinect.SKEL_LEFT_ELBOW), kinect.getJoint(Kinect.SKEL_LEFT_HIP)));
				pinLog("Dist (Hand & Hip)", PVector.dist(kinect.getJoint(Kinect.SKEL_LEFT_HAND), kinect.getJoint(Kinect.SKEL_LEFT_HIP)));
				
				if(PVector.angleBetween(kinect.getJoint(Kinect.SKEL_LEFT_HAND), kinect.getJoint(Kinect.SKEL_LEFT_SHOULDER)) < 0.16f) {
					pinLog("Scale Gesture", "ON");
					
					scaling = map(PVector.dist(kinect.getJoint(Kinect.SKEL_LEFT_HAND), kinect.getJoint(Kinect.SKEL_LEFT_HIP)), 0f, 1000f, -200, -1500);
					
					menu.position(0, 0, scaling);
					
					pinLog("Scaling", scaling);
					
					isScaling = true;
				} else {
					pinLog("Scale Gesture", "OFF");
					
					isScaling = false;
				}
	
				if(PVector.angleBetween(kinect.getJoint(Kinect.SKEL_RIGHT_HAND), kinect.getJoint(Kinect.SKEL_RIGHT_SHOULDER)) < 0.12f) {
					pinLog("Rotate Gesture", "ON");
					
					if(!isRotating) {
						lastRightHand = kinect.getJoint(Kinect.SKEL_RIGHT_HAND);
						isRotating = true;
					}
					
					float rightHandSpeed = kinect.getJoint(Kinect.SKEL_RIGHT_HAND).x - lastRightHand.x;
					
					lastRightHand = kinect.getJoint(Kinect.SKEL_RIGHT_HAND);
					
					pinLog("Right Hand Speed", rightHandSpeed);
					
					if(abs(rightHandSpeed) > 40) {
						if(backRotationBlock && ((rotationSpeedY < 0.0f && rightHandSpeed > 0.0f) || (rotationSpeedY > 0.0f && rightHandSpeed < 0.0f))) {
							backRotationBlock = false;
						} else {
							rotationSpeedY += map(rightHandSpeed, -500, 500, -0.2f, 0.2f);
							backRotationBlock = true;
						}
					} else {
						if(!backRotationBlock) {
//							rotationSpeedY += map(rightHandSpeed, -40, 40, -0.01f, 0.01f);
						}
					}
				} else {
					pinLog("Rotate Gesture", "OFF");
					isRotating = false;
				}
				
				rotationSpeedY *= 0.90;
				
				if(abs(rotationSpeedY) < 0.001f) {
					rotationSpeedY = 0.0f;
					backRotationBlock = false;
				}

				pinLog("rotationSpeedY", rotationSpeedY);
				
				menu.rotation(0, 0, menu.rotation().z+rotationSpeedY);
				
				// TRANS INTO WORLD
				/*
				if(isScaling && menu.getActiveWorld() != Menu.NO_ACTIVE_WORLD && scaling > -600f && !menu.inWorld) {
					menu.inWorld = true;
					transitToWorld(menu.getActiveWorld());
				}
				*/
				/*
				
				if(!isInGoBackGesture) ticksInGoBackGesture = 0;
				if(menu.inWorld && isScaling && isRotating) {
					
					float handsZL  = lastHandsZL-kinect.getJoint(Kinect.SKEL_LEFT_HAND).z;
					float handsZR  = lastHandsZL-kinect.getJoint(Kinect.SKEL_RIGHT_HAND).z;
					pinLog("Hand Z L", handsZL);
					pinLog("Hand Z R", handsZR);
					if(isInGoBackGesture) {
						if(ticksInGoBackGesture > 30) {
							if(lastHandsZL-handsZL > 100.0f && lastHandsZR-handsZR > 100.0f) {
								transitToMenu(0);
								
								menu.inWorld = false;
							}
						}
						ticksInGoBackGesture++;
					} else {
						ticksInGoBackGesture = 0;
					}
					isInGoBackGesture = true;
				}
				
				lastHandsZL = kinect.getJoint(Kinect.SKEL_LEFT_HAND).z;
				lastHandsZR = kinect.getJoint(Kinect.SKEL_RIGHT_HAND).z;
			*/
			}
		
		//	pinLog("IN WORLD", menu.inWorld);

		}
	
	}


	// -----------------------------------------------------------------
	// Keyboard events

	public void keyPressed() {
		switch (key) {
		case 'd':
//			switchToDebug();
			switchMode(FORCED_DEBUG);
			break;
		case 'r':
			break;
		
		// Spektakel Debug KEYS
		
		case 's':
			bildweltSpektakel.spawnNewToxicSystem();
			break;
		case 'a':
			bildweltSpektakel.spawnNewDude();
			break;
		case '0':
			transitToMenu(fakeActiveWorld);
			break;
		case '1':
			transitToWorld(0);
			fakeActiveWorld = 0;
			break;
		case '2':
			transitToWorld(1);
			fakeActiveWorld = 1;
			break;
		case '3':
			transitToWorld(2);
			fakeActiveWorld = 2;
			break;
		case '4':
			transitToWorld(3);
			fakeActiveWorld = 3;
			break;
		case '5':
			transitToWorld(4);
			fakeActiveWorld = 4;
			break;			
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

	public void drawMainScene() {
		engine.draw();
	}

	private void drawRealWorldScreen() {
		offCenterOptik.drawRealWorldScreen();
	}
	
	public void mouseMoved(MouseEvent e) {
		if(FREEMODE) {
			mouseHead.x = map(e.getX(), width, 0, -offCenterOptik.realScreenDim.x/2f, offCenterOptik.realScreenDim.x/2f);
			mouseHead.y = map(e.getY(), 0, height, offCenterOptik.realScreenPos.y+Settings.REAL_SCREEN_DIMENSIONS_HEIGHT_MM, offCenterOptik.realScreenPos.y+offCenterOptik.realScreenDim.y);
			
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
	

	/*
	 * SimpleOpenNI user events
	 * Redirect to Kinect because SimpleOpenNI can only handle PApplet as a listener
	 */
	public void onNewUser(int uid) { kinect.onNewUser(uid); }
	public void onLostUser(int uid) { kinect.onLostUser(uid); }
	public void onExitUser(int uid) { kinect.onExitUser(uid); }
	public void onReEnterUser(int uid) { kinect.onReEnterUser(uid); }
	public void onStartCalibration(int uid) { kinect.onStartCalibration(uid); }
	public void onEndCalibration(int uid, boolean successfull) { kinect.onEndCalibration(uid, successfull); }
	public void onStartPose(String pose, int uid) { kinect.onStartPose(pose, uid); }
	public void onEndPose(String pose, int uid) { kinect.onEndPose(pose, uid); }
	
	
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