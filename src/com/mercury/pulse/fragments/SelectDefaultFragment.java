package com.mercury.pulse.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.example.pulse.R;


public class SelectDefaultFragment extends Fragment implements OnItemClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroupContainer, Bundle savedInstanceState) {
		super.onCreateView(layoutInflater, viewGroupContainer, savedInstanceState);
		View v = layoutInflater.inflate(R.layout.fragment_selectdefault, viewGroupContainer, false);
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}