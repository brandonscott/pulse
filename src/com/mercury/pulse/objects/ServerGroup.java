package com.mercury.pulse.objects;

public class ServerGroup {

	private int serverGroupID;
	private String serverGroupName;
	private int icon;
	
	public ServerGroup(int serverGroupID, String serverGroupName, int icon) {
		super();
		this.serverGroupID = serverGroupID;
		this.serverGroupName = serverGroupName;
		this.icon = icon;
	}
	
	public int getServerGroupID() {
		return serverGroupID;
	}

	public String getServerGroupName() {
		return serverGroupName;
	}
	
	public int getIcon() {
		return icon;
	}
	
}