package com.mercury.pulse.objects;

public class NavDrawerListItem {

	private int icon;
	private String title;

	public NavDrawerListItem(int iconResource, String title){
		this.icon = iconResource;
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public int getIconResource(){
		return icon;
	}
}