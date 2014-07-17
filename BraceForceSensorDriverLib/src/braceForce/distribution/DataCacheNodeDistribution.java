package braceForce.distribution;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;

import com.esotericsoftware.kryonet.Connection;

public interface DataCacheNodeDistribution {

	//call by app node
	void subscribeSensorDataEvent(String sensorID, int appNodePort, String appNodeAddress);
	Hashtable getSensorData(String sensorID, long Timestamp, long maxTimeDifference);
	ArrayList getSensorNodeList();
	//to work around serialization/deserialization issue
	//return only ID for those sensor nodes
	//instead of BraceNode objects
	ArrayList getSensorNodeListSimple();
	ArrayList getSensorList(String sensorNodeID);
	
	//call by sensor node
	void reportSensorDataChange(String sensorID, Hashtable sensorData);
	
    //Define a method which will be called periodically to check
	//sensor state table, global concurrent queue
	//if detecting sensor  reading difference from neighboring node (in the order of discovery)
	//are less than an application-specific threshold, this sensor node
	//data is temporailly suppressed until reading difference is bigger than the threshold
	//calling sensor node suppress for turning on/off reporting sensor data change
	//void checkSensorDataTable();
}
