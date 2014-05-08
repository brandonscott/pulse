package com.mercury.pulse.adapters;

import java.util.ArrayList;

import com.example.pulse.R;
import com.mercury.pulse.objects.ServerGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends ArrayAdapter<ServerGroup> {

	private ArrayList<ServerGroup>			mTitles;
	private int										mLayout;

	public NavDrawerListAdapter(Context context, int resource, ArrayList<ServerGroup> objects) {
		super(context, resource, objects);
		mTitles = objects;
		mLayout = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(mLayout, null);
		}
		TextView title = (TextView)v.findViewById(R.id.main_navitem_title);
		ImageView icon = (ImageView)v.findViewById(R.id.main_navitem_icon);
		title.setText(mTitles.get(position).getServerGroupName());
		icon.setImageResource(R.drawable.ic_action_person);
		return v;
	}
}