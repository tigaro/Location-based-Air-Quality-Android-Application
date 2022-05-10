package android.example.location_basedairquality;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NodeRepository {
	
	private NodeDao nodeDao;
	private LiveData<List<Node>> allNodes;
	
	public NodeRepository(Application application){
		NodeDatabase database = NodeDatabase.getInstance(application);
		nodeDao = database.nodeDao();
		allNodes = nodeDao.getAllNodes();
	}
	
	public void insert(Node node){
		InsertNodeTask insertNodeTask = new InsertNodeTask(nodeDao);
		insertNodeTask.InsertNodeTask(node);
	}
	
	public void update(Node node){
		UpdateNodeTask updateNodeTask = new UpdateNodeTask(nodeDao);
		updateNodeTask.UpdateNodeTask(node);
	}
	
	public void delete(Node node){
		DeleteNodeTask deleteNodeTask = new DeleteNodeTask(nodeDao);
		deleteNodeTask.DeleteNodeTask(node);
	}
	
	public void deleteAllNodes(){
		DeleteAllNodeTask deleteAllNodeTask = new DeleteAllNodeTask(nodeDao);
		deleteAllNodeTask.DeleteAllNodeTask();
	}
	
	public LiveData<List<Node>> getAllNodes() {
		return allNodes;
	}
	
	public LiveData<Node> getSingleNodeByKey(int id) {
		return nodeDao.getSingleNodeByKey(id);
	}
	public Node getSingleNodeByNodeID(long nodeID) {
		return nodeDao.getSingleNodeByNodeID(nodeID);
	}
	
	
	public List<Node> getAllNodeSorted(String column, String isAsc){
		return nodeDao.getAllNodesSorted(column,isAsc);
	}
	
	public void updateAirQualityOffNode(double temperature,
	                                    double humidity, double pressure, double altitude, double gas, long nodeid, long lastNodeUpdate) {
		UpdateAirQualityOffNode updateAirQualityOffNode = new UpdateAirQualityOffNode(nodeDao);
		updateAirQualityOffNode.UpdateAirQualityOffNode(temperature, humidity, pressure, altitude, gas, nodeid, lastNodeUpdate);
	}
	
	public void  updateBLEonNode(int rssi, String bleid, long lastSeenBLE){
		UpdateBLEonNode updateBLEonNode = new UpdateBLEonNode(nodeDao);
		updateBLEonNode.UpdateBLEonNode(rssi, bleid, lastSeenBLE);
	}
	
	private static class UpdateBLEonNode {
		private NodeDao nodeDao;
		private UpdateBLEonNode(NodeDao nodeDao) {
			this.nodeDao = nodeDao;
		}
		private void UpdateBLEonNode(int rssi, String bleid, long lastSeenBLE){
			new Thread(() -> nodeDao.updateBLEonNode(rssi, bleid, lastSeenBLE)).start();
		}
	}
	
	private static class UpdateAirQualityOffNode {
		private NodeDao nodeDao;
		private UpdateAirQualityOffNode(NodeDao nodeDao) {
			this.nodeDao = nodeDao;
		}
		private void UpdateAirQualityOffNode(double temperature, double humidity, double pressure, double altitude, double gas, long nodeid, long lastNodeUpdate){
			new Thread(() -> nodeDao.updateAirQualityOffNode(temperature, humidity, pressure, altitude, gas, nodeid, lastNodeUpdate)).start();
		}
	}
	
	private static class InsertNodeTask {
		private NodeDao nodeDao;
		private InsertNodeTask(NodeDao nodeDao) {
			this.nodeDao = nodeDao;
		}
		private void InsertNodeTask(Node... nodes){
			new Thread(() -> nodeDao.insert(nodes[0])).start();
		}
	}
	
	private static class UpdateNodeTask {
		private NodeDao nodeDao;
		private UpdateNodeTask(NodeDao nodeDao) {
			this.nodeDao = nodeDao;
		}
		private void UpdateNodeTask(Node... nodes){
			new Thread(() -> nodeDao.update(nodes[0])).start();
		}
	}
	
	private static class DeleteNodeTask {
		private NodeDao nodeDao;
		private DeleteNodeTask(NodeDao nodeDao) {
			this.nodeDao = nodeDao;
		}
		private void DeleteNodeTask(Node... nodes){
			new Thread(() -> nodeDao.delete(nodes[0])).start();
		}
	}
	
	private static class DeleteAllNodeTask {
		private NodeDao nodeDao;
		private DeleteAllNodeTask(NodeDao nodeDao) {
			this.nodeDao = nodeDao;
		}
		private void DeleteAllNodeTask(){
			new Thread(() -> nodeDao.deleteAllNodes()).start();
		}
	}
}
