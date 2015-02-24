package il.co.isrcorp.publictransportationintegration;

import il.co.isrcorp.publictransport.isr.messages.Message;
import il.co.isrcorp.publictransport.isr.messages.MessagesManager;
import il.co.isrcorp.publictransport.isr.routes.CurrentRoutesInfo;
import il.co.isrcorp.publictransport.isr.routes.RouteStop;
import il.co.isrcorp.publictransport.isr.schedule.ScheduleManager;
import il.co.isrcorp.publictransport.isr.schedule.Trip;

import java.io.File;

import kml.KmlBuilder;
import android.content.Context;
import android.os.Environment;

import com.example.il.co.isrcorp.spmcommunicationcore.BCTEnums.PreChecksTypes;
import com.example.il.co.isrcorp.spmcommunicationcore.Utils;

/**	This class is responsible on parsing and handling data came from SPM. 
 * 
 * @author Adi
 *
 */


public class SpmResponseParser {

	/**
	 * Used to send data to SPM.
	 */
	MessagesToSpmSender messagesToSpmSender;
	
	/**
	 * Used to get global application's info.
	 */
	private ApplicationInfo appInfo;
	
	public CurrentRoutesInfo currentRouteInfo;

	/**
	 * Represents to manage driver's schedule. 
	 */
	private ScheduleManager scheduleManager;
	
	/**
	 * Represent messages manager
	 */
	private MessagesManager messagesManager;
	
	private Context context;
	
	private KmlBuilder kmlBuilder;
	
	public SpmResponseParser (Context context, MessagesToSpmSender connector){
		this.messagesToSpmSender = connector;
		this.context = context;
		
		appInfo = ApplicationInfo.getInstance();
		scheduleManager = ScheduleManager.getInstance();
		currentRouteInfo = CurrentRoutesInfo.getInstance();
		messagesManager = MessagesManager.getInstance();
		
		kmlBuilder = new KmlBuilder();
	}
	

	public SpmResponseParser() {
		currentRouteInfo = CurrentRoutesInfo.getInstance();
	}


	public void handleSelectRoute(String[] result) {
		
		try{
		
		currentRouteInfo.setRouteStartTime(result[9]);
		
		currentRouteInfo.setDirection(result[1]);
		
		currentRouteInfo.setDestination(currentRouteInfo.routeStopsList.get(currentRouteInfo.routeStopsList.size()-1).getStopName());
		
		currentRouteInfo.setRouteNum(result[1]);
		
	    currentRouteInfo.setRouteName(result[1]);
		
	    currentRouteInfo.updateRouteStopsETA();
	    
	    messagesToSpmSender.sendGetCurrentRoute();
	    
		}
		catch (NullPointerException npe){
			Utils.logger("couldn't get route Stop ");
			npe.printStackTrace();
		}
	}

	/** This method gets data for single line of schedule and add it to complete schedule list. <br>
	 *  It overrides existing item if the RTE name is equals, in case there was some change at the RTE details.<br>
	 *  If item was added, save app info to file, in order to save existing schedule state.
	 *      
	 * @param result data of single schedule item
	 */
	public void handleDriverScheduleResponse(String[] result) {

		Trip item = new Trip();
//		String[] splitedArrayItem;
//
//		for (String itemInArray : result) {
//			splitedArrayItem = itemInArray.split("=");
//
//			if ("1".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//
//			if ("2".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("3".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("4".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("5".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("6".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("7".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("8".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("9".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("10".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("11".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("12".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("13".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("14".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//			if ("15".equalsIgnoreCase(splitedArrayItem[0])) {
//				item.setDriverID(splitedArrayItem[1]);
//				continue;
//			}
//
//		}
//		
		item.setDriverID(result[0]);
		item.setTripName(result[1]);
		item.setTripStartDate(result[2]);
		item.setTripEndtDate(result[3]);
		item.setCTP_INT(result[4]);
		item.setTripID(result[5]);
		item.setBlockID(result[6]);
		item.setRTEname(result[7]);
		item.setLineSign(result[8]);
		item.setLineAlternate(result[9]);
		item.setDirection(result[10]);
		item.setFrom(result[11]);
		item.setTo(result[12]);
		item.setVehicleID(result[13]);
		item.setMOTRtID(result[14]);
		
		try{
		
			if(scheduleManager.schedule.put(item.getTripID(), item) != null)
				MyUtils.saveScheduleToFile(MainActivity.SCHEDULE, scheduleManager);
		}
		catch (NullPointerException npe){
			Utils.logger("failed to save schedule item");
		}
		
		
	}

	// $SelectNextRoute,5590203x,F,141207142504
	public void handleSelectNextRoute(String[] result) {
		
		
	}

	
	
//	$RouteStop,10801321,0,6014,NW 19 St & NW 10 Av,0,0,20,0
//	$RouteStop,10801321,1,5590,Miramar Town Center,29950,1480,20,0
//	$RouteLength,10801321,29950
//	$SelectRoute,10801321,F,314738.79N,0351114.98E,141009075944,0.0,0,1117119807,141005173153,522
	
	
	/** This method receive, parses and save current route stop
	 * @param result
	 * @return true if route stop successfully handled, false otherwise.
	 */
	public boolean handleRouteStop(String[] result) {
		RouteStop stop = new RouteStop(); 
		
		try{
		stop.setStopNum(Integer.valueOf(result[2]));
		stop.setStopId(result[3]);
		stop.setStopName(result[4]);
		stop.setSecondsFromPrevStop(Integer.valueOf(result[6]));
		
		System.out.println("route stop"+" "+stop.getStopNum()+" "+stop.getStopName());
		
		currentRouteInfo.routeStopsList.put(Integer.valueOf(stop.getStopNum()), stop);
		return true;
		}
		catch (Exception exception){
			exception.printStackTrace();
			return false;
		}
	}

	/** This method checks if we already have KML file for current route.<br>
	 * @param currentRouteDetails 
	 * @return true if we have the KML file with all route coords, false otherwise.
	 */
	public boolean handleCurentRoute(String[] currentRouteDetails) {
		
		String fileName = currentRouteDetails[1]+"_"+currentRouteDetails[4]+".kml";
		
		String rtekmlFileName = currentRouteDetails[1]+"_*"+".kml";
		
		File file =  new File(context.getExternalFilesDir(null), fileName);
		
		// if file not exists, mean we need to start getting the route's coords
		if (!file.exists()){
			
			// check if we've already start getting the route's coords and app crashed
			if(currentRouteInfo.startReceivingCoords)
				return false;
		
		// delete previous version of the rte's kml file
		removeRTEPreviousFiles(rtekmlFileName);
		
		// we save the file name in order to save new route's coords
		currentRouteInfo.currentRouteFileName = fileName;
	
		currentRouteInfo.endReceivingCoords = false;

		// clear previous coords received
		currentRouteInfo.currentRouteCoordsReceived = "";
		
		// start requesting route's coords
		messagesToSpmSender.sendGetRouteCoords("0");
		
		return false;
		}
		else{
		    return true;
		}
	
		
	}

	int numCoordsReceived = 0;
	int packetIndex = 0;
	
	/** This method receives list of coordinates info.<br>
	 *  If there's coordinates in the list we save them to separate file and updater current received coords number and flag.<br>
	 *  When we've finished receiving the ccords, we save current coords flag as finished receiving coords.<br>
	 * @param result list of route coords info
	 * @return true if we've got all route's coords, or false if we didn't finish receiving them.
	 */
	public boolean handleRouteCoords(String[] result) {
		
		currentRouteInfo.startReceivingCoords = true;
		
		// if 'NoSuchCoord', we finished receiving all route's coords
		if ("NoSuchCoord".equalsIgnoreCase(result[2])){
			
			currentRouteInfo.endReceivingCoords = true;
			
			// init receiving route's coords flags
			currentRouteInfo.startReceivingCoords = false;
			
			// Since we've got all coordinates and saved them, we need to save current status of routeInfo.
			// On application's next launch, we'll check startReceivingCoords and endReceivingCoords state, in order to know whether we got all coords.
			MyUtils.saveRouteInfo(MainActivity.ROUTE_INFO_FILE, currentRouteInfo);
			
			return true;
			
			}
		else{
			
			packetIndex = Integer.valueOf(result[1]);
			numCoordsReceived = Integer.valueOf(result[2]);
			
			// before we'll save the coordinates we need to parse them to decimal format, for navigation app use
			
			// extract all separated coordinates  
			String [] completeSplitedCoordinates = result[3].split(";");
			
			//represent single coordinate lat+lon details
			String [] singleSplitedCoordinate;
			
			// would store all parsed coordinates to save
			String parsedCoordinates = "";
			
			// represent lat/lon of coordinate. used to pass the degrees/minutes/seconds of single lat/lon to parser method
			StringBuffer value;
			
			// iterate over each coordinate, parse it's lat/lon values and save to parsedCoordinates string 
			for(String coordinates: completeSplitedCoordinates){
				singleSplitedCoordinate = coordinates.split("\\*");
				value = new StringBuffer(singleSplitedCoordinate[1]);
				value.deleteCharAt(9);
				parsedCoordinates+=String.valueOf(MyUtils.DMStoDecimal(Double.parseDouble(value.substring(0, 2)), Double.parseDouble(value.substring(2, 4)), Double.parseDouble(value.substring(4, 9))))+",";
				
				value = new StringBuffer(singleSplitedCoordinate[0]);
				value.deleteCharAt(9);
				parsedCoordinates+=String.valueOf(MyUtils.DMStoDecimal(Double.parseDouble(value.substring(0, 2)), Double.parseDouble(value.substring(2, 4)), Double.parseDouble(value.substring(4, 9))));
				
				parsedCoordinates+=",2357";
						
				parsedCoordinates+= "\r\n";
			}
			
			// save parsed coords to temp variable
			currentRouteInfo.currentRouteCoordsReceived+=parsedCoordinates;
			
			currentRouteInfo.nextCoordsIndex = ""+(packetIndex+numCoordsReceived);
			
			messagesToSpmSender.sendGetRouteCoords(currentRouteInfo.nextCoordsIndex);
			
			MyUtils.saveRouteInfo(MainActivity.ROUTE_INFO_FILE, currentRouteInfo);
			
			return false;
		}
		
		
	}
	
