package android.example.location_basedairquality;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NodeViewModel extends AndroidViewModel {
	
	private NodeRepository repository;
	private LiveData<List<Node>> allNodes;
	
	public NodeViewModel(@NonNull Application application) {
		super(application);
		repository = new NodeRepository(application);
		allNodes = repository.getAllNodes();
	}
	
	public void insert(Node node){
		repository.insert(node);
	}
	
	public void delete(Node node){
		repository.delete(node);
	}
	
	public void update(Node node){
		repository.update(node);
	}
	
	public void deleteAllNodes(){
		repository.deleteAllNodes();
	}
	
	public LiveData<List<Node>> getAllNodes() {
		return allNodes;
	}
	
	public LiveData<Node> getSingleNodeByKey(int id) {
		return repository.getSingleNodeByKey(id);
	}
	public Node getSingleNodeByNodeID(long nodeID) {
		return repository.getSingleNodeByNodeID(nodeID);
	}
	
	public List<Node> getAllNodesSorted(String column, String isAsc){
		return  repository.getAllNodeSorted(column,isAsc);
	}
	
	public void updateAirQualityOffNode(double temperature,
	                                    double humidity, double pressure, double altitude, double gas, long nodeid, long lastNodeUpdate){
		repository.updateAirQualityOffNode(temperature, humidity, pressure, altitude, gas, nodeid, lastNodeUpdate);
	}
	
	public void updateBLEonNode(int rssi, String bleid, long lastSeenBLE){
		repository.updateBLEonNode(rssi, bleid, lastSeenBLE);
	}
}
