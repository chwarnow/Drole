package xx.codeflower.spielraum.motion.filter.data;

import xx.codeflower.spielraum.motion.data.MotionDataCollection;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.filter.MotionDataFilter;

public class SpikeReductionFilter extends MotionDataFilter {

	private MotionDataCollection mdc = new MotionDataCollection();
	
	private MotionDataSet movement = new MotionDataSet();
	
	@Override
	public void filter(MotionDataSet mds) {
		
	}

}
