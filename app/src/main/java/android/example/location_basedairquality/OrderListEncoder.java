package android.example.location_basedairquality;

public class OrderListEncoder {
	
	String title = "";
	
	String rssiTopMostDesc = "Closest Node:";
	String rssiTopMostAsc = "Furthest Node:";
	String temperatureTopMost = "Highest Temperature Node:";
	String humidityTopMost = "Highes Humidity Node:";
	String gasTopMost = "Highest Gas Node:";
	String pressureTopMost = "Highest Pressure Node:";
	
	String sortedBy = "Nodes sorted by ";
	String orderASC = "Ascending";
	String orderDESC = "Descending";
	String rssi = "RSSI ";
	String temperature = "Temperature ";
	String humidity = "Humidity ";
	String gas = "Gas ";
	String pressure = "Pressure ";
	String altitude = "Altitude ";
	
	String bleId = "Bluetooth ID ";
	String lastSeenBLE = "RSSI Update ";
	String lastNodeUpdated = "Airquality Update ";
	
	public String getTitle(int recyclerview, String column, String order) {
		switch (recyclerview) {
			case 0:
				if (column.equals("rssi")) {
					if (order.equals("DESC")) {
						return rssiTopMostAsc;
					} else {
						return rssiTopMostDesc;
					}
				} else if (column.equals("temperature")) {
					return temperatureTopMost;
				} else if (column.equals("humidity")) {
					return humidityTopMost;
				} else if (column.equals("gas")) {
					return gasTopMost;
				} else if (column.equals("pressure")) {
					return pressureTopMost;
				}
			case 1:
				this.title = sortedBy;
				if (column.equals("rssi")) {
					if (order.equals("ASC")) {
						return title + rssi + orderASC;
					} else {
						return title + rssi + orderDESC;
					}
				} else if (column.equals("pressure")) {
					if (order.equals("ASC")) {
						return title + pressure + orderASC;
					} else {
						return title + pressure + orderDESC;
					}
				} else if (column.equals("gas")) {
					if (order.equals("ASC")) {
						return title + gas + orderASC;
					} else {
						return title + gas + orderDESC;
					}
				} else if (column.equals("humidity")) {
					if (order.equals("ASC")) {
						return title + humidity + orderASC;
					} else {
						return title + humidity + orderDESC;
					}
					
				} else if (column.equals("temperature")) {
					if (order.equals("ASC")) {
						return title + temperature + orderASC;
					} else {
						return title + temperature + orderDESC;
					}
				} else if (column.equals("altitude")){
					if(order.equals("ASC")){
						return title + altitude + orderASC;
					} else {
						return title + altitude + orderDESC;
					}
				}
			default:
				return "Error in Sorting";
		}
	}
}



