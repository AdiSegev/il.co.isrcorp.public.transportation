//package com.igo.slapiclient;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.Messenger;
//
//import com.example.il.co.isrcorp.spmcommunicationcore.Utils;
//import com.igo.slapiclient.CPD.CDBException;
//
//public class SLAPIClient {
//
//private MainActivity mActivity;
///**
// * Used to parse and handle data came from SPM.
// */
//private SpmResponseParser spmParser;
//
//private OverlayServiceIncomingMessageHandler              mIncomingHandler = null;
//private Messenger                    mMessenger       = null;
//private com.igo.slapiclient.SLAPITransmitter             mTransmitter     = null;
//
//final int HANDLE_GET_ACCELERATION = 666;
//	final int HANDLE_SHOW_MESSAGE_BOX = 667;
//	final int HANDLE_MESSAGE_BOX_PRESSED = 668;
//	final int HANDLE_GET_SPEED_LIMIT = 669;
//	final int HANDLE_GET_VEHICLE_TYPE = 670;
//	final int HANDLE_SEND_ACCELERATION = 671;
//
//	private ServerHelloBroadcastReceiver mHelloReceiver;
//
//	private ServerByeBroadcastReceiver mByeReceiver;
//	
//private OverlayServiceIncomingMessageHandler overlayHandler = null;
//
//
///** Messenger for communicating with the overlay service.
// *  We use this service to draw the 'Exit' button from IGO.
// *  
// *  */
//Messenger mOverlayServiceMessanger = null;
//
///** Flag indicating whether we have called bind on the service. */
//boolean mIsOverLayServiceBound;
//
//public SLAPIClient (MainActivity activity){
//	mActivity = activity;
//
//    mIncomingHandler = new OverlayServiceIncomingMessageHandler();
//    mIncomingHandler.client = this;
//    mMessenger = new Messenger(mIncomingHandler);
//    mTransmitter = new SLAPITransmitter(mActivity, mMessenger);
//    
//    overlayHandler = new OverlayServiceIncomingMessageHandler();
//    mOverlayServiceMessanger = new Messenger(overlayHandler);
//    
//    mHelloReceiver = new ServerHelloBroadcastReceiver();
//    mByeReceiver = new ServerByeBroadcastReceiver();
//    
//	spmParser = new SpmResponseParser();
//    
//    mActivity.registerReceiver(mHelloReceiver,new IntentFilter("com.navngo.igo.javaclient.SLAPI_SERVER_HELLO"));
//    mActivity.registerReceiver(mByeReceiver,new IntentFilter("com.navngo.igo.javaclient.SLAPI_SERVER_BYE"));
//		
//    mTransmitter.doBindService();
//  
//}
//
///**
// * Class for interacting with the main interface of the service.
// */
//private ServiceConnection mConnection = new ServiceConnection() {
//	public void onServiceConnected(ComponentName className,
//			IBinder service) {
//		// This is called when the connection with the service has been
//		// established, giving us the service object we can use to
//		// interact with the service.  We are communicating with our
//		// service through an IDL interface, so get a client-side
//		// representation of that from the raw service object.
//		mOverlayServiceMessanger = new Messenger(service);
//	}
//
//	public void onServiceDisconnected(ComponentName className) {
//		// This is called when the connection with the service has been
//		// unexpectedly disconnected -- that is, its process crashed.
//		mOverlayServiceMessanger = null;
//	}
//};
//
//void doBindService() {
//	// Establish a connection with the service.  We use an explicit
//	// class name because there is no reason to be able to let other
//	// applications replace our component.
//	mActivity.bindService(new Intent(mActivity, MyOverlayService.class), mConnection, Context.BIND_AUTO_CREATE);
//	mIsOverLayServiceBound = true;
//}
//
//void doUnbindService() {
//	if (mIsOverLayServiceBound) {
//		// If we have received the service, and hence registered with
//		// it, then now is the time to unregister.
//		if (mOverlayServiceMessanger != null) {
//			
//		}
//
//		// Detach our existing connection.
//		mActivity.unbindService(mConnection);
//		mIsOverLayServiceBound = false;
//	}
//}
//
///** Handles the messages coming from the service */
//class OverlayServiceIncomingMessageHandler extends Handler {
//public SLAPIClient client = null;
//	@Override
//	public void handleMessage(Message msg) {
//	  if ( client != null )
//	    client.handleOverlayMessage(msg);
//	}
//}
//
//public void handleOverlayMessage(Message msg) {
//	// TODO Auto-generated method stub
//	
//}
//
///** Handles the messages coming from the service */
//class IncomingHandler extends Handler {
//public SLAPIClient client = null;
//	@Override
//	public void handleMessage(Message msg) {
//	  if ( client != null )
//	    client.handleThisMessage(msg);
//	}
//}
//
///** Displays the message received from the SLAPI server */
//protected void handleThisMessage(Message msg) {
//	Bundle d = msg.getData();
//	CPD cpd = new CPD();
//	switch (msg.what) {
//	case SLAPIMessageIds.SLAPI_OUT_ADDRESS:
//	case SLAPIMessageIds.SLAPI_OUT_ADDRESSFROMPOS:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), "Address:"
//				+ d.getString("Address"));
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_ARRIVEDTODESTINATION:
//		// break; intentional fall through
//	case SLAPIMessageIds.SLAPI_OUT_TRUCKPROFILESETUPDONE:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), "");
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_ARRIVEDTOWAYPOINT:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//				"Waypoint(arg1) = " + msg.arg1);
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_CURSORADDRESS:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), ""); // TODO_tavisaman
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_CURSORPOS:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), ""); // TODO_tavisaman
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_KMLROUTELOADED:
//		
//		// check if route successfully loaded (0=failed, 1=success)
//		
//		if(msg.arg1 == 1){
//		mTransmitter.sendRequest(SLAPIMessageIds.SLAPI_SHOWHMI,1);
//		mActivity.onKmlLoaded(true);
//		}
//		else if (msg.arg1 == 0){
//			mActivity.onKmlLoaded(false);
//		}
//	break;
//	case SLAPIMessageIds.SLAPI_OUT_DESTINATIONSET:
//		// break; intentional fall through
//	
//	case SLAPIMessageIds.SLAPI_OUT_LANGUAGESET:
//		// break; intentional fall through
//	case SLAPIMessageIds.SLAPI_OUT_PLANMODESET:
//		// break; intentional fall through
//	case SLAPIMessageIds.SLAPI_OUT_STARTSET:
//		// break; intentional fall through
//	case SLAPIMessageIds.SLAPI_OUT_VEHICLESET:
//		// break; intentional fall through
//	case SLAPIMessageIds.SLAPI_OUT_WAYPOINTSET:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//				"Success(arg1):" + msg.arg1 + "\n" + "Error Code(arg2):"
//						+ Data2String.errorCode(msg.arg2));
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_ERROR:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//				"OriginalMessage:" + d.getInt("OriginalMessage") + "\n"
//						+ "ErrorCode:"
//						+ Data2String.errorCode(d.getInt("ErrorCode"))
//						+ "\n" + "ErrorMessage:"
//						+ d.getString("ErrorMessage"));
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_GPSSTATUS:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), "GPSFix:"
//				+ Data2String.gpsFixValue(d.getInt("GPSFix")) + "\n"
//				+ "Satellites tracked:" + d.getInt("NofSatellitesTracked")
//				+ "\n" + "Satellites in view:"
//				+ d.getInt("NofSatellitesinView") + "\n" + "HDOP:"
//				+ d.getInt("HDOP") + "\n" + "UTCTime:"
//				+ Data2String.utcTime(d) + "\n" + "Latitude:"
//				+ d.getFloat("Latitude") + "\n" + "Longitude:"
//				+ d.getFloat("Longitude") + "\n" + "Altitude:"
//				+ d.getInt("Altitude") + "\n" + "Course:"
//				+ d.getInt("Course") + "\n" + "Speed:" + d.getInt("Speed")
//				+ "\n");
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_GPSTIME:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), "UTCTime:"
//				+ Data2String.utcTime(d));
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_LANGUAGE:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//				"Language:" + Data2String.localeID(msg.arg1));
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_MESSAGE_BOX_PRESSED:
//		int button = d.getInt("Button");
//		if (button > 0) {
//			showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//					"Dialogue dismissed with key "+d.getInt("Button"));
//		} else {
//			showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//					"Dialogue timed out");
//		}
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_POSITION:
//		// break; intentional fall through
//	case SLAPIMessageIds.SLAPI_OUT_POSFROMADDRESS:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//				"Latitude:" + d.getFloat("Latitude") + "\n" + "Longitude:"
//						+ d.getFloat("Longitude"));
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_ROUTEGUIDANCEDATA:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), ""); // TODO_tavisaman hianyzik a doksibol
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_ROUTEINFO:
//		float[] waypoints = d.getFloatArray("WayPoints");
//		String info = "PlanMode:" + Data2String.planMode(d.getInt("PlanMode"))
//		+ "\n" + "Number of Waypoints:" + waypoints.length/4;
//		
//		for (int i=0; i<waypoints.length; i+=4) {
//			info += "\n ["+waypoints[i+0]+","+waypoints[i+1]+" "+waypoints[i+2]+"m "+waypoints[i+3]+"s";
//		}
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),info);
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_SPEEDANDDIRECTION:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//				"Speed(arg1):" + msg.arg1 + "\n" + "Direction(arg2):"
//						+ msg.arg2);
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_UX_CALLBACK:
//		int handle;
//		cpd.setBytes(msg.getData().getByteArray("Bytes"));
//		try {
//			handle = cpd.getInt();
//			if (handle == HANDLE_GET_ACCELERATION) {
//				cpd.stepOverEmpty();
//				showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//						"cb_getAcceleration():"+
//						cpd.tupleAsString());
//			}
//			else if (handle == HANDLE_SHOW_MESSAGE_BOX) {
//				cpd.stepOverEmpty();
//				showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//						"cb_showMessageBox():"+
//						cpd.tupleAsString());
//			}
//			else if (handle == HANDLE_MESSAGE_BOX_PRESSED) {
//				cpd.stepOverEmpty();
//				showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//						"cb_messageBoxPressed():"+
//						cpd.tupleAsString());
//			}
//			else if (handle == HANDLE_GET_SPEED_LIMIT) {
//				cpd.stepOverEmpty();
//				showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//						"cb_getSpeedLimit():"+
//						cpd.tupleAsString());
//			}
//			else if (handle == HANDLE_GET_VEHICLE_TYPE) {
//				cpd.stepOverEmpty();
//				showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//						"cb_getVehicleType():"+
//						cpd.tupleAsString());
//			}
//			else if (handle == HANDLE_SEND_ACCELERATION) {
//				cpd.stepOverEmpty();
//				showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//						"cb_sendAcceleration():"+
//						cpd.tupleAsString());
//			}
//			else {
//				showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//						"Handle:" + handle + " (unregistered)");
//			}
//		} catch (CDBException e) {
//			showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//					"Exception: " + e);
//		}
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_UX_RESULT:
//		 cpd.setBytes(msg.getData().getByteArray("Bytes"));
//		 showServerMessage(SLAPIMessageIds.MessageName(msg.what),cpd.contentAsString());
//		break;
//	case SLAPIMessageIds.SLAPI_OUT_VERSION:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), "Version:"
//				+ d.getString("Version"));
//		break;
//	case com.igo.slapiclient.SLAPIMessageIds.SLAPI_SERVER_HELLO:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what),
//				"ACK(arg2) = " + msg.arg2);
//		
//		mTransmitter.sendRequest(SLAPIMessageIds.SLAPI_SHOWHMI,0);
//		
//		break;
//	case SLAPIMessageIds.SLAPI_SERVER_BYE:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), "");
//		break;
//	default:
//		showServerMessage(SLAPIMessageIds.MessageName(msg.what), "arg1:"
//				+ msg.arg1 + "\n" + "arg2" + msg.arg2);
//	}
//}
//
//
///**
// * This method send the current route's KML file path to IGO to let it start building the route.
// */
//public void sendKMLFileLocation() {
//	Message outMsg = Message.obtain(null, SLAPIMessageIds.SLAPI_LOADKMLROUTE);
//	Bundle data = new Bundle();
//	data.putString("Path", "/mnt/sdcard/"+spmParser.routesInfo.currentRouteFileName);
//	outMsg.setData(data);
//	mTransmitter.sendRequest(outMsg);
//}
//
//public void unBindFromIGOServer(){
//	mTransmitter.doUnbindService();
//	try{
//	  mActivity.unregisterReceiver(mHelloReceiver);
//	  mActivity.unregisterReceiver(mByeReceiver);
//	}
//	catch (IllegalArgumentException exception){
//	exception.printStackTrace();	
//	}
//    
//}
///** Informs the user on the result of the message. */
//protected void showServerMessage(CharSequence messageName,
//		CharSequence content) {
//	
//	Utils.logger("message from IGO "+content.toString());
//}
//
//private class ServerHelloBroadcastReceiver extends BroadcastReceiver {
//	@Override
//	public void onReceive(Context arg0, Intent arg1) {
//		// TODO_tavisaman Auto-generated method stub
//		showServerMessage("SERVER_HELLO","Broadcast received.");
////		mTransmitter.sendRequest(msgId)
//		
//	}
//}
//
//private class ServerByeBroadcastReceiver extends BroadcastReceiver {
//	@Override
//	public void onReceive(Context arg0, Intent arg1) {
//		// TODO_tavisaman Auto-generated method stub
//		showServerMessage("SERVER_BYE","Broadcast received.");
//	}
//}
//
//// Calling Activity must implement this interface 
//public interface OnIgoClientUpdateListener { 
//   public void onKmlLoaded(boolean kmlLoaded);
//} 
//
//
//}
