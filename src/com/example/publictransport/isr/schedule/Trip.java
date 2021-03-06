package com.example.publictransport.isr.schedule;

import java.io.Serializable;
import java.util.Calendar;

/** This class represents single trip of complete schedule
 * 
 * @author Adi
 *
 */
public class Trip implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2487564309559947251L;
			
			String uniqueTripNum; 
			String driverID;
			String tripName;
			String tripStartDate;// (TimeDate)
			String tripEndtDate;// (TimeDate)
			String CTP_INT;// � boolean (True = there is CTP in this trip, False � there isn't)
			String tripID;
			String MOTRtID;
			String blockID;
			String RTEname;
			String lineSign;
			String lineAlternate;
			String direction;
			String  from;
			String to;
			String vehicleID;
			
			
			public String getDriverID() {
				return driverID;
			}
			public void setDriverID(String driverID) {
				this.driverID = driverID;
			}
			public String getTripName() {
				return tripName;
			}
			public void setTripName(String tripName) {
				this.tripName = tripName;
			}
			public String getTripStartDate() {
				return tripStartDate;
			}
			public void setTripStartDate(String tripStartDate) {
				
				this.tripStartDate = tripStartDate.substring(6, 8)+":"+tripStartDate.substring(8, 10)+":"+tripStartDate.substring(10, 12);
			}
			
			public String getCTP_INT() {
				return CTP_INT;
			}
			public void setCTP_INT(String cTP_INT) {
				CTP_INT = cTP_INT;
			}
			public String getTripID() {
				return tripID;
			}
			public void setTripID(String tripID) {
				this.tripID = tripID;
			}
			public String getBlockID() {
				return blockID;
			}
			public void setBlockID(String blockID) {
				this.blockID = blockID;
			}
			public String getRTEname() {
				return RTEname;
			}
			public void setRTEname(String rTEname) {
				RTEname = rTEname;
			}
			public String getLineSign() {
				return lineSign;
			}
			public void setLineSign(String lineSign) {
				this.lineSign = lineSign;
			}
			public String getLineAlternate() {
				return lineAlternate;
			}
			public void setLineAlternate(String lineAlternate) {
				this.lineAlternate = lineAlternate;
			}
			public String getDirection() {
				return direction;
			}
			public void setDirection(String direction) {
				this.direction = direction;
			}
			public String getFrom() {
				return from;
			}
			public void setFrom(String from) {
				this.from = from;
			}
			public String getTo() {
				return to;
			}
			public void setTo(String to) {
				this.to = to;
			}
			public String getVehicleID() {
				return vehicleID;
			}
			public void setVehicleID(String vehicleID) {
				this.vehicleID = vehicleID;
			}
			public String getTripEndtDate() {
				return tripEndtDate;
			}
			public void setTripEndtDate(String tripEndtDate) {
				this.tripEndtDate = tripEndtDate.substring(6, 8)+":"+tripEndtDate.substring(8, 10)+":"+tripEndtDate.substring(10, 12);
			}
			public String getUniqueTripNum() {
				return uniqueTripNum;
			}
			public void setUniqueTripNum(String uniqueTripNum) {
				this.uniqueTripNum = uniqueTripNum;
			}
			public String getMOTRtID() {
				return MOTRtID;
			}
			public void setMOTRtID(String mOTRtID) {
				MOTRtID = mOTRtID;
			}
			
			

}
