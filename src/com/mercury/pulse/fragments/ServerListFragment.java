package com.mercury.pulse.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pulse.R;
import com.mercury.pulse.activities.ServerInfoActivity;
import com.mercury.pulse.adapters.ServerListAdapter;
import com.mercury.pulse.objects.Server;


public class ServerListFragment extends Fragment implements OnItemClickListener {

	private ProgressBar							mProgressBar;
	private TextView							mTextView;
	private GridView							mGridView;
	private ArrayList<Server>					mServerList;
	private ServerListAdapter					mServerListAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		//inf.inflate(R.menu.loanslist, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		return false;
	}
	
	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroupContainer, Bundle savedInstanceState) {
		super.onCreateView(layoutInflater, viewGroupContainer, savedInstanceState);
		View v = layoutInflater.inflate(R.layout.fragment_serverlist, viewGroupContainer, false);
		mProgressBar = (ProgressBar)v.findViewById(R.id.serverlist_progressbar);
		mTextView = (TextView)v.findViewById(R.id.serverlist_textview);
		mGridView = (GridView)v.findViewById(R.id.serverlist_gridview);
		mGridView.setOnItemClickListener(this);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		new GetServers().execute();
	}

	private class GetServers extends AsyncTask<Void, Void, Exception> {
		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
			mGridView.setVisibility(GridView.INVISIBLE);
			mTextView.setVisibility(TextView.INVISIBLE);
			mServerList = new ArrayList<Server>();
		}
		
		@Override
		protected Exception doInBackground(Void... params) {
			try {
				Server newServer = new Server(1, "Win7");
				mServerList.add(newServer);
				newServer = new Server(2, "Win8");
				mServerList.add(newServer);
				newServer = new Server(3, "Win9");
				mServerList.add(newServer);
				newServer = new Server(4, "Win5");
				mServerList.add(newServer);
				newServer = new Server(5, "Win3");
				mServerList.add(newServer);
				newServer = new Server(7, "Win3");
				mServerList.add(newServer);
				newServer = new Server(6, "Win3");
				mServerList.add(newServer);				
			} catch (Exception e) {
				return e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Exception exception){
			mProgressBar.setVisibility(ProgressBar.INVISIBLE);
			if (exception != null) {
				Toast.makeText(getActivity().getBaseContext(), "Generic Exception Generated", Toast.LENGTH_LONG).show();
				Log.e("ServerListFragment", "Generic Exception generated");
				exception.printStackTrace();
			} else {
				if (mServerList == null || mServerList.size() < 1) {
					mTextView.setVisibility(TextView.VISIBLE);
					Log.i("ServerListFragment", "Server ArrayList empty");
				} else {
					try {
						mServerListAdapter = new ServerListAdapter(getActivity(), mServerList);
						mGridView.setAdapter(mServerListAdapter);
						mGridView.setVisibility(GridView.VISIBLE);
					} catch (Exception e){
						Toast.makeText(getActivity().getBaseContext(), "Generic Exception Generated", Toast.LENGTH_LONG).show();
						Log.e("ServerListFragment", "Generic Exception generated");
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(getActivity(), ServerInfoActivity.class);                      
		startActivity(i);
	}
}