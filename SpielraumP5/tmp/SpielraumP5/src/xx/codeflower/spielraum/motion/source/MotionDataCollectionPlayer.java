package xx.codeflower.spielraum.motion.source;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

import xx.codeflower.base.SerializedFile;
import xx.codeflower.spielraum.motion.data.MotionDataCollection;
import xx.codeflower.spielraum.motion.data.MotionDataSet;

public class MotionDataCollectionPlayer extends MotionSource {

	private long startTime = 0;
	
	private int dataPullIndex = 0;
	
	private MotionDataCollection mdc;
	
	private PVector normalizationVector;
	
	private ArrayList<Long> ttimes;
	
	public MotionDataCollectionPlayer(PApplet parent, MotionDataCollection mdc) {
		super(parent);
		setCollection(mdc);
	}

	public MotionDataCollectionPlayer(PApplet parent, String filename) {
		super(parent);
		SerializedFile<MotionDataCollection> sf = new SerializedFile<MotionDataCollection>();
		setCollection(sf.load(filename));
	}
	
	private void setCollection(MotionDataCollection mdc) {
		this.mdc = mdc;
		this.normalizationVector = new PVector(1/mdc.get(0).width, 1/mdc.get(0).height, 1);
	}
	
	private void initTime() {
		startTime = System.currentTimeMillis();
	}
	
	public long getTime() {
		return System.currentTimeMillis()-startTime;
	}
	
	private void startPlayback() {
		System.out.println("Starting Playback ...");
		startTime = 0;
		dataPullIndex = 0;
		
		initTime();
		
		ttimes = mdc.getTimes();
	}
	
	public MotionDataSet getMotionData(float ms) {
		if(ms > 0) {
			if(getTime() >= ttimes.get(0)) {
				ttimes.remove(0);
				dataPullIndex++;
			}
		} else {
			if(getTime() > ms) {
				initTime();
				dataPullIndex++;
			}
		}
		
		if(dataPullIndex == mdc.size()) {
			startPlayback();
			return mdc.get(0).clone();
		} else {
			return mdc.get(dataPullIndex).clone();
		}
	}
	
	public MotionDataSet getMotionData() {
		return getMotionData(0);
	}
	
	public MotionDataSet getNormalizedMotionData() {
		return getMotionData().normalize();
	}

	public MotionDataSet getNormalizedMotionData(float ms) {
		return getMotionData(ms).normalize();
	}

	@Override
	public boolean start(float width, float height) {
		this.width 	= width;
		this.height = height;
		startPlayback();
		return true;
	}

	@Override
	public void update() {
		newDataForUser(1, getMotionData());
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
}
