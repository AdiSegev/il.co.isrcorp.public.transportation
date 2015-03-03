package il.co.isrcorp.applicationUpdate;

import java.io.Serializable;
import java.util.ArrayList;

/** This class Would hold all configurations required for updater process.
 * 
 * @author Adi
 *
 */
public class UpdateConfiguration implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1725403142151222716L;
	
	protected static UpdateConfiguration config = null;
	
	/**
	 *  full address of FTP server.
	 */
	protected String ftpServerAddress;
	/**
	 * user name to login to FTP server.
	 */
	protected String  FTPUserName;
	/**
	 * password to login to FTP server.
	 */
	protected String FTPPassword;
	/**
	 * relative file path to applications apk in FTP server.
	 */
	protected String filePath;
	/**
	 * full file name of application's apk.
	 */
	protected String apkName;
	/**
	 *  valid network connection types.
	 */
	protected int connectionType;
	/**
	 *  use only specific Wi-Fi networks or allow using public Wi-Fi networks.
	 */
	protected boolean publicWiFiAllowed;
	/**
	 *  full details for valid Wi-Fi network, including SSID and password for each network(SSID1*Pass1;...SSIDn*Passn).
	 */
	protected ArrayList <WiFiNetwork> wiFiNetworksDetails = new ArrayList<WiFiNetwork>();
	/**
	 *  should we use the Wifi, when there's valid WiFi or force using mobile network (SIM/ETHERNET)
	 */
	protected boolean preferWiFiOverMobile; 
	
	/**
	 * Flag indicates if we need to updater current version.<br>
	 * Set to true when 'UpdateApplication' command received.<br>
	 * Set to false after user has confirms new version's installation.
	 */
	protected boolean updateRequired = false;

	protected long crc;
	
	
	  public long getCrc() {
		return crc;
	}


	public void setCrc(long crc) {
		this.crc = crc;
	}


	public static UpdateConfiguration getInstance() {
	      if(config == null) {
	    	  config = new UpdateConfiguration();
	      }
	      return config;
	   }

}
