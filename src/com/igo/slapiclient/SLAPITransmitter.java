// owners: anagy
// experts: tivan, ptarjan

package com.igo.slapiclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class SLAPITransmitter {
	private final static String logname = "SLAPIClient";
	private boolean mVerbose = true;
	private Context mCallingService;
	private Messenger mMessenger;
	
	public SLAPITransmitter(Context theActivity, Messenger messenger) {
	  init(theActivity,messenger);
	}
	
	public void init(Context theContext, Messenger messenger) {
    mCallingService = theContext;
    mMessenger = messenger;
  }
	
	public void setVerbose(boolean really) {
		mVerbose = really;
	}

	/** Informs the user on some event */
	public void showToastShort(CharSequence text) {
		if (mVerbose) {
            Toast.makeText(mCallingService, text, Toast.LENGTH_SHORT).show();
        }
		Log.d(logname,(String) text);
	}

	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = new Messenger(service);

			// As part of the sample, tell the user what happened.
			showToastShort("SLAPI service connected");
			
			// send Hello from IGO client
			sendRequest(1);
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			showToastShort("SLAPI server disconnected.");
		}
	};

	public void doBindService() {
		// Establish a connection with the service.  We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		mCallingService.bindService(new Intent(SLAPIMessageIds.SLAPIServiceName), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		showToastShort("SLAPI binding.");
	}

	public void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (mService != null) {
				sendRequest(SLAPIMessageIds.SLAPI_CLIENT_BYE, mMessenger.hashCode());
			}

			// Detach our existing connection.
			mCallingService.unbindService(mConnection);
			mIsBound = false;
			showToastShort("SLAPI unbinding.");
		}
	}
	
	/** Sends a simple (2 ints) request to the server */
	public void sendRequest(int msgId, int wParam, int lParam) {
		Message msg = Message.obtain(null, msgId, wParam, lParam);
		sendRequest(msg);
	}
	
	/** Sends a simple (1 int) request to the server */
	public void sendRequest(int msgId, int wParam) {
		Message msg = Message.obtain(null, msgId, wParam, 0);
		sendRequest(msg);
	}
	
	/** Sends a simple (no args) request to the server */
	public void sendRequest(int msgId) {
		Message msg = Message.obtain(null, msgId);
		sendRequest(msg);
	}
	
	/** Send a byte array based request to the server */
	public void sendRequest(Message msg, byte[] cpd) {
		msg.getData().putByteArray("Bytes", cpd);

		sendRequest(msg);
	}

	/** Sends a request to the server */
	public void sendRequest(Message msg) {
		showToastShort("Sending " + SLAPIMessageIds.MessageName(msg.what)+ " to the SLAPI server.");
		msg.replyTo = mMessenger;

		try {
			if(mService != null)
			mService.send(msg);
//	    ((SLAPIClient) mCallingService).logMessage(msg);

		} catch (RemoteException e) {
			// There is nothing special we need to do if the service
			// has crashed.
		}
	}
}
