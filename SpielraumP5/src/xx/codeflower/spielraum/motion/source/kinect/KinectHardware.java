package xx.codeflower.spielraum.motion.source.kinect;

import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;
import xx.codeflower.base.SerializedFile;
import xx.codeflower.spielraum.motion.data.MotionDataCollection;
import xx.codeflower.spielraum.motion.data.MotionDataSet;
import xx.codeflower.spielraum.motion.source.MotionSource;

public class KinectHardware extends MotionSource {

	public CodeflowersSimpleOpenNI kinect;
	
	private HashMap<Integer, MotionDataSet> mmds = new HashMap<Integer, MotionDataSet>();
	
	private PVector nil = new PVector(-1, -1, -1);
	
	private PVector dataMap = new PVector(1, 1, 1);
	
	private boolean recordMotionData = false;
	private MotionDataCollection mdc = new MotionDataCollection();
	
	private boolean isRunning = false;
	
	private boolean convertToRealWorld 	= false;
	private boolean mapData 			= false;
	
	public KinectHardware(PApplet parent) {
		super(parent);
	}

	@Override
	public boolean start(float width, float height) {
		this.width  = width;
		this.height = height;
		startKinect();
		isRunning = true;
		return true;
	}
	
	private void startKinect() {
		System.out.println("Starting Kinect Hardware ...");
		kinect = new CodeflowersSimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
		
		System.out.println("Initializing Depth Map");
		kinect.enableDepth();
		
		System.out.println("Enable User Tracking");
		kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
		
		changeDataMapping(new PVector(this.width/(float)kinect.depthWidth(), this.height/(float)kinect.depthHeight(), 1f));
		
		System.out.println("Kinect Ready!");
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public void changeDataMapping(PVector dataMap) {
		dataMap = dataMap.get();
		System.out.println("Data correction set to "+dataMap);
	}
	
	public void recordMotionData() {
		recordMotionData = true;
	}
	
	public void stopRecording() {
		recordMotionData = false;
	}
	
	public void saveMotionData(String filename) {
		stopRecording();
		
		SerializedFile<MotionDataCollection> sf = new SerializedFile<MotionDataCollection>();
		sf.save(mdc, filename);
		
		mdc = new MotionDataCollection();
	}
	
	@Override
	public void update() {
		if(kinect != null) {
		kinect.update();
		
		for(int userid : kinect.getUsers()) {
			if(kinect.isTrackingSkeleton(userid)) {
				MotionDataSet mds = new MotionDataSet();
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_HEAD, mds.HEAD);
				if(convertToRealWorld) kinect.convertRealWorldToProjective(mds.HEAD, mds.HEAD);
				if(mapData) mds.HEAD.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_NECK, mds.NECK);
				kinect.convertRealWorldToProjective(mds.NECK, mds.NECK);
				mds.NECK.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_SHOULDER, mds.LEFT_SHOULDER);
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_SHOULDER, mds.RIGHT_SHOULDER);
				kinect.convertRealWorldToProjective(mds.LEFT_SHOULDER, mds.LEFT_SHOULDER);
				kinect.convertRealWorldToProjective(mds.RIGHT_SHOULDER, mds.RIGHT_SHOULDER);
				mds.LEFT_SHOULDER.mult(dataMap);
				mds.RIGHT_SHOULDER.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_ELBOW, mds.LEFT_ELBOW);
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_ELBOW, mds.RIGHT_ELBOW);
				kinect.convertRealWorldToProjective(mds.LEFT_ELBOW, mds.LEFT_ELBOW);
				kinect.convertRealWorldToProjective(mds.RIGHT_ELBOW, mds.RIGHT_ELBOW);
				mds.LEFT_ELBOW.mult(dataMap);
				mds.RIGHT_ELBOW.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_HAND, mds.LEFT_HAND);
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_HAND, mds.RIGHT_HAND);
				kinect.convertRealWorldToProjective(mds.LEFT_HAND, mds.LEFT_HAND);
				kinect.convertRealWorldToProjective(mds.RIGHT_HAND, mds.RIGHT_HAND);
				mds.LEFT_HAND.mult(dataMap);
				mds.RIGHT_HAND.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_TORSO, mds.TORSO);
				kinect.convertRealWorldToProjective(mds.TORSO, mds.TORSO);
				mds.TORSO.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_HIP, mds.LEFT_HIP);
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_HIP, mds.RIGHT_HIP);
				kinect.convertRealWorldToProjective(mds.LEFT_HIP, mds.LEFT_HIP);
				kinect.convertRealWorldToProjective(mds.RIGHT_HIP, mds.RIGHT_HIP);
				mds.LEFT_HIP.mult(dataMap);
				mds.RIGHT_HIP.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_KNEE, mds.LEFT_KNEE);
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_KNEE, mds.RIGHT_KNEE);
				kinect.convertRealWorldToProjective(mds.LEFT_KNEE, mds.LEFT_KNEE);
				kinect.convertRealWorldToProjective(mds.RIGHT_KNEE, mds.RIGHT_KNEE);
				mds.LEFT_KNEE.mult(dataMap);
				mds.RIGHT_KNEE.mult(dataMap);
				
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_FOOT, mds.LEFT_FOOT);
				kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_FOOT, mds.RIGHT_FOOT);
				kinect.convertRealWorldToProjective(mds.LEFT_FOOT, mds.LEFT_FOOT);
				kinect.convertRealWorldToProjective(mds.RIGHT_FOOT, mds.RIGHT_FOOT);
				mds.LEFT_FOOT.mult(dataMap);
				mds.RIGHT_FOOT.mult(dataMap);
				
				newDataForUser(userid, mds);
				
				if(recordMotionData) mdc.add(mds);
			}
		}		
		
		/*
			for(Map.Entry<Integer, MotionDataSet> es : mmds.entrySet()) {
				int userid = es.getKey();
				if(kinect.isTrackingSkeleton(userid)) {
					
					MotionDataSet mds = es.getValue();
					MotionDataSet lastmds = es.getValue().clone();
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_HEAD, mds.HEAD);
					kinect.convertRealWorldToProjective(mds.HEAD, mds.HEAD);
					mds.HEAD.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_NECK, mds.NECK);
					kinect.convertRealWorldToProjective(mds.NECK, mds.NECK);
					mds.NECK.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_SHOULDER, mds.LEFT_SHOULDER);
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_SHOULDER, mds.RIGHT_SHOULDER);
					kinect.convertRealWorldToProjective(mds.LEFT_SHOULDER, mds.LEFT_SHOULDER);
					kinect.convertRealWorldToProjective(mds.RIGHT_SHOULDER, mds.RIGHT_SHOULDER);
					mds.LEFT_SHOULDER.mult(dataMap);
					mds.RIGHT_SHOULDER.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_ELBOW, mds.LEFT_ELBOW);
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_ELBOW, mds.RIGHT_ELBOW);
					kinect.convertRealWorldToProjective(mds.LEFT_ELBOW, mds.LEFT_ELBOW);
					kinect.convertRealWorldToProjective(mds.RIGHT_ELBOW, mds.RIGHT_ELBOW);
					mds.LEFT_ELBOW.mult(dataMap);
					mds.RIGHT_ELBOW.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_HAND, mds.LEFT_HAND);
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_HAND, mds.RIGHT_HAND);
					kinect.convertRealWorldToProjective(mds.LEFT_HAND, mds.LEFT_HAND);
					kinect.convertRealWorldToProjective(mds.RIGHT_HAND, mds.RIGHT_HAND);
					mds.LEFT_HAND.mult(dataMap);
					mds.RIGHT_HAND.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_TORSO, mds.TORSO);
					kinect.convertRealWorldToProjective(mds.TORSO, mds.TORSO);
					mds.TORSO.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_HIP, mds.LEFT_HIP);
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_HIP, mds.RIGHT_HIP);
					kinect.convertRealWorldToProjective(mds.LEFT_HIP, mds.LEFT_HIP);
					kinect.convertRealWorldToProjective(mds.RIGHT_HIP, mds.RIGHT_HIP);
					mds.LEFT_HIP.mult(dataMap);
					mds.RIGHT_HIP.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_KNEE, mds.LEFT_KNEE);
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_KNEE, mds.RIGHT_KNEE);
					kinect.convertRealWorldToProjective(mds.LEFT_KNEE, mds.LEFT_KNEE);
					kinect.convertRealWorldToProjective(mds.RIGHT_KNEE, mds.RIGHT_KNEE);
					mds.LEFT_KNEE.mult(dataMap);
					mds.RIGHT_KNEE.mult(dataMap);
					
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_LEFT_FOOT, mds.LEFT_FOOT);
					kinect.getJointPositionSkeleton(userid, SimpleOpenNI.SKEL_RIGHT_FOOT, mds.RIGHT_FOOT);
					kinect.convertRealWorldToProjective(mds.LEFT_FOOT, mds.LEFT_FOOT);
					kinect.convertRealWorldToProjective(mds.RIGHT_FOOT, mds.RIGHT_FOOT);
					mds.LEFT_FOOT.mult(dataMap);
					mds.RIGHT_FOOT.mult(dataMap);
					
					mds.calcDirVectors(lastmds);
					
					newDataForUser(userid, mds);
					
					if(recordMotionData) mdc.add(mds);
				}
			}
			*/
		}
	}
	
	// SimpleOpenNI events
	public void onNewUser(int userid) {
		System.out.println("New User found: " + userid);
		System.out.println("Start pose detection ...");

		kinect.startPoseDetection("Psi", userid);
		
		newUserFound(userid);
	}

	public void onLostUser(int userid) {
		System.out.println("Lost User: " + userid);
		mmds.remove(userid);
		userLost(userid);
	}

	public void onStartCalibration(int userid) {
		System.out.println("Start calibration for user: " + userid);
	}

	public void onEndCalibration(int userid, boolean successfull) {
		System.out.println("End calibration for user: " + userid + ", successfull: "
				+ successfull);

		if (successfull) {
			System.out.println("User calibrated!");
			kinect.startTrackingSkeleton(userid);
			
			mmds.put(userid, new MotionDataSet(nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), nil.get(), (int) this.width, (int) this.height));
			
			newUserFound(userid);
		} else {
			System.out.println("Failed to calibrate user: "+userid);
			System.out.println("Start pose detection ...");
			kinect.startPoseDetection("Psi", userid);
		}
	}

	public void onStartPose(String pose, int userid) {
		System.out.println("Start pose for user: " + userid + ", pose: " + pose);
		System.out.println("Stop pose detection ...");

		kinect.stopPoseDetection(userid);
		kinect.requestCalibrationSkeleton(userid, true);

	}

	public void onEndPose(String pose, int userid) {
		System.out.println("End pose for user: " + userid + ", pose: " + pose);
	}

	public void stop() {
		if(isRunning) {
			isRunning = false;
			kinect.close();
		}
	}
	
}
