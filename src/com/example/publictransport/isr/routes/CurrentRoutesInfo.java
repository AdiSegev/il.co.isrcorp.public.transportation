package com.example.publictransport.isr.routes;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.example.il.co.isrcorp.spmcommunicationcore.Utils;
import com.example.publitransportationintegration.MyUtils;

/** This class stores info about current route. 
 * 
 * @author Adi
 *
 */
public class CurrentRoutesInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5535630506420494618L;

	/**
	 * Holds current route's stops list.
	 */
	transient public ConcurrentHashMap<Integer, RouteStop> routeStopsList = new ConcurrentHashMap<Integer, RouteStop>();
	
	/**
	 * Represents route's start time.<br> 
	 * We declare it as Calendar instance in order to construct route stops ETA base on routeStartTimes.
	 */
	Calendar routeStartTime = Calendar.getInstance();
	
	public boolean startReceivingCoords = false;
	
	public boolean endReceivingCoords  = false;
	
	public String currentRouteCoordsReceived = "";
	
	public String nextCoordsIndex = "";
	
	public String currentRouteFileName = "";
	
	public String currentRouteFileDirectoryName = "";
	
	String routeName = "";
	
	String routeNum = "";
	
String direction = "";

transient ValueComparator bvc;

transient TreeMap<Integer, RouteStop> sorted_map;

