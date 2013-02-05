package xx.codeflower.spielraum.motion.scene;

import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PGraphics;

public class SceneList {

	public static final short OFFSCREEN = 0, DRAWING = 10, FADEIN = 20, FADEOUT = 30;
	
	private PApplet parent;
	
	private HashMap<Integer, Scene> scenes = new HashMap<Integer, Scene>();

	private Scene activeScene, nextScene;
	
	public short state = OFFSCREEN;
	
	public SceneList(PApplet parent) {
		this.parent = parent;
	}
	
	public void activateScene(int code) {
		if(activeScene == null) {
			activeScene = scenes.get(code);
			activeScene.fadeIn();
			state = FADEIN;
		} else {
			nextScene = scenes.get(code);
			activeScene.fadeOut();
			state = FADEOUT;
		}
	}
	
	public void activateScene(Scene scene) { activateScene(scene.code); }
	
	public Scene activeScene() { return activeScene; }
	
	public boolean sceneExists(int code) {
		return scenes.containsKey(code);
	}
	
	public void addScene(Scene scene) {
		this.scenes.put(scene.code, scene);
	}
	
	public void removeScene(Scene scene) { removeScene(scene.code); }

	public void removeScene(int code) {
		this.scenes.remove(code);
	}

	public void draw(PGraphics g) {
		if(state == FADEOUT && activeScene.state == Scene.OFFSCREEN) {
			activeScene = nextScene;
			activeScene.fadeIn();
			state = FADEIN;
		}
		if(state == FADEIN && activeScene.state == Scene.DRAWING) {
			state = DRAWING;
		}

		if(state != OFFSCREEN)  activeScene.metaDraw(g);
	}
	
}
