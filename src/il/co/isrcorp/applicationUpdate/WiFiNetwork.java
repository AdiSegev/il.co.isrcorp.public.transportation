package il.co.isrcorp.applicationUpdate;

import java.io.Serializable;

public class WiFiNetwork implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 384724551864393898L;
	
	public static final int NOT_SECURED = 1;
	public static final int WPA_SECURED = 2;
	public static final int WEP_SECURED = 3;
	
	public String SSID;
	public String password;
	public int security;
	
	public int getSecurity() {
		return security;
	}
	public void setSecurity(int security) {
		this.security = security;
	}
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
