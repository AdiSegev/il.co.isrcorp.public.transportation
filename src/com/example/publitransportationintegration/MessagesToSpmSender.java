package com.example.publitransportationintegration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.il.co.isrcorp.spmcommunicationcore.SpmDataPublisher;

/** This class responsible on sending messages from Application to SPM.<br>
 *  We're doing it using local {@link SpmDataPublisher member}.
 * @author isr
 *
 */
public class MessagesToSpmSender {

	/**
	 * Used to send message to SPM.
	 */
	public SpmDataPublisher spmDataPublisher ;
	
	/**
	 * Used to get global application's info.
	 */
	private ApplicationInfo appInfo = ApplicationInfo.getInstance();
	
	/**
	 * Used to request temps.
	 */
	MyTimerTask myTask;
	
	Timer myTimer;

	/**
	 * Represents list of $Z messages sent to server.<br>
	 * We'll remove message after we'll get acknowledge from SPM as described at {@link MessagesToSpmSender#handle$ZResponse(String[])}. 
	 */
	ArrayList<String> $Zcommands = new ArrayList<String>();
	
	/**
	 * request SPM general info
	 */
	static byte[] getInfoCmd = new byte [] {'$','I'};
	/**
	 * Configure SPM to send $S & $Position every 15 seconds
	 */
	static byte[] setContinuousCmd = new byte [] {'$','C',',','5'};
	/**
	 * Request current spm status from SPM
	 */
	static byte[] getSpmStatusCmd = new byte [] {'$','G','e','t','S','p','m','S','t','a','t','u','s'};
	/**
	 * Request login from SPM
	 */
	static byte[] spmloginCmd = new byte[]{'$','L','o','g','i','n',',','7',','};
	/**
	 * Used to add at the end of Login command (after driver's id)
	 */
	static byte[] loginCmdEndCommas = new byte[]{',',',',',',','};
	/**
	 * Request current driver.
	 */
	static byte[] getDriverCmd = new byte[] {'$', 'G', 'e', 't','D', 'r','i', 'v', 'e','r'};
	/**
	 * Request current status from SPM
	 */
	static byte[] getCurrentStatusCmd = new byte[] {'$', 'G', 'e', 't','C', 'u','r', 'S', 't','a','t','u','s'};
	
    /**
     * Used to send $App ack to SPM
     */
    static byte[] $appCmd = new byte []{'$','A','p','p'};
    
    /**
     * Request current route info from server
     */
    static byte[] getCurrentrouteCmd = new byte[] {'$', 'R', 'e', 'q','u', 'e','s', 't', 'C','u','r','r','e','n','t','R','o','u','t','e'};
    
    /**
	 * Request current stop list SPM
	 */
	static byte[] getStopListCmd = new byte[] {'$', 'S', 't', 'o','p', 'L','i', 's', 't'};
	
	/**
	 * Request last stop from SPM
	 */
	static byte[] getLastStopCmd = new byte[] {'$', 'G', 'e', 't','L', 'a','s', 't', 'S','t','o','p'};
    /**
     * Request next route coords block
     */
    static String getRouteCoordsCmd = "$RouteCoords,";
    /**
     * Represent $Z ack command. <br>We'll add it to each command only before it being sent, in order to save only the payload before so we can validate it at {@link MessagesToSpmSender#handle$ZResponse(String[])}  
     */
    String $Zcmd = "$Z,";
    
	public MessagesToSpmSender(SpmDataPublisher spmDataPublisher) {
		this.spmDataPublisher = spmDataPublisher;
	}
	
	
	
	/**	This method constructs and send login request using xml-pt
	 * @param loginType represents input type (Manual/Card)
	 * @param employeeType represents worker (Driver/Technical)
	 * @param employeeId Id to check
	 * @param runId runId to check (only for Drivers)
	 * @param manualLoginReason indicates different reason for manual login<br>
	 * 			   1 - Lost Id Card <br>
   				   2 - Forgot Id Card <br>
   				   3 - Defective Id Card<br>
   				   4 - Defective Card Reader<br>
	 * @param currentPreCheckVersion String represents current version of preCheck list
	 * @param currentRoute current route if exists
	 */
	protected void sendLoginRequestByRdteNotify(int loginType, int employeeType, int employeeId, int runId, String manualLoginReason, String currentPreCheckVersion, String currentRoute){
		String loginContent = "$RDTENotify,xml-pt\\c1\\c0";
		
		// if it's manual login we know if it's driver or technician and more details
		if(loginType == 2){
			loginContent += "\\c1="+loginType+"\\c2="+employeeType+"\\c3="+employeeId;
			
			if(runId != -1)
				loginContent += "\\c4="+runId;	
			
			if("lostIdCard".equalsIgnoreCase(manualLoginReason))
				loginContent += "\\c5="+1;
			
			if("forgotIdCard".equalsIgnoreCase(manualLoginReason))
				loginContent += "\\c5="+2;
			
			if("defectiveIdCard".equalsIgnoreCase(manualLoginReason))
				loginContent += "\\c5="+3;
			
			if("defectiveCardReader".equalsIgnoreCase(manualLoginReason))
				loginContent += "\\c5="+4;
		}
		else{ // if it's card login, we only have the employee Id
			loginContent += "\\c1="+loginType+"\\c3="+employeeId;
		}
		
		if(!"".equalsIgnoreCase(currentPreCheckVersion)){
			loginContent += "\\c7="+currentPreCheckVersion;
		}
		
		if (currentRoute != null){
			loginContent+="\\c8="+currentRoute;
		}
		
//		$Zcommands.add(loginContent);
//		
//		loginContent = $Zcmd+loginContent;
		
		spmDataPublisher.sendMessage(loginContent.getBytes());
		
	}
		/**
	 * Request current driver from SPM.
	 */
	protected void sendGetDriver(){
		spmDataPublisher.sendMessage(getDriverCmd);
	}
	
	/** Request login from SPM.
	 * @param driverId driver ID to verify
	 */
	protected void sendLoginRequestToSPM(int driverId){
		byte [] idBytes = MyUtils.intToBytes(driverId);
		// we add 4 at the end in order to put 4 commas at the end
		byte [] completeMessage = new byte [spmloginCmd.length+idBytes.length+4];
		
		System.arraycopy(spmloginCmd, 0, completeMessage, 0, spmloginCmd.length);
		System.arraycopy(idBytes, 0, completeMessage, spmloginCmd.length, idBytes.length);
		System.arraycopy(loginCmdEndCommas, 0, completeMessage, spmloginCmd.length+idBytes.length, loginCmdEndCommas.length);
		
		spmDataPublisher.sendMessage(completeMessage);
		
	}
	
	/**
	 * This method send request to SPM to get current status request
	 */
	public void sendGetCurrentStatus() {
		spmDataPublisher.sendMessage(getCurrentStatusCmd);
	}
	
	/**
	 * This method initiate SPM configuration.<br>
	 * It also starting thread for getting current temps.
	 */
	public void sendSPMInitCommandsMessage() {
		spmDataPublisher.sendMessage(setContinuousCmd);
		spmDataPublisher.sendMessage(getSpmStatusCmd);
		spmDataPublisher.sendMessage(getInfoCmd);
		
////		 myTask = new MyTimerTask();
//	     Timer myTimer = new Timer();
//	     myTimer.schedule(myTask, 0, 1000*10);
	}
	
	/**
	 * Send $App to SPM as ack to previous $App received
	 */
	public void send$App() {
		spmDataPublisher.sendMessage($appCmd);
		
	}


	/**
	 * Close all running threads
	 */
	public void close(){
		if(myTask!= null)
		myTask.cancel();
		
		if(myTimer!= null)
		myTimer.cancel();
	}
	
	class MyTimerTask extends TimerTask {
		  public void run() {
		  }
		}

	/**
	 * This method sends the general application's info to server.
	 */
	public void sendGeneralApplicationInfo() {
		String message = "xml-pt\\c0\\c0\\c1="+
				"\\c2="+appInfo.getApplicationName()+
				"\\c3="+appInfo.getApplicationVersion()+
				"\\c4="+appInfo.getApplicationLastUpdate()+
//				"\\c5="+appInfo.getApplicationName()+  not implemented yet
				"\\c6="+appInfo.getAndroidVersion()+
				"\\c7="+appInfo.getAndroidBuildNumber()+
//				"\\c8="+appInfo.getApplicationName()+  not implemented yet
//				"\\c9="+appInfo.getApplicationName()+ not implemented yet
				"\\c10="+appInfo.getIMEI1()+
//				"\\c11="+appInfo.getApplicationName()+ not implemented yet
				"\\c12="+appInfo.getSPMSerialNumber()+
//				"\\c13="+appInfo.getApplicationName()+ not implemented yet
//				"\\c14="+appInfo.getApplicationName()+ not implemented yet
				"\\c15="+appInfo.getSPMApplicationName()+
				"\\c16="+appInfo.getSPMApplicationVersion();
		$Zcommands.add(message);
		
		message = $Zcmd+message;
		
		spmDataPublisher.sendMessage(message.getBytes());
	}



	/** Request current driver's schedule.
	 * @param dbId unique DB id for this session
	 */
	public void sendGetSchedule(String dbId) {
		String message = "xml-pt\\c1\\c2\\c999="+dbId;
		
		$Zcommands.add(message);
		
		message = $Zcmd+message;
		
		spmDataPublisher.sendMessage(message.getBytes());
		
		
	}	

	
	/** This method handles SPM acknowledges for previous $Z sent by application.<br>
	 *  We'll iterate over {@link MessagesToSpmSender#$Zcommands} list and check for each item if it equals the received $Z message.<br>
	 *  If it's equals, we'll remove it from the list, if it dosen't, we'll resend it to server.   
	 * @param result
	 */
	public void handle$ZResponse(String[] result) {
		
		for (int i = 0; i<$Zcommands.size();i++){
			
			if($Zcommands.get(i).equalsIgnoreCase(result[1])){
				$Zcommands.remove(i);
			}
			else{
				spmDataPublisher.sendMessage($Zcommands.get(i).getBytes());
			}
		}
		
		
	}

	/**
	 * Request current route info from server
	 */
	public void sendGetCurrentRoute() {
		spmDataPublisher.sendMessage(getCurrentrouteCmd);
		
	}



	/** This methods request for next route coords block from server.
	 * @param index the next first coord requested 
	 */
	public void sendGetRouteCoords(String index) {
		System.out.println("sendin coords "+(getRouteCoordsCmd+index));
		
		if(!spmDataPublisher.sendMessage((getRouteCoordsCmd+index).getBytes())){
			System.out.println("sendin coords failed");
		}
		
	}
	
	/**
	 * Request current stop list from SPM.
	 */
	public void sendGetStopList(){
		
		spmDataPublisher.sendMessage(getStopListCmd);
		
	}



	/**
	 * Request high frequency sending position details from SPM.<br>
	 * Used when we start navigation in order to get more accurate info of current position.
	 */
	public void sendFrequentlySPMInitCommandsMessage() {
	spmDataPublisher.sendMessage("$C,1".getBytes());
		
	}



	/**
	 * Request last stop from SPM
	 */
	public void sendGetLastStop() {
		spmDataPublisher.sendMessage(getLastStopCmd);	
		}



	/** Request current schedule from SPM second floor
	 * 
	 * @param driverId loggedIn driver Id
	 * @param driverRunId loggedIn runId, if exists
	 */
	public void sendGetRdteSchedule(String driverId, String driverRunId) {
		
		String message = "$RDTENotify,SchedReq\\c"+driverId+"\\c"+driverRunId;
		spmDataPublisher.sendMessage(message.getBytes());
	}



	/** This method handles responding to input devices status changes.
	 * @param result 
	 */
	public void handle$ICResponse(String[] result) {
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
		case 1: //Front Door Event
		break;
		case 2: // Rear Door Event
		break;
		case 5: // Covert Alarm Event
			if(deviceNewStatus == 1){
				
			}
		break;
		}
	}


	/** Request stop's lat and lon values from SPM
	 * @param stopId 
	 */
	public void requestStopLatLon(String stopId) {
		String message = "$StopLatLon,"+stopId;
		spmDataPublisher.sendMessage(message.getBytes());
			
		
	}



	public void sendRequestNextMessage(int currentMessageIndex) {
		String message = "$msglist,"+(currentMessageIndex+1);
		spmDataPublisher.sendMessage(message.getBytes());
		
	}
 
}