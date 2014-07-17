package BraceForce.SensorData.Android;

import java.util.List;

import BraceForce.SensorData.SensorDataChangeListener;
import android.os.Bundle;

public interface AndroidDataManagerAddonInterface {
	void startPollingSensor(String id, AndroidSensorDataContentProvider dataProvider );
	
	//retrieve sensor data in once off manner
	List<Bundle> returnSensorDataAndroid( String id, boolean clearDataBuffer);
	
	//link sensor with eventModel for real time data sensing
	void startSensorDataAcquistionEventModel(String id, AndroidSensorDataContentProvider dataProvider, SensorDataChangeListener listener);
}
