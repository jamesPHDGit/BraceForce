package BraceForce.Distribution;

import java.util.ArrayList;
import java.util.Hashtable;

public interface SensorNodeDistribution {

	ArrayList getListOfSensors();
	void subScribeSensorEvent(String sensorID, String dataCacheStationID);
	Hashtable getSensorData(String sensorID, long dateTime, long timeDifference);
	
}
