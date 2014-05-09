
package com.mercury.pulse.objects;

import java.util.concurrent.TimeUnit;

public class Pulse {
	
	private int pulseID;
	private int serverID;
	private int ram_usage;
	private int cpu_usage;
	private int disk_usage;
	private int uptime;
	private int timestamp;
	
	public Pulse(int pulseID, int serverID, int ram_usage, int cpu_usage, int disk_usage, int uptime, int timestamp) {
		super();
		this.pulseID = pulseID;
		this.serverID = serverID;
		this.ram_usage = ram_usage;
		this.cpu_usage = cpu_usage;
		this.disk_usage = disk_usage;
		this.uptime = uptime;
		this.timestamp = timestamp;
	}
	
	public int getPulseID() {
		return pulseID;
	}
	
	public int getServerID() {
		return serverID;
	}
	
	public int getCPUUsage() {
		return cpu_usage;
	}
	
	public int getRAMUsage() {
		return ram_usage;
	}
	
	public int getHDDUsage() {
		return disk_usage;
	}
	
	public String getUptime() {
		return calculateTime(uptime);
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	/**
	 * converts second into hours, minutes, and seconds (http://stackoverflow.com/a/11358616)
	 * @author Keppil
	 * @param seconds seconds to break down into separate figures
	 * @return String of days hours minutes
	 */
	public static String calculateTime(long seconds) {
	    int day = (int) TimeUnit.SECONDS.toDays(seconds);
	    long hours = TimeUnit.SECONDS.toHours(seconds) -
	                 TimeUnit.DAYS.toHours(day);
	    long minute = TimeUnit.SECONDS.toMinutes(seconds) - 
	                  TimeUnit.DAYS.toMinutes(day) -
	                  TimeUnit.HOURS.toMinutes(hours);
	    return (day + " days " + hours + " hours " + minute + " minutes");
	}

}