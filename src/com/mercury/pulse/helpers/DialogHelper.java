package com.mercury.pulse.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

public class DialogHelper {
	/**
	 * calling this method makes a custom alert dialog box appear on screen
	 * 
	 * @param context; the calling activity is needed to be specified as context
	 * @param title; the text that goes in the title of the alert dialog
	 * @param message; the text that forms the body of the alert dialog
	 */
	public void createAlert(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
		.setMessage(Html.fromHtml(message))
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});		
		AlertDialog alert = builder.create();
		alert.show();

		TextView alertMessage = (TextView)alert.findViewById(android.R.id.message);
		alertMessage.setGravity(Gravity.CENTER_HORIZONTAL);
	}

	/**
	 * calling this method makes a custom alert dialog box appear on screen. This overloaded
	 * method allows an icon to be specified as well.
	 * 
	 * @param context; the calling activity is needed to be specified as context
	 * @param title; the text that goes in the title of the alert dialog
	 * @param message; the text that forms the body of the alert dialog
	 * @param icon; the icon that is displayed next to the title (this needs to be a drawable reference)
	 */
	public void createAlert(Context context, String title, String message, int icon) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
		.setMessage(Html.fromHtml(message))
		.setCancelable(false)
		.setIcon(icon)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});		
		AlertDialog alert = builder.create();
		alert.show();

		TextView alertMessage = (TextView)alert.findViewById(android.R.id.message);
		alertMessage.setGravity(Gravity.CENTER_HORIZONTAL);		
	}
}
