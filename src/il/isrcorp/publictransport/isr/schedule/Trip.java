package il.isrcorp.publictransport.isr.schedule;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
			
			String driverID;
			String tripName;
			/**
			 * Represent trip's start date and time. <br> 
			 * Saved as Calendar object so we can easily compare if it's before/after current time, using {@link Calendar#before(Object)} and {@link Calendar#after(Object)} functions.<br>
			 * We can also get specific fields(etc. Hour/Minutes) using (@link {@link Calendar#get(int)} function. 
			 */
			Calendar tripStartDate;
			/**
			 * Represent trip's end date and time. <br> 
			 * Saved as Calendar object so we can easily compare if it's before/after current time, using {@link Calendar#before(Object)} and {@link Calendar#after(Object)} functions.<br>
			 * We can also get specific fields(etc. Hour/Minutes) using (@link {@link Calendar#get(int)} function. 
			 */
			Calendar tripEndtDate;
			String CTP_INT;// – boolean (True = there is CTP in this trip, False – there isn't)
			String tripID;
			String MOTRtID;
			String operationArea;
			String RTEname;
			String lineSign;
			String lineAlternate;
			String direction;
			String  from;
			String to;
			
			SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
			
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
			public Calendar getTripStartDate() {
				return tripStartDate;
			}
			public void setTripStartDate(String tripStartDate) {
				
				this.tripStartDate = Calendar.getInstance();
				try {
					this.tripStartDate.setTime(formater.parse(tripStartDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			public String getOperationArea() {
				return operationArea;
			}
			public void setOperationArea(String operationArea) {
				this.operationArea = operationArea;
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
			public Calendar getTripEndtDate() {
				return tripEndtDate;
			}
			public void setTripEndtDate(String tripEndtDate) {

				this.tripEndtDate = Calendar.getInstance();
				try {
					this.tripEndtDate.setTime(formater.parse(tripEndtDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			public String getMOTRtID() {
				return MOTRtID;
			}
			public void setMOTRtID(String mOTRtID) {
				MOTRtID = mOTRtID;
			}
			
			

}
