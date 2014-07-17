package BraceForce.Distribution;

import java.util.Hashtable;

import android.util.Log;

public class AppNodeDistributionImpl implements AppNodeDistribution{

	@Override
	public void reportSensorDataChange(String sensorID, Hashtable sensorData) {
		// TODO Auto-generated method stub
		Log.d("Server", "reportSensorDataChange is called for sensorID: " + sensorID);
		Log.d("Server", "Sensor data:" + sensorData.get("data"));
		
	}

}
