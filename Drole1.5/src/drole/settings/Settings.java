package drole.settings;

public class Settings {

	// Some standard settings
	public static boolean USE_KINECT						= 	true;
	
	public static boolean USE_GESTURES						= 	false;
	
	public static short MAX_LOG_ENTRYS						=	  30;
	
	public static boolean DRAW_LOGS							=	true;
	
	public static boolean MOUSE_IS_HAND						= 	false;
	
	// Kinect
	public static float KINECT_Y_FUNCTION					= 	0.44547f;
	
	// Settings LEAP
	
	// Pixel Dimension of screen
	public static int VIRTUAL_SCREEN_WIDTH					= 	1080;
	public static int VIRTUAL_SCREEN_HEIGHT					= 	1080;
	
	// Dimension of the real worlds screen in mm
	public static int REAL_SCREEN_DIMENSIONS_WIDTH_MM 		= 	1800;
	public static int REAL_SCREEN_DIMENSIONS_HEIGHT_MM 		= 	1800;
	public static int REAL_SCREEN_DIMENSIONS_DEPTH_MM 		= 	   0;
	
	// Dimension of the virtual room in mm
	public static int VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM 		=   3000;
	public static int VIRTUAL_ROOM_DIMENSIONS_HEIGHT_MM 	=   3000;
	public static int VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM 		=   3000;

	
	// Position of the real world screens left lower corner in TRACKER SPACE (in relation to the kinect) given in mm!
	public static int REAL_SCREEN_POSITION_X_MM				=  	 -900;
	public static int REAL_SCREEN_POSITION_Y_MM				= 	-1855;
	public static int REAL_SCREEN_POSITION_Z_MM				= 	    0;
	
	
	// MENU
	public static float MENU_GLOBE_POSITION_X				=   0;
	public static float MENU_GLOBE_POSITION_Y				=   0;
	public static float MENU_GLOBE_POSITION_Z				=   -(VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM/2f);
	
	public static float MENU_GLOBE_RADIUS_MM				=  800;
	
	// WORLDS 
	public static String[] WORLDS							= new String[]{
			"data/images/menu/01_archtektur.png",
			"data/images/menu/02_assoziationen.png",
			"data/images/menu/03_Mikro_makro.png",
			"data/images/menu/04_optik_sehen.png",
			"data/images/menu/05_Spektakel.png"
	};
	
}
