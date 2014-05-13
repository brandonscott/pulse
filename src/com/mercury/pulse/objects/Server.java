package com.mercury.pulse.objects;

public class Server {

	private int serverID, online;
	private String serverName, serverWindowsVersion, serverServicePack;
	
	public Server(int serverID, String serverName, String serverWindowsVersion) {
		super();
		this.serverID = serverID;
		this.serverName = serverName;
		this.serverWindowsVersion = serverWindowsVersion;
	}
	
	public Server(int serverID, String serverName, String serverWindowsVersion, String serverServicePack, int online) {
		super();
		this.serverID = serverID;
		this.serverName = serverName;
		this.serverWindowsVersion = serverWindowsVersion;
		this.serverServicePack = serverServicePack;
		this.online = online;
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
	
	public int isOnline() {
		return online;
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