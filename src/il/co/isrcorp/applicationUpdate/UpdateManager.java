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
	

	public UpdateManager(Context context){
		mContext = context;
		UpdateUtils.mContext = mContext;
		
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
		
		// check if we need to updater current version
		if (config != null && config.updateRequired && isApkFileExists()){ // in case we already have the apk file, start install process
			checkStartingApplicationInstallation();
		}
		else if (config != null && config.updateRequired && !isApkFileExists()){ // in case we don't have apk file, download it
			downloadApk();
		}
	}
	
	protected void downloadApk() {
	NetworkInfo netInfo = networkConnectivity.getNetworkConnection();
	
	if(netInfo == null)
		return;
	
	// start downloading
	new AsyncTask <Object, Void, Object> ()  {
		    public Object doInBackground(Object... obj) {
		    	 try {	        
			        FTPClient f = new FTPClient();
			        f.connect(config.ftpServerAddress);
			        boolean login= f.login(config.ftpServerAddress, config.FTPPassword);
			        int count=0;
			        while((!login)&&(count<5)){
			        	 login= f.login(config.ftpServerAddress, config.FTPPassword);
			        	 count++;
			        }
			        if(!login){
			        	return false;
			        	}
			        
			        f.enterLocalPassiveMode();
			        f.setFileType(FTP.BINARY_FILE_TYPE);
			        FTPFile[] files = f.listFiles(config.filePath);
			        boolean findFile=false;
			        
			        Toast.makeText(mContext, "download start", Toast.LENGTH_SHORT).show();
			        
			        for (int i=0; i < files.length; i++) {
			            if (config.apkName.equalsIgnoreCase(files[i].getName())) {
			            	findFile = true;
			                
			                File downloadFile1 = new File(mContext.getExternalFilesDir(null)+"/SAVE",files[i].getName());
			                
			                FileOutputStream fileOutput = new FileOutputStream(downloadFile1);
			    	        InputStream inputStream = f.retrieveFileStream(config.filePath+config.apkName); //urlConnection.getInputStream();
			    
			    	        byte[] buffer = new byte[1024];
			    	        int bufferLength = 0;
			    
			    	        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
			    	            fileOutput.write(buffer, 0, bufferLength);
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
			    } catch (IOException e) {
			            e.printStackTrace();
			    }
			return false;
		  } 
		 
		  public void onPostExecute(Object res) {
			  if (res instanceof Boolean){
				 if((Boolean)res){
					  Toast.makeText(mContext, "download success", Toast.LENGTH_SHORT).show();
					 checkStartingApplicationInstallation();
				 }
			  }
		  } 
		 
		}.execute();
		
	}

	
	private boolean isApkFileExists() {
		File file = new File(mContext.getExternalFilesDir(null)+"/SAVE",config.apkName);
		return file.exists();
	}

	/**
	 * This method check if user wants to install new version, and handle application's installation process.
	 */
	private void checkStartingApplicationInstallation() {
		
		 
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
		
		config.ftpServerAddress = updateConfigInfo[2];
		config.FTPUserName = updateConfigInfo[3];
		config.FTPPassword = updateConfigInfo[4];
		config.filePath = updateConfigInfo[5];
		config.apkName = updateConfigInfo[6];
		
		try{
		config.ConnectionType = Integer.valueOf(updateConfigInfo[7]);	
		}
		catch (NumberFormatException formatException){
			UpdateUtils.logger("illegal connection type: "+updateConfigInfo[7]);
			formatException.printStackTrace();
		}
		
		// we need to check if we should use WiFi. If we souldn't use it, we can ignore the following fields.
		if (config.ConnectionType !=0 && (config.ConnectionType == NetworkConnectivity.TYPE_WIFI ||config.ConnectionType == NetworkConnectivity.TYPE_ALL)){
		try{
			int publicAllowed = Integer.valueOf(updateConfigInfo[8]);
			config.publicWiFiAllowed = publicAllowed == 1 ? true:false;	
			}
			catch (NumberFormatException formatException){
				UpdateUtils.logger("illegal publicWiFiAllowed: "+updateConfigInfo[8]);
				formatException.printStackTrace();
			}
			
			populateWiFiNetworks(updateConfigInfo[9]);
			
		}
		
		// if we're allowed to use all network connections, we need to know if we should prefer wiFi over mobile network.
		if(config.ConnectionType !=0 && (config.ConnectionType == NetworkConnectivity.TYPE_ALL)){
			try{
				int prefferdConnection = Integer.valueOf(updateConfigInfo[10]);
				config.preferWiFiOverMobile = prefferdConnection == 1 ? true:false;	
				}
				catch (NumberFormatException formatException){
					UpdateUtils.logger("illegal preferWiFiOverMobile: "+updateConfigInfo[10]);
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
		for (String networkItem : wifiNetworkList) {
			splitedNetwork = networkItem.split("\\*");
			network = new WiFiNetwork();
			try{
			network.setSecurity(Integer.valueOf(splitedNetwork[0]));
			}
			catch (NumberFormatException formatException){
				UpdateUtils.logger("illegal security: "+splitedNetwork[0]);
			}
			network.setSSID("\""+ splitedNetwork[1] +"\""); //  Please note the quotes. String should contain ssid in quotes
			network.setPassword("\""+ splitedNetwork[2] +"\""); // Please note the quotes. String should contain password in quotes
			
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
		UpdateUtils.saveUpdateConfigToFile(UPDATE_CONFIG_FILENAME, config);
		
		// check if we suppose to use only WiFi. 
		// If so, we don't need to start downloading process, it'll be done when we have WiFi connection.
		if(config.ConnectionType == NetworkConnectivity.TYPE_WIFI || (config.ConnectionType == NetworkConnectivity.TYPE_ALL && config.preferWiFiOverMobile)){
			networkConnectivity.enableWiFi();
			return;
		}
			
			
		if(networkConnectivity.getNetworkConnection()!= null){
			downloadApk();
		}
		
		
	}

	public void wifiNetworkAvailable() {
		
		// check if we need to updater current version
		if (config != null && config.updateRequired && !isApkFileExists()){ // in case we don't have apk file, download it
			downloadApk();
		}
		
	}

	public void close() {
		networkConnectivity.close();
		
	}
}
