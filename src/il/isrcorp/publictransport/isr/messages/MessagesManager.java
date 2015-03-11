package il.isrcorp.publictransport.isr.messages;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/** This class responsible on managing messaging mechanism.
 * @author isr
 *
 */
public class MessagesManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3828633928838752059L;

	
	/* * Represent list of all messages
	 */
	public ConcurrentHashMap<Integer, Message> messagesList = new ConcurrentHashMap<Integer, Message>();
	

	transient ValueComparator bvc;

	transient TreeMap<Integer, Message> sorted_map;

	public static MessagesManager messagesManagerInstance = null;
	
	protected MessagesManager(){
	}
	
	  public static MessagesManager getInstance() {
	      if(messagesManagerInstance == null) {
	    	  messagesManagerInstance = new MessagesManager();
	      }
	      return messagesManagerInstance;
	   }
	  
	  public void sortMessages(){
			bvc = new ValueComparator(messagesList);
			sorted_map = new TreeMap<Integer,Message>(bvc);
			sorted_map.putAll(messagesList);
			
	  }
	  
	  /** This class used to compare between 2 stop list indexes.
		 * @author Adi
		 *
		 */
		class ValueComparator implements Comparator<Integer> {

			ConcurrentHashMap<Integer, Message> base;

			public ValueComparator(ConcurrentHashMap<Integer, Message> routeStops) {
				this.base = routeStops;
			}

			@Override
			public int compare(Integer a, Integer b) {
				if (base.get(a).getId() >= base.get(b).getId()) {
					return 1;
				} else {
					return -1;
				}
			}
		}
	
}
