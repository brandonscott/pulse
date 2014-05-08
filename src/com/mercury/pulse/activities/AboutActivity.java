package com.mercury.pulse.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import com.example.pulse.R;

public class AboutActivity extends Activity {
	
	private TextView mTextViewBoringStuff;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		init();
	}
	
	public void init() {
		mTextViewBoringStuff = (TextView)findViewById(R.id.aboutfragment_boringstuff_references);
		mTextViewBoringStuff.setText(Html.fromHtml(""
				+ "<center><strong>License</strong></center><br />"
				+ "Pulse &copy; is released under XXX license.<br /><br />"
				+ "<strong>References</strong><br />"
				+ "Pulse implements the following 3rd party libraries & resources:<br />"
				+ "RoundedImageView.java,<br />"
				+ "Solar System Icons by Dan Wiersema,"));	
		mTextViewBoringStuff.setGravity(Gravity.CENTER);
	}

}
