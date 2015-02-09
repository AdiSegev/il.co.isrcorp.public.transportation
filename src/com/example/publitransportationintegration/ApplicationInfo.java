package com.example.publitransportationintegration;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

/** This class stores global data of application.<br>
 *  We'll save it's instance when application closed in order to use it next time application loads.<br>
 *  We make it singleton since we don't want to cause conflicts.
 *  
 * @author Adi
 *
 */
public class ApplicationInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static ApplicationInfo appInfoInstance = null;
	 

	
	public String applicationName;
	public String applicationVersion;
	public String applicationLastUpdate;// (The date the application was updated on the device – yymmddhhmmss)
	public String applicationBuildnumber;// ?????
	public String androidVersion;
	public String androidBuildNumber;
	public String manufacturerImageVersion;
	public String manufacturerImageBuildNumber;
	public String IMEI1;
	public String IMEI2;
	public String SPMSerialNumber = "";
	public String sim1SerialNumber;
	public String sim2SerialNumber; 
	public String SPMApplicationName = "";
	public String SPMApplicationVersion = "";

	boolean preChecksListInitiazed = false;
	
	transient private Context context;


	public String driverMessagesListVersion ="";
	
	protected ApplicationInfo(){
	}
	
	  public static ApplicationInfo getInstance() {
	      if(appInfoInstance == null) {
	    	  appInfoInstance = new ApplicationInfo();
	      }
	      return appInfoInstance;
	   }
	
	public Context getContext() {
		return context;
	}

	/** Set the context class member.<br>
	 *  We also initialize {@link PackageManager} and {@link PackageInfo} members here, for future use.
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
		
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName() {
		int stringId = context.getApplicationInfo().labelRes;
		this.applicationName = context.getString(stringId);
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion() {
		PackageManager manager;
		PackageInfo info;
		
		manager = context.getPackageManager();
		try {
			info = manager.getPackageInfo(getContext().getPackageName(), 0);
			this.applicationVersion = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getApplicationLastUpdate() {
		return applicationLastUpdate;
	}

	public void setApplicationLastUpdate() {
		PackageManager manager;
		PackageInfo info;
		
manager = context.getPackageManager();
		
		try {
			info = manager.getPackageInfo(getContext().getPackageName(), 0);
			DateFormat df1 = new SimpleDateFormat("yyMMddhhmmss", Locale.US);
			String formattedDate = df1.format(new Date(info.lastUpdateTime));
			this.applicationLastUpdate = formattedDate;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public String getApplicationBuildnumber() {
		return applicationBuildnumber;
	}

	public void setApplicationBuildnumber(String applicationBuildnumber) {
		this.applicationBuildnumber = applicationBuildnumber;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion() {
		this.androidVersion = String.valueOf(Build.VERSION.SDK_INT);
	}

	public String getAndroidBuildNumber() {
		return androidBuildNumber;
	}

	public void setAndroidBuildNumber() {
		this.androidBuildNumber = Build.DISPLAY;
	}

	public String getManufacturerImageVersion() {
		return manufacturerImageVersion;
	}

	public void setManufacturerImageVersion(String manufacturerImageVersion) {
		this.manufacturerImageVersion = manufacturerImageVersion;
	}

	public String getManufacturerImageBuildNumber() {
		return manufacturerImageBuildNumber;
	}

	public void setManufacturerImageBuildNumber(String manufacturerImageBuildNumber) {
		this.manufacturerImageBuildNumber = manufacturerImageBuildNumber;
	}

	public String getIMEI1() {
		return IMEI1;
	}

	public void setIMEI1() {
		TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
		IMEI1 = manager.getDeviceId();
	}

	public String getIMEI2() {
		return IMEI2;
	}

	public void setIMEI2(String iMEI2) {
		IMEI2 = iMEI2;
	}

	public String getSPMSerialNumber() {
		return SPMSerialNumber;
	}

	public void setSPMSerialNumber(String sPMSerialNumber) {
		SPMSerialNumber = sPMSerialNumber;
	}

	public String getSim1SerialNumber() {
		return sim1SerialNumber;
	}

	public void setSim1SerialNumber(String sim1SerialNumber) {
		this.sim1SerialNumber = sim1SerialNumber;
	}

	public String getSim2SerialNumber() {
		return sim2SerialNumber;
	}

	public void setSim2SerialNumber(String sim2SerialNumber) {
		this.sim2SerialNumber = sim2SerialNumber;
	}

	public String getSPMApplicationName() {
		return SPMApplicationName;
	}

	public void setSPMApplicationName(String sPMApplicationName) {
		SPMApplicationName = sPMApplicationName;
	}

	public String getSPMApplicationVersion() {
		return SPMApplicationVersion;
	}

	public void setSPMApplicationVersion(String sPMApplicationVersion) {
		SPMApplicationVersion = sPMApplicationVersion;
	}

	/**
	 * This method would set value to {@link ApplicationInfo} class members that we need to send to server. 
	 */
	public void initGeneralApplicationInfo(){
		setAndroidBuildNumber();
		setAndroidVersion();
		setApplicationLastUpdate();
		setApplicationName();
		setApplicationVersion();
		setIMEI1();
	}
	
}
