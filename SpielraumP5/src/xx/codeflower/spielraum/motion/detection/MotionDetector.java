package xx.codeflower.spielraum.motion.detection;

import java.util.ArrayList;

import xx.codeflower.spielraum.motion.data.MotionDataCollection;
import xx.codeflower.spielraum.motion.data.MotionDataSet;

public abstract class MotionDetector {

	private ArrayList<MotionDetectionListener> listeners = new ArrayList<MotionDetectionListener>(); 
	
	protected MotionDataCollection mdc = new MotionDataCollection();
	
	protected int sampleSize = 50;
	
	public void addListener(MotionDetectionListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(MotionDetectionListener listener) {
		listeners.remove(listener);
	}
	
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	public void update(MotionDataSet mds) {
		mdc.add(mds);
		newData();
	}
	
	protected abstract void newData();
	
	public void reset() {
		mdc.clear();
	}
	
	@SuppressWarnings("unused")
	protected void motionDetected(String motion) {
		for(MotionDetectionListener l : listeners) l.motionDetected(motion);
	}
	
}
