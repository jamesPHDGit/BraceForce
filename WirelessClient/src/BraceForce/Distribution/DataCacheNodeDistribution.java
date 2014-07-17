package BraceForce.Distribution;

import java.util.Hashtable;

public interface DataCacheNodeDistribution {

	void subscribeSensorDataEvent(String sensorID);
	Hashtable getSensorData(String sensorID, long Timestamp, long maxTimeDifference);
	
}
