package BraceForce.SensorData;

import java.util.Hashtable;
import java.util.List;

public interface SensorDataContentProvider {

	List<Hashtable> getSensorData(int numberOfReading);
	
	void addSensorData(Hashtable sensordata);
	
	void addSensorData(List<Hashtable> sensordataList);
}
