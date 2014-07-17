package braceForce.distribution;

import java.util.Hashtable;

import android.util.Log;

public class AppNodeDistributionImpl implements AppNodeDistribution{

	@Override
	public void reportSensorDataChange(String sensorID, Hashtable sensorData) {
		// TODO Auto-generated method stub
		ISensorEventSubscriber subscriber = AppNodeD2DManager.getSubscriberForTheSensor(sensorID);
		if ( subscriber != null ){
			subscriber.onSensorDataChanged(sensorID, sensorData);
		}
		Log.d("Server", "reportSensorDataChange is called for sensorID: " + sensorID);
		Log.d("Server", "Sensor data:" + sensorData.get("data"));
		
	}

}
