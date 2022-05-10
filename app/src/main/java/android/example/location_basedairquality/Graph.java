package android.example.location_basedairquality;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;
import java.util.Date;


public class Graph {
	
	ArrayList<LineGraphSeries<DataPoint>> lineGraphSeriesArrayList = new ArrayList<>();
	LineGraphSeries<DataPoint> temperature = new LineGraphSeries<>();
	LineGraphSeries<DataPoint> humidity = new LineGraphSeries<>();
	LineGraphSeries<DataPoint> gas = new LineGraphSeries<>();
	LineGraphSeries<DataPoint> rssi = new LineGraphSeries<>();
	
	
	public Graph(){
		lineGraphSeriesArrayList.add(gas);
		lineGraphSeriesArrayList.add(rssi);
		lineGraphSeriesArrayList.add(temperature);
		lineGraphSeriesArrayList.add(humidity);
		
		temperature.setTitle("Temperatur");
		humidity.setTitle("Humidity");
		gas.setTitle("Gas");
		rssi.setTitle("RSSI");
	}
	
	public ArrayList<LineGraphSeries<DataPoint>> getLineGraphSeriesArrayList() {
		return lineGraphSeriesArrayList;
	}
	
	public void setTemperature(DataPoint dP) {
		temperature.appendData(dP, true, 100, false);
	}
	
	public void setHumidity(DataPoint dP) {
		humidity.appendData(dP, true, 100, false);
	}
	
	public void setGas(DataPoint dP) {
		gas.appendData(dP, true, 100, false);
	}
	
	public void setRssi(DataPoint dP) {
		rssi.appendData(dP, true, 100, false);
	}
}