public static CurrentRoutesInfo currentRouteInfoInstance = null;


	public String destination;

	private StringBuffer value;
	
	public String getRouteNum() {
		return routeNum;
	}

	public void setRouteNum(String routeNum) {
		this.routeNum = routeNum.substring(0, 3)+"-"+this.direction;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		int dir = Integer.valueOf(direction.substring(4,5));
		
		switch (dir){
		
		case 1:
			this.direction = "A";
		break;
		
		case 2 : 
			this.direction = "R";
		break;
		case 3 :
			this.direction = "C";
		break;
		
		case 4 :
			this.direction = "N";
		break;
		
		case 5 :
			this.direction = "E";
		break;
		
		case 6 :
			this.direction = "S";
		break;
		
		case 7 :	
			this.direction = "W";
		break;
		
		}
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	 
	protected CurrentRoutesInfo(){
	}
	
	  public static CurrentRoutesInfo getInstance() {
	      if(currentRouteInfoInstance == null) {
	    	  currentRouteInfoInstance = new CurrentRoutesInfo();
	      }
	      return currentRouteInfoInstance;
	   }
	  
	  
	public void setRouteStartTime(String originStartTime){
		routeStartTime.set(Calendar.YEAR, Integer.valueOf(originStartTime.substring(0, 2)));
		routeStartTime.set(Calendar.MONTH, Integer.valueOf(originStartTime.substring(2, 4)));
		routeStartTime.set(Calendar.DAY_OF_MONTH, Integer.valueOf(originStartTime.substring(4, 6)));
		routeStartTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(originStartTime.substring(6, 8)));
		routeStartTime.set(Calendar.MINUTE, Integer.valueOf(originStartTime.substring(8, 10)));
		routeStartTime.set(Calendar.SECOND, Integer.valueOf(originStartTime.substring(10, 12)));
	}
	
	/** This method constructs start time as String (HH/MM/SS).
	 * @return route start time as String 
	 */
	public String getRouteStartTime(){
		
		String result = "";
		
		if(routeStartTime.get(Calendar.HOUR_OF_DAY)<10){
			result ="0"+routeStartTime.get(Calendar.HOUR_OF_DAY);
		}
		else{
			result =""+routeStartTime.get(Calendar.HOUR_OF_DAY);
		}
		
		if(routeStartTime.get(Calendar.MINUTE)<10){
			result +="/0"+routeStartTime.get(Calendar.MINUTE);
		}
		else{
			result +="/"+routeStartTime.get(Calendar.MINUTE);
		}
		
		if(routeStartTime.get(Calendar.SECOND)<10){
			result +="/0"+routeStartTime.get(Calendar.SECOND);
		}
		else{
			result +="/"+routeStartTime.get(Calendar.SECOND);
		}
		
		return result;
	}
	
	/** This method returns route's date start (DD/MM/YYYY) as String.
	 * @return route's date as String.
	 */
	public String getRouteStartDate(){
		
		String result = "";
		
		if(routeStartTime.get(Calendar.DAY_OF_MONTH)<10){
			result ="0"+routeStartTime.get(Calendar.DAY_OF_MONTH);
		}
		else{
			result =""+routeStartTime.get(Calendar.DAY_OF_MONTH);
		}
		
		if(routeStartTime.get(Calendar.MONTH)<10){
			result +="/0"+routeStartTime.get(Calendar.MONTH);
		}
		
		else{
			result +="/"+routeStartTime.get(Calendar.MONTH);
		}
		
			result +="/20"+routeStartTime.get(Calendar.YEAR);
				
		return result;
	}
	
	public void updateRouteStopsETA(){
		bvc = new ValueComparator(routeStopsList);
		sorted_map = new TreeMap<Integer,RouteStop>(bvc);
		sorted_map.putAll(routeStopsList);
		
		Calendar tempCalendar = routeStartTime;
		
		for (Entry<Integer,RouteStop> entry : sorted_map.entrySet()) {
			
			setStopETA(entry.getValue(),tempCalendar,entry.getValue().secondsFromPrevStop);
		}
	}
	
	private void setStopETA(RouteStop stop ,Calendar tempCalendar,int secondsToAdd) {
		
		tempCalendar.add(Calendar.SECOND, secondsToAdd);
		
		String result = "";
		
		if(tempCalendar.get(Calendar.HOUR_OF_DAY)<10){
			result ="0"+tempCalendar.get(Calendar.HOUR_OF_DAY);
		}
		else{
			result =""+tempCalendar.get(Calendar.HOUR_OF_DAY);
		}
		
		if(tempCalendar.get(Calendar.MINUTE)<10){
			result +=":0"+tempCalendar.get(Calendar.MINUTE);
		}
		else{
			result +=":"+tempCalendar.get(Calendar.MINUTE);
		}
		
		if(tempCalendar.get(Calendar.SECOND)<10){
			result +=":0"+tempCalendar.get(Calendar.SECOND);
		}
		else{
			result +=":"+tempCalendar.get(Calendar.SECOND);
		}
		
		stop.setEstimatedTimeArrive(result);
		
	}
	

	public void setRouteStopLocation(String[] result) {
		try{
		RouteStop stop = routeStopsList.get(Integer.parseInt(result[1]));
		
		value = new StringBuffer(result[2]);
		value.deleteCharAt(9);
		stop.setLat(String.valueOf(MyUtils.DMStoDecimal(Double.parseDouble(value.substring(0, 2)), Double.parseDouble(value.substring(2, 4)), Double.parseDouble(value.substring(4, 9)))));
		
		value = new StringBuffer(result[3]);
		value.deleteCharAt(9);
		stop.setLon(String.valueOf(MyUtils.DMStoDecimal(Double.parseDouble(value.substring(0, 2)), Double.parseDouble(value.substring(2, 4)), Double.parseDouble(value.substring(4, 9)))));
		
		System.out.println("stop "+stop.getStopNum()+" position: Lat: "+stop.getLat()+", Lon: "+stop.getLon());
		}
		catch (NumberFormatException nfe){
			Utils.logger("illegal stop num: "+result[1]);
			nfe.printStackTrace();
		}
		
		
	}


	/** This class used to compare between 2 stop list indexes.
	 * @author Adi
	 *
	 */
	class ValueComparator implements Comparator<Integer> {

		ConcurrentHashMap<Integer, RouteStop> base;

		public ValueComparator(ConcurrentHashMap<Integer, RouteStop> routeStops) {
			this.base = routeStops;
		}

		@Override
		public int compare(Integer a, Integer b) {
			if (base.get(a).getStopNum() >= base.get(b).getStopNum()) {
				return 1;
			} else {
				return -1;
			}
		}
	}


	
}
