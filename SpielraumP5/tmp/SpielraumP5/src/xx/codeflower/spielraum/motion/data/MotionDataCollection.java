package xx.codeflower.spielraum.motion.data;

import java.io.Serializable;
import java.util.ArrayList;

public class MotionDataCollection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long startTime = 0;
	
	private int index = 0;
	
	private ArrayList<Long> times = new ArrayList<Long>();
	
	private ArrayList<MotionDataSet> mdss = new ArrayList<MotionDataSet>();
	
	public ArrayList<MotionDataSet> getSets() {
		return mdss;
	}
	
	private void initTime() {
		startTime = System.currentTimeMillis();
	}
	
	public long getTime() {
		return System.currentTimeMillis()-startTime;
	}
	
	public int size() {
		return times.size();
	}
	
	public void clear() {
		times.clear();
		mdss.clear();
	}
	
	public ArrayList<Long> getTimes() {
		ArrayList<Long> ttimes = new ArrayList<Long>();
		for(Long t : times) ttimes.add(t);
		return ttimes;
	}
	
	public MotionDataSet get(int i) {
		return mdss.get(i);
	}
	
	public void add(MotionDataSet mds) {
		if(startTime == 0) initTime();
		
		times.add(getTime());
		mdss.add(mds.clone());
	}
	
	@Override
	public String toString() {
		String s = "";
		for(int i = 0; i < mdss.size(); i++) s = s+mdss.get(i)+"\n";
		return s;
	}
	
}
