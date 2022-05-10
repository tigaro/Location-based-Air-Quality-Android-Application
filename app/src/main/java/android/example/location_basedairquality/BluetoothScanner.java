package android.example.location_basedairquality;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BluetoothScanner {
	
	private static BluetoothScanner instance;
	
	private MainActivity mainActivity;
	private String deviceCallbackName, deviceCallbackAddress;
	private Bluetooth deviceCallbackValue;
	private BluetoothAdapter BluetoothAdapter;
	private int deviceCallbackRssi;
	MutableLiveData<List<Bluetooth>> deviceMutableLiveDataList = new MutableLiveData<List<Bluetooth>>();
	private List<Bluetooth> deviceList = new ArrayList<Bluetooth>();
	MutableLiveData<Boolean> isScanning = new MutableLiveData<Boolean>(false);
	private boolean inListBluetoothStandard;
	
	
	private List<BluetoothBackup> backUpDeviceList = new ArrayList<BluetoothBackup>();
	private String backUpDeviceCallbackName, BackUpDeviceCallbackAddress;
	private BluetoothBackup backupDeviceCallbackValue;
	MutableLiveData<List<BluetoothBackup>> backUpDeviceMutableLiveDataList = new MutableLiveData<List<BluetoothBackup>>();
	private BluetoothLeScanner backUpScanner;
	private String decodedPayload;
	boolean inListBluetoothBackUp;
	private String trimmedPayload;
	private BluetoothAdapter backUpBluetoothAdapter;
	
	private Handler mHandler;
	
	ScanCallback scanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			super.onScanResult(callbackType, result);
			Thread thread = new Thread(() -> {
				try{
					if (result.getDevice() != null && result.getDevice().getName() != null && result.getDevice().getName().startsWith("Node")) {
						deviceCallbackName = result.getDevice().getName();
						deviceCallbackRssi = result.getRssi();
						deviceCallbackAddress = result.getDevice().getAddress();
						Log.e("BLE", deviceCallbackAddress);
						deviceCallbackValue = new Bluetooth(deviceCallbackName, deviceCallbackRssi, deviceCallbackAddress);
						inListBluetoothStandard = false;
						
						if (deviceList.isEmpty()) {
							deviceList.add(deviceCallbackValue);
							deviceMutableLiveDataList.postValue(deviceList);
						} else {
							for (Bluetooth devices : deviceList) {
								if (devices.getBleId().equals(deviceCallbackName)) {
									inListBluetoothStandard = true;
									if (devices.getRSSI() != deviceCallbackValue.getRSSI()) {
										devices.setRSSI(deviceCallbackValue.getRSSI());
										deviceMutableLiveDataList.postValue(deviceList);
									}
								}
							}
							if (inListBluetoothStandard == false) {
								deviceList.add(deviceCallbackValue);
								deviceMutableLiveDataList.postValue(deviceList);
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			});
			thread.start();
		}
		
		@Override
		public void onBatchScanResults(List<ScanResult> results) {
			super.onBatchScanResults(results);
			for (ScanResult result : results) {
				if (result.getDevice().getName() != null && result.getDevice().getName().startsWith("Node")) {
					Log.e("BLE NEUUUU:", result.getDevice().getName());
				}
			}
		}
		
		@Override
		public void onScanFailed(int errorCode) {
			super.onScanFailed(errorCode);
		}
	};
	
	private BluetoothAdapter.LeScanCallback ScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
			Thread thread = new Thread(() -> {
				try {
					if (bluetoothDevice != null && bluetoothDevice.getName() != null && (bluetoothDevice.getName().startsWith("Node") || bluetoothDevice.getName().startsWith("Test"))) {
						decodedPayload = new String(bytes, "UTF-8");
						if (decodedPayload.contains("Node")) {
							trimmedPayload = decodedPayload.substring(2, 30);
							backUpDeviceCallbackName = bluetoothDevice.getName().
									replaceAll("[^0-9]", "");
							BackUpDeviceCallbackAddress = bluetoothDevice.getAddress();
							backupDeviceCallbackValue = new BluetoothBackup(trimmedPayload,
									Long.valueOf(bluetoothDevice.getName().
											replaceAll("[^0-9]", "")), BackUpDeviceCallbackAddress);
							inListBluetoothBackUp = false;
							
							if (backUpDeviceList.isEmpty()) {
								backUpDeviceList.add(backupDeviceCallbackValue);
								backUpDeviceMutableLiveDataList.postValue(backUpDeviceList);
							} else {
								List<BluetoothBackup> copylist = new ArrayList<BluetoothBackup>(backUpDeviceList);
								for (BluetoothBackup BackUpdevices : copylist) {
									if (String.valueOf(BackUpdevices.getNodeIDBackUp()).equals(backUpDeviceCallbackName)) {//
										inListBluetoothBackUp = true;
										if (BackUpdevices.getTemperatureBackUp() != backupDeviceCallbackValue.getTemperatureBackUp() ||
												BackUpdevices.getHumidityBackUp() != backupDeviceCallbackValue.getHumidityBackUp() ||
												BackUpdevices.getPressureBackUp() != backupDeviceCallbackValue.getPressureBackUp() ||
												BackUpdevices.getAltitudeBackUp() != backupDeviceCallbackValue.getAltitudeBackUp() ||
												BackUpdevices.getGasBackUp() != backupDeviceCallbackValue.getGasBackUp()) {
											
											BackUpdevices.setTemperatureBackUp(backupDeviceCallbackValue.getTemperatureBackUp());
											BackUpdevices.setHumidityBackUp(backupDeviceCallbackValue.getHumidityBackUp());
											BackUpdevices.setPressureBackUp(backupDeviceCallbackValue.getPressureBackUp());
											BackUpdevices.setAltitudeBackUp(backupDeviceCallbackValue.getAltitudeBackUp());
											BackUpdevices.setGasBackUp(backupDeviceCallbackValue.getGasBackUp());
											BackUpdevices.setAddressBackUp(BackUpDeviceCallbackAddress);
											
											backUpDeviceMutableLiveDataList.postValue(backUpDeviceList);
										}
									}
								}
								if (inListBluetoothBackUp == false) {
									backUpDeviceList.add(backupDeviceCallbackValue);
									backUpDeviceMutableLiveDataList.postValue(backUpDeviceList);
									Log.e("BLE", "In List added");
								}
							}
							
						}
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			});
			thread.start();
		}
	};
	
	private BluetoothScanner(MainActivity activity) {
		mainActivity = activity;
		mHandler = new Handler();
		final BluetoothManager bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
		backUpBluetoothAdapter = bluetoothManager.getAdapter();
		backUpScanner = backUpBluetoothAdapter.getBluetoothLeScanner();
		BluetoothAdapter = bluetoothManager.getAdapter();
	}
	
	public static BluetoothScanner getInstance(MainActivity activity) {
		if (instance == null) {
			instance = new BluetoothScanner(activity);
		}
		return instance;
	}
	
	public void discovery() {
		if (isScanning.getValue() == false) {
			isScanning.postValue(true);/*
			mHandler.postDelayed(() -> {
				backUpScanner.stopScan(scanCallback);
				BluetoothAdapter.stopLeScan(ScanCallback);
				isScanning.postValue(false);
			}, 100000); //5000*/
			backUpScanner.startScan(scanCallback);
			BluetoothAdapter.startLeScan(ScanCallback);
		}
	}
}
