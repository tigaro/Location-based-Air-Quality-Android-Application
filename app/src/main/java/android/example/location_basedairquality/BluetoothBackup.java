package android.example.location_basedairquality;

import android.util.Log;

public class BluetoothBackup {
	
	private long nodeIDBackUp;
	private String AddressBackUp;
	
	private double temperatureBackUp, humidityBackUp, pressureBackUp, altitudeBackUp, gasBackUp;
	
	private String positivNegative, temperature, humidity, pressure, altitude, gas;
	private Character temperatureIndex, humidityIndex, pressureIndex, altitudeIndex, gasIndex;
	
	public BluetoothBackup(String code, long nodeID, String AddressBackUp) {
		//11T111H1111P11111A11111G1111
		//11T100H1000P10000A10000G1000
		positivNegative = code.substring(0,2).replaceAll("[^0-9]", "");
		temperatureIndex = code.charAt(2);
		temperature = code.substring(3,6).replaceAll("[^0-9]", "");
		humidityIndex = code.charAt(6);
		humidity = code.substring(7,11).replaceAll("[^0-9]", "");
		pressureIndex = code.charAt(11);
		pressure = code.substring(12,17).replaceAll("[^0-9]", "");
		altitudeIndex = code.charAt(17);
		altitude = code.substring(18,23).replaceAll("[^0-9]", "");
		gasIndex = code.charAt(23);
		gas = code.substring(24,28).replaceAll("[^0-9]", "");
		
		switch(temperatureIndex){
			case 't':
				this.temperatureBackUp = Double.parseDouble(temperature)/100;
				break;
			case 'T':
				this.temperatureBackUp = Double.parseDouble(temperature)/10;
				break;
			default:
				this.temperatureBackUp = 0;
		}
		
		switch(humidityIndex){
			case 'h':
				this.humidityBackUp = Double.parseDouble(humidity)/1000;
				break;
			case 'H':
				this.humidityBackUp = Double.parseDouble(humidity)/100;
				break;
			case 'X':
				this.humidityBackUp = 100.0;
				break;
			default:
				this.humidityBackUp = 0;
		}
		
		switch(pressureIndex){
			case 'P':
				this.pressureBackUp = Double.parseDouble(pressure)/10;
				break;
			case 'p':
				this.pressureBackUp = Double.parseDouble(pressure)/100;
				break;
			case 'c':
				this.pressureBackUp = Double.parseDouble(pressure)/1000;
				break;
			case 'd':
				this.pressureBackUp = Double.parseDouble(pressure)/10000;
				break;
			default:
				this.pressureBackUp = 0;
		}
		switch(altitudeIndex){
			case 'A':
				this.altitudeBackUp = Double.parseDouble(altitude)/10;
				break;
			case 'a':
				this.altitudeBackUp = Double.parseDouble(altitude)/100;
				break;
			case 'y':
				this.altitudeBackUp = Double.parseDouble(altitude)/1000;
				break;
			case 'z':
				this.altitudeBackUp = Double.parseDouble(altitude)/10000;
				break;
			default:
				this.altitudeBackUp = 0;
		}
		
		switch (gasIndex){
			case 'G':
				this.gasBackUp = Double.parseDouble(gas)/10;
				break;
			case 'g':
				this.gasBackUp = Double.parseDouble(gas)/100;
				break;
			case 'u':
				this.gasBackUp = Double.parseDouble(gas)/1000;
				break;
			default:
				this.gasBackUp = 0;
		}
		
		if(positivNegative.startsWith("1")){
			this.altitudeBackUp = altitudeBackUp*-1;
		}
		if(positivNegative.startsWith("1",1)){
			this.temperatureBackUp = temperatureBackUp*-1;
		}
		
		this.nodeIDBackUp = nodeID;
		this.AddressBackUp = AddressBackUp;
		
		String combined = positivNegative + "/" + temperatureIndex
				+ temperature + "/" + humidityIndex + humidity + "/"
				+ pressureIndex + pressure + "/" + altitudeIndex + altitude + "/"
				+ gasIndex + gas + "/";
		Log.e("Decoded completly", combined);
		
	}
	
	public long getNodeIDBackUp() {
		return nodeIDBackUp;
	}
	
	public void setAddressBackUp(String addressBackUp) {
		this.AddressBackUp = addressBackUp;
	}
	
	public double getTemperatureBackUp() {
		return temperatureBackUp;
	}
	
	public void setTemperatureBackUp(double temperatureBackUp) {
		this.temperatureBackUp = temperatureBackUp;
	}
	
	public double getHumidityBackUp() {
		return humidityBackUp;
	}
	
	public void setHumidityBackUp(double humidityBackUp) {
		this.humidityBackUp = humidityBackUp;
	}
	
	public double getPressureBackUp() {
		return pressureBackUp;
	}
	
	public void setPressureBackUp(double pressureBackUp) {
		this.pressureBackUp = pressureBackUp;
	}
	
	public double getAltitudeBackUp() {
		return altitudeBackUp;
	}
	
	public void setAltitudeBackUp(double altitudeBackUp) {
		this.altitudeBackUp = altitudeBackUp;
	}
	
	public double getGasBackUp() {
		return gasBackUp;
	}
	
	public void setGasBackUp(double gasBackUp) {
		this.gasBackUp = gasBackUp;
	}
}
