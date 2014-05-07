package com.mercury.pulse.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.pulse.R;
import com.mercury.pulse.views.PieChartView;
import com.mercury.pulse.views.SmallPieChartView;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubError;

public class ServerInfoActivity extends Activity {
	
	private Pubnub pubnub = new Pubnub("pub-c-18bc7bd1-2981-4cc4-9c4e-234d25519d36", "sub-c-5782df52-d147-11e3-93dd-02ee2ddab7fe");
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
		pubnub();
	}
	
	public void pubnub() {
		 try {
			   pubnub.subscribe("pulses", new Callback() {
			 
			       @Override
			       public void connectCallback(String channel, Object message) {
			           System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
			                      + " : " + message.getClass() + " : "
			                      + message.toString());
			       }
			 
			       @Override
			       public void disconnectCallback(String channel, Object message) {
			           System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
			                      + " : " + message.getClass() + " : "
			                      + message.toString());
			       }
			 
			       public void reconnectCallback(String channel, Object message) {
			           System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
			                      + " : " + message.getClass() + " : "
			                      + message.toString());
			       }
			 
			       @Override
			       public void successCallback(String channel, Object message) {
			           System.out.println("SUBSCRIBE : " + channel + " : "
			                      + message.getClass() + " : " + message.toString());
			       }
			 
			       @Override
			       public void errorCallback(String channel, PubnubError error) {
			           System.out.println("SUBSCRIBE : ERROR on channel " + channel
			                      + " : " + error.toString());
			       }
			     }
			   );
			 } catch (PubnubException e) {
			   System.out.println(e.toString());
			 }
	}

	
	public void updateData(){
		new UpdateData().execute();
	}

	public class UpdateData extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute(){
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
			mPieChart.setData(25);
			mPieChart2.setData(76);
			mPieChart3.setData(83);
			mPieChart.setVisibility(View.VISIBLE);
			mPieChart2.setVisibility(View.VISIBLE);
			mPieChart3.setVisibility(View.VISIBLE);
		}
	}


}
