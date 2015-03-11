package com.example.publitransportationintegration;

import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.il.co.isrcorp.spmcommunicationcore.SerialPortPreferences;
import com.example.publictransport.isr.messages.MessagesManager;
import com.example.publictransport.isr.routes.CurrentRoutesInfo;
import com.example.publictransport.isr.routes.RouteStop;
import com.example.publictransport.isr.schedule.ScheduleManager;

public class MainActivity extends Activity{

	public static final String SCHEDULE = "schedule";
	public static final String ROUTE_INFO_FILE = "route_info";
	public static final String APP_INFO_FILE = "app_info";
	public static final String MESSAGES_MANAGER = "messages_info";
	
	public static final int LOST_SPM_COMMUNICATION = 1;
	public static final int RESTORE_SPM_COMMUNICATION = 2;
	
	
	private static Context context;
//	/**
//	 * Used to enable receiving messages from publisher.
//	 */
//	SpmDataPublisher spmDataPublisher;
//	
//	/**
//	 * Used to send data to SPM.
//	 */
//	private MessagesToSpmSender messagesToSpmSender;
//	
//	/**
//	 * Used to parse and handle data came from SPM.
//	 */
//	private SpmResponseParser spmParser;

	/**
	 * Represents to manage driver's schedule. 
	 */
	private ScheduleManager scheduleManager;
	/**
	 * Contains details of the current route including list of all route's stops.
	 */
	private CurrentRoutesInfo currentRouteInfo;

	/**
	 * Represent messages manager
	 */
	private MessagesManager messagesManager;
//	/**
//	 * Responsible on building route's KMl file
//	 */
//	private KmlBuilder kmlBuilder;
	/**
	 * Used to get global application's info.
	 */
	private ApplicationInfo appInfo;
	/**
	 * Represent driver start the navigation Activity. <br>Used as flag at {@link MainActivity #onResume()}. 
	 */
	private boolean navigationStarted = false;
	/**
	 * Used to show custom circle progress bar dialog indicates waiting for establishing SPM communication
	 */
	WaitForSpmDialog progress;
	
	protected Messenger spmBridgeService;
	
	public static Context getAppContext() {
		return MainActivity.context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MyUtils.context = this;
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment(),"frag").commit();
		}

		// read global info of application, Including DbId, and full checks list
		appInfo = (MyUtils.readAppInfoFile(APP_INFO_FILE));  
		
		if(appInfo == null){
			appInfo = ApplicationInfo.getInstance();
		}
		else{
			ApplicationInfo.appInfoInstance = appInfo;
		}
		
		// read schedule info from file if exists, else, create new instance.
		scheduleManager = (MyUtils.readScheduleFile(SCHEDULE));  
				
				if(scheduleManager == null){
					scheduleManager = ScheduleManager.getInstance();
				}
				else{
					ScheduleManager.scheduleInstance = scheduleManager;
				}
		
		// read routeInfo if exists
		currentRouteInfo  = MyUtils.readRouteInfoFile(ROUTE_INFO_FILE);
		
		// if we don't have route info, create new.
		if (currentRouteInfo == null){
			
			currentRouteInfo	 = CurrentRoutesInfo.getInstance();
				
		}
		else{
			// init new roure's stops list
			if (currentRouteInfo.routeStopsList == null){
				currentRouteInfo.routeStopsList = new ConcurrentHashMap<Integer, RouteStop>();
			}
			
			CurrentRoutesInfo.currentRouteInfoInstance = currentRouteInfo;
			
		}
		
		// read global info of application, Including DbId, and full checks list
		messagesManager = (MyUtils.readMessagesManagerFile(MESSAGES_MANAGER));  
				
				if(appInfo == null){
					appInfo = ApplicationInfo.getInstance();
				}
				else{
					MessagesManager.messagesManagerInstance = messagesManager;
				}
				
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
			// we also check if driver started navigation. If he is, we don't need to send general info and shouldn't launch html again, as it already being shown.  
		 	if(isSerialPortConfigured()){
		   
	
		  	}
		 	bindService(new Intent("com.example.publitransportationintegration.SpmParserBrisgeService"), mConnection, Context.BIND_AUTO_CREATE);
		 	
		 	 // Register mMessageReceiver to receive messages. 
		 	  LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("spm-event"));
		 	  
		 	if(messagesManager!= null && messagesManager.messagesList.size()>0){
				messagesManager.sortMessages();
				
				Fragment fragment = getFragmentManager().findFragmentByTag("frag");
				
				TextView tv = (TextView)fragment.getView().findViewById(R.id.output);
				String text = "";
				for (int i = 0;i<messagesManager.messagesList.size();i++){
					text+=messagesManager.messagesList.get(i).getText()+"\n";
				}
						tv.setText(text); 
						}
	       super.onResume();
	}
	
