package com.mercury.pulse.objects;

public class ServerGroup {

	private int serverGroupID;
	private String serverGroupName;
	
	public ServerGroup(int serverGroupID, String serverGroupName) {
		super();
		this.serverGroupID = serverGroupID;
		this.serverGroupName = serverGroupName;
	}
	
	public int getServerGroupID() {
		return serverGroupID;
	}

	public String getServerGroupName() {
		return serverGroupName;
	}
	
}