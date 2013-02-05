package xx.codeflower.spielraump5;

import java.lang.reflect.Method;

import processing.core.PApplet;
import xx.codeflower.spielraum.motion.detection.MotionDetectionListener;
import xx.codeflower.spielraum.motion.detection.WalkDetector;
import xx.codeflower.spielraum.motion.source.MotionDataCollectionPlayer;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.source.MotionListener;

public class SpielraumP5 implements MotionListener, MotionDetectionListener {

	private PApplet parent;
	
	private MotionDataCollectionPlayer player;
	
	private MotionDataSet mds;
	
	private WalkDetector wd;
	
	private Method motionDetected;
	
	public SpielraumP5(PApplet parent, String filename) {
		this.parent	= parent;
		
		player = new MotionDataCollectionPlayer(parent, parent.sketchPath(filename));
		player.addListener(this);
		
		wd = new WalkDetector(12.0f);
		wd.addListener(this);
		
	    try {
	    	motionDetected = parent.getClass().getMethod("motionDetected", String.class);
	    } catch (Exception e) {}
	}
	
	public void start() {
		player.start(parent.width, parent.height);
	}
	
	public MotionDataSet getMotionData() {
		player.update();
		wd.update(mds.clone());
		return mds;
	}

	@Override
	public void onNewUser(int userid) {}

	@Override
	public void onLostUser(int userid) {}

	@Override
	public void onNewUserData(int userid, MotionDataSet mds) {
		this.mds = mds;
	}

	@Override
	public void motionDetected(String motion) {
		if(motionDetected != null) {
			try {
				motionDetected.invoke(parent, motion);
		    } catch (Exception e) {
		    	System.err.println("Disabling motionDetected() because of an error.");
		    	e.printStackTrace();
		    	motionDetected = null;
		    }
		}
	}
	
}
