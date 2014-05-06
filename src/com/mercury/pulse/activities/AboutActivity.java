package com.mercury.pulse.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
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
		mTextViewBoringStuff = (TextView)findViewById(R.id.aboutfragment_boringstuff);
		mTextViewBoringStuff.setText(Html.fromHtml(""
				+ "<strong>License</strong><br />"
				+ "Pulse &copy; is release under XXX license.<br /><br />"
				+ "<strong>References</strong><br />"
				+ "Pulse implements the following 3rd party libraries & resources:<br />"
				+ "RoundedImageView.java,<br />"
				+ "Solar System Icons by Dan Wiersema,"));	
	}

}
