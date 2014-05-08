package com.mercury.pulse.activities;

import java.text.DecimalFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.example.pulse.R;

public class GraphActivity extends Activity {
	//week in 30 second segments = 20160
	private final static int WEEK = 20160;
	private GraphView graphView;

	double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
	
	private void selectRange(int date, GraphView graphView) {
		//view port for hour
		if (date == 0) {
			//set viewport at end of range over an hour
			graphView.setViewPort(20040,120);
		}
		//view port for day
		else if (date == 1) {
			//set viewport at end of range over a day
			graphView.setViewPort(17280,2880);
		}
		//view port for week
		else if (date == 2) {
			graphView.setViewPort(0,20160);
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		Intent intent = getIntent();
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		final java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(this);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.date_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,
					long id) {
				selectRange(pos,graphView);
				view.invalidate();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		GraphViewData[] data = new GraphViewData[WEEK];
		for (int i=0; i<WEEK; i++) {
			data[i] = new GraphViewData(i, Math.floor(Math.random()*100)+1);
		}
		graphView = new LineGraphView(
				this
				, intent.getStringExtra("title")+" Usage"
				);
		//set vertical label number to increments of 10
		graphView.getGraphViewStyle().setNumVerticalLabels(11);
		// add data
		graphView.addSeries(new GraphViewSeries(data));
		//selectRange(2,graphView);
		//change number to time
		graphView.setCustomLabelFormatter(new CustomLabelFormatter () {
			public String formatLabel(double value, boolean isValueX) {
				if(isValueX) {
					return dateTimeFormatter.format(new Date((long)value*30000));
				}
				return null; //let graphview generate Y-axis label for us
			}

		});
		graphView.setScrollable(true);
		// optional - activate scaling / zooming
		graphView.setScalable(true);
		graphView.setManualYAxisBounds(100.0, 0.0);
		 
		LinearLayout weekLayout = (LinearLayout) findViewById(R.id.graph);
		weekLayout.addView(graphView);

	}
	
	

}