	/** This method delete all previous kml file for the given rte.
	 * @param rteName
	 */
	private void removeRTEPreviousFiles(String rteName) {
		File[] listOfFiles = context.getExternalFilesDir(null).listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		  File file1 = listOfFiles[i];
		  if (file1.isFile() && file1.getName().endsWith(rteName)) {
		    file1.delete();
		  } 
		}
	}


	/** This method receives and parses Rdte schedule.<br>
	 * @param fullSchedule
	 */
	public void handleRdteScheduleResponse(String[] fullSchedule) {
		
		// clear previous schedule
		scheduleManager.schedule.clear();
		
		String [] tripList = fullSchedule[3].replace("*", "~").split("~"); 
		
		// check if we got all trips
		if(Integer.valueOf(fullSchedule[2]) != tripList.length){
			/////
			
			// indicates we didn't get all trips. request schedule again.
			//messagesToSpmSender.sendGetRdteSchedule();
			return;
		}
		
		Trip item;
		String [] tripDetails;
		
		// iterate over all trip list and save trips to schedule member 
		for (String trip : tripList){
			item = new Trip();
			try{
			
			tripDetails = trip.split(";");
			
			item.setUniqueTripNum(tripDetails[0]);
			item.setDriverID(tripDetails[1]);
			item.setTripName(tripDetails[2]);
			item.setTripStartDate(tripDetails[3]);
			item.setTripEndtDate(tripDetails[4]);
			item.setCTP_INT(tripDetails[5]);
			item.setTripID(tripDetails[6]);
			item.setBlockID(tripDetails[7]);
			item.setRTEname(tripDetails[8]);
			item.setLineSign(tripDetails[9]);
			item.setLineAlternate(tripDetails[10]);
			item.setDirection(tripDetails[11]);
			item.setFrom(tripDetails[12]);
			item.setTo(tripDetails[13]);
			item.setVehicleID(tripDetails[14]);
			item.setMOTRtID(tripDetails[15]);
			
			// save trip. 
			scheduleManager.schedule.put(item.getUniqueTripNum(), item);
			}
			catch (ArrayIndexOutOfBoundsException outOfBoundsException){
				Utils.logger("failed to add trip "+item.getUniqueTripNum());
				outOfBoundsException.printStackTrace();
			}
		}
		
	}


	/** This method deletes schedule items from {@link ScheduleManager#schedule}.<br>
	 *  
	 * @param scheduleDeleteCommand contains details about requested schedule items to delete
	 */
	public void handleRdteDeleteScheduleResponse(String[] scheduleDeleteCommand) {
		
		String [] scheduleItems = scheduleDeleteCommand[2].split("*");
		
		// if we didn't get any items, it indicates we should delete all previous trips from schedule 
		if(scheduleItems.length == 1 ){
			scheduleManager.schedule.clear();
			return;
		}
		
		// iterate over the list and remove requested trips
		for(String tripId : scheduleItems){
			try{
			scheduleManager.schedule.remove(tripId);
			}
			catch (Exception exception){
				Utils.logger("failed to remove schedule item "+tripId );
				exception.printStackTrace();
			}
		}
				
		
	}


	
	
	/** This method handle receiving single message from message list.
	 * 
	 * @param messageDetails details of received message
	 */
	public boolean handleMsgList(String[] messageDetails) {

		// Check if we've received all messages
		if(messageDetails.length ==3){
			// save messages
			MyUtils.saveMessagesManagerToFile(MainActivity.MESSAGES_MANAGER, messagesManager);
			return true;
		}
		
		 
		try{
			int messageId = Integer.valueOf(messageDetails[1]);
			
			// if we just getting messages, clean old list
			if(messageId == 0){
				messagesManager.messagesList.clear();
			}
			
		Message message = new Message();
		message.setId(messageId);
		message.setText(messageDetails[3]);
		
		// save message to list
		messagesManager.messagesList.put(messageId,message);
		
		// request next message
		messagesToSpmSender.sendRequestNextMessage(messageId);
		return false;
		}
		catch (NumberFormatException formatException){
			System.out.println("failed to save message "+messageDetails[3]);
			return false;
		}
	}
}
