package xx.codeflower.spielraum.motion.source;

import xx.codeflower.spielraum.motion.data.MotionDataSet;

public interface MotionListener {

	public void onNewUser(int userid);
	
	public void onLostUser(int userid);
	
	public void onNewUserData(int userid, MotionDataSet mds);
	
}
