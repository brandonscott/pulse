package com.mercury.pulse.adapters;

import java.util.ArrayList;

import com.example.pulse.R;
import com.mercury.pulse.objects.NavDrawerListItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerListItem> {

	private ArrayList<NavDrawerListItem>			mTitles;
	private int										mLayout;

	public NavDrawerListAdapter(Context context, int resource, ArrayList<NavDrawerListItem> objects) {
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
		Log.d("NavDrawerListAadapter", "Setting title to "+mTitles.get(position).getTitle());
		Log.d("NavDrawerListAadapter", "Setting icon to "+Integer.toString(mTitles.get(position).getIconResource()));
		title.setText(mTitles.get(position).getTitle());
		icon.setImageResource(mTitles.get(position).getIconResource());
		return v;
	}
}