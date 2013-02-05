/* ----------------------------------------------------------------------------
 * SimpleOpenNI
 * ----------------------------------------------------------------------------
 * Copyright (C) 2011 Max Rheiner / Interaction Design Zhdk
 *
 * This file is part of SimpleOpenNI.
 *
 * SimpleOpenNI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * SimpleOpenNI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleOpenNI.  If not, see <http://www.gnu.org/licenses/>.
 * ----------------------------------------------------------------------------
 */
 
package xx.codeflower.spielraum.motion.source;

import java.lang.reflect.Method;

import processing.core.*;

import SimpleOpenNI.*;
   
public class CodeflowersSimpleOpenNI extends ContextWrapper implements SimpleOpenNIConstants
{
	static 
        {   // load the nativ shared lib
            String sysStr = System.getProperty("os.name").toLowerCase();
            String libName = "SimpleOpenNI";
            String archStr = System.getProperty("os.arch").toLowerCase();

            // check which system + architecture
            if(sysStr.indexOf("win") >= 0)
            {   // windows
                if(archStr.indexOf("86") >= 0)
                    // 32bit
                    libName += "32";
                else if(archStr.indexOf("64") >= 0)
                    libName += "64";
             }
            else if(sysStr.indexOf("nix") >= 0 || sysStr.indexOf("linux") >=  0 )
            {   // unix
                if(archStr.indexOf("86") >= 0)
                    // 32bit
                    libName += "32";
                else if(archStr.indexOf("64") >= 0)
                {
                    System.out.println("----");
                    libName += "64";
                }
            }
            else if(sysStr.indexOf("mac") >= 0)
            {     // mac
            }

            try{
              //System.out.println("-- " + System.getProperty("user.dir"));
              System.loadLibrary(libName);
            }
            catch(UnsatisfiedLinkError e)
            {
              System.out.println("Can't load SimpleOpenNI library (" +  libName  + ") : " + e);
			  System.out.println("Verify if you installed SimpleOpenNI correctly.\nhttp://code.google.com/p/simple-openni/wiki/Installation");
            }
	}

    public static void start()
	{
	  if(_initFlag)
		return;

	  _initFlag = true;
	  initContext();
	}
    
	public static int deviceCount()
	{
	  start();
	  return ContextWrapper.deviceCount();
	}

    public static int deviceNames(StrVector nodeNames)
	{
	  start();
	  return ContextWrapper.deviceNames(nodeNames);	  
	}

	///////////////////////////////////////////////////////////////////////////
	// callback vars
	protected Object			_userCbObject;
	protected Object			_calibrationCbObject;
	protected Object			_poseCbObject;
	protected Object			_handsCbObject;
	protected Object			_gestureCbObject;
	protected Object			_sessionCbObject;

	protected Method 			_newUserMethod;
	protected Method 			_lostUserMethod;
	protected Method 			_exitUserMethod;
	protected Method 			_reEnterUserMethod;
	
	protected Method 			_startCalibrationMethod;
	protected Method 			_endCalibrationMethod;
	
	protected Method 			_startPoseMethod;
	protected Method 			_endPoseMethod;

	// hands cb
	protected Method 			_createHandsMethod;
	protected Method 			_updateHandsMethod;
	protected Method 			_destroyHandsMethod;
	
	// gesture cb
	protected Method 			_recognizeGestureMethod;
	protected Method 			_progressGestureMethod;

	// nite session cb
	protected Method 			_startSessionMethod;
	protected Method 			_endSessionMethod;
	protected Method 			_focusSessionMethod;
	
	
	protected String 			_filename;	
	protected MotionSource		_parent;
	
	protected PImage			_depthImage;
	protected int[]				_depthRaw;
	protected PVector[]			_depthMapRealWorld;
	protected XnPoint3D[] 		_depthMapRealWorldXn;
	//protected XnPoint3DArray	_depthMapRealWorldArray;
	
	protected PImage			_rgbImage;

	protected PImage			_irImage;
	
	protected PImage			_sceneImage;
  	protected int[]				_sceneRaw;

  	protected int[]				_userRaw;
	
	// update flags
	protected long				_depthMapTimeStamp;
	protected long				_depthImageTimeStamp;
	protected long				_depthRealWorldTimeStamp;
	
	protected long				_rgbTimeStamp;
	
	protected long				_irImageTimeStamp;
	
	protected long				_sceneMapTimeStamp;
	protected long				_sceneImageTimeStamp;

	static protected boolean	_initFlag = false;

	
	/**
	* Creates the OpenNI context ands inits the modules
	* 
	* @param parent
	*          PApplet
	* @param initXMLFile
	*          String
	*/
	public CodeflowersSimpleOpenNI(MotionSource parent, String initXMLFile)
	{
		this._parent 	= parent;
		initVars();
		
		// setup the callbacks
		setupCallbackFunc();
		
		// load the initfile
		this.init(initXMLFile);
	}
	
	/**
	* Creates the OpenNI context ands inits the modules
	* 
	* @param parent
	*          PApplet
	* @param initXMLFile
	*          String
	* @param runMode
	*     	   - RunMode_Default, RunMode_SingleThreaded = Runs all in a single thread	
	*		   - RunMode_MultiThreaded = Runs the openNI/NIITE in another thread than processing	
	*/
	public CodeflowersSimpleOpenNI(MotionSource parent, String initXMLFile,int runMode)
	{
		this._parent 	= parent;
		initVars();
		
		// setup the callbacks
		setupCallbackFunc();
		
		// load the initfile
		this.init(initXMLFile,runMode);
	}
	/**
	* Creates the OpenNI context ands inits the modules
	* 
	* @param parent
	*          PApplet
	*/
	public CodeflowersSimpleOpenNI(MotionSource parent)
	{
		this._parent 	= parent;
		initVars();
		
		// setup the callbacks
		setupCallbackFunc();
		
		// load the initfile
		this.init();
	}
	
	/**
	* Creates the OpenNI context ands inits the modules
	* 
	* @param deviceIndex
	*          int
	* @param parent
	*          PApplet
	*/
	public CodeflowersSimpleOpenNI(int deviceIndex, MotionSource parent)
	{
		this._parent 	= parent;
		initVars();
		
		// setup the callbacks
		setupCallbackFunc();
		
		// load the initfile
		this.init(deviceIndex,RUN_MODE_SINGLE_THREADED);
	}
	
	/**
	* Creates the OpenNI context ands inits the modules
	* 
	* @param parent
	*          PApplet
	* @param runMode
	*     	   - RunMode_Default, RunMode_SingleThreaded = Runs all in a single thread	
	*		   - RunMode_MultiThreaded = Runs the openNI/NIITE in another thread than processing	
	*/
	public CodeflowersSimpleOpenNI(MotionSource parent,int runMode)
	{
		this._parent 	= parent;
		initVars();
		
		// setup the callbacks
		setupCallbackFunc();
		
		// load the initfile
		this.init(runMode);
	}
	
	protected void initVars()
	{
		_depthMapTimeStamp			= -1;
		_depthImageTimeStamp		= -1;
		_depthRealWorldTimeStamp	= -1;
		
		_rgbTimeStamp				= -1;
		
		_irImageTimeStamp			= -1;
		
		_sceneMapTimeStamp			= -1;
		_sceneImageTimeStamp		= -1;		
	
	}
	
	protected void setupCallbackFunc()
	{
		_userCbObject 			= _parent;
		_calibrationCbObject 	= _parent;
		_poseCbObject 			= _parent;
		_handsCbObject 			= _parent;
		_gestureCbObject 		= _parent;
		_sessionCbObject 		= _parent;

		_newUserMethod			= null;
		_lostUserMethod 		= null;
		_exitUserMethod			= null;
		_reEnterUserMethod 		= null;

		_startCalibrationMethod = null;
		_endCalibrationMethod	= null;
		
		_startPoseMethod 		= null;
		_endPoseMethod			= null;

		_createHandsMethod		= null;
		_updateHandsMethod		= null;
		_destroyHandsMethod		= null;
	
		// user callbacks
		_newUserMethod = getMethodRef("onNewUser",new Class[] { int.class });
		_lostUserMethod = getMethodRef("onLostUser",new Class[] { int.class });
		_exitUserMethod = getMethodRef("onExitUser",new Class[] { int.class });
		_reEnterUserMethod = getMethodRef("onReEnterUser",new Class[] { int.class });

		// calibrations callbacks
		_startCalibrationMethod = getMethodRef("onStartCalibration",new Class[] { int.class });
		_endCalibrationMethod = getMethodRef("onEndCalibration",new Class[] { int.class, boolean.class });
		
		// pose callbacks
		_startPoseMethod = getMethodRef("onStartPose",new Class[] { String.class,int.class });
		_endPoseMethod = getMethodRef("onEndPose",new Class[] { String.class,int.class });
		
		// hands
		_createHandsMethod = getMethodRef("onCreateHands",new Class[] { int.class,PVector.class,float.class });
		_updateHandsMethod = getMethodRef("onUpdateHands",new Class[] { int.class,PVector.class,float.class });
		_destroyHandsMethod = getMethodRef("onDestroyHands",new Class[] { int.class,float.class });

		// gesture
		_recognizeGestureMethod = getMethodRef("onRecognizeGesture",new Class[] { String.class,PVector.class,PVector.class  });
		_progressGestureMethod = getMethodRef("onProgressGesture",new Class[] { String.class,PVector.class,float.class });

		// nite
		_startSessionMethod = getMethodRef("onStartSession",new Class[] { PVector.class  });
		_endSessionMethod = getMethodRef("onEndSession",new Class[] {});
		_focusSessionMethod = getMethodRef("onFocusSession",new Class[] { String.class,PVector.class,float.class });
	}
	
	protected Method getMethodRef(String methodName,Class[] paraList)
	{
		Method	ret = null;
		try {
			ret = _parent.getClass().getMethod(methodName,paraList);																									
		} 
		catch (Exception e) 
		{ // no such method, or an error.. which is fine, just ignore
		}
		return ret;
	}
	
	public static Method getMethodRef(Object obj,String methodName,Class[] paraList)
	{
		Method	ret = null;
		try {
			ret = obj.getClass().getMethod(methodName,paraList);																									
		} 
		catch (Exception e) 
		{ // no such method, or an error.. which is fine, just ignore
		}
		return ret;
	}
	
	/**
	* 
	*/  
	public void dispose() 
	{
		close();
	}

	public void finalize() 
	{
		close();
	}

	private void setupDepth()
	{	
		_depthImage 		= new PImage(depthWidth(), depthHeight(),PConstants.RGB);
		_depthRaw 			= new int[depthMapSize()];
		_depthMapRealWorld 	= new PVector[depthMapSize()];
		_depthMapRealWorldXn = new XnPoint3D[depthMapSize()];
			
		for(int i=0;i < depthMapSize();i++ )
		{
			_depthMapRealWorld[i] 	= new PVector();
			_depthMapRealWorldXn[i] = new XnPoint3D();
		}
		
		//_depthMapRealWorldArray	= new XnPoint3DArray(depthMapSize());
	}
	
	/**
	* Enable the depthMap data collection
	*/  
	public boolean enableDepth() 
	{
		if(super.enableDepth())
		{	// setup the var for depth calc
			setupDepth();
			return true;
		}
		else
			return false;
	}
	
	/**
	* Enable the depthMap data collection
	* 
	* @param width
	*          int
	* @param height
	*          int
	* @param fps
	*          int
	* @return returns true if depthMap generation was succesfull
	*/
	public boolean enableDepth(int width,int height,int fps) 
	{
		if(super.enableDepth(width,height,fps))
		{	// setup the var for depth calc
			setupDepth();
			return true;
		}
		else
			return false;
	}	
	
	public PImage depthImage() 
	{
		updateDepthImage();
		return _depthImage;
	}
	
	public int[] depthMap()
	{
		updateDepthRaw();
		return _depthRaw;
	}

	public PVector[] depthMapRealWorld()
	{
		updateDepthRealWorld();
		return _depthMapRealWorld;
	}	
	
	private void setupRGB()
	{
		_rgbImage = new PImage(rgbWidth(), rgbHeight(),PConstants.RGB);
	}
	
	/**
	* Enable the camera image collection
	*/  
	public boolean enableRGB() 
	{
		if(super.enableRGB())
		{	// setup the var for depth calc
			setupRGB();
			return true;
		}
		else
			return false;
	}	

	/**
	* Enable the camera image collection
	* 
	* @param width
	*          int
	* @param height
	*          int
	* @param fps
	*          int
	* @return returns true if rgbMap generation was succesfull
	*/
	public boolean enableRGB(int width,int height,int fps) 
	{
		if(super.enableRGB(width,height,fps))
		{	// setup the var for depth calc
			setupRGB();
			return true;
		}
		else
			return false;
	}	

	public PImage rgbImage() 
	{
		updateImage();
		return _rgbImage;
	}
		
	private void setupIR()
	{
		_irImage = new PImage(irWidth(), irHeight(),PConstants.RGB);
	}
		
	/**
	* Enable the irMap data collection
	* ir is only available if there is no rgbImage activated at the same time 
	*/  
	public boolean enableIR() 
	{
		if(super.enableIR())
		{	// setup the var for depth calc
			setupIR();
			return true;
		}
		else
			return false;
	}
	
	/**
	* Enable the irMap data collection
	* ir is only available if there is no irImage activated at the same time 
	* 
	* @param width
	*          int
	* @param height
	*          int
	* @param fps
	*          int
	* @return returns true if irMap generation was succesfull
	*/
	public boolean enableIR(int width,int height,int fps) 
	{
		if(super.enableIR(width,height,fps))
		{	// setup the var for depth calc
			setupIR();
			return true;
		}
		else
			return false;
	}		
	
	public PImage irImage() 
	{
		updateIrImage();
		return _irImage;
	}

	private void setupScene()
	{
		_sceneImage = new PImage(sceneWidth(), sceneHeight(),PConstants.RGB);
		_sceneRaw = new int[sceneWidth() * sceneHeight()];
	}
	
	/**
	* Enable the scene data collection
	*/  
	public boolean enableScene() 
	{
		if(super.enableScene())
		{	// setup the var for depth calc
			setupScene();
			return true;
		}
		else
			return false;
	}
	
	/**
	* Enable the scene data collection
	* 
	* @param width
	*          int
	* @param height
	*          int
	* @param fps
	*          int
	* @return returns true if sceneMap generation was succesfull
	*/
	public boolean enableScene(int width,int height,int fps) 
	{
		if(super.enableScene(width,height,fps))
		{	// setup the var for depth calc
			setupScene();
			return true;
		}
		else
			return false;
	}	
	
	public PImage sceneImage()
	{
		updateSceneImage();
		return _sceneImage;
	}
	
	public int[] sceneMap()
	{
		updateSceneRaw();
		return _sceneRaw;
	}

	
	public void getSceneFloor(PVector point,PVector normal)
	{
		XnVector3D p = new XnVector3D();
		XnVector3D n = new XnVector3D();
		
		super.getSceneFloor(p, n);
		point.set(p.getX(),p.getY(),p.getZ());
		normal.set(n.getX(),n.getY(),n.getZ());
	}
	
	private void setupUser()
	{
		_userRaw = new int[userWidth() * userHeight()];

		// setup callbacks
		// user callbacks
		_newUserMethod = getMethodRef(_userCbObject,"onNewUser",new Class[] { int.class });
		_lostUserMethod = getMethodRef(_userCbObject,"onLostUser",new Class[] { int.class });
		_exitUserMethod = getMethodRef(_userCbObject,"onExitUser",new Class[] { int.class });
		_reEnterUserMethod = getMethodRef(_userCbObject,"onReEnterUser",new Class[] { int.class });

		// calibrations callbacks
		_startCalibrationMethod = getMethodRef(_calibrationCbObject,"onStartCalibration",new Class[] { int.class });
		_endCalibrationMethod = getMethodRef(_calibrationCbObject,"onEndCalibration",new Class[] { int.class, boolean.class });
		
		// pose callbacks
		_startPoseMethod = getMethodRef(_poseCbObject,"onStartPose",new Class[] { String.class,int.class });
		_endPoseMethod = getMethodRef(_poseCbObject,"onEndPose",new Class[] { String.class,int.class });
	}
	
	/**
	* Enable user 
	*/  
	public boolean enableUser(int flags) 
	{
	  return enableUser(flags,_parent);
	}

	/**
	* Enable user 
	*/  
	public boolean enableUser(int flags,Object cbObject) 
	{
		_userCbObject 			= cbObject;
		_calibrationCbObject 	= cbObject;
		_poseCbObject 			= cbObject;

		if(super.enableUser(flags))
		{
			setupUser();
			return true;
		}
		else
			return false;
	}
	
        public boolean	saveCalibrationDataSkeleton(int user,String calibrationFile)
        {
            String path = calibrationFile;
            PApplet.createPath(path);
            PApplet.println(path);
            return (super.saveCalibrationDataSkeleton(user,path));
        }

        public boolean	loadCalibrationDataSkeleton(int user,String calibrationFile)
        {
            String path = calibrationFile;
            return (super.loadCalibrationDataSkeleton(user,path));
        }

	public int[] getUsersPixels(int user)
	{
		int size = userWidth() * userHeight();
		if(size == 0)
			return _userRaw;
			
		if(_userRaw.length != userWidth() * userHeight())
		{	// resize the array
			_userRaw = new int[userWidth() * userHeight()];
		}

		super.getUserPixels(user,_userRaw);
		return _userRaw;
	}
		
	public boolean getCoM(int user,PVector com)
	{
		boolean ret;
		XnPoint3D com1 = new XnPoint3D();
		ret = super.getCoM(user,com1);	
		com.set(com1.getX(),
				com1.getY(),
				com1.getZ());
		
		return ret;
	}
	
	public int[] getUsers() 
	{
	  IntVector intVec = new IntVector();
	  getUsers(intVec);

	  int[] userList = new int[(int)intVec.size()];
	  for(int i=0;i < intVec.size();i++)
		userList[i] = intVec.get(i);

	  return userList;
	}

	private void setupHands()
	{
		// hands
		_createHandsMethod = getMethodRef(_handsCbObject,"onCreateHands",new Class[] { int.class,PVector.class,float.class });
		_updateHandsMethod = getMethodRef(_handsCbObject,"onUpdateHands",new Class[] { int.class,PVector.class,float.class });
		_destroyHandsMethod = getMethodRef(_handsCbObject,"onDestroyHands",new Class[] { int.class,float.class });
	}
	
	/**
	* Enable hands  
	*/  
	public boolean enableHands() 	
	{
	  return enableHands(_parent);
	}

	/**
	* Enable hands  
	*/  
	public boolean enableHands(Object cbObject) 
	{
		_handsCbObject = cbObject;

		if(super.enableHands())
		{
			setupHands();
			return true;
		}
		else
			return false;
	}	

	public void	startTrackingHands(PVector pos)
	{
		XnVector3D vec = new XnVector3D();
		vec.setX(pos.x);
		vec.setY(pos.y);
		vec.setZ(pos.z);
		super.startTrackingHands(vec);
	}

	private void setupGesture()
	{
		// gesture
		_recognizeGestureMethod = getMethodRef(_gestureCbObject,"onRecognizeGesture",new Class[] { String.class,PVector.class,PVector.class  });
		_progressGestureMethod = getMethodRef(_gestureCbObject,"onProgressGesture",new Class[] { String.class,PVector.class,float.class });
	}
	
	/**
	* Enable hands  
	*/  
	public boolean enableGesture() 	
	{
	  return enableGesture(_parent);
	}

	/**
	* Enable gesture  
	*/  
	public boolean enableGesture(Object cbObject) 
	{
		_gestureCbObject = cbObject;
		if(super.enableGesture())
		{
			setupGesture();
			return true;
		}
		else
			return false;
	}	
	
	/**
	* Enable recorder	
	*/
	public boolean enableRecorder(int recordMedium,String filePath)
	{
		String path = filePath;
		PApplet.createPath(path);

		if(super.enableRecorder(recordMedium,path))
		{
			return true;
		}
		else
			return false;
	}
	
	/**
	* Enable the player
	*/  
	public boolean openFileRecording(String filePath)
	{
		String path = filePath;
		
		if(super.openFileRecording(path))
		{	// get all the nodes that are in use and init them

			if((nodes() & NODE_DEPTH) > 0)
				setupDepth();
			if((nodes() & NODE_IMAGE) > 0)
				setupRGB();
			if((nodes() & NODE_IR) > 0)
				setupIR();
			if((nodes() & NODE_SCENE) > 0)
				setupScene();
			if((nodes() & NODE_USER) > 0)
				setupUser();
			if((nodes() & NODE_GESTURE) > 0)
				setupGesture();
			if((nodes() & NODE_HANDS) > 0)
				setupHands();
			
			return true;
		}
		else
			return false;
	}
	
	protected void updateDepthRaw()
	{
		if((nodes() & NODE_DEPTH) == 0)
			return;
		if(_depthMapTimeStamp ==  updateTimeStamp())
			return;

		depthMap(_depthRaw);
		_depthMapTimeStamp = updateTimeStamp();
	}	
	
	protected void updateDepthImage()
	{
		if((nodes() & NODE_DEPTH) == 0)
			return;
		if(_depthImageTimeStamp ==  updateTimeStamp())
			return;
	
		_depthImage.loadPixels();
			depthImage(_depthImage.pixels);
		_depthImage.updatePixels();
		_depthImageTimeStamp = updateTimeStamp();
	}
	
	protected void updateDepthRealWorld()
	{
		if((nodes() & NODE_DEPTH) == 0)
			return;	
		if(_depthRealWorldTimeStamp ==  updateTimeStamp())
			return;

		depthMapRealWorld(_depthMapRealWorldXn);
		
		XnPoint3D vec;
		for(int i=0;i < _depthMapRealWorldXn.length;i++)
		{
			vec = _depthMapRealWorldXn[i];
			_depthMapRealWorld[i].set(vec.getX(),
									  vec.getY(),
								      vec.getZ());
		}
	
		/*
		int now = _parent.millis();
		XnPoint3D vec;
		depthMapRealWorldA(_depthMapRealWorldArray);
		
		_parent.println("depthMapRealWorld calc: " + (_parent.millis()-now));
		now = _parent.millis();
		
		for(int i=0;i < depthMapSize();i++)
		{
			vec = _depthMapRealWorldArray.getitem(i);
			_depthMapRealWorld[i].set(vec.getX(),
									  vec.getY(),
								      vec.getZ());
		}
		_parent.println("updateDepthRealWorld calc: " + (_parent.millis()-now));
	*/
		
		/*
		int now = _parent.millis();
		
		XnPoint3D vec;
		
		XnPoint3DArray array = depthMapRealWorldA();
		
		_parent.println("depthMapRealWorld calc: " + (_parent.millis()-now));
		now = _parent.millis();
		
		for(int i=0;i < depthMapSize();i++)
		{
			vec = array.getitem(i);
			_depthMapRealWorld[i].set(vec.getX(),
									  vec.getY(),
								      vec.getZ());
		}
		
		_parent.println("updateDepthRealWorld calc: " + (_parent.millis()-now));
		*/
		
		_depthRealWorldTimeStamp = updateTimeStamp();
	}
	
	protected void updateImage()
	{
		if((nodes() & NODE_IMAGE) == 0)
			return;
		if(_rgbTimeStamp ==  updateTimeStamp())
			return;
		
		// copy the rgb map
		_rgbImage.loadPixels();
			rgbImage(_rgbImage.pixels);
		_rgbImage.updatePixels();
		
		_rgbTimeStamp = updateTimeStamp();
	}
	
	protected void updateIrImage()
	{
		if((nodes() & NODE_IR) == 0)
			return;
		if(_irImageTimeStamp ==  updateTimeStamp())
			return;
					
		_irImage.loadPixels();
			irImage(_irImage.pixels);
		_irImage.updatePixels();
		
		_irImageTimeStamp = updateTimeStamp();
	}
		
	protected void updateSceneRaw()
	{
		if((nodes() & NODE_SCENE) == 0)
			return;
		if(_sceneMapTimeStamp ==  updateTimeStamp())
			return;
					
		sceneMap(_sceneRaw);
		
		_sceneMapTimeStamp = updateTimeStamp();
	}
	
	protected void updateSceneImage()
	{
		if((nodes() & NODE_SCENE) == 0)
			return;
		if(_sceneImageTimeStamp ==  updateTimeStamp())
			return;
		
		// copy the scene map
		_sceneImage.loadPixels();
			sceneImage(_sceneImage.pixels);
		_sceneImage.updatePixels();
		_sceneImageTimeStamp = updateTimeStamp();
	}
	
	/**
	* Enable the user data collection
	*/  
	public void update() 
	{
		super.update();
	}	
	
	/**
	* Draws a limb from joint1 to joint2
	* 
	* @param userId
	*          int
	* @param joint1
	*          int
	* @param joint2
	*          int
	*/
	public void drawLimb(PGraphics g, int userId, int joint1, int  joint2)
	{
		if (!isCalibratedSkeleton(userId))
			return;
		if (!isTrackingSkeleton(userId))
			return;

		XnSkeletonJointPosition joint1Pos = new XnSkeletonJointPosition();
		XnSkeletonJointPosition joint2Pos = new XnSkeletonJointPosition();
		
		getJointPositionSkeleton(userId, joint1, joint1Pos);
		getJointPositionSkeleton(userId, joint2, joint2Pos);

		if (joint1Pos.getFConfidence() < 0.5 || joint2Pos.getFConfidence() < 0.5)
			return;
			
		// calc the 3d coordinate to screen coordinates
		XnVector3D pt1 = new XnVector3D();
		XnVector3D pt2 = new XnVector3D();
		
		convertRealWorldToProjective(joint1Pos.getPosition(), pt1);
		convertRealWorldToProjective(joint2Pos.getPosition(), pt2);
		
		g.line(pt1.getX(), pt1.getY(),
					 pt2.getX(), pt2.getY());

	}

	/**
	* gets the coordinates of a joint
	* 
	* @param userId
	*          int
	* @param joint
	*          int
	* @param jointPos
	*          PVector
	* @return The confidence of this joint
	*          float
	*/	
	public float getJointPositionSkeleton(int userId,int joint,PVector jointPos)
	{
		if (!isCalibratedSkeleton(userId))
			return 0.0f;
		if (!isTrackingSkeleton(userId))
			return 0.0f;

		XnSkeletonJointPosition jointPos1 = new XnSkeletonJointPosition();
		
		getJointPositionSkeleton(userId, joint, jointPos1);
		jointPos.set(jointPos1.getPosition().getX(),
					 jointPos1.getPosition().getY(),
					 jointPos1.getPosition().getZ());

		return jointPos1.getFConfidence();
	}

	/**
	* gets the orientation of a joint
	* 
	* @param userId
	*          int
	* @param joint
	*          int
	* @param jointOrientation
	*          PMatrix3D
	* @return The confidence of this joint
	*          float
	*/	
	public float getJointOrientationSkeleton(int userId,int joint,PMatrix3D jointOrientation)
	{
		if (!isCalibratedSkeleton(userId))
			return 0.0f;
		if (!isTrackingSkeleton(userId))
			return 0.0f;

		XnSkeletonJointOrientation jointOrientation1 = new XnSkeletonJointOrientation();
		
		getJointOrientationSkeleton(userId, joint, jointOrientation1);
		
		// set the matrix by hand, openNI matrix is only 3*3(only rotation, no translation)
		float[] mat = jointOrientation1.getOrientation().getElements();
		jointOrientation.set(mat[0], mat[1], mat[2], 0,
							 mat[3], mat[4], mat[5], 0,
							 mat[6], mat[7], mat[8], 0,
							 0,		 0,		 0, 	 1);

		return jointOrientation1.getFConfidence();
	}

	
	
	public void convertRealWorldToProjective(PVector world,PVector proj) 
	{
		XnVector3D w = new XnVector3D();
		XnVector3D p = new XnVector3D();

		w.setX(world.x);
		w.setY(world.y);
		w.setZ(world.z);
		convertRealWorldToProjective(w,p);
		proj.set(p.getX(),
				 p.getY(),
				 p.getZ());
	}

	/*
	public void convertRealWorldToProjective(Vector3D worldArray, Vector3D projArray) 
	{
	}
	*/
	
	public void convertProjectiveToRealWorld(PVector proj, PVector world) 
	{
		XnVector3D p = new XnVector3D();
		XnVector3D w = new XnVector3D();

		p.setX( proj.x);
		p.setY( proj.y);
		p.setZ( proj.z);
		convertProjectiveToRealWorld(p,w);
		world.set(w.getX(),
				  w.getY(),
				  w.getZ());
	}

	/*
	public void convertProjectiveToRealWorld(Vector3D projArray, Vector3D worldArray) 
	{
	}
	*/
	/**
	*  gets the transformation matrix of a user defined coordinatesystem
	* 
	* @param xformMat
	*          PMatrix3D
	*/	
	public void getUserCoordsysTransMat(PMatrix3D xformMat)
	{
	  //xformMat.identity();
	  if(hasUserCoordsys() == false)
		return;

	  float[] mat = new float[16];
	  getUserCoordsysTransMat(mat);

	  xformMat.set(mat[0], mat[1], mat[2], mat[3],
				   mat[4], mat[5], mat[6], mat[7],
				   mat[8], mat[9], mat[10], mat[11],
				   mat[12], mat[13], mat[14], mat[15]);

	}

	/**
	*  Calculates a point in the user defined coordinate system back to the 3d system of the 3d camera
	* 
	* @param point
	*          PVector
	*/	
    public void calcUserCoordsys(PVector point)
	{
	  if(hasUserCoordsys() == false)
		return;

	  XnPoint3D p = new XnPoint3D();
	  calcUserCoordsys(p);
	  point.set(p.getX(),
				p.getY(),
				p.getZ());
	}

	/**
	*  Applies only the rotation part of 'mat' to 
	* 
	* @param mat
	*          PMatrix3D
	*/	   
    public void calcUserCoordsys(PMatrix3D mat)
	{
	  if(hasUserCoordsys() == false)
		return;

	  XnMatrix3X3 matRet = new XnMatrix3X3();
	  float[] matRetArry = matRet.getElements();
	  matRetArry[0] = mat.m00;
	  matRetArry[1] = mat.m01;
	  matRetArry[2] = mat.m02;

	  matRetArry[3] = mat.m10;
	  matRetArry[4] = mat.m11;
	  matRetArry[5] = mat.m12;

	  matRetArry[6] = mat.m20;
	  matRetArry[7] = mat.m21;
	  matRetArry[8] = mat.m22;

	  calcUserCoordsys(matRet);
	  matRetArry = matRet.getElements();

	  mat.set(matRetArry[0], matRetArry[1], matRetArry[2], 0,
			  matRetArry[3], matRetArry[4], matRetArry[5], 0,
			  matRetArry[6], matRetArry[7], matRetArry[8], 0,
			  0, 		0, 			0, 			1);
	}

	/**
	*  Calculates a point in origninal 3d camera coordinate system to the coordinate system defined by the user
	* 
	* @param point
	*          PVector
	*/	
    public void calcUserCoordsysBack(PVector point)
	{
	  if(hasUserCoordsys() == false)
		return;

	  XnPoint3D p = new XnPoint3D();
	  calcUserCoordsysBack(p);
	  point.set(p.getX(),
				p.getY(),
				p.getZ());
	}

	/**
	*  Calculates a point in origninal 3d camera coordinate system to the coordinate system defined by the user
	* 
	* @param mat
	*          PMatrix3D
	*/	    
	public void calcUserCoordsysBack(PMatrix3D mat)
	{
	  if(hasUserCoordsys() == false)
		return;

	  XnMatrix3X3 matRet = new XnMatrix3X3();
	  float[] matRetArry = matRet.getElements();
	  matRetArry[0] = mat.m00;
	  matRetArry[1] = mat.m01;
	  matRetArry[2] = mat.m02;

	  matRetArry[3] = mat.m10;
	  matRetArry[4] = mat.m11;
	  matRetArry[5] = mat.m12;

	  matRetArry[6] = mat.m20;
	  matRetArry[7] = mat.m21;
	  matRetArry[8] = mat.m22;

	  calcUserCoordsysBack(matRet);
	  matRetArry = matRet.getElements();

	  mat.set(matRetArry[0], matRetArry[1], matRetArry[2], 0,
			  matRetArry[3], matRetArry[4], matRetArry[5], 0,
			  matRetArry[6], matRetArry[7], matRetArry[8], 0,
			  0, 		0, 			0, 			1);
	}

    public void getUserCoordsys(PMatrix3D mat)
	{
	  if(hasUserCoordsys() == false)
		return;

	  float matRet[] = new float[16];
	  getUserCoordsys(matRet);

	  mat.set(matRet[0], matRet[1], matRet[2], matRet[3],
			  matRet[4], matRet[5], matRet[6], matRet[7],
			  matRet[8], matRet[9], matRet[10], matRet[11],
			  matRet[12], matRet[13], matRet[14], matRet[15]);

	}

    public void getUserCoordsysBack(PMatrix3D mat)
	{
	  if(hasUserCoordsys() == false)
		return;

	  float matRet[] = new float[16];
	  getUserCoordsysBack(matRet);

	  mat.set(matRet[0], matRet[1], matRet[2], matRet[3],
			  matRet[4], matRet[5], matRet[6], matRet[7],
			  matRet[8], matRet[9], matRet[10], matRet[11],
			  matRet[12], matRet[13], matRet[14], matRet[15]);
	}

	///////////////////////////////////////////////////////////////////////////
	// helper methods
	/**
	*  Helper method that draw the 3d camera and the frustum of the camera
	* 
	*/	    
	public void drawCamFrustum(PGraphics g)
	{
		g.pushStyle();
		g.pushMatrix();

			if(hasUserCoordsys())
			{	// move the camera to the real nullpoint
				PMatrix3D mat = new PMatrix3D();
				getUserCoordsys(mat);
				g.applyMatrix(mat);
			}

			// draw cam case
			g.stroke(200,200,0);  
			g.noFill();
			g.beginShape();
				g.vertex(270 * .5f,40 * .5f,0.0f);
				g.vertex(-270 * .5f,40 * .5f,0.0f);
				g.vertex(-270 * .5f,-40 * .5f,0.0f);
				g.vertex(270 * .5f,-40 * .5f,0.0f);
			g.endShape(PConstants.CLOSE);
			
			g.beginShape();
				g.vertex(220 * .5f,40 * .5f,-50.0f);
				g.vertex(-220 * .5f,40 * .5f,-50.0f);
				g.vertex(-220 * .5f,-40 * .5f,-50.0f);
				g.vertex(220 * .5f,-40 * .5f,-50.0f);
			g.endShape(PConstants.CLOSE);
			
			g.beginShape(PConstants.LINES);
				g.vertex(270 * .5f,40 * .5f,0.0f);
				g.vertex(220 * .5f,40 * .5f,-50.0f);
				
				g.vertex(-270 * .5f,40 * .5f,0.0f);
				g.vertex(-220 * .5f,40 * .5f,-50.0f);
				
				g.vertex(-270 * .5f,-40 * .5f,0.0f);
				g.vertex(-220 * .5f,-40 * .5f,-50.0f);
				
				g.vertex(270 * .5f,-40 * .5f,0.0f);
				g.vertex(220 * .5f,-40 * .5f,-50.0f);
			g.endShape();
			
			// draw cam opening angles
			g.stroke(200,200,0,50);  
			g.line(0.0f,0.0f,0.0f,
						   0.0f,0.0f,1000.0f);
			
			// calculate the angles of the cam, values are in radians, radius is 10m
			float distDepth = 10000;
			
			float valueH = distDepth * PApplet.tan(hFieldOfView() * .5f); 
			float valueV = distDepth * PApplet.tan(vFieldOfView() * .5f);       
			
			g.stroke(200,200,0,100);  
			g.line(0.0f,0.0f,0.0f,
						 valueH,valueV,distDepth);
			g.line(0.0f,0.0f,0.0f,
						 -valueH,valueV,distDepth);
			g.line(0.0f,0.0f,0.0f,
						 valueH,-valueV,distDepth);
			g.line(0.0f,0.0f,0.0f,
						 -valueH,-valueV,distDepth);
			g.beginShape();
				g.vertex(valueH,valueV,distDepth);
				g.vertex(-valueH,valueV,distDepth);
				g.vertex(-valueH,-valueV,distDepth);
				g.vertex(valueH,-valueV,distDepth);
			g.endShape(PConstants.CLOSE);
		
		g.popMatrix();	
		g.popStyle();	
	}
	
	///////////////////////////////////////////////////////////////////////////
    // geometry helper functions

	public static boolean rayTriangleIntersection(PVector p, PVector dir, 
												  PVector vec0,PVector vec1, PVector vec2,
												  PVector hit) 
	{	 
	  float[] hitRet= new float[3];
	  if(rayTriangleIntersection(p.array(), dir.array(), vec0.array(), vec1.array(), vec2.array(), hitRet))
	  {
		hit.set(hitRet);
		return true;
	  }
	  else
		return false;
	}

