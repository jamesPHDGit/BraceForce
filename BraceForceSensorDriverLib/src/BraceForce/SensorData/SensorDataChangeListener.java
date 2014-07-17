package BraceForce.SensorData;

import BraceForce.SensorData.Android.AndroidSensorDataContentProvider;

public interface SensorDataChangeListener {

	void onDataChanged( SensorDataChangeEvent event);
	void bindDataProvider( AndroidSensorDataContentProvider androidProvider );
}
