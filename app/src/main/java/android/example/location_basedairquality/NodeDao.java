package android.example.location_basedairquality;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface NodeDao {
	
	@Insert
	void insert(Node node);
	
	@Delete
	void delete(Node node);
	
	@Update
	void update(Node node);
	
	@Query("DELETE FROM node_table")
	void deleteAllNodes();
	
	@Query("SELECT * FROM node_table ORDER BY NodeID DESC")
	LiveData<List<Node>> getAllNodes(); //Observable
	
	@Query("SELECT * FROM node_table ORDER BY " +
			"CASE WHEN :column = 'temperature'       AND :isAsc = 'DESC' THEN Temperature     END DESC, " +
			"CASE WHEN :column = 'temperature'       AND :isAsc = 'ASC' THEN Temperature     END ASC, " +
			"CASE WHEN :column = 'humidity'         AND :isAsc = 'DESC' THEN Humidity        END DESC, " +
			"CASE WHEN :column = 'humidity'         AND :isAsc = 'ASC' THEN Humidity        END ASC, " +
			"CASE WHEN :column = 'gas'         AND :isAsc = 'DESC' THEN gas        END DESC, " +
			"CASE WHEN :column = 'gas'         AND :isAsc = 'ASC' THEN gas        END ASC, " +
			"CASE WHEN :column = 'pressure'        AND :isAsc = 'DESC' THEN pressure            END DESC, " +
			"CASE WHEN :column = 'pressure'        AND :isAsc = 'ASC' THEN pressure            END ASC, " +
			"CASE WHEN :column = 'rssi'              AND :isAsc = 'DESC' THEN RSSI END DESC, " +
			"CASE WHEN :column = 'rssi'              AND :isAsc = 'ASC' THEN RSSI             END ASC ")
	List<Node> getAllNodesSorted(String column, String isAsc); //Observable
	
	@Query("SELECT * FROM node_table WHERE id=:id")
	LiveData<Node> getSingleNodeByKey(int id); //Observable
	
	@Query("SELECT * FROM node_table WHERE nodeID=:nodeID")
	Node getSingleNodeByNodeID(long nodeID); //Observable
	
	@Query("UPDATE node_table SET temperature=:temperature, " +
			"humidity=:humidity, pressure=:pressure, altitude=:altitude, gas=:gas, lastNodeUpdate=:lastNodeUpdate WHERE nodeID=:nodeid  ")
	void updateAirQualityOffNode(double temperature,
	                             double humidity, double pressure, double altitude, double gas, long nodeid, long lastNodeUpdate); //Observable
	
	@Query("UPDATE node_table SET RSSI=:rssi, " +
			"lastSeenBLE=:lastSeenBLE WHERE bleID=:bleid  ")
	void updateBLEonNode(int rssi, String bleid, long lastSeenBLE); //Observable
}
