package il.co.isrcorp.publictransport.isr.messages;

import java.io.Serializable;

/** This class represent single message to be used by driver.
 * @author Adi
 *
 */
public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1541132680136265358L;

	int id;
	String text;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
