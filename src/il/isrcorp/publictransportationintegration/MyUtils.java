package il.isrcorp.publictransportationintegration;

import il.isrcorp.publictransport.isr.messages.MessagesManager;
import il.isrcorp.publictransport.isr.routes.CurrentRouteInfo;
import il.isrcorp.publictransport.isr.schedule.ScheduleManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.CharBuffer;
import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;


public class MyUtils {
	transient  static Context context;

	public static void saveToFile(String filename, byte[] bytes) {

		File file = new File(context.getExternalFilesDir(null), filename);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			// fos.flush();
			fos.write(bytes);
			fos.close();

		} catch (FileNotFoundException ex) {

			ex.printStackTrace();

		} catch (IOException ex) {

			ex.printStackTrace();

		}

	}

	public static void saveFileToExternalSdCard(String filename, String packageName) {
		Bundle b = new Bundle();
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/SAVE_PACKAGE", filename);
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			fos.write(packageName.getBytes());
			fos.flush();
			fos.close();
			
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
	
	public static int readFileFromExternalSdCard(String filename, byte [] dest) {
		try{
		final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Package", filename);
		
		if (file.exists()) {
			
			InputStream input = new FileInputStream(file);
			try {
				int byteCnt = input.read(dest);
				input.close();
				return byteCnt; // true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			}
			catch (NullPointerException npe){
				System.out.println("got null in read");
			} catch (FileNotFoundException e) {
				e.printStackTrace();

			} catch (IOException e) {
				
				e.printStackTrace();
			}

		return -1;
	}
	
	public static int readFile(String filename, byte[] dest) {

		final File file = new File(context.getExternalFilesDir(null), filename);
		if (file.exists()) {
			try {
				InputStream input = new FileInputStream(file);
				try {
					int byteCnt = input.read(dest);
					input.close();
					return byteCnt; // true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;// false;

	}
	

	public static byte[] intToBytes(int num) {
		byte[] bytes = new byte[digitsInNumber(num)];
		boolean neg = (num < 0);
		num = Math.abs(num);
		for (int i = bytes.length - 1, j = 10; i >= 0; i--, j *= 10) {
			bytes[i] = (byte) (i == 0 && neg ? '-' : (num % j) / (j / 10) + 48);
		}

		return bytes;
	}

	public static int digitsInNumber(int num) {
		return num == 0 ? 1
				: (int) (Math.floor(Math.log10(Math.abs(num))) + 1 + (num < 0 ? 1
						: 0));
	}
	
	public static void getUpdatedTime(Calendar calendar,int numSecondsToAdd){
		 calendar = Calendar.getInstance();
	        System.out.println("Original = " + calendar.getTime());
	 
	        //
	        // Substract 2 hour from the current time
	        //
	        calendar.add(Calendar.HOUR, -2);
	        
	        
//		System.out.println(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.M)+":"+calendar.get(Calendar.SECOND));
	}

	public static String reverseString(String string) {
	
		StringBuffer buffer = new StringBuffer(string);
		return buffer.reverse().toString();
	}
	
	public static void saveAppInfoToFile(String fileName, ApplicationInfo appInfo) {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			ObjectOutputStream os;
			os = new ObjectOutputStream(fos);
			os.writeObject(appInfo);
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
		public static ApplicationInfo readAppInfoFile(String filename) {
		try{
		final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
		
		if (file.exists()) {
			ObjectInputStream input;
				input = new ObjectInputStream(new FileInputStream(file));
				ApplicationInfo appInfo = (ApplicationInfo) input
						.readObject();
				input.close();
				return appInfo;
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

		
		public static void saveMessagesManagerToFile(String fileName, MessagesManager messagesManager) {
			File file = new File(context.getExternalFilesDir(null), fileName);
			try {
				FileOutputStream fos = new FileOutputStream(file, false);
				ObjectOutputStream os;
				os = new ObjectOutputStream(fos);
				os.writeObject(messagesManager);
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
			public static MessagesManager readMessagesManagerFile(String filename) {
			try{
			final File file = new File(context.getExternalFilesDir(null), filename);
			
			if (file.exists()) {
				ObjectInputStream input;
					input = new ObjectInputStream(new FileInputStream(file));
					MessagesManager appInfo = (MessagesManager) input
							.readObject();
					input.close();
					return appInfo;
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

			
	/** MicroNet uses different path to save to internal storage, so we need to change the default path. 
	 * @param fileName
	 * @param appInfo
	 */
	public static void saveScheduleToFile(String fileName,
			ScheduleManager schedule) {
		
		File file = new File(context.getExternalFilesDir(null), fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			ObjectOutputStream os;
			os = new ObjectOutputStream(fos);
			os.writeObject(schedule);
			fos.close();
			os.close();
		} catch (NullPointerException npe){
			System.out.println("got null in save");
		}
		catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/** MicroNet uses different path to save to internal storage, so we need to change the default path.
	 * @param filename
	 * @return
	 */
	public static ScheduleManager readScheduleFile(String filename) {
		
		try{
		final File file = new File(context.getExternalFilesDir(null), filename);
		
		// if the file older than one day, don't use it.
		if (System.currentTimeMillis() < (file.lastModified()*1000*60*60*24)){
			return null;
		}
		
		if (file.exists()) {
			
			ObjectInputStream input;
				input = new ObjectInputStream(new FileInputStream(file));
				ScheduleManager schedule = (ScheduleManager) input
						.readObject();
				input.close();
				return schedule;
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

	public static void saveRouteInfo(String routeInfoFile,
			CurrentRouteInfo currentRouteInfo) {

		File file = new File(context.getExternalFilesDir(null), routeInfoFile);
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			ObjectOutputStream os;
			os = new ObjectOutputStream(fos);
			os.writeObject(currentRouteInfo);
			fos.close();
			os.close();
		} catch (NullPointerException npe){
			System.out.println("got null in save");
		}
		catch (FileNotFoundException e) {
			System.out.println("micronet didnt write to file");

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CurrentRouteInfo readRouteInfoFile(String filename) {
		
		try{
		final File file = new File(context.getExternalFilesDir(null), filename);
				
		if (file.exists()) {
			
			ObjectInputStream input;
				input = new ObjectInputStream(new FileInputStream(file));
				CurrentRouteInfo currentRouteInfo = (CurrentRouteInfo) input
						.readObject();
				input.close();
				return currentRouteInfo;
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
	public static void saveKmlFile(String currentRouteFileName,	String fileContent) {
		File file = new File(context.getExternalFilesDir(null), currentRouteFileName);
		FileWriter fw;
		try {
			fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(fileContent);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String readRouteCoords(String currentRouteFileName) {
		String result = null;
		File file = new File(context.getExternalFilesDir(null), currentRouteFileName);
		
		//Creates a FileReader Object
	      FileReader fr;
		try {
			fr = new FileReader(file);
			
			BufferedReader in = new BufferedReader(fr);
			
			result = in.readLine();
			
		      fr.close();
		      in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result; 
		}
		return result; 
	      
	}
	
/** This method receives coordinate info as DDMMSS format and returns it as decimal format, e.g 31.434455455.
 * @param degrees
 * @param minutes
 * @param seconds
 * @return
 */
public static double DMStoDecimal(double degrees, double minutes, double seconds){
		
		return ((degrees+minutes/60+seconds/3600));
	}

}