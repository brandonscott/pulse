package com.mercury.pulse.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.example.pulse.R;
import com.mercury.pulse.views.PieChartView;
import com.mercury.pulse.views.SmallPieChartView;

public class ServerInfoActivity extends Activity {
	
	private PieChartView					mPieChart;
	private SmallPieChartView 				mPieChart2, mPieChart3;
	
	@Override
	/**
	 * override onCreate to provide out layout file and execute init()
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverinfo);
		
		mPieChart = (PieChartView) findViewById(R.id.stats_piechart);
		mPieChart2 = (SmallPieChartView) findViewById(R.id.stats_piechart2);
		mPieChart3 = (SmallPieChartView) findViewById(R.id.stats_piechart3);

		updateData();
	}

	
	public void updateData(){
		new UpdateData().execute();
	}

	public class UpdateData extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute(){
			//mProgress.setVisibility(ProgressBar.VISIBLE);
			//mAverageReturn.setVisibility(TextView.GONE);
			//mAverageReturnLbl.setVisibility(TextView.GONE);
			//mPieChartLbl.setVisibility(TextView.GONE);
			mPieChart.setVisibility(View.GONE);
			mPieChart2.setVisibility(View.GONE);
			mPieChart3.setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void no){
			//mProgress.setVisibility(ProgressBar.INVISIBLE);
			//mAverageReturnLbl.setVisibility(TextView.VISIBLE);
			//mAverageReturnLbl.setText("No data could be generated");
			//mAverageReturn.setText("Days Without Issue");
			//mAverageReturnLbl.setText("Days Without Issue");
			//mAverageReturnLbl.setVisibility(TextView.VISIBLE);
			//mAverageReturn.setText(String.format(getActivity().getResources().getString(R.string.stats_averageloan_value),
					//mAverageReturnAmount));
			//mPieChartLbl.setVisibility(TextView.VISIBLE);
			mPieChart.setData(25);
			mPieChart2.setData(76);
			mPieChart3.setData(83);
			mPieChart.setVisibility(View.VISIBLE);
			mPieChart2.setVisibility(View.VISIBLE);
			mPieChart3.setVisibility(View.VISIBLE);
		}
	}


}
