package il.co.isrcorp.applicationUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

/** This class would manage updater process.
 * 
 * @author Adi
 *
 */
public class UpdateManager{

	private static final String UPDATE_CONFIG_FILENAME = "update_config";
	private Context mContext;
	UpdateConfiguration config;
	private NetworkConnectivity networkConnectivity;
	protected boolean downloadStarted = false;
	protected boolean installationRequested = false;
	private int apkValidatorCounter = 0;
	 private int downloadRetries = 0;

	public UpdateManager(Context context){
		mContext = context;
		UpdateUtils.mContext = mContext;
		
		// create SAVE folder for saving update configurations details
		File updateConfigurationFolder =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SAVE");
		
		if(!updateConfigurationFolder.exists())
			updateConfigurationFolder.mkdir();
		
		// create SAVE folder for saving apk file
				File mainFolder =  new File(mContext.getExternalFilesDir(null), "SAVE");
				
				if(!mainFolder.exists())
					mainFolder.mkdir();
				
//				create TEMP folder for saving temp apk file (we'll install the app's new version from temp folder)
				File tempFolder =  new File(mContext.getExternalFilesDir(null), "TEMP");
				
				if(!tempFolder.exists())
					tempFolder.mkdir();
				
		// check if we already have updater configuration info
		config = UpdateUtils.readUpdateConfigFile(UPDATE_CONFIG_FILENAME);
		
//		if we have we want to use it
		if (config != null){
			UpdateConfiguration.config = config;
		}
		
		networkConnectivity = new NetworkConnectivity(mContext, this);
		
		if (config == null)
			return;
		
		if (!config.updateRequired)
			return;
		
		// check if we can start downloading and installing new version
		if (isApkFileExists() && isApkFileValid()){ // in case we already have the apk file and it's valid, start install process
			checkStartingApplicationInstallation();
		}
//		else if (networkConnectivity.gotValidNetworkConnection() && !downloadStarted){ // in case we don't have valid apk file, download it
//			downloadApk();
//		}
	}
	
