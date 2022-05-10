package android.example.location_basedairquality;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "node_table")
public class Node {
	
	@PrimaryKey(autoGenerate = true)
	private int id;
	
	private long nodeID, lastSeenBLE, lastNodeUpdate;
	private double temperature, humidity, pressure, altitude, gas;
	private String bleID;
	private int RSSI;

	public Node(long nodeID, double temperature, double humidity, double pressure, double altitude, double gas, String bleID, int RSSI, long lastNodeUpdate, long lastSeenBLE) {
		this.nodeID = nodeID;
		this.temperature = temperature;
		this.humidity = humidity;
		this.pressure = pressure;
		this.altitude = altitude;
		this.gas = gas;
		this.bleID = bleID;
		this.RSSI = RSSI;
		this.lastSeenBLE = lastSeenBLE;
		this.lastNodeUpdate = lastNodeUpdate;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public long getNodeID() {
		return nodeID;
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	public double getHumidity() {
		return humidity;
	}
	
	public double getPressure() {
		return pressure;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	public double getGas() {
		return gas;
	}
	
	public String getBleID() {
		return bleID;
	}
	
	public int getRSSI() {
		return RSSI;
	}
	
	public long getLastSeenBLE() {
		return lastSeenBLE;
	}
	
	public long getLastNodeUpdate() {
		return lastNodeUpdate;
	}
	
	public void setBleID(String bleID) {
		this.bleID = bleID;
	}
}
