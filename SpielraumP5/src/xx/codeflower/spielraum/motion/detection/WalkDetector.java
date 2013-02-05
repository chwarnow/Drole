package xx.codeflower.spielraum.motion.detection;

public class WalkDetector extends MotionDetector {
	
	private boolean isWalking = false;
	private boolean init = false;
	private float thres = 8f;
	
	public WalkDetector(float thres) {
		this.thres = thres;
		motionDetected("WALKING");
	}
	
	protected void newData() {
		if(mdc.getSets().size() == sampleSize) {
			float allMoveX = 0;
			float allMoveY = 0;
			float allMoveZ = 0;
			allMoveX = (mdc.get(0).LEFT_FOOT.x-mdc.get(mdc.size()-1).LEFT_FOOT.x);
			allMoveY = (mdc.get(0).LEFT_FOOT.y-mdc.get(mdc.size()-1).LEFT_FOOT.y);
			allMoveZ = (mdc.get(0).LEFT_FOOT.z-mdc.get(mdc.size()-1).LEFT_FOOT.z);
			
			/*
			for(int i = 0; i < mdc.size(); i++) {
				if(i < mdc.size()-1) {
					allMove += (mdc.get(i+1).LEFT_FOOT.x-mdc.get(i).LEFT_FOOT.x);
					allMove += (mdc.get(i+1).LEFT_FOOT.y-mdc.get(i).LEFT_FOOT.y);
					allMove += (mdc.get(i+1).LEFT_FOOT.z-mdc.get(i).LEFT_FOOT.z);
				}
			}
			*/
			
			if(Math.abs(allMoveX) > thres || Math.abs(allMoveY) > thres || Math.abs(allMoveZ) > thres) {
				if(!isWalking || init == false) {
					init = true;
					isWalking = true;
					motionDetected("WALKING");
				}
			} else {
				if(isWalking || init == false) {
					init = true;
					isWalking = false;
					motionDetected("STANDING");
				}
			}
			
			reset();
		}
	}
	
}
