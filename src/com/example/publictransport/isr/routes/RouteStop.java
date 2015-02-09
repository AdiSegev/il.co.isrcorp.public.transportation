package com.example.publictransport.isr.routes;

import java.io.Serializable;

public class RouteStop implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7435325957662149323L;
	int stopNum;
	String stopName;
	String estimatedTimeArrive;
	int secondsFromPrevStop;
	String stopId;
	String lat;
	String lon;
	
	
	public int getSecondsFromPrevStop() {
		return secondsFromPrevStop;
	}
	public void setSecondsFromPrevStop(int secondsFromPrevStop) {
		this.secondsFromPrevStop = secondsFromPrevStop;
	}
	public int getStopNum() {
		return stopNum;
	}
	public void setStopNum(int stopNum) {
		this.stopNum = stopNum;
	}
	public String getStopName() {
		return stopName;
	}
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}
	public String getEstimatedTimeArrive() {
		return estimatedTimeArrive;
	}
	public void setEstimatedTimeArrive(String estimatedTimeArrive) {
		this.estimatedTimeArrive = estimatedTimeArrive;
	}
	public String getStopId() {
		return stopId;
	}
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	
}
