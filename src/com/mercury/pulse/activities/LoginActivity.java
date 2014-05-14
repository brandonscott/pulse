package com.mercury.pulse.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pulse.R;
import com.mercury.pulse.helpers.ConnectionHelper;
import com.mercury.pulse.helpers.PreferencesHandler;
import com.mercury.pulse.objects.JSONServiceHandler;

public class LoginActivity extends Activity {

	//API URL to parse our JSON list of servers from
	private String loginURL = "http://cadence-bu.cloudapp.net/auth";
	//JSON Pulse Node names
	private static final String JSON_USERID = "id";
	//define some instance variables
	private TextView mUsername, mPassword, mLoginStatus;
	private Button mSignIn;
	private ProgressBar mSpinner;
	//create a preferences handler
	private PreferencesHandler preferencesHandler = new PreferencesHandler();
	private ConnectionHelper connectionDetector = new ConnectionHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//ewww saving password in sharedPreferences ***I FEEL DIRTY***
		preferencesHandler.savePreference(this, "userid", null);
		preferencesHandler.savePreference(this, "username", null);
		preferencesHandler.savePreference(this, "password", null);
	}

	public void sign_in_button_handler(View view) {
		if (connectionDetector.isConnected()) {
			new LoginAuth().execute();
		} else {
			Toast.makeText(getApplicationContext(), "No network connection!", Toast.LENGTH_LONG).show();
		}		
	}

	public class LoginAuth extends AsyncTask<Void, Void, Void> {
		private boolean loginSuccess = false;
		@Override
		protected void onPreExecute() {
			mUsername = (TextView) findViewById (R.id.email);
			mPassword = (TextView) findViewById (R.id.password);
			mLoginStatus = (TextView) findViewById (R.id.loginstatus);
			mSpinner = (ProgressBar) findViewById (R.id.loginactivity_progressbar);
			mSignIn = (Button) findViewById (R.id.button1);

			mSpinner.setVisibility(View.VISIBLE);
			mSignIn.setEnabled(false);
			mLoginStatus.setText("Logging In...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			//parse the latest Pulse update
			try {
				//creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();
				//making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(loginURL, JSONServiceHandler.GET, mUsername.getText().toString(), mPassword.getText().toString());
				Log.d("Latest Pulse: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						JSONObject obj= new JSONObject(jsonStr);
						try {
							if (obj.getInt(JSON_USERID) != 0) {
								loginSuccess = true;
								preferencesHandler.savePreference(getApplicationContext(), "userid", obj.getInt(JSON_USERID));
								Log.e("obj.getInt(JSON_USERID)", obj.getInt(JSON_USERID)+"");
								preferencesHandler.savePreference(getApplicationContext(), "username", mUsername.getText().toString());
								preferencesHandler.savePreference(getApplicationContext(), "password", mPassword.getText().toString());

								Intent i = new Intent(getBaseContext(), MainActivity.class);                      
								startActivity(i);
								finish();
							}
						} catch (Exception e) {
							Log.e("LoginAuth", "Parsing login auth JSON failed");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void no) {
			if (loginSuccess != true) {
				mSpinner.setVisibility(View.INVISIBLE);
				mSignIn.setEnabled(true);
				mLoginStatus.setText("");
				mPassword.requestFocus();
				Toast.makeText(getApplicationContext(), "Login Incorrect", Toast.LENGTH_SHORT).show();
			}
		}
	}
}