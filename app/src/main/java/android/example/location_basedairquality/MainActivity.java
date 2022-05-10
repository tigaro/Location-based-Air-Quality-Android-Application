package android.example.location_basedairquality;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.stealthcopter.networktools.subnet.Device;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	
	List<Node> TopMostNode;
	ServerReceivedMessages serverReceivedMessages = new ServerReceivedMessages();
	SubNetDeviceFinder subNetDeviceFinder;
	private NodeViewModel nodeViewModelAllNodes, nodeViewModelTopMostNode;
	private OrderClass orderTopMostNode, orderAllNodes;
	private Handler subnetSearchHandler = new Handler();
	private Handler bluetoothHandler = new Handler();
	int permissionCheck;
	OrderListEncoder encoder;
	RecyclerView recyclerViewTopMostNode, recyclerViewAllNodes;
	NodeAdapter adapterTopMostNode, adapterAllNodes;
	BluetoothScanner scannerBtle;
	
	ImageView wifiImageOn, wifiImageOff, bluetoothImageOn, bluetoothImageOff, mainMenu;
	TextView textViewSingleTopMostNode, textViewAllNodes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "BLE not supported", Toast.LENGTH_LONG).show();
			finish();
		}
		permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
			} else {
				requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
			}
		}
		
		wifiImageOn = findViewById(R.id.wifiOn);
		wifiImageOff = findViewById(R.id.wifiOff);
		bluetoothImageOn = findViewById(R.id.bleIconOn);
		bluetoothImageOff = findViewById(R.id.bleIconoff);
		textViewSingleTopMostNode = findViewById(R.id.TopMostNodeCharacteristic);
		textViewAllNodes = findViewById(R.id.AllAvailableNodesCharacteristic);
		
		orderTopMostNode = new OrderClass();
		orderAllNodes = new OrderClass();
		encoder = new OrderListEncoder();
		
		mainMenu = findViewById(R.id.menuIconMainActivity);
		mainMenu.setOnClickListener(view -> {
			
			PopupMenu popup = new PopupMenu(getApplicationContext(), view);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.mainactivity_menu, popup.getMenu());
			popup.show();
			popup.setOnMenuItemClickListener(menuItem -> {
				switch (menuItem.getItemId()) {
					case R.id.deleteAllNodes:
						nodeViewModelAllNodes.deleteAllNodes();
						Toast.makeText(MainActivity.this, "All Nodes deleted", Toast.LENGTH_LONG).show();
						return true;
					default:
						return false;
				}
			});
		});
		
		recyclerViewTopMostNode = findViewById(R.id.recycler_view_topMostNode);
		recyclerViewTopMostNode.setLayoutManager(new LinearLayoutManager(this));
		recyclerViewTopMostNode.setHasFixedSize(true);
		
		adapterTopMostNode = new NodeAdapter(1, orderTopMostNode);
		recyclerViewTopMostNode.setAdapter(adapterTopMostNode);
		nodeViewModelTopMostNode = ViewModelProviders.of(this).get(NodeViewModel.class);
		
		nodeViewModelTopMostNode.getAllNodes().observe(this, nodes -> {
			Thread thread = new Thread(() -> {
				if (nodes.isEmpty()) {
					adapterTopMostNode.submitList(nodeViewModelAllNodes.getAllNodesSorted(orderTopMostNode.getOrderType(), orderTopMostNode.getIsAscDesc()));
				} else {
					TopMostNode = nodeViewModelAllNodes.getAllNodesSorted(orderTopMostNode.getOrderType(), orderTopMostNode.getIsAscDesc()).subList(0, 1);
					adapterTopMostNode.submitList(TopMostNode);
				}
			});
			thread.start();
		});
		
		recyclerViewAllNodes = findViewById(R.id.recycler_view_allNodes);
		recyclerViewAllNodes.setLayoutManager(new LinearLayoutManager(this));
		recyclerViewAllNodes.setHasFixedSize(true);
		
		adapterAllNodes = new NodeAdapter(orderAllNodes);
		recyclerViewAllNodes.setAdapter(adapterAllNodes);
		nodeViewModelAllNodes = ViewModelProviders.of(this).get(NodeViewModel.class);

		nodeViewModelAllNodes.getAllNodes().observe(this, nodes -> {
			Thread thread = new Thread(() -> {
				adapterAllNodes.submitList(nodeViewModelAllNodes.getAllNodesSorted(orderAllNodes.getOrderType(), orderAllNodes.getIsAscDesc()));
				Log.e("OnChangeNode", orderTopMostNode.getOrderType());
				Log.e("OnChangeNode", orderTopMostNode.getIsAscDesc());
			});
			thread.start();
			
		});
		
		subNetDeviceFinder = SubNetDeviceFinder.getInstance();
		if(subNetDeviceFinder.foundSubnetIp == null){
			reSearchforSubnet(subNetDeviceFinder.foundSubnetIp);
		}
		
		subNetDeviceFinder.getSubnetDevicesMutableLiveData().observe(this, device -> {
			if(!subNetDeviceFinder.getFoundSubnetIp().equals("")){
				try {
					Log.e("Device2", device.ip);
					Log.e("Device2", device.hostname);
					serverReceivedMessages = ServerReceivedMessages.getInstance("http://" + device.ip + "/events", getApplication(), MainActivity.this, adapterAllNodes, adapterTopMostNode);
					serverReceivedMessages.requestBuilder();
					serverReceivedMessages.startServerSentListener();
					subnetSearchHandler.removeCallbacksAndMessages(null);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("Subnet:", "no Subnetdevice in list!");
				}
			}
			
			serverReceivedMessages.getConnection().observe(MainActivity.this, aBoolean -> {
				if (!aBoolean) {
					Toast.makeText(MainActivity.this, "No Connection to the Webserver!\nTrying to Reconnect...", Toast.LENGTH_SHORT).show();
					wifiImageOff.setVisibility(ImageView.VISIBLE);
					wifiImageOn.setVisibility(ImageView.INVISIBLE);
					subNetDeviceFinder.setFoundSubnetIp("");
					reSearchforSubnet(subNetDeviceFinder.foundSubnetIp);
					Log.e("Reconnect:", "Trying to reconnect");
				} else {
					wifiImageOff.setVisibility(ImageView.INVISIBLE);
					wifiImageOn.setVisibility(ImageView.VISIBLE);
					subnetSearchHandler.removeCallbacksAndMessages(null);
					Toast toast = Toast.makeText(MainActivity.this, "Connected to Webserver", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER,0,0);
					toast.show();
				}
			});
		});
		
		orderTopMostNode.getOrder().observe(this, strings -> {
			Thread thread = new Thread(() -> {
				try{
					TopMostNode = nodeViewModelAllNodes.getAllNodesSorted(orderTopMostNode.getOrderType(), orderTopMostNode.getIsAscDesc()).subList(0, 1);
					adapterTopMostNode.submitList(TopMostNode);
					Log.e("Row", strings.get(0));
					Log.e("Column", strings.get(1));
					Log.e("Column:", "changed!");
				}catch (Exception e){
					e.printStackTrace();
				}
			});
			thread.start();
			textViewSingleTopMostNode.setText(encoder.getTitle(0, orderTopMostNode.getOrderType(), orderTopMostNode.getIsAscDesc()));
		});
		
		orderAllNodes.getOrder().observe(this, strings -> {
			Thread thread = new Thread(() -> {
				adapterAllNodes.submitList(nodeViewModelAllNodes.getAllNodesSorted(orderAllNodes.getOrderType(), orderAllNodes.getIsAscDesc()));
				recyclerViewAllNodes.smoothScrollToPosition(0);
				Log.e("Row", strings.get(0));
				Log.e("Column", strings.get(1));
				Log.e("Column:", "changed!");
			});
			thread.start();
			textViewAllNodes.setText(encoder.getTitle(1, orderAllNodes.getOrderType(), orderAllNodes.getIsAscDesc()));
		});
		
		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}
			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				nodeViewModelAllNodes.delete(adapterAllNodes.getNodeAt(viewHolder.getAdapterPosition()));
				Toast.makeText(MainActivity.this, "Node deleted", Toast.LENGTH_LONG).show();
			}
		}).attachToRecyclerView(recyclerViewAllNodes);
		
		adapterAllNodes.setOnItemClickListener(node -> {
			if(serverReceivedMessages.getConnection().getValue()){
				Intent intent = new Intent(MainActivity.this, GraphActivity.class);
				intent.putExtra("ID", node.getId());
				startActivity(intent);
			}else{
				Toast toast = Toast.makeText(MainActivity.this, "You need an active Connection " +
						"to see live Data as Graphs!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();
			}
		});
		
		scannerBtle = BluetoothScanner.getInstance(MainActivity.this);
		
		scannerBtle.deviceMutableLiveDataList.observe(this, bluetooth_devices -> {
			Thread thread = new Thread(() -> {
				String trimmedBLEiD;
				List<Bluetooth> copyList = new ArrayList<Bluetooth>(bluetooth_devices);
				for (Bluetooth devices : copyList) {
					trimmedBLEiD = devices.getBleId().replaceAll("[^0-9]", "");
					if (nodeViewModelAllNodes.getSingleNodeByNodeID(Long.parseLong(devices.getBleId().replaceAll("[^0-9]", ""))) != null
							&& nodeViewModelAllNodes.getSingleNodeByNodeID(Long.parseLong(devices.getBleId().replaceAll("[^0-9]", ""))).getRSSI() != devices.getRSSI()) {
						try{
							if(nodeViewModelAllNodes.getSingleNodeByNodeID(Long.parseLong(devices.getBleId().replaceAll("[^0-9]", ""))).getBleID().equals("unknown")){
								Node node = nodeViewModelAllNodes.getSingleNodeByNodeID(Long.parseLong(devices.getBleId().replaceAll("[^0-9]", "")));
								node.setBleID(devices.getAddress());
								nodeViewModelAllNodes.update(node);
							}
							nodeViewModelAllNodes.updateBLEonNode(devices.getRSSI(), devices.getAddress(), new Date().getTime());
						}catch (Exception e){
							Log.e("Exception inserting BLE RSSI value", e.toString());
						}
					}
					else if(nodeViewModelAllNodes.getSingleNodeByNodeID(Long.parseLong(trimmedBLEiD)) == null){
						nodeViewModelAllNodes.insert(new Node(Long.parseLong(trimmedBLEiD), 0, 0, 0,0,0, devices.getAddress(), devices.getRSSI(),new Date(1900,1,1,0,0).getTime(), new Date().getTime() ));
						adapterAllNodes.submitList(nodeViewModelAllNodes.getAllNodesSorted(orderAllNodes.getOrderType(), orderAllNodes.getIsAscDesc()));
					}
				}
			});
			thread.start();
			adapterAllNodes.notifyDataSetChanged();
			adapterTopMostNode.notifyDataSetChanged();
		});
		
		scannerBtle.isScanning.observe(this, aBoolean -> {
			if (!aBoolean) {
				scannerBtle.discovery();
			}
		});
		
		scannerBtle.backUpDeviceMutableLiveDataList.observe(this, bluetoothBackups_devices -> {
			try{
			if (!serverReceivedMessages.getConnection().getValue()){
				Thread thread = new Thread(() -> {
					List<BluetoothBackup> copyList = new ArrayList<BluetoothBackup>(bluetoothBackups_devices);
					try {
						for (BluetoothBackup Backupdevices : copyList) {
							if (nodeViewModelAllNodes.getSingleNodeByNodeID(Backupdevices.getNodeIDBackUp()) != null
									&& (nodeViewModelAllNodes.getSingleNodeByNodeID(Backupdevices.getNodeIDBackUp()).getAltitude() != Backupdevices.getAltitudeBackUp() ||
										nodeViewModelAllNodes.getSingleNodeByNodeID(Backupdevices.getNodeIDBackUp()).getHumidity() != Backupdevices.getHumidityBackUp() ||
										nodeViewModelAllNodes.getSingleNodeByNodeID(Backupdevices.getNodeIDBackUp()).getTemperature() != Backupdevices.getTemperatureBackUp() ||
										nodeViewModelAllNodes.getSingleNodeByNodeID(Backupdevices.getNodeIDBackUp()).getGas() != Backupdevices.getGasBackUp() ||
										nodeViewModelAllNodes.getSingleNodeByNodeID(Backupdevices.getNodeIDBackUp()).getPressure() != Backupdevices.getPressureBackUp())){
								try {
									Log.e("BLE", "Replacing values due to Bluetooth Backup!");
									nodeViewModelAllNodes.updateAirQualityOffNode(Backupdevices.getTemperatureBackUp(), Backupdevices.getHumidityBackUp(),
											Backupdevices.getPressureBackUp(), Backupdevices.getAltitudeBackUp(), Backupdevices.getGasBackUp(), Backupdevices.getNodeIDBackUp(), new Date().getTime());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("Exception", e.toString());
					}
				});
				thread.start();
			}
		}catch (Exception e){
				e.printStackTrace();
			}
		});
		
		checkForBLEUpdate(bluetoothImageOn, bluetoothImageOff);
		textViewAllNodes.setText(encoder.getTitle(1, orderAllNodes.getOrderType(), orderAllNodes.getIsAscDesc()));
		textViewSingleTopMostNode.setText(encoder.getTitle(0, orderTopMostNode.getOrderType(), orderTopMostNode.getIsAscDesc()));
	}
	
	private void checkForBLEUpdate(ImageView blueoothIconOn, ImageView blueoothIconOff) { //No prototype! Actually important!
		Log.e("BLE Icon", "Updating");
		bluetoothHandler.postDelayed(() -> {
			checkForBLEUpdate(blueoothIconOn, blueoothIconOff);
			List<Node> nodeList= new ArrayList<Node>(nodeViewModelAllNodes.getAllNodes().getValue());
			List<Long> differences = new ArrayList<Long>();
			long smallestDif = 0;
			for(Node node: nodeList){
				differences.add((-node.getLastSeenBLE() + new Date().getTime())/1000); //Vorzeichen vertauscht!
			}

			for(Long dif: differences){
				if(smallestDif == 0 || dif < smallestDif){
					smallestDif = dif;
				}
			}
			Log.e("Smallest Dif", Long.toString(smallestDif));
			if(smallestDif > -45 && smallestDif < 45 && !nodeList.isEmpty()){ //Threshold of last BLE seen (45 sec timeout)
				blueoothIconOff.setVisibility(ImageView.INVISIBLE);
				blueoothIconOn.setVisibility(ImageView.VISIBLE);
			}else{
				blueoothIconOn.setVisibility(ImageView.INVISIBLE);
				blueoothIconOff.setVisibility(ImageView.VISIBLE);
			}
			if(!scannerBtle.isScanning.getValue()){
				scannerBtle.discovery();
			}
		}, 1000);
	}
	
	private void reSearchforSubnet(String subnetDevices) { //No prototype! Actually important!
		subnetSearchHandler.postDelayed(() -> {
			if (subnetDevices == null || !serverReceivedMessages.getConnection().getValue()) {
				subNetDeviceFinder.SubnetDeviceStartSearch();
				Toast toast = Toast.makeText(MainActivity.this, "Looking for Webserver", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM,0,0);
				toast.show();				}
			reSearchforSubnet(subnetDevices);
		}, 10000);
	}
}