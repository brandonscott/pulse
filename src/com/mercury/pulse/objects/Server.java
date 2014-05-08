package com.mercury.pulse.objects;

public class Server {

	private int serverID;
	private String serverName, serverWindowsVersion, serverServicePack;
	
	public Server(int serverID, String serverName) {
		super();
		this.serverID = serverID;
		this.serverName = serverName;
	}
	
	public Server(int serverID, String serverName, String serverWindowsVersion, String serverServicePack) {
		super();
		this.serverID = serverID;
		this.serverName = serverName;
		this.serverWindowsVersion = serverWindowsVersion;
		this.serverServicePack = serverServicePack;
	}
	
	public int getServerID() {
		return serverID;
	}

	public String getServerName() {
		return serverName;
	}
	
	public String getServerWindowsVersion() {
		return serverWindowsVersion;
	}
	
	public String getServicePack() {
		return serverServicePack;
	}
	
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}
	
	public void setWindowsVersion(String serverWindowsVersion) {
		this.serverWindowsVersion = serverWindowsVersion;
	}
	
	public void setServicePack(String serverServicePack) {
		this.serverServicePack = serverServicePack;
	}
}