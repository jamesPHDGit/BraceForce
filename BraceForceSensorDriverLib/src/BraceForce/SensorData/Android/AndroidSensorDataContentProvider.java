package BraceForce.SensorData.Android;

import java.util.Hashtable;
import java.util.List;

import BraceForce.SensorData.SensorDataContentProvider;
import android.os.Bundle;

public interface AndroidSensorDataContentProvider extends
		SensorDataContentProvider {

	List<Bundle> getSensorDataReading(int numberOfReading);
	List<Bundle> getSensorDataReading();
	void addSensorData(Bundle sensordata);
	void addSensorDataList(List<Bundle> sensorDataList);
}
