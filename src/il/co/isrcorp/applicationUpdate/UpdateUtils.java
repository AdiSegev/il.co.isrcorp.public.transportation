package il.co.isrcorp.applicationUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class UpdateUtils {
	/**
	 * used for debug
	 */
	protected static final String TAG = "il.co.isrcorp.applicationUpdate";
	
	/**
	 * Flag for logger using in debug mode.
	 */
	private static boolean DEBUG  = true;
	protected static Context mContext;
	public UpdateUtils (Context context){
		mContext = context;
	}
	/** This method save the application's updater configuration's parameters to file.<br>
	 *  We're saving it external public storage, since Android OS deletes all data from other storages when removing/updating application.
	 *   
	 * @param fileName
	 * @param updateConfig
	 */
	public static void saveUpdateConfigToFile(String fileName, UpdateConfiguration updateConfig) {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/SAVE", fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			ObjectOutputStream os;
			os = new ObjectOutputStream(fos);
			os.writeObject(updateConfig);
			fos.close();
			os.close();
			
		} catch (NullPointerException npe){
			System.out.println("got null in save");
		}
		catch (FileNotFoundException e) {
			System.out.println("didnt write to file");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
		public static UpdateConfiguration readUpdateConfigFile(String filename) {
		try{
		final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/SAVE", filename);
		
		if (file.exists()) {
			ObjectInputStream input;
				input = new ObjectInputStream(new FileInputStream(file));
				UpdateConfiguration updateConfig = (UpdateConfiguration) input
						.readObject();
				input.close();
				return updateConfig;
			}
			}
			catch (NullPointerException npe){
				System.out.println("got null in read");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();

			} catch (StreamCorruptedException e) {
				e.printStackTrace();

			} catch (FileNotFoundException e) {
				e.printStackTrace();

			} catch (IOException e) {
				
				e.printStackTrace();
			}

		return null;
	}

		/**
		 * This method logs the data to logger, each byte separated by ','
		 * 
		 * @param buffer data to log
		 * @param length length of data
		 */
		public static void logger(String data) {
			
			if (!DEBUG  ) return; 
			Log.d(TAG, new Date()+" "+data);
		}
}
