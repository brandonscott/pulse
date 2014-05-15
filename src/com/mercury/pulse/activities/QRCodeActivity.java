package com.mercury.pulse.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.pulse.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mercury.pulse.helpers.DialogHelper;

public class QRCodeActivity extends Activity implements OnClickListener {
	private DialogHelper dialogbox = new DialogHelper();
	private Button scanBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);

		init();
	}
	
	@Override
	public void onBackPressed() {
	    finish();
	    Intent i = new Intent(getBaseContext(), MainActivity.class);                      
	    startActivity(i);
	}

	private void init() {
		scanBtn = (Button)findViewById(R.id.btn_scan_button);
		scanBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		//check for scan button
		if(v.getId()==R.id.btn_scan_button) {
			//instantiate ZXing
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve result of scanning - instantiate ZXing object
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		//check we have a valid result
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			int serverID;

			if (scanContent != null) {
				try {
					serverID = Integer.parseInt(scanContent);
				} catch (NumberFormatException e) {
					//invalid QR code
					dialogbox.createAlert(this, "Oh Noes!", "Invalid QR code! Please try again");
					return;
				}

				//launch ServerInfoActivity for this QR code
				Bundle b = new Bundle();
				b.putInt("SERVERID", serverID);
				Intent i = new Intent(this, ServerInfoActivity.class);
				i.putExtras(b);
				startActivity(i);
			}
		}
	}

}
