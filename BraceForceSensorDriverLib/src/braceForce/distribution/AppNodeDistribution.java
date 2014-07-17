package braceForce.distribution;

import java.util.Hashtable;

public interface AppNodeDistribution {

	void reportSensorDataChange(String sensorID, Hashtable sensorData);
}
