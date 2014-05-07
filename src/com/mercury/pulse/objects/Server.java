package com.mercury.pulse.objects;

public class Server {

	private int serverID;
	private String serverName;
	
	public Server(int serverID, String serverName) {
		super();
		this.serverID = serverID;
		this.serverName = serverName;
	}
	
	public int getServerID() {
		return serverID;
	}

	public String getServerName() {
		return serverName;
	}
	
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
}