package drole.settings;

public class Settings {

	// Some standard settings
	public static boolean USE_KINECT						= 	false;
	
	public static boolean USE_GESTURES						= 	false;
	
	public static short MAX_LOG_ENTRYS						=	  30;
	
	public static boolean DRAW_LOGS							=	false;
	
	// Settings LEAP
	
	// Pixel Dimension of screen
	public static int VIRTUAL_SCREEN_WIDTH					= 	1080;
	public static int VIRTUAL_SCREEN_HEIGHT					= 	1080;
	
	// Dimension of the real worlds screen in mm
	public static int REAL_SCREEN_DIMENSIONS_WIDTH_MM 		= 	1800;
	public static int REAL_SCREEN_DIMENSIONS_HEIGHT_MM 		= 	1800;
	public static int REAL_SCREEN_DIMENSIONS_DEPTH_MM 		= 	   0;
	
	// Dimension of the virtual room in mm
	public static int VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM 		=  1800;

	
	// Position of the real world screens left lower corner in TRACKER SPACE (in relation to the kinect) given in mm!
	public static int REAL_SCREEN_POSITION_X_MM				=  	 -900;
	public static int REAL_SCREEN_POSITION_Y_MM				= 	-1855;
	public static int REAL_SCREEN_POSITION_Z_MM				= 	    0;
	
	
	// Settings Dennys home
	
	// Pixel Dimension of screen
	/*
	public static int VIRTUAL_SCREEN_WIDTH					= 	800;
	public static int VIRTUAL_SCREEN_HEIGHT					= 	800;
	
	// Dimension of the real worlds screen in mm
	public static int REAL_SCREEN_DIMENSIONS_WIDTH_MM 		= 	1570;
	public static int REAL_SCREEN_DIMENSIONS_HEIGHT_MM 		= 	1570;
	public static int REAL_SCREEN_DIMENSIONS_DEPTH_MM 		= 	   0;
	
	// Position of the real world screens left lower corner in TRACKER SPACE (in relation to the kinect) given in mm!
	public static int REAL_SCREEN_POSITION_X_MM				=  	 -900;
	public static int REAL_SCREEN_POSITION_Y_MM				= 	  180;
	public static int REAL_SCREEN_POSITION_Z_MM				= 	 -550;
	*/
}
