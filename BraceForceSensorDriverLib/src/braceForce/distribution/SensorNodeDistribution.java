package braceForce.distribution;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;

import com.esotericsoftware.kryonet.Connection;

public interface SensorNodeDistribution {

	ArrayList getListOfSensors();
	ArrayList getListOfSensors(Context androidContext);
	Hashtable getSensorData(String sensorID, long dateTime, long timeDifference);
	Hashtable getSensorData(Context androidContext, String sensorID,
			long dateTime, long timeDifference);
	void subScribeSensorEvent(Context androidContext, String sensorID,
			String dataCacheStationID);
	
	
	//add a function call where sensor data is not required to report back
	void suppressSensorEvent(String sensorID, boolean suppressMode);
	
	/*
	 * InetAddress remoteAddress = InetAddress.getByName(connection.getRemoteAddressTCP().getAddress().getHostName());
			String hostAddress = connection.getRemoteAddressTCP().getAddress().getHostAddress();
			String hostName =  connection.getRemoteAddressTCP().getAddress().getHostName();
			int hostPort = connection.getRemoteAddressTCP().getPort();
			
	 */
	//de-commission these two methods as EsotericSoftware package has stack overflow
	//issue dealing with Connection object, a bug??  Not interested in this stage, walk
	//around this brick wall 
	void subScribeSensorEvent(String sensorID, String dataCacheStationID, 
			String hostAddress, String hostName,
			int hostPort 
			);
	void subScribeSensorEvent(Context androidContext, String sensorID,
			String dataCacheStationID, String hostAddress, String hostName,
			int hostPort );
//	void subScribeSensorEvent(String sensorID, String dataCacheStationID, Connection conn);
//	void subScribeSensorEvent(Context androidContext, String sensorID,
//			String dataCacheStationID, Connection connection);
}
