package il.co.isrcorp.publictransportationintegration;


import il.co.isrcorp.publictransportationintegration.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class WaitForSpmDialog extends ProgressDialog {
	Context context;
	public WaitForSpmDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("progress on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_spm_dialog);
		setCancelable(false);
		  setIndeterminate(true);		
		  
		  TextView tv = (TextView)findViewById(R.id.waitForSPMdialogtitle);
		  tv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Intent launchHome = new Intent(Intent.ACTION_MAIN);
		           launchHome.addCategory(Intent.CATEGORY_HOME);
		           launchHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		           context.startActivity(launchHome);
				return false;
			}
		});
	}


}
