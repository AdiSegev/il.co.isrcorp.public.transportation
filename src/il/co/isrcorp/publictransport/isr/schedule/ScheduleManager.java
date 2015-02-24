package il.co.isrcorp.publictransport.isr.schedule;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/** This class represent complete schedule
 * @author Adi
 *
 */
public class ScheduleManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3267089555434387317L;
	
	
	/**
	 * Represent list of all trips
	 */
	public ConcurrentHashMap<String, Trip> schedule = new ConcurrentHashMap<String, Trip>();
	
	public static ScheduleManager scheduleInstance = null;
	
	protected ScheduleManager(){
	}
	
	  public static ScheduleManager getInstance() {
	      if(scheduleInstance == null) {
	    	  scheduleInstance = new ScheduleManager();
	      }
	      return scheduleInstance;
	   }


}