	public static int raySphereIntersection(PVector p, PVector dir, 
											PVector sphereCenter,float sphereRadius,
										    PVector hit1,PVector hit2) 
	{	 
	  float[] hit1Ret= new float[3];
	  float[] hit2Ret= new float[3];

	  int ret = raySphereIntersection(p.array(), dir.array(), 
									  sphereCenter.array(), sphereRadius, 
									  hit1Ret, hit2Ret);

	  if(ret > 0)
	  {
		hit1.set(hit1Ret);
		
		if(ret > 1)
		  hit2.set(hit2Ret);
	  }
	  return ret;
	}
	///////////////////////////////////////////////////////////////////////////
	// callbacks
	protected void onNewUserCb(long userId) 
	{
		try {
			_newUserMethod.invoke(_userCbObject, new Object[] { (int)userId });
		} 
		catch (Exception e) 
		{
		}	
	}

	protected void onLostUserCb(long userId)
	{
		try {
			_lostUserMethod.invoke(_userCbObject, new Object[] { (int)userId });		
		} 
		catch (Exception e) 
		{
		}	
	}
	protected void onExitUserCb(long userId)
	{
		try {
			_exitUserMethod.invoke(_userCbObject, new Object[] { (int)userId });		
		} 
		catch (Exception e) 
		{
		}	
	}
	protected void onReEnterUserCb(long userId)
	{
		try {
			_reEnterUserMethod.invoke(_userCbObject, new Object[] { (int)userId });		
		} 
		catch (Exception e) 
		{
		}	
	}

	protected void onStartCalibrationCb(long userId) 
	{
		try {
			_startCalibrationMethod.invoke(_calibrationCbObject, new Object[] { (int)userId });	
		} 
		catch (Exception e) 
		{
		}	
	}

	protected void onEndCalibrationCb(long userId, boolean successFlag) 
	{
		try {
			_endCalibrationMethod.invoke(_calibrationCbObject, new Object[] { (int)userId, successFlag});
		} 
		catch (Exception e) 
		{
		}	
	}

	protected void onStartPoseCb(String strPose, long userId) 
	{
		try {
			_startPoseMethod.invoke(_poseCbObject, new Object[] { strPose,(int)userId });
		} 
		catch (Exception e) 
		{
		}	
	}

	protected void onEndPoseCb(String strPose, long userId)
	{
		try {
			_endPoseMethod.invoke(_poseCbObject, new Object[] { strPose,(int)userId });
		} 
		catch (Exception e) 
		{
		}	
	}

	// hands
	protected void onCreateHandsCb(long nId, XnPoint3D pPosition, float fTime)
	{
		try {
			_createHandsMethod.invoke(_handsCbObject, new Object[] { (int)nId,new PVector(pPosition.getX(),pPosition.getY(),pPosition.getZ()),fTime});
		} 
		catch (Exception e) 
		{}	
	}	
	
	protected void onUpdateHandsCb(long nId, XnPoint3D pPosition, float fTime)
	{
		try {
			_updateHandsMethod.invoke(_handsCbObject, new Object[] { (int)nId,new PVector(pPosition.getX(),pPosition.getY(),pPosition.getZ()),fTime});
		} 
		catch (Exception e) 
		{}	
	}	
	
	protected void onDestroyHandsCb(long nId, float fTime)
	{
		try {
			_destroyHandsMethod.invoke(_handsCbObject, new Object[] { (int)nId,fTime});
		} 
		catch (Exception e) 
		{}	
	}	
  
	protected void onRecognizeGestureCb(String strGesture, XnPoint3D pIdPosition, XnPoint3D pEndPosition) 
	{
		try {
			_recognizeGestureMethod.invoke(_gestureCbObject, new Object[] { strGesture,
																   new PVector(pIdPosition.getX(),pIdPosition.getY(),pIdPosition.getZ()),
																   new PVector(pEndPosition.getX(),pEndPosition.getY(),pEndPosition.getZ())
																 });
		} 
		catch (Exception e) 
		{}	
	}

	protected void onProgressGestureCb(String strGesture, XnPoint3D pPosition, float fProgress) 
	{
		try {
			_progressGestureMethod.invoke(_gestureCbObject, new Object[] { strGesture,
																  new PVector(pPosition.getX(),pPosition.getY(),pPosition.getZ()),
																  fProgress
																 });
		} 
		catch (Exception e) 
		{}	
	}	
		
	// nite callbacks
	protected void onStartSessionCb(XnPoint3D ptPosition)
	{
		try {
			_startSessionMethod.invoke(_sessionCbObject, new Object[] { new PVector(ptPosition.getX(),ptPosition.getY(),ptPosition.getZ()) });
		} 
		catch (Exception e) 
		{}	
	}
	
	protected void onEndSessionCb()
	{
		try {
			_endSessionMethod.invoke(_sessionCbObject, new Object[] { });
		} 
		catch (Exception e) 
		{}		}
	
	protected void onFocusSessionCb(String strFocus, XnPoint3D ptPosition, float fProgress)
	{
		try {
			_focusSessionMethod.invoke(_sessionCbObject, new Object[] {strFocus, 
															  new PVector(ptPosition.getX(),ptPosition.getY(),ptPosition.getZ()),
															  fProgress });
		} 
		catch (Exception e) 
		{}	
	
	}

}

