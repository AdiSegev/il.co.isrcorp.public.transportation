package com.igo.slapiclient;

public final class SLAPIMessageIds {
	final static String SLAPIServiceName = "com.navngo.igo.javaclient.SLAPI_SERVER";
	/** Handshake messages
	 */
	public static final int SLAPI_CLIENT_HELLO				= 1;
	public static final int SLAPI_SERVER_HELLO				= 2;
	public static final int SLAPI_CLIENT_BYE				= 3;
	public static final int SLAPI_SERVER_BYE				= 4;

	/** Incoming message ids using arg1, arg2 only */
	public static final int SLAPI_DELETEMARKER				= 100;
	public static final int SLAPI_DELETEROUTE				= 101;
	public static final int SLAPI_EXIT						= 102;
	public static final int SLAPI_GETADDRESS				= 103;
	public static final int SLAPI_GETADDRESSFROMPOS		= 104;
	public static final int SLAPI_GETCURSORADDRESS			= 105;
	public static final int SLAPI_GETCURSORPOS				= 106;
	public static final int SLAPI_GETGPSSTATUS				= 107;
	public static final int SLAPI_GETGPSTIME				= 108;
	public static final int SLAPI_GETLANGUAGE				= 109;
	public static final int SLAPI_GETPOSITION				= 110;
	public static final int SLAPI_GETROUTEGUIDANCEDATA		= 111;
	public static final int SLAPI_GETROUTEINFO				= 112;
	public static final int SLAPI_GETSPEEDANDDIRECTION		= 113;
	public static final int SLAPI_GETVERSION				= 114;
	public static final int SLAPI_SETLANGUAGE				= 115;
	public static final int SLAPI_SETPLANMODE				= 116;
	public static final int SLAPI_SETSOUND					= 117;
	public static final int SLAPI_SETSTARTPOS				= 118;
	public static final int SLAPI_SETVEHICLE				= 119;
	public static final int SLAPI_SHOWHMI					= 120;

	/** Incoming message ids using complex data structures */
	/** This message is generated internally for incoming messages using complex data structures */
	public static final int SLAPI_COPYDATA					= 200;
	public static final int SLAPI_ADDFAVORITEADDRESS		= 201;
	public static final int SLAPI_ADDFAVORITEPOS			= 202;
	public static final int SLAPI_GETPOSFROMADDRESS		= 203;
	public static final int SLAPI_LOADKMLROUTE				= 204;
	public static final int SLAPI_SETDESTINATIONADDRESS	= 205;
	public static final int SLAPI_SETDESTINATIONPOS		= 206;
	public static final int SLAPI_SETMARKER				= 207;
	public static final int SLAPI_SETSTARTADDRESS			= 208;
	public static final int SLAPI_SETTRUCKSETTING			= 209;
	public static final int SLAPI_SETUPTRUCKPROFILE		= 210;
	public static final int SLAPI_SETWAYPOINTADDRESS		= 211;
	public static final int SLAPI_SETWAYPOINTPOS			= 212;
	public static final int SLAPI_SHOWPOS					= 213;
	public static final int SLAPI_UX_GETVAR				= 214;
	public static final int SLAPI_UX_INIT					= 215;
	public static final int SLAPI_UX_LIST_SPLICE			= 216;
	public static final int SLAPI_UX_REGISTER_CALLBACK		= 217;
	public static final int SLAPI_UX_RUN					= 218;
	public static final int SLAPI_UX_RUN_GET_ACCELERATION  = 218 + 500;
	public static final int SLAPI_UX_RUN_GET_SPEED_LIMIT  = 218 + 501;
	public static final int SLAPI_UX_RUN_GET_VEHICLE_TYPE  = 218 + 502;
	public static final int SLAPI_UX_RUN_SHOW_MESSAGE_BOX  = 218 + 503;
	public static final int SLAPI_UX_RUN_SET_SPEED_LIMIT0  = 218 + 504;
	public static final int SLAPI_UX_RUN_SET_SPEED_LIMIT1  = 218 + 505;
	public static final int SLAPI_UX_RUN_SET_SPEED_LIMIT3  = 218 + 506;
	public static final int SLAPI_UX_RUN_SET_DA_TABLE0     = 218 + 507;
	public static final int SLAPI_UX_RUN_SET_DA_TABLE1     = 218 + 508;
	public static final int SLAPI_UX_RUN_SET_DA_TABLE3     = 218 + 509;
	public static final int SLAPI_UX_SETVAR				= 219;
	public static final int SLAPI_SHOW_MESSAGE_BOX         = 220;	// Android specific, not part of the base SLAPI protocol

	/** Outgoing messages using wParam, lParam:
	 */
	public static final int SLAPI_OUT_ARRIVEDTODESTINATION	= 300;
	public static final int SLAPI_OUT_ARRIVEDTOWAYPOINT	= 301;
	public static final int SLAPI_OUT_DESTINATIONSET		= 302;
	public static final int SLAPI_OUT_KMLROUTELOADED		= 303;
	public static final int SLAPI_OUT_LANGUAGESET			= 304;
	public static final int SLAPI_OUT_LANGUAGE				= 305;
	public static final int SLAPI_OUT_PLANMODESET			= 306;
	public static final int SLAPI_OUT_POSFROMADDRESS		= 307;
	public static final int SLAPI_OUT_POSITION				= 308;
	public static final int SLAPI_OUT_SPEEDANDDIRECTION	= 309;
	public static final int SLAPI_OUT_STARTSET				= 310;
	public static final int SLAPI_OUT_TRUCKPROFILESETUPDONE = 311;
	public static final int SLAPI_OUT_VEHICLESET			= 312;
	public static final int SLAPI_OUT_WAYPOINTSET			= 313;

	/** Outgoing messages using complex data structures:
	 */
	public static final int SLAPI_OUT_ADDRESSFROMPOS		= 314;
	public static final int SLAPI_OUT_ADDRESS				= 315;
	public static final int SLAPI_OUT_CURSORADDRESS		= 316;
	public static final int SLAPI_OUT_CURSORPOS			= 317;
	public static final int SLAPI_OUT_ERROR				= 318;
	public static final int SLAPI_OUT_GPSSTATUS			= 319;
	public static final int SLAPI_OUT_GPSTIME				= 320;
	public static final int SLAPI_OUT_ROUTEGUIDANCEDATA	= 321;
	public static final int SLAPI_OUT_ROUTEINFO			= 322;
	public static final int SLAPI_OUT_UX_CALLBACK			= 323;
	public static final int SLAPI_OUT_UX_RESULT			= 324;
	public static final int SLAPI_OUT_VERSION				= 325;

	public static final int SLAPI_OUT_MESSAGE_BOX_PRESSED  = 326;	// Android specific, not part of base SLAPI protocol

	/** Special client id with the meaning broadcast message */
		public static final int HWND_BROADCAST	= 0xB0ADCA57;

	/** Delivers the name belonging to the message id. */
	public static String MessageName(int msgId) {
		switch (msgId)
		{
			case SLAPIMessageIds.SLAPI_SERVER_HELLO:	return "WM_SLAPI_SERVER_HELLO";
			case SLAPIMessageIds.SLAPI_CLIENT_HELLO:	return "WM_SLAPI_CLIENT_HELLO";
			case SLAPIMessageIds.SLAPI_SERVER_BYE:	return "WM_SLAPI_SERVER_BYE";
			case SLAPIMessageIds.SLAPI_CLIENT_BYE:	return "WM_SLAPI_CLIENT_BYE";

		// CLIENT MESSAGES
			case SLAPIMessageIds.SLAPI_GETPOSITION:	return "WM_SLAPI_GETPOSITION";
			case SLAPIMessageIds.SLAPI_GETROUTEGUIDANCEDATA:	return "WM_SLAPI_GETROUTEGUIDANCEDATA";
			case SLAPIMessageIds.SLAPI_SETDESTINATIONPOS:	return "WM_SLAPI_SETDESTINATIONPOS";
			case SLAPIMessageIds.SLAPI_SETDESTINATIONADDRESS:	return "WM_SLAPI_SETDESTINATIONADDRESS";
			case SLAPIMessageIds.SLAPI_GETADDRESSFROMPOS:	return "WM_SLAPI_GETADDRESSFROMPOS";
			case SLAPIMessageIds.SLAPI_GETPOSFROMADDRESS:	return "WM_SLAPI_GETPOSFROMADDRESS";
			case SLAPIMessageIds.SLAPI_LOADKMLROUTE:	return "WM_SLAPI_LOADKMLROUTE";
			case SLAPIMessageIds.SLAPI_DELETEROUTE:	return "WM_SLAPI_DELETEROUTE";
			case SLAPIMessageIds.SLAPI_SETWAYPOINTPOS:	return "WM_SLAPI_SETWAYPOINTPOS";
			case SLAPIMessageIds.SLAPI_SETWAYPOINTADDRESS:	return "WM_SLAPI_SETWAYPOINTADDRESS";
			case SLAPIMessageIds.SLAPI_SETSTARTPOS:	return "WM_SLAPI_SETSTARTPOS";
			case SLAPIMessageIds.SLAPI_SETSTARTADDRESS:	return "WM_SLAPI_SETSTARTADDRESS";
			case SLAPIMessageIds.SLAPI_SETPLANMODE:	return "WM_SLAPI_SETPLANMODE";
			case SLAPIMessageIds.SLAPI_SETVEHICLE:	return "WM_SLAPI_SETVEHICLE";
			case SLAPIMessageIds.SLAPI_SETLANGUAGE:	return "WM_SLAPI_SETLANGUAGE";
			case SLAPIMessageIds.SLAPI_GETLANGUAGE:	return "WM_SLAPI_GETLANGUAGE";
			case SLAPIMessageIds.SLAPI_GETGPSTIME:	return "WM_SLAPI_GETGPSTIME";
			case SLAPIMessageIds.SLAPI_GETGPSSTATUS:	return "WM_SLAPI_GETGPSSTATUS";
			case SLAPIMessageIds.SLAPI_GETSPEEDANDDIRECTION:	return "WM_SLAPI_GETSPEEDANDDIRECTION";
			case SLAPIMessageIds.SLAPI_SETTRUCKSETTING:	return "WM_SLAPI_SETTRUCKSETTING";
			case SLAPIMessageIds.SLAPI_SETUPTRUCKPROFILE:	return "WM_SLAPI_SETUPTRUCKPROFILE";
			case SLAPIMessageIds.SLAPI_SHOWHMI:	return "WM_SLAPI_SHOWHMI";
			case SLAPIMessageIds.SLAPI_SHOWPOS:	return "WM_SLAPI_SHOWPOS";
			case SLAPIMessageIds.SLAPI_GETROUTEINFO:	return "WM_SLAPI_GETROUTEINFO";
			case SLAPIMessageIds.SLAPI_SETMARKER:	return "WM_SLAPI_SETMARKER";
			case SLAPIMessageIds.SLAPI_DELETEMARKER:	return "WM_SLAPI_DELETEMARKER";
			case SLAPIMessageIds.SLAPI_ADDFAVORITEPOS:	return "WM_SLAPI_ADDFAVORITEPOS";
			case SLAPIMessageIds.SLAPI_ADDFAVORITEADDRESS:	return "WM_SLAPI_ADDFAVORITEADDRESS";
			case SLAPIMessageIds.SLAPI_SETSOUND:	return "WM_SLAPI_SETSOUND";
			case SLAPIMessageIds.SLAPI_GETADDRESS:	return "WM_SLAPI_GETADDRESS";
			case SLAPIMessageIds.SLAPI_GETVERSION:	return "WM_SLAPI_GETVERSION";
			case SLAPIMessageIds.SLAPI_EXIT:	return "WM_SLAPI_EXIT";

			case SLAPIMessageIds.SLAPI_UX_INIT:	return "WM_SLAPI_UX_INIT";
			case SLAPIMessageIds.SLAPI_UX_REGISTER_CALLBACK:	return "WM_SLAPI_UX_REGISTER_CALLBACK";
			case SLAPIMessageIds.SLAPI_UX_RUN:	return "WM_SLAPI_UX_RUN";
			case SLAPIMessageIds.SLAPI_UX_SETVAR:	return "WM_SLAPI_UX_SETVAR";
			case SLAPIMessageIds.SLAPI_UX_GETVAR:	return "WM_SLAPI_UX_GETVAR";
			case SLAPIMessageIds.SLAPI_UX_LIST_SPLICE:	return "WM_SLAPI_UX_LIST_SPLICE";
			case SLAPIMessageIds.SLAPI_GETCURSORPOS:	return "WM_SLAPI_GETCURSORPOS";
			case SLAPIMessageIds.SLAPI_GETCURSORADDRESS:	return "WM_SLAPI_GETCURSORADDRESS";
			case SLAPIMessageIds.SLAPI_SHOW_MESSAGE_BOX:	return "WM_SLAPI_SHOW_MESSAGE_BOX";
			
		// SERVER MESSAGES								 
			case SLAPIMessageIds.SLAPI_OUT_POSITION:	return "WM_SLAPI_OUT_POSITION";
			case SLAPIMessageIds.SLAPI_OUT_ROUTEGUIDANCEDATA:	return "WM_SLAPI_OUT_ROUTEGUIDANCEDATA";
			case SLAPIMessageIds.SLAPI_OUT_DESTINATIONSET:	return "WM_SLAPI_OUT_DESTINATIONSET";
			case SLAPIMessageIds.SLAPI_OUT_ADDRESSFROMPOS:	return "WM_SLAPI_OUT_ADDRESSFROMPOS";
			case SLAPIMessageIds.SLAPI_OUT_POSFROMADDRESS:	return "WM_SLAPI_OUT_POSFROMADDRESS";
			case SLAPIMessageIds.SLAPI_OUT_KMLROUTELOADED:	return "WM_SLAPI_OUT_KMLROUTELOADED";
			case SLAPIMessageIds.SLAPI_OUT_WAYPOINTSET:	return "WM_SLAPI_OUT_WAYPOINTSET";
			case SLAPIMessageIds.SLAPI_OUT_STARTSET:	return "WM_SLAPI_OUT_STARTSET";
			case SLAPIMessageIds.SLAPI_OUT_PLANMODESET:	return "WM_SLAPI_OUT_PLANMODESET";
			case SLAPIMessageIds.SLAPI_OUT_VEHICLESET:	return "WM_SLAPI_OUT_VEHICLESET";
			case SLAPIMessageIds.SLAPI_OUT_LANGUAGESET:	return "WM_SLAPI_OUT_LANGUAGESET";
			case SLAPIMessageIds.SLAPI_OUT_LANGUAGE:	return "WM_SLAPI_OUT_LANGUAGE";
			case SLAPIMessageIds.SLAPI_OUT_GPSTIME:	return "WM_SLAPI_OUT_GPSTIME";
			case SLAPIMessageIds.SLAPI_OUT_GPSSTATUS:	return "WM_SLAPI_OUT_GPSSTATUS";
			case SLAPIMessageIds.SLAPI_OUT_SPEEDANDDIRECTION:	return "WM_SLAPI_OUT_SPEEDANDDIRECTION";
			case SLAPIMessageIds.SLAPI_OUT_TRUCKPROFILESETUPDONE:	return "WM_SLAPI_OUT_TRUCKPROFILESETUPDONE";
			case SLAPIMessageIds.SLAPI_OUT_ROUTEINFO:	return "WM_SLAPI_OUT_ROUTEINFO";
			case SLAPIMessageIds.SLAPI_OUT_ADDRESS:	return "WM_SLAPI_OUT_ADDRESS";
			case SLAPIMessageIds.SLAPI_OUT_VERSION:	return "WM_SLAPI_OUT_VERSION";
			case SLAPIMessageIds.SLAPI_OUT_ARRIVEDTOWAYPOINT:	return "WM_SLAPI_OUT_ARRIVEDTOWAYPOINT";
			case SLAPIMessageIds.SLAPI_OUT_ARRIVEDTODESTINATION:	return "WM_SLAPI_OUT_ARRIVEDTODESTINATION";
			case SLAPIMessageIds.SLAPI_OUT_UX_CALLBACK:	return "WM_SLAPI_OUT_UX_CALLBACK";
			case SLAPIMessageIds.SLAPI_OUT_UX_RESULT:	return "WM_SLAPI_OUT_UX_RESULT";
			case SLAPIMessageIds.SLAPI_OUT_ERROR:	return "WM_SLAPI_OUT_ERROR";
			case SLAPIMessageIds.SLAPI_OUT_CURSORPOS:	return "WM_SLAPI_OUT_CURSORPOS";
			case SLAPIMessageIds.SLAPI_OUT_CURSORADDRESS:	return "WM_SLAPI_OUT_CURSORADDRESS";
			case SLAPIMessageIds.SLAPI_OUT_MESSAGE_BOX_PRESSED:	return "WM_SLAPI_OUT_MESSAGE_BOX_PRESSED";
			
			case SLAPIMessageIds.SLAPI_COPYDATA:	return "WM_COPYDATA";

			default:
				//D5(new String("SLAPIClient"), "Unidentified message ID: " + msgId);
				return "Unknown message(" + msgId + ")";
		}
	}
}