//
//	/**
//	 * This method registering to Publisher (SpmDataPublisher class) in order to
//	 * be notified about events. Then, it starts the SpmDataPublisher running
//	 * thread.
//	 *
//	 */
//	public void startCommunication() {
//
//		spmDataPublisher = new SpmDataPublisher(getApplicationContext());
//		spmDataPublisher.addObserver(this);
//		spmDataPublisher.subscriberType = SpmDataPublisher.PUBLIC_TRANSPORTATION;
//		
//		
//		// We need to verify we have constant communication with SPM
//		communicationLostNotifier = new SPMCommunicationLostNotifierer();
//	    checkKeepAliveTimer = new Timer();
//	    try{
//	    // We set the checkKeepAliveTimer to notify on lost communication event to app, using the communicationLostNotifier.
//	    // We're waiting 5 seconds before execution. 
//	    checkKeepAliveTimer.schedule(communicationLostNotifier, 1000*5);
//	    }
//	    catch (IllegalStateException illegalStateException){
//	    	System.out.println("keep alive thread canceled");
//	    }
//	    
//		spmDataPublisher.startCommunication();
//		
//		messagesToSpmSender = new MessagesToSpmSender(spmDataPublisher);
//		
//		spmParser = new SpmResponseParser(this, messagesToSpmSender);
//		
//		messagesToSpmSender.sendSPMInitCommandsMessage();
//	
//		messagesToSpmSender.sendGetStopList();
//		
//		
//	}

	/** This method verifies that the serial port and baudrate has been settled.<br>
	 *  If they hasn't been settled we start {@link SerialPortPreferences } activity to set them.
	 * @return
	 */
	private boolean isSerialPortConfigured() {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String path = sp.getString("DEVICE", "");//sp.getString("DEVICE", "");"/dev/ttyS1"
		int baudrate = 115200;//Integer.decode(sp.getString("BAUDRATE", "-1"));

		/* Check parameters */
		if ( (path.length() == 0) || (baudrate == -1)) {
			startActivityForResult(new Intent(this,SerialPortPreferences.class), Activity.RESULT_OK);
			return false;
		}
		else{
			return true;
		}
			
	}
	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			
			return rootView;
		}
	}
	

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  
			spmBridgeService = new Messenger(service);
			
		}


		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			spmBridgeService = null;
		}
	};

	// handler for received Intents for the "my-event" event  
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	  @Override 
	  public void onReceive(Context context, Intent intent) {
	    // Extract data included in the Intent 
	    String [] message = intent.getStringArrayExtra("message");
	    int messageType =  intent.getIntExtra("type",-1);
	    
	    
	    handleSpmCommand(messageType,message);
	  } 
	}; 

	/**
	 * @param message 
	 * 
	 */
	private void sendToSpm(String message) {
		Message welcome = new Message();
		Bundle b =new Bundle();
		b.putString("data", message);
		
		welcome.setData(b);
		try {
			spmBridgeService.send(welcome);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
protected void handleSpmCommand(int messageType, String[] message) {
	Log.d("receiver", "Got message in MainActivity: " + message[0]);
		
	}

@Override
	protected void onStop() {
	 // Unregister since the activity is not visible 
	  LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onStop();
	}



	// This method receives data from publisher.
		// If data is EventsFromSPM instance, it includes 2 fields. 
		// Type - data's type, and data as String array.
//		@Override
//		public void update(Observable observable, Object data) {
//			handleHelloCommand();
//			if (data instanceof EventsFromSPM) {
//
//				String[] result = (String[]) ((EventsFromSPM) data).getData();
//				String dataToJavascript;
//			
//				switch (((EventsFromSPM) data).getType()) {
//				case EventsFromSPM.HELLO:
//					
//					Utils.logger("got hello in update");
//					break;
//				case EventsFromSPM.GPSINFO:
//					break;
//				case EventsFromSPM.SPMCFGINFO :
//					
//					break;
//				case EventsFromSPM.POSITIONINFO:
//					break;
//				case EventsFromSPM.SPMSTATUS:
//					
//					break;
//				case EventsFromSPM.INFO:
//					
//					// When application gets that info from SPM for the first time we save appInfo to file.
//					// We assume that this info dosen't change.
//					// So we check if we've saved that info before. If not we're updating appInfo and saving it to file.
//					// Then we need to send application's general to server, since we didn't send it yet.
//					if("".equalsIgnoreCase(appInfo.getSPMApplicationName())){
//						appInfo.setSPMApplicationName(result[2]);
//						appInfo.setSPMApplicationVersion(result[3]);
//						appInfo.setSPMSerialNumber(result[5]);
//						
//						MyUtils.saveAppInfoToFile(APP_INFO_FILE, appInfo);
//						
//						messagesToSpmSender.sendGeneralApplicationInfo();
//					}
//				break;
//				case EventsFromSPM.$Z:
//					messagesToSpmSender.handle$ZResponse(result);
//				break;
//				case EventsFromSPM.$IC:
//					handleICResponse(result);
//				break;
//				case PublicTransportation.LOGIN:
//				break;
//				case PublicTransportation.MSGLIST:
//					spmParser.handleMsgList(result);
//				break;
//				case PublicTransportation.MSGACKED:
//				break;
//				case PublicTransportation.MESSAGE:
//					System.out.println("got message "+result[1]);
//				break;
//				case PublicTransportation.MESSAGE_HISTORY:
//				break;
//				case PublicTransportation.CUR_DRIVER:
//				break;
//				case PublicTransportation.DRIVER:
//				break;
//				case PublicTransportation.RF_CARD:
//				break;
//				case PublicTransportation.PASSENGERCOUNT:
//				break;
//				case EventsFromSPM.NMEADATA:
//					System.out.println("got NMEA");
//					break;
//				case PublicTransportation.$APP:
//					messagesToSpmSender.send$App();
//					break;
//				
//				case PublicTransportation.SELECTROUTE:
//					spmParser.handleSelectRoute(result);
//					messagesToSpmSender.sendGetLastStop();
//				break;
//				case PublicTransportation.CURRENTROUTE:
//					
//					// We need to check if we have all the data we need to build the KML file for current route coordinates.
//					// if we have all required coordinates and there's no existing KML file we'll start building the KML file.
//					
//					if(!spmParser.handleCurentRoute(result)){
//						// check if application closed while receiving route coords. If so, start requesting missing of coords
//						if(currentRouteInfo.startReceivingCoords && !currentRouteInfo.endReceivingCoords){
//							messagesToSpmSender.sendGetRouteCoords(currentRouteInfo.nextCoordsIndex);
//						}
//					}
//				break;
//				case PublicTransportation.SELECTNEXTROUTE:
//					spmParser.handleSelectNextRoute(result);
//					
//				break;
//				case PublicTransportation.ROUTECOORDS:
//					
//					// check if we got all route's coords
//					if(spmParser.handleRouteCoords(result)){
//						kmlBuilder.buildKML();
//					}
//					
//				break;
//				case PublicTransportation.ROUTESTOP:
//					
//					// if we successfully handled this stop, request it's lat/lon details
//					if(spmParser.handleRouteStop(result))
//						messagesToSpmSender.requestStopLatLon(result[2]);
//					
//				break;
//				case PublicTransportation.ROUTEPROGRESS:
//					
//					int newProgress = ((int)(Float.valueOf((result[1].substring(0, result[1].length()-1)))).floatValue()); 
//					
//					
//				break;
//				case PublicTransportation.APPROACHSTOP:
//					messagesToSpmSender.send$App();
//				break;
//				case PublicTransportation.EXITSTOP:
//				break;
//				case PublicTransportation.LEAVEROUTE:
//				break;
//				case PublicTransportation.RETURNTOROUTE:
//				break;
//				case PublicTransportation.TRIGGERENDOFROUTE:
//					// remove all stops from stops list
//					currentRouteInfo.routeStopsList.clear();
//					
//				break;
//				case PublicTransportation.LASTSTOP:
//				break;
//				case PublicTransportation.STOPLATLON:
//					currentRouteInfo.setRouteStopLocation(result);
//				break;
//				case PublicTransportation.DRIVER_SCHEDULE:
//					messagesToSpmSender.send$App();
//					spmParser.handleDriverScheduleResponse(result);
//				break;
//				
//				}
//				}
//			
//		}

	/**
	 * This method handles the Hello command.<br>
	 */
//	private void handleHelloCommand() {
//		// cancl previous notification of lost SPM communication
//		checkKeepAliveTimer.cancel();
//		checkKeepAliveTimer = new Timer();
//		communicationLostNotifier = new SPMCommunicationLostNotifierer();
//		 try{
//			
//		    // We set the checkKeepAliveTimer to notify on lost communication event to app, using the communicationLostNotifier.
//		    // We're waiting 5 seconds before execution. 
//		    checkKeepAliveTimer.schedule(communicationLostNotifier, 1000*5);
//		    }
//		 catch (IllegalStateException illegalStateException){
//		    	System.out.println("keep alive thread canceled");
//		    }
//		 
//		 // If we've lost communication and we connected again, init SPM communication
//		if(spmCommunicationLost){
//			spmCommunicationLost = false;
//			messagesToSpmSender.sendSPMInitCommandsMessage();
//			if(progress!= null)
//			progress.cancel();
//		}
//	}

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
		
		public void switchActivity(View v){
			Intent intent = new Intent (this, SecondActivity.class);
			startActivity(intent);
		}
		
}
