package il.co.isrcorp.applicationUpdate;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/** This class is responsible on managing network connectivity.
 * 
 * @author Adi
 *
 */
public class NetworkConnectivity {

	// List of network connection types
	public static final int TYPE_WIFI = 1;
	public static final int TYPE_ETHERNET = 2;
	public static final int TYPE_SIM = 3;
	public static final int TYPE_ALL = 4;
	
	private UpdateConfiguration config;
	private ConnectivityManager connMgr;
	private WifiManager wifi;
	private NetworkInfo netInfo;
	private Context mContext;
	private UpdateManager updateManager;
	
	protected NetworkConnectivity(Context context, UpdateManager updateManager){
		mContext = context;
		config = updateManager.config;
		wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		this.updateManager = updateManager;
		connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		netInfo = connMgr.getActiveNetworkInfo();
		
		// check if we need to use WiFi, if so, save known WiFi networks.
		if(config !=null && (config.ConnectionType == TYPE_WIFI ||config.ConnectionType == TYPE_ALL)){
			saveKnownNetworks();
		}
		
		// we need to register to WiFi receiver even if we're not using WiFi, in order to disable the WiFi, if we need to.
		registerWiFiReceiver();
	}

	/**
	 * This method saves the configurations of known WiFi networks stored in {@link UpdateConfiguration#wiFiNetworksDetails}.
	 */
	private void saveKnownNetworks() {
		// clear all previous configuration
		deletePreviousNetworks();
		
		// We're saving the network configuration according to it's security level
		for (WiFiNetwork network  : config.wiFiNetworksDetails) {
			switch (network.getSecurity()){
			case WiFiNetwork.WEP_SECURED:
				saveWEPNetwork(network);
				break;
			case WiFiNetwork.WPA_SECURED:
				saveWPANetwork(network);
				break;
			case WiFiNetwork.NOT_SECURED:
				saveOpenNetwork(network);
				break;
			}
		}
		
	}

	private void deletePreviousNetworks() {
		List <WifiConfiguration> list = wifi.getConfiguredNetworks();
		if(list == null)
			return;
		for (WifiConfiguration wifiConfiguration : list) {
			wifi.removeNetwork(wifiConfiguration.networkId);
		}
		
	}

	private void saveOpenNetwork(WiFiNetwork network){
		
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID =  network.getSSID();
		conf.priority = 1000;
		wifi.addNetwork(conf);
		wifi.saveConfiguration();
	}

	private void saveWPANetwork(WiFiNetwork network){
		
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID =network.getSSID();
		conf.preSharedKey =network.getPassword();
		conf.priority = 9999;
		wifi.addNetwork(conf);
		wifi.saveConfiguration();
		
	}
	private void saveWEPNetwork(WiFiNetwork network){
		
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = network.getSSID();
		conf.wepKeys[0] = network.getPassword();  
		conf.wepTxKeyIndex = 0; 
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); 
		conf.priority = 5000;
		wifi.addNetwork(conf);
		wifi.saveConfiguration();
	}

	/**
	 * 
	 */
	private void registerWiFiReceiver() {
		// Register for the WiFi changed event
		final IntentFilter filters = new IntentFilter();
		filters.addAction("android.net.wifi.STATE_CHANGE");
		mContext.registerReceiver(wiFiReceiver, filters);
	}
	
	protected void enableWiFi(){
		if(wifi.getWifiState()==WifiManager.WIFI_STATE_DISABLED || wifi.getWifiState()==WifiManager.WIFI_STATE_DISABLING)
		wifi.setWifiEnabled(true);
	}
	/** This method initialize {@link NetworkConnectivity#config} with new updater config.<br>
	 * 	Then we register no WiFi if needed
	 * @param newConfig
	 */
	protected void setUpdateConfig(UpdateConfiguration newConfig){
		config = newConfig;
		
		// check if we need to use WiFi, if so, save known WiFi networks.
				if(config !=null && (config.ConnectionType == TYPE_WIFI ||config.ConnectionType == TYPE_ALL)){
					saveKnownNetworks();
				}
				
				registerWiFiReceiver();
	}
	
	protected NetworkInfo getNetworkConnection(){
		netInfo = connMgr.getActiveNetworkInfo();
		return netInfo;
		
	}
	protected void close(){
		try {
			mContext.unregisterReceiver(wiFiReceiver);
		}
		catch (IllegalArgumentException  ex){
			ex.printStackTrace();
		}
		
	}
	
	// handler for received Intents for the WiFi change event 
	private BroadcastReceiver wiFiReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  
		  
		  // exit if we didn't get any updater configurations or we don't need to updater 
		  if (config == null || !config.updateRequired )
			  return;
		  
		  
		  
		  switch (wifi.getWifiState()){
		  
		  case WifiManager.WIFI_STATE_DISABLED:
			  // if we want to use WiFi and we need to updater to new version, turn WiFi on
			  if(config.ConnectionType == TYPE_WIFI || (config.ConnectionType == TYPE_ALL  && config.preferWiFiOverMobile))
			  wifi.setWifiEnabled(true);
			  break;
		  case WifiManager.WIFI_STATE_ENABLED:
			  
			  
			  Toast.makeText(context, String.valueOf(wifi.getWifiState()), Toast.LENGTH_SHORT).show();
			  
			  // if we don't want to use WiFi and we need to updater to new version, turn WiFi off
			  if(((config.ConnectionType != TYPE_WIFI && (config.ConnectionType != TYPE_ALL && config.preferWiFiOverMobile)))){
			  wifi.setWifiEnabled(false);
			  return;
			  }
			  
			  // Android OS connect automatically to open WiFi network.
			  // if we're allowed to connect to public networks, we can exit.
			  if(config.publicWiFiAllowed){
				
				  if (getNetworkConnection() != null && netInfo.isConnected())
					  updateManager.downloadApk();
				  
				  return;
			  }
			  
			  // If we've got here it means we should connect only to valid WiFi network.
			  // We need to check if the current connected network is in our allowed networks, if not, we should disconnect from it.
			  // If we have connected to valid network, start downloading new apk.
			  if(gotValidNetwork(wifi.getConnectionInfo().getSSID())){
				  updateManager.downloadApk();
			  }
			  // send current application version info to server
		  break;
		  }
		  
		
	  }


	};
	
	/** This method checks if the current WiFi network is valid
	 *  
	 * @param ssid SSID of current connected network
	 * @return true if it's valid, false if it's invalid network
	 */
	private boolean gotValidNetwork(String ssid) {
		for (WiFiNetwork network  : config.wiFiNetworksDetails) {
			if(network.getSSID().equalsIgnoreCase(ssid)){
				Toast.makeText(mContext, "got valid network "+wifi.getConnectionInfo().getSSID(), Toast.LENGTH_SHORT).show();
				return true;
				}
		}
		return false;
	}
}
