package com.example.publitransportationintegration;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SecondActivity extends Activity {

	protected Messenger spmBridgeService;
	
	private Messenger spmBridgeResponse = new Messenger(new ServiceResponseHanlder());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		bindService(new Intent("com.example.publitransportationintegration.SpmParserBrisgeService"), mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_second,
					container, false);
			return rootView;
		}
	}
	
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  
			spmBridgeService = new Messenger(service);
			
			Message welcome = new Message();
			welcome.replyTo = spmBridgeResponse;
			welcome.arg1 = 1;
			try {
				spmBridgeService.send(welcome);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			spmBridgeService = null;
		}
	};
	

private class ServiceResponseHanlder extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			
//			switch (msg.what){
//			case MainActivity.LOST_SPM_COMMUNICATION:
//				 progress = new WaitForSpmDialog(MainActivity.this);
////			     
//				 progress.show();
//			break;
//			case RESTORE_SPM_COMMUNICATION:
//				if(progress!= null)
//				progress.cancel();
//			break;
//			}
//			Fragment fragment = getFragmentManager().findFragmentByTag("frag");
//			
//			TextView tv = (TextView)fragment.getView().findViewById(R.id.output);
//			String text = tv.getText().toString();
//			
//			text+= msg.getData().getStringArray("data")[0];
//			
//			tv.setText(text);
			
					System.out.println("got message from service in second activity");
			
				}
		}


@Override
	protected void onStop() {
		if (mConnection != null)
			unbindService(mConnection);
		super.onStop();
	}

	public void switchActivity(View v){
		Intent intent = new Intent (this, MainActivity.class);
		startActivity(intent);
	}

}
