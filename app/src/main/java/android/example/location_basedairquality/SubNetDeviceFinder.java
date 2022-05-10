package android.example.location_basedairquality;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import java.util.ArrayList;
import java.util.Objects;

public class SubNetDeviceFinder {
	
	private static SubNetDeviceFinder instance;
	Thread thread;
	SubnetDevices subnetDevices;
	String foundSubnetIp;
	MutableLiveData<Device> subnetDevicesMutableLiveData = new MutableLiveData<Device>();
	
	private SubNetDeviceFinder() {
		SubnetDeviceStartSearch();
	}
	
	public static SubNetDeviceFinder getInstance() {
		if (instance == null) {
			instance = new SubNetDeviceFinder();
		}
		return instance;
	}
	
	public MutableLiveData<Device> getSubnetDevicesMutableLiveData() {
		return subnetDevicesMutableLiveData;
	}
	
	public String getFoundSubnetIp() {
		return foundSubnetIp;
	}
	
	public void setFoundSubnetIp(String foundSubnetIp) {
		this.foundSubnetIp = foundSubnetIp;
	}
	
	public void SubnetDeviceStartSearch() {
		subnetDevices = SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
			@Override
			public void onDeviceFound(Device device) {
				if (device.hostname.startsWith("WS-MCUDEVICE")) {
					Log.e("Device found:", device.hostname);
				}
			}
			
			public void onFinished(ArrayList<Device> devicesFound) {
				thread = new Thread(() -> {
					try {
						for (Device device : devicesFound) {
							if (Objects.requireNonNull(device.hostname.startsWith("WS-MCUDEVICE"))) {
								subnetDevicesMutableLiveData.postValue(device);
								foundSubnetIp = device.ip;
								Log.e("Subnet:", "updated");
								thread.interrupt();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				thread.start();
			}
		});
	}
}




