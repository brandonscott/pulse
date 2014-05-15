package com.mercury.pulse.adapters;

import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pulse.R;
import com.mercury.pulse.objects.Server;


public class ServerListAdapter extends ArrayAdapter<Server> {

	private ArrayList<Server> mServers;

	public ServerListAdapter(Context context, ArrayList<Server> objects) {
		super(context, R.layout.fragment_serverlistitem, objects);
		mServers = objects;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fragment_serverlistitem, null); //specify layout for individual server objects
		}
		Server server = mServers.get(position);
		if (server != null) {
			//set textview to server name
			TextView name = (TextView)v.findViewById(R.id.serverlist_servername);
			name.setText(server.getServerName());

			ImageView icon = (ImageView)v.findViewById(R.id.serverlist_imageborder);
			//set icon to windows logo
			
			if (server.getServerWindowsVersion().toLowerCase().contains("linux")) {
				icon.setImageResource(R.drawable.linuxlogo2);
			} else {
				icon.setImageResource(R.drawable.winlogo);
			}			

			v.setTag(server);
		}
		return v;
	}

}