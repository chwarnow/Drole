package xx.codeflower.spielraum.motion;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import ddf.minim.Minim;

import netP5.NetAddress;

import oscP5.OscMessage;
import oscP5.OscP5;

import processing.core.*;
import xx.codeflower.spielraum.motion.content.FunkyFun;
import xx.codeflower.spielraum.motion.content.SimpleMotionSceneAndSoundPlayerExample;
import xx.codeflower.spielraum.motion.content.SimpleMotionSceneExample;
import xx.codeflower.spielraum.motion.content.SimpleMotionScenePlayerExample;
import xx.codeflower.spielraum.motion.content.WelcomeScene;
import xx.codeflower.spielraum.motion.content.aufreisser.AufreisserScene;
import xx.codeflower.spielraum.motion.content.raindrops.RaindropsScene;
import xx.codeflower.spielraum.motion.content.schatten.SchattenScene;
import xx.codeflower.spielraum.motion.content.starfield.StarfieldScene;
import xx.codeflower.spielraum.motion.scene.BeatScene;
import xx.codeflower.spielraum.motion.scene.EditableScene;
import xx.codeflower.spielraum.motion.scene.KanalScene;
import xx.codeflower.spielraum.motion.scene.Scene;
import xx.codeflower.spielraum.motion.scene.SceneList;
import xx.codeflower.spielraum.motion.scene.StartScene;
import xx.codeflower.spielraum.motion.source.KinectHardware;
import xx.codeflower.spielraum.motion.source.MotionDataCollectionPlayer;

public class MApplet extends PApplet {

	private Rectangle2D.Float rect2d = new Rectangle2D.Float();
	
	private static final long serialVersionUID = 1L;

	public static final short STARTING 				=  0;
	public static final short DRAWING_SCENE 		= 30;
	
	private ArrayList<String> logs = new ArrayList<String>();
	
	private boolean drawLogs = true;
	
	private short state = STARTING;
	
	private PFont mainFont, tagFont;
	
	private int startFontSize = 300;
	private int mainFontSize = 14;
	
	private SceneList scenes;
	
	private StartScene startScene;
	private KanalScene kanalScene;
	private BeatScene beatScene;
	
	private SimpleMotionSceneAndSoundPlayerExample sme;
	private SimpleMotionSceneExample sme2;
	private RaindropsScene raindrops;
	private AufreisserScene aufreisser;
	private SchattenScene schatten;
	private StarfieldScene starfield;
	
	private FunkyFun ffs;
	
	private MotionDataCollectionPlayer player = null;
	
	private OscP5 oscP5;
	private NetAddress myRemoteLocation;
	
	private KinectHardware kinect;
	
	private String captureFolder = "data/motion/", captureFile = "capture_1347647473385.motion";
	
	private Minim minim = new Minim(this);
	
	public void setup() {
		log("Setting size");
		size(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, OPENGL);
//		size(700, 600, P3D);
		
		smooth();
		
		log("Sarting sound");
		
//		oscP5 = new OscP5(this, 12000);
//		myRemoteLocation = new NetAddress("10.254.255.9", 12000);
		
		log("Creating fonts");
		hint(ENABLE_NATIVE_FONTS);
		
//		println(PFont.list());
		
		tagFont = createFont("SuperGroteskOT-Bld", startFontSize);
		mainFont = createFont("SuperGroteskOT", mainFontSize);
		
		textFont(mainFont);
		
		log("Loading Scenes");
		
		kinect = new KinectHardware(this);
		
		scenes = new SceneList(this);
		
		/*
		startScene = new StartScene(this, 49, 50);
		kanalScene = new KanalScene(this, 50, 50);
		beatScene = new BeatScene(this, 51, 50);
		
		scenes.addScene(startScene);
		scenes.addScene(kanalScene);
		scenes.addScene(beatScene);
		*/
		
		/*
		kinect.addListener(testScene1);
		kinect.addListener(testScene2);
		
		
		kinect.addListener(sme);
		scenes.addScene(sme);
		*/
		
		ffs = new FunkyFun(this, 52, 50, minim);
//		ffs = new FunkyFun(this, 52, 50, minim);
		
		scenes.addScene(ffs);
		
		log("Entering main loop");
	}
	
	public void draw() {
		//fill(255);
		//rect(0, 0, width, height);
		
		//background(255);
		
		if(player != null) player.update();
		
		if(kinect.isRunning()) {
			kinect.update();
			image(kinect.kinect.depthImage(), 0, 0, width, height);
		}
		
		scenes.draw(g);
		
		if(drawLogs) {
			drawFPS();
			drawLogs();
		}
	}
	
	private void drawFPS() {
		noStroke();
		fill(0, 100);
		rect(width-80, 0, 80, 35);
		
		fill(255);
		textFont(mainFont);
		
		text(String.valueOf(Math.floor(frameRate))+" fps", width-60, 20);
	}
	
	private void drawLogs() {
		noStroke();
		fill(0, 100);
		rect(0, 0, 300, height);
		
		fill(255);
		textFont(mainFont);
		
		try {
			int i = 0;
			for(String s : logs) text("--> "+s, 10, 10+(++i*mainFontSize));
		} catch(ConcurrentModificationException e) {}
	}
	
	public void log(Object msg) {
		if(logs.size() > 40) logs.clear();
		logs.add(msg.toString());
		println("--> "+msg.toString());
	}

	public void keyPressed() {
		if(key == 'r') {
			log("Recording motion data.");
			kinect.recordMotionData();
		}
		if(key == 's') {
			log("Saving motion data to file: ");
			captureFile = "capture_"+System.currentTimeMillis()+".motion";
			log(captureFolder+captureFile);
			
			kinect.saveMotionData(captureFolder+captureFile);
			
//			scenes.removeScene(sme);
			
//			sme = new SimpleMotionSceneAndSoundPlayerExample(this, 49, 50, minim);
			
//			player = new MotionDataCollectionPlayer(this, captureFolder+captureFile);
//			player.addListener(sme);
//			player.start(width, height);
			
//			scenes.addScene(sme);
//			scenes.activateScene(sme);
		}
		if(key == 'p') {
//			sme = new SimpleMotionSceneAndSoundPlayerExample(this, 49, 50, minim);
			
			ffs = new FunkyFun(this, 52, 50, minim);
			
			player = new MotionDataCollectionPlayer(this, captureFolder+captureFile);
			player.addListener(ffs);
			player.start(width, height);
			
			scenes.addScene(ffs);
			scenes.activateScene(ffs);
		}
		if(key == 'k') {
			kinect.start(width, height);
			
			/*
			sme = new SimpleMotionSceneAndSoundPlayerExample(this, 49, 50, minim);
			
			kinect.addListener(sme);
			
			scenes.addScene(sme);
			scenes.activateScene(sme);
			*/
			
/*
			raindrops = new RaindropsScene(this, 50, 50);
			
			kinect.addListener(raindrops);
			
			scenes.addScene(raindrops);
			scenes.activateScene(raindrops);
*/
			

			aufreisser = new AufreisserScene(this, 50, 50);
			
			kinect.addListener(aufreisser);
			
			scenes.addScene(aufreisser);
			scenes.activateScene(aufreisser);

			
/*
			schatten = new SchattenScene(this, 50, 50);
			
			kinect.addListener(schatten);
			
			scenes.addScene(schatten);
			scenes.activateScene(schatten);
*/
			
/*
			starfield = new StarfieldScene(this, 50, 50);
			
			kinect.addListener(starfield);
			
			scenes.addScene(starfield);
			scenes.activateScene(starfield);
			
*/
		}
		if(key == ' ') {
			drawLogs = !drawLogs;
		}
		
//		println(keyCode);
		
		if(scenes.sceneExists(keyCode)) {
			scenes.activateScene(keyCode);
			state = DRAWING_SCENE;

//			OscMessage myMessage = new OscMessage("/changescene");				  
//			myMessage.add(keyCode);
//			oscP5.send(myMessage, myRemoteLocation);

		} /*
		else if(keyCode == 67) {
			drawLogs = !drawLogs;
		} else if(keyCode == 69) {
			drawLogs = false;
			if(scenes.activeScene().code == 50) kanalScene.startEditMode();
			if(scenes.activeScene().code == 51) beatScene.startEditMode();
			if(scenes.activeScene().code == 49) startScene.startEditMode();
		} else if(keyCode == 83) {
			drawLogs = true;
			if(scenes.activeScene().code == 49) {
				startScene.save();
				startScene.endEditMode();
			}
			if(scenes.activeScene().code == 50) {
				kanalScene.save();
				kanalScene.endEditMode();
			}
			if(scenes.activeScene().code == 51) {
				beatScene.save();
				beatScene.endEditMode();	
			}
		} else if(keyCode == 81) {
			if(scenes.activeScene().code == 50 || scenes.activeScene().code == 51 || scenes.activeScene().code == 49) {
				EditableScene s = (EditableScene)scenes.activeScene();
				s.keyPressed(keyEvent);
			}
		}
		*/
	}

	// WHY BUT WHYYYY?

	@Override
	public void mouseDragged(MouseEvent e) {
		if(kanalScene != null) kanalScene.mouseDragged(e);
		if(beatScene != null) beatScene.mouseDragged(e);
		if(startScene != null) startScene.mouseDragged(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(kanalScene != null) kanalScene.mousePressed(e);
		if(beatScene != null) beatScene.mousePressed(e);
		if(startScene != null) startScene.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(kanalScene != null) kanalScene.mouseReleased(e);
		if(beatScene != null) beatScene.mouseReleased(e);
		if(startScene != null) startScene.mouseReleased(e);
	}
	
	public void dispose() {
		minim.stop();
		kinect.stop();
	}
	
	public static void main(String args[]) {
//		PApplet.main(new String[] {"--display=2", "--full-screen", "xx.codeflower.spielraum.motion.MApplet"});
		PApplet.main(new String[] {"--display=2", "xx.codeflower.spielraum.motion.MApplet"});
	}
}