	protected void downloadApk() {
	NetworkInfo netInfo = networkConnectivity.getNetworkConnection();
	
	if(netInfo == null)
		return;
	
	downloadStarted = true;
	
	// start downloading
	new AsyncTask <Object, Void, Object> ()  {
		   

			public Object doInBackground(Object... obj) {
		    	 try {	        
			        FTPClient f = new FTPClient();
			        f.connect(config.ftpServerAddress);
			        boolean login= f.login(config.FTPUserName, config.FTPPassword);
			        int count=0;
			        while((!login)&&(count<5)){
			        	 login= f.login(config.FTPUserName, config.FTPPassword);
			        	 count++;
			        }
			        if(!login){
			        	return false;
			        	}
			        
			        f.enterLocalPassiveMode();
			        f.setFileType(FTP.BINARY_FILE_TYPE);
			        FTPFile[] files = f.listFiles(config.filePath);
			        
			        if(files.length<1){
			       	 UpdateUtils.logger("no files found in server");
			       	 return false;
			        }
			        
			        boolean findFile=false;
			        new Thread(new Runnable() {

						public void run() {
							Looper.prepare();
							 Toast.makeText(mContext, "download start", Toast.LENGTH_SHORT).show();
							 UpdateUtils.logger("download start");
							Looper.loop();
						}
					}).start();
			       
			        
			        for (int i=0; i < files.length; i++) {
			            if (config.apkName.equalsIgnoreCase(files[i].getName())) {
			            	findFile = true;
			                
			            	config.setApkFileLength(files[i].getSize());
			            	
			            	UpdateUtils.saveUpdateConfigToFile(UPDATE_CONFIG_FILENAME, config);
			                
			                File downloadFile1 = new File(mContext.getExternalFilesDir(null)+"/SAVE",files[i].getName());
			                
			                FileOutputStream fileOutput = new FileOutputStream(downloadFile1);
			    	        InputStream inputStream = f.retrieveFileStream(config.filePath+config.apkName); //urlConnection.getInputStream();
			    
			    	        byte[] buffer = new byte[1024];
			    	        int bufferLength = 0;
			    try{
			    	        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
			    	            fileOutput.write(buffer, 0, bufferLength);
			    	        }
			    }catch(SocketException socketException){
			    	socketException.printStackTrace();
			    	
			    }
			    	        fileOutput.close();  
			            }
			        }
			        f.disconnect();
			        if(findFile){
			        	return true;
			        	
			        }

			    } catch (MalformedURLException e) {
			            e.printStackTrace();
			    } catch (SocketException ex) {
			    	
			        ex.printStackTrace();
			        
			   	 if(downloadRetries < 5){
			   		downloadRetries++;
		    		 downloadApk();
				 }
			    } catch (IOException e) {
			            e.printStackTrace();
			    }
		    	 
			return false;
		  } 
		 
		  public void onPostExecute(Object res) {
			  if (res instanceof Boolean){
				 if((Boolean)res){
					 downloadStarted = false;
					 UpdateUtils.logger("download success");
//					 new Thread(new Runnable() {
//
//							public void run() {
//								Looper.prepare();
//								 Toast.makeText(mContext, "download success", Toast.LENGTH_SHORT).show();
//								Looper.loop();
//							}
//						}).start();
					 // ensure we have valid apk before trying to install it
					 while(apkValidatorCounter < 5){
						 apkValidatorCounter++;
						 
						 if(isApkFileValid()){
							 checkStartingApplicationInstallation();
							 break;
							 }
							 else{
								 downloadApk();
							 }	 
					 }
					 
				 }
			  }
		  } 
		 
		}.execute();
		
	}

	
	protected boolean isApkFileExists() {
		File file = new File(mContext.getExternalFilesDir(null)+"/SAVE",config.apkName);
		return file.exists();
	}

	/** This method checks if the existing apk file is the apk stored in the server.<br>
	 *  We're doing it by comparing the CRC32 checksum sent when update required command received
	 *  
	 * @return true if it's valid apk false if not.
	 */
	protected boolean isApkFileValid() {
		File file = new File(mContext.getExternalFilesDir(null)+"/SAVE",config.apkName);
		if(file.length() != config.getApkFileLength())
			return false;
		
		return true;
//		BufferedReader in;
//		try {
//			in = new BufferedReader(new FileReader(file));
//			String result = "";
//			String inputDataLine ="";
//			
//			while((inputDataLine = in.readLine())!= null){
//				result = inputDataLine;
//			}
//			
//			in.close();
//			CRC32 apkFileLength = new CRC32();
//			
//			apkFileLength.update(result.getBytes(), 0, result.getBytes().length);
//			
//			if(config.getCrc() == apkFileLength.getValue()){
//				return true;
//			}
//			else{
//				return false;
//			}
//			
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
		
		
	}
	/**
	 * This method check if user wants to install new version, and handle application's installation process.
	 */
	private void checkStartingApplicationInstallation() {
		
		if(installationRequested)
			return;
		
		installationRequested = true;
		 
		new AlertDialog.Builder(mContext) 
		    .setTitle(mContext.getApplicationInfo().labelRes)
		    .setMessage("קיימת גירסה חדשה של האפליקציה."+"\nהאם לעדכן עכשיו?")
		    .setIcon(mContext.getApplicationInfo().icon)
		    
		    .setPositiveButton("אישור", new DialogInterface.OnClickListener() { 
		    public void onClick(DialogInterface dialog, int which) { 
		    	config.updateRequired = false;
		    	UpdateUtils.saveUpdateConfigToFile(UPDATE_CONFIG_FILENAME, config);
		    	installApk();
		    	dialog.dismiss();
		    } 
		    }) 
		    
		    .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) { 
		    } 
		    })
		     .show(); 
	}

	
	
	  public void installApk(){
		    moveApkFile();
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        Uri uri = Uri.fromFile(new File(mContext.getExternalFilesDir(null)+"/TEMP/"+config.apkName));
	        intent.setDataAndType(uri, "application/vnd.android.package-archive");
	        mContext.startActivity(intent);
	    }

	/**
	 * This method copies the new apk to temp folder, and deletes the file from main folder.
	 */
	private void moveApkFile() {
		  
		  InputStream inStream = null;
			OutputStream outStream = null;
		 
		    	try{
		 
		    	    File sourcefile =new File(mContext.getExternalFilesDir(null)+"/SAVE",config.apkName);
		    	    File tempfile =new File(mContext.getExternalFilesDir(null)+"/TEMP",config.apkName);
		 
		    	    // clear previous file
		    	    if (tempfile.exists())
		    	    	tempfile.delete();
		    	    
		    	    inStream = new FileInputStream(sourcefile);
		    	    outStream = new FileOutputStream(tempfile);
		 
		    	    byte[] buffer = new byte[1024];
		 
		    	    int length;
		    	    //copy the file content in bytes 
		    	    while ((length = inStream.read(buffer)) > 0){
		 
		    	    	outStream.write(buffer, 0, length);
		 
		    	    }
		 
		    	    inStream.close();
		    	    outStream.close();
		 
		    	    //delete the original file
		    	    sourcefile.delete();
		 
		    	    System.out.println("File is copied successful!");
		 
		    	}catch(IOException e){
		    	    e.printStackTrace();
		    	}
	}

	/** This method would be called by the application when it get the updater configurations info from the server.
	 * 
	 * @param updateConficInfo - complete updater configuration
	 */
	public void updateConfigurationReceived(String [] updateConficInfo){
		
		// check if we've successfully saved received configuration before save it to file.
		if(populateUpdateConfig(updateConficInfo)){
			UpdateUtils.saveUpdateConfigToFile(UPDATE_CONFIG_FILENAME, config);
			
			// updater networkConnecticon reference with new updater config
			networkConnectivity.setUpdateConfig(config);
		}
		
		
		
		
		
	}
	
	/** This method populate the {@link UpdateConfiguration#config} with the new updater configuration info.
	 *  
	 * @param updateConfigInfo new updater configuration info
	 */
	private boolean populateUpdateConfig(String [] updateConfigInfo) {
		// clear previous config if we had
				if(UpdateConfiguration.config!= null){
					UpdateConfiguration.config = null;
				}
		
		config = UpdateConfiguration.getInstance();
	
		try{
		
		config.ftpServerAddress = updateConfigInfo[1];
		config.FTPUserName = updateConfigInfo[2];
		config.FTPPassword = updateConfigInfo[3];
		config.filePath = updateConfigInfo[4];
		config.apkName = updateConfigInfo[5];
		
		try{
			switch(Integer.valueOf(updateConfigInfo[6])){
			case 1:
				config.connectionType = NetworkConnectivity.TYPE_WIFI;
				break;
			case 2:
				config.connectionType = NetworkConnectivity.TYPE_ETHERNET;
				break;
			case 3:
				config.connectionType = NetworkConnectivity.TYPE_SIM;
				break;
			case 4:
				config.connectionType = NetworkConnectivity.TYPE_ALL;
				break;
			}
			
		}
		catch (NumberFormatException formatException){
			UpdateUtils.logger("illegal connection type: "+updateConfigInfo[6]);
			formatException.printStackTrace();
		}
		
		// we need to check if we should use WiFi. If we souldn't use it, we can ignore the following fields.
		if (config.connectionType == NetworkConnectivity.TYPE_WIFI ||config.connectionType == NetworkConnectivity.TYPE_ALL){
		try{
			int publicAllowed = Integer.valueOf(updateConfigInfo[7]);
			config.publicWiFiAllowed = publicAllowed == 1 ? true:false;	
			}
			catch (NumberFormatException formatException){
				UpdateUtils.logger("illegal publicWiFiAllowed: "+updateConfigInfo[7]);
				formatException.printStackTrace();
			}
			
			populateWiFiNetworks(updateConfigInfo[8]);
			
		}
		
		// if we're allowed to use all network connections, we need to know if we should prefer wiFi over mobile network.
		if(config.connectionType == NetworkConnectivity.TYPE_ALL){
			try{
				int prefferdConnection = Integer.valueOf(updateConfigInfo[9]);
				config.preferWiFiOverMobile = prefferdConnection == 1 ? true:false;	
				}
				catch (NumberFormatException formatException){
					UpdateUtils.logger("illegal preferWiFiOverMobile: "+updateConfigInfo[9]);
					formatException.printStackTrace();
				}
		}
		}
		catch (IndexOutOfBoundsException boundsException){
			boundsException.printStackTrace();
			return false;
		}
		
		return true;
	}

	/** This method populate {@link UpdateConfiguration#wiFiNetworksDetails}} with allowed networks
	 * @param networksInfo complete info of networks
	 */
	private void populateWiFiNetworks(String networksInfo) {
		String [] wifiNetworkList = networksInfo.split(";");
		String [] splitedNetwork;
		WiFiNetwork network;
		
		// we check this in case we've got only one network and user didn't add ';' at the end.
		if(wifiNetworkList.length == 1){
			splitedNetwork = wifiNetworkList[0].split("\\*");
			network = new WiFiNetwork();
			
			try{
			int networkSecurity = Integer.valueOf(splitedNetwork[0]);
			network.setSecurity(Integer.valueOf(splitedNetwork[0]));
			// if it's secured network, we need to store the password
			if(networkSecurity != WiFiNetwork.NOT_SECURED){
			
			network.setSSID("\""+ splitedNetwork[1] +"\""); //  Please note the quotes. String should contain ssid in quotes
			network.setPassword("\""+ splitedNetwork[2] +"\""); // Please note the quotes. String should contain password in quotes
			}
			else{
				network.setSSID("\""+ splitedNetwork[1] +"\""); //  Please note the quotes. String should contain ssid in quotes
			}
			}
			catch (NumberFormatException formatException){
				UpdateUtils.logger("illegal security: "+splitedNetwork[0]);
			}
			
			
			config.wiFiNetworksDetails.add(network);
			return;
		}
		
		for (String networkItem : wifiNetworkList) {
			splitedNetwork = networkItem.split("\\*");
			network = new WiFiNetwork();
			
			try{
			int networkSecurity = Integer.valueOf(splitedNetwork[0]);
			network.setSecurity(Integer.valueOf(splitedNetwork[0]));
			// if it's secured network, we need to store the password
			if(networkSecurity != WiFiNetwork.NOT_SECURED){
			
			network.setSSID("\""+ splitedNetwork[1] +"\""); //  Please note the quotes. String should contain ssid in quotes
			network.setPassword("\""+ splitedNetwork[2] +"\""); // Please note the quotes. String should contain password in quotes
			}
			else{
				network.setSSID("\""+ splitedNetwork[1] +"\""); //  Please note the quotes. String should contain ssid in quotes
			}
			}
			catch (NumberFormatException formatException){
				UpdateUtils.logger("illegal security: "+splitedNetwork[0]);
			}
			
			
			config.wiFiNetworksDetails.add(network);
			
		}
	}

	/**
	 * This method would be called by the application when it being informed by the server of new app version.
	 */
	public void updateRequiredReceived(){
		
		if (config == null)
			return;
		
		config.updateRequired = true;
		installationRequested = false;
		downloadStarted = false;
		apkValidatorCounter = 0;
		downloadRetries = 0;
		UpdateUtils.saveUpdateConfigToFile(UPDATE_CONFIG_FILENAME, config);
		
		 File sourcefile =new File(mContext.getExternalFilesDir(null)+"/SAVE",config.apkName);
		 
		 if(sourcefile.exists())
			 sourcefile.delete();
		 
		// check if we suppose to use only WiFi. 
		if(config.connectionType == NetworkConnectivity.TYPE_WIFI || (config.connectionType == NetworkConnectivity.TYPE_ALL && config.preferWiFiOverMobile)){
			networkConnectivity.registerWiFiReceiver();
			// enable WiFi if it's disabled
			if(!networkConnectivity.isWiFiEnabled()){
				networkConnectivity.enableWiFi();
				return;
			} 
			// if WiFi enabled, try to connect to WiFi network  
			else if(networkConnectivity.connectToVaildNetwork()  && !downloadStarted){
			downloadApk();
			return;
			}
		} 
		// If we shouldn't use WiFi, we need to ensure disable WiFi
		else if(networkConnectivity.isWiFiEnabled()){
			networkConnectivity.diableWiFi();
		}
		// If we've got the valid connection type we can start downloading
		else if(networkConnectivity.getNetworkConnection()!= null && networkConnectivity.gotValidNetworkConnection() && !downloadStarted){
			downloadApk();
		}
		
		
	}


	/** This method returns the current update configurations.<br>
	 *  
	 * @return current update configuration start with "CurrentConfig" command or "CurrentConfig\\cNoConfig" if there's no configurations
	 */
	public String getUpdateConfiguration(){
		
		String configuration = "CurrentConfig\\c";
		
		if(config != null){
			configuration += config.ftpServerAddress+"\\c";
			configuration += config.FTPUserName+"\\c";
			configuration += config.FTPPassword+"\\c";
			configuration += config.filePath+"\\c";
			configuration += config.apkName+"\\c";
			
			switch (config.connectionType){
			case NetworkConnectivity.TYPE_WIFI:
				configuration += 1+"\\c";
				break;
			case NetworkConnectivity.TYPE_ETHERNET:
				configuration += 2+"\\c";
				break;
			case NetworkConnectivity.TYPE_SIM:
				configuration +=3+"\\c";
				break;
			case NetworkConnectivity.TYPE_ALL:
				configuration += 4+"\\c";
				break;
			}
			
			if (config.connectionType == NetworkConnectivity.TYPE_WIFI ||config.connectionType == NetworkConnectivity.TYPE_ALL){
			configuration += (config.publicWiFiAllowed ? "1":"2")+"\\c";

			for (WiFiNetwork network : config.wiFiNetworksDetails) {
				configuration +=addNetworkDetails(network);
			}
				configuration+="\\c";
			}
			
			else{
				configuration+="\\c\\c";
			}
			
			if(config.connectionType == NetworkConnectivity.TYPE_ALL){
			configuration+=config.preferWiFiOverMobile ? "1":"2"+"\\c";
			}
			else{
				configuration+="\\c";
			}
		}
		
		return ("CurrentConfig\\c".equalsIgnoreCase(configuration)?configuration+="NoConfig":configuration);
	}
	
	private String addNetworkDetails(WiFiNetwork network){
		String details = ""+network.getSecurity();
		details +="*"+network.getSSID().substring(1, network.getSSID().length()-1)+"*";
		details +=(network.getPassword()!=null ? network.getPassword().substring(1, network.getPassword().length()-1):" ")+";";
		return details;
	}
	public void close() {
		networkConnectivity.close();
		
	}
}
