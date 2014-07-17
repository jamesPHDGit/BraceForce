package braceForce.distribution;

import java.util.Hashtable;

public interface ISensorEventSubscriber {

	void onSensorDataChanged(String sensorID, Hashtable sensorData);
}
