package com.mercury.pulse.testing;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;

import com.example.pulse.R;
import com.mercury.pulse.activities.LoginActivity;

public class LoginActivityUnitTest extends ActivityUnitTestCase<LoginActivity> {  

	private Intent mStartIntent;  
	private Button mLoginButton;
	private EditText mEmail, mPassword;

	public LoginActivityUnitTest() {  
		super(LoginActivity.class);  
	}  

	@Override  
	protected void setUp() throws Exception {  
		super.setUp();  

		mStartIntent = new Intent(Intent.ACTION_MAIN);  
	}

	@SmallTest  
	public void testGUIComponents() {
		LoginActivity activity = startActivity(mStartIntent, null, null);

		mLoginButton = (Button) activity.findViewById(R.id.button1); 
		assertNotNull("Button failed to instantiate", mLoginButton);
		mEmail = (EditText) activity.findViewById(R.id.email); 
		assertNotNull("Email field failed to instantiate", mEmail);
		mPassword = (EditText) activity.findViewById(R.id.password); 
		assertNotNull("Password field failed to instantiate", mPassword);
	}

	@MediumTest  
	public void testLifeCycleCreate() {  
		LoginActivity activity = startActivity(mStartIntent, null, null);
		
		getInstrumentation().callActivityOnStart(activity);  
		getInstrumentation().callActivityOnResume(activity); 
		getInstrumentation().callActivityOnPause(activity);  
		getInstrumentation().callActivityOnStop(activity);  
	}

}  