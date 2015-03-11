package com.example.publitransportationintegration;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import kml.KmlBuilder;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.example.il.co.isrcorp.spmcommunicationcore.EventsFromSPM;
import com.example.il.co.isrcorp.spmcommunicationcore.PublicTransportation;
import com.example.il.co.isrcorp.spmcommunicationcore.SpmDataPublisher;
import com.example.il.co.isrcorp.spmcommunicationcore.Utils;
import com.example.publictransport.isr.routes.CurrentRoutesInfo;

public class SpmParserBrisgeService extends Service implements Observer{
	
	/**
	 * Used to enable receiving messages from publisher.
	 */
	SpmDataPublisher spmDataPublisher;
	
	/**
	 * Used to send data to SPM.
	 */
	private MessagesToSpmSender messagesToSpmSender;
	
	/**
	 * Used to parse and handle data came from SPM.
	 */
	private SpmResponseParser spmParser;

	/**
	 * Responsible on building route's KMl file
	 */
	private KmlBuilder kmlBuilder;
	/**
	 * Used to get global application's info.
	 */
	private ApplicationInfo appInfo;
	
	private Messenger msg = new Messenger(new ClientRequestHanlder());
	
	Messenger mResponseMessenger = null;

	private CurrentRoutesInfo currentRouteInfo;
	
	/**
	 * Used to notify we lost communication with SPM.
	 */
	SPMCommunicationLostNotifierer communicationLostNotifier;
	
	Timer checkKeepAliveTimer;
	

	/**
	 * Flag indicates we lost communication with SPM
	 */
	public boolean spmCommunicationLost = false;
	public SpmParserBrisgeService() {
		
	}

	
	@Override
	public void onCreate() {
		appInfo = ApplicationInfo.getInstance();
		currentRouteInfo = CurrentRoutesInfo.getInstance();
		MyUtils.context = this;
		
 		// We need to check if app temporarily went to background (not during startNavigation) or starting for the first time.
 		// If it just went to background we din't need to reconnect to the SPM, cause we didn't disconnect from it.
 		// if the spmdataPublisher is null it means we didn't make any previous connection, or we've disconnect it.
 		if (spmDataPublisher == null)
 		startCommunication();
 		
		// We check if there's valuable data at SPMApplicationName field, to ensure we have all data we need to send to server.
		// In case we don't have all data, we'll get it from SPM later (when we'll get SPM Info from SPM) and send it.
		if(!"".equalsIgnoreCase(appInfo.getSPMApplicationName())){
		messagesToSpmSender.sendGeneralApplicationInfo();
		}
		
		kmlBuilder = new KmlBuilder();
		
		super.onCreate();
	}




	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		return msg.getBinder();
	}


	/**
	 * This method registering to Publisher (SpmDataPublisher class) in order to
	 * be notified about events. Then, it starts the SpmDataPublisher running
	 * thread.
	 *
	 */
	public void startCommunication() {

		spmDataPublisher = new SpmDataPublisher(getApplicationContext());
		spmDataPublisher.addObserver(this);
		spmDataPublisher.subscriberType = SpmDataPublisher.PUBLIC_TRANSPORTATION;
		
//		
		// We need to verify we have constant communication with SPM
		communicationLostNotifier = new SPMCommunicationLostNotifierer();
	    checkKeepAliveTimer = new Timer();
	    try{
	    // We set the checkKeepAliveTimer to notify on lost communication event to app, using the communicationLostNotifier.
	    // We're waiting 5 seconds before execution. 
	    checkKeepAliveTimer.schedule(communicationLostNotifier, 1000*5);
	    }
	    catch (IllegalStateException illegalStateException){
	    	System.out.println("keep alive thread canceled");
	    }
	    
		spmDataPublisher.startCommunication();
		
		messagesToSpmSender = new MessagesToSpmSender(spmDataPublisher);
		
		spmParser = new SpmResponseParser(this, messagesToSpmSender);
		
		messagesToSpmSender.sendSPMInitCommandsMessage();
	
		messagesToSpmSender.sendGetStopList();
		
		
	}
	private class ClientRequestHanlder extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			mResponseMessenger = msg.replyTo;
			String data = msg.getData().getString("data");
			
			System.out.println("got message from client "+data);
				}
		}
	
	Bundle bundle = new Bundle();
	@Override
	public void update(Observable observable, Object data) {
		
		
		if (data instanceof EventsFromSPM) {

			
			handleHelloCommand();
			
			String[] result = (String[]) ((EventsFromSPM) data).getData();
			int messageType = ((EventsFromSPM) data).getType();
			
			String dataToJavascript;
		
//			if(result.length>0)
//			sendMessage(result);
			
//			
			switch (((EventsFromSPM) data).getType()) {
			case EventsFromSPM.HELLO:
				
				break;
			case EventsFromSPM.GPSINFO:
				break;
			case EventsFromSPM.SPMCFGINFO :
				
				sendMessage(messageType, result);
				
				break;
			case EventsFromSPM.POSITIONINFO:
				break;
			case EventsFromSPM.SPMSTATUS:
				
				break;
			case EventsFromSPM.INFO:
				
				// When application gets that info from SPM for the first time we save appInfo to file.
				// We assume that this info dosen't change.
				// So we check if we've saved that info before. If not we're updating appInfo and saving it to file.
				// Then we need to send application's general to server, since we didn't send it yet.
				if("".equalsIgnoreCase(appInfo.getSPMApplicationName())){
					appInfo.setSPMApplicationName(result[2]);
					appInfo.setSPMApplicationVersion(result[3]);
					appInfo.setSPMSerialNumber(result[5]);
					
					MyUtils.saveAppInfoToFile(MainActivity.APP_INFO_FILE, appInfo);
					
					messagesToSpmSender.sendGeneralApplicationInfo();
				}
			break;
			case EventsFromSPM.$Z:
				messagesToSpmSender.handle$ZResponse(result);
			break;
			case EventsFromSPM.$IC:
				handleICResponse(result);
			break;
			case PublicTransportation.LOGIN:
			break;
			case PublicTransportation.MSGLIST:
				spmParser.handleMsgList(result);
			break;
			case PublicTransportation.MSGACKED:
			break;
			case PublicTransportation.MESSAGE:
				System.out.println("got message "+result[1]);
			break;
			case PublicTransportation.MESSAGE_HISTORY:
			break;
			case PublicTransportation.CUR_DRIVER:
			break;
			case PublicTransportation.DRIVER:
			break;
			case PublicTransportation.RF_CARD:
			break;
			case PublicTransportation.PASSENGERCOUNT:
			break;
			case EventsFromSPM.NMEADATA:
				System.out.println("got NMEA");
				break;
			case PublicTransportation.$APP:
				messagesToSpmSender.send$App();
				break;
			
			case PublicTransportation.SELECTROUTE:
				spmParser.handleSelectRoute(result);
				messagesToSpmSender.sendGetLastStop();
			break;
			case PublicTransportation.CURRENTROUTE:
				
				// We need to check if we have all the data we need to build the KML file for current route coordinates.
				// if we have all required coordinates and there's no existing KML file we'll start building the KML file.
				
				if(!spmParser.handleCurentRoute(result)){
					// check if application closed while receiving route coords. If so, start requesting missing of coords
					if(currentRouteInfo.startReceivingCoords && !currentRouteInfo.endReceivingCoords){
						messagesToSpmSender.sendGetRouteCoords(currentRouteInfo.nextCoordsIndex);
					}
				}
			break;
			case PublicTransportation.SELECTNEXTROUTE:
				spmParser.handleSelectNextRoute(result);
				
			break;
			case PublicTransportation.ROUTECOORDS:
				
				// check if we got all route's coords
				if(spmParser.handleRouteCoords(result)){
					kmlBuilder.buildKML();
				}
				
			break;
			case PublicTransportation.ROUTESTOP:
				
				// if we successfully handled this stop, request it's lat/lon details
				if(spmParser.handleRouteStop(result))
					messagesToSpmSender.requestStopLatLon(result[2]);
				
			break;
			case PublicTransportation.ROUTEPROGRESS:
				
				int newProgress = ((int)(Float.valueOf((result[1].substring(0, result[1].length()-1)))).floatValue()); 
				
				
			break;
			case PublicTransportation.APPROACHSTOP:
				messagesToSpmSender.send$App();
			break;
			case PublicTransportation.EXITSTOP:
			break;
			case PublicTransportation.LEAVEROUTE:
			break;
			case PublicTransportation.RETURNTOROUTE:
			break;
			case PublicTransportation.TRIGGERENDOFROUTE:
				// remove all stops from stops list
				currentRouteInfo.routeStopsList.clear();
				
			break;
			case PublicTransportation.LASTSTOP:
			break;
			case PublicTransportation.STOPLATLON:
				currentRouteInfo.setRouteStopLocation(result);
			break;
			case PublicTransportation.DRIVER_SCHEDULE:
				messagesToSpmSender.send$App();
				spmParser.handleDriverScheduleResponse(result);
			break;
			
			}
			}
		
		
	}
	
	// Send an Intent with an action named "spm-event".  
	private void sendMessage(int messageType, String [] data) { 
	  Intent intent = new Intent("spm-event");
	  // add data 
	  intent.putExtra("type", messageType);
	  intent.putExtra("message", data);
	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}  

	
	/**
	 * This method handles the Hello command.<br>
	 */
	private void handleHelloCommand() {
		// cancl previous notification of lost SPM communication
		checkKeepAliveTimer.cancel();
		checkKeepAliveTimer = new Timer();
		communicationLostNotifier = new SPMCommunicationLostNotifierer();
		 try{
			
		    // We set the checkKeepAliveTimer to notify on lost communication event to app, using the communicationLostNotifier.
		    // We're waiting 5 seconds before execution. 
		    checkKeepAliveTimer.schedule(communicationLostNotifier, 1000*5);
		    }
		 catch (IllegalStateException illegalStateException){
		    	System.out.println("keep alive thread canceled");
		    }
		 
		 // If we've lost communication and we connected again, init SPM communication
//		if(spmCommunicationLost){
//			spmCommunicationLost = false;
//			messagesToSpmSender.sendSPMInitCommandsMessage();
//			updateClient(MainActivity.RESTORE_SPM_COMMUNICATION);
//		}
	}
	


		private void handleICResponse(String[] result) {
			int inputDevice = -1;
			int deviceNewStatus = -1;
			
			try{
			inputDevice = Integer.parseInt(result[1]);
			deviceNewStatus  = Integer.parseInt(result[2]);
			}
			catch (NumberFormatException nme){
				nme.printStackTrace();
			}
			
			switch (inputDevice){
			case 1: //Front Door Event. deviceNewStatus: 1=closed, 0 = open
			
			break;
			case 2: // Rear Door Event  deviceNewStatus: 1=closed, 0 = open
			break;
			case 5: // Covert Alarm Event
				if(deviceNewStatus == 1){
				}
			break;
			}
		}

		/** This class notify the app when SPM communication lost.
		 * @author isr
		 *
		 */
		class SPMCommunicationLostNotifierer extends TimerTask {
			  public void run() {
				  spmCommunicationLost = true;
				  
				  // notify app that SPM communication lost
				  System.out.println("SPM COMMUNICATION LOST");
//				  updateClient(MainActivity.LOST_SPM_COMMUNICATION);
				  
//				  new Thread(new Runnable() {
//
//						public void run() {
//							Looper.prepare();
//							  progress = new WaitForSpmDialog(MainActivity.this);
//							     
//							  progress.show();
//							Looper.loop();
//						}
//					}).start();
				
					
//				  spmDataPublisher.sendMessage(getTempCmd);
			  }
			}
}
