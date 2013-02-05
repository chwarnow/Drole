package xx.codeflower.spielraum.motion.source;

import java.util.ArrayList;

import processing.core.PApplet;
import xx.codeflower.spielraum.motion.data.MotionDataSet;

public abstract class MotionSource {

	protected PApplet parent;
	
	protected float width, height;
	
	private ArrayList<MotionListener> listeners = new ArrayList<MotionListener>();
	
	// Use parent to log events
	public MotionSource(PApplet parent) {
		this.parent = parent;
	}
	
	// Initialize hardware, load files and start streaming motion data etc...
	// No event should be thrown before start() is called
	public abstract boolean start(float width, float height);

	public abstract void update();
	
	public abstract void stop();
	
	public void addListener(MotionListener ml) {
		this.listeners.add(ml);
	}
	
	public void removeListener(MotionListener ml) {
		this.listeners.remove(ml);
	}
	
	// Events
	
	public void newUserFound(int userid) {
		System.out.println("New User found: " + userid);
		for(MotionListener ml : listeners) ml.onNewUser(userid);
	}

	public void userLost(int userid) {
		System.out.println("Lost User: " + userid);
		for(MotionListener ml : listeners) ml.onLostUser(userid);
	}
	
	public void newDataForUser(int userid, MotionDataSet mds) {
		for(MotionListener ml : listeners) ml.onNewUserData(userid, mds);
	}
	
}
