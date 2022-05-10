package android.example.location_basedairquality;

public class Bluetooth {
	
	private String bleId, address;
	private int rssi;
	
	public Bluetooth(String bleID, int rssi, String address) {
		this.bleId = bleID;
		this.rssi = rssi;
		this.address = address;
	}
	
	public String getBleId() {
		return bleId;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setRSSI(int rssi) {
		this.rssi = rssi;
	}
	
	public int getRSSI() {
		return rssi;
	}
}

