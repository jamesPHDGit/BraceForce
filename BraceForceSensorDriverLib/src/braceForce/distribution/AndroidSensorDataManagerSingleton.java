package braceForce.distribution;

import BraceForce.SensorData.Android.AndroidSensorDataManager;
import android.content.Context;

//Create this singleton class for Event-driven data acquisition
//Sensor Node shall clean up the object
public class AndroidSensorDataManagerSingleton {

	private static AndroidSensorDataManager dataManager = null;
	
	
	protected AndroidSensorDataManagerSingleton() {
		
	}
	
	public static AndroidSensorDataManager getInstance(Context mContext) {
		if ( dataManager == null ){
			dataManager = new AndroidSensorDataManager(mContext);
		}
		return dataManager;
	}
	
	public static void cleanup(){
		if ( dataManager != null ){
		dataManager.shutdown();
		dataManager = null;
		}
	}
}
