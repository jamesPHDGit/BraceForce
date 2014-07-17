package BraceForce.SensorData.Android;

import java.util.List;
import java.util.Set;

import BraceForce.SensorData.SensorDataChangeEvent;
import BraceForce.SensorData.SensorDataChangeListener;
import BraceForce.SensorData.Android.AndroidSensorDataManager.SensorAutoDetection;
import BraceForce.SensorLink.Android.BuiltIn.BuiltInSensorType;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;
import braceForce.distribution.AppNodeDiscoveryService;
import braceForce.distribution.AppNodeDiscoveryService.MyLocalBinder;
import braceForce.distribution.ISensorEventSubscriber;

public class BraceSensorManager {

	private static AndroidSensorDataManager sensorMgr;
	private static AndroidSensorDataContentProvider provider;
	private static final String Time_Stamp = "timestamp";
	
	//for network sensor
	private static AppNodeDiscoveryService myService;
	static boolean isBound = false;
	private static boolean workRun = false;
	private static Thread threadAppClient;
	
	private static ServiceConnection myConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service){
			MyLocalBinder binder = ( MyLocalBinder )service;
			myService = binder.getService();
			isBound = true;
		}
		
		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};
	
	
	public static void cleanup(){
		sensorMgr.shutdown();
		
	}
	
	public static void cleanup(Context context, boolean network){
		workRun = false;
		try{
			context.unbindService(myConnection);
		}
		catch(Exception ex){
			//not always working
			//Android programming paradigm
		}
	}
	
	//used for network sensor
	public static void subscribeSensorEvent(String sensorID, Context context, ISensorEventSubscriber listener, boolean networkSensor ) {
		workRun = true;
		final ISensorEventSubscriber thisClone = listener;
		final String localSensorID  = sensorID;
		Intent intent = new Intent(context, AppNodeDiscoveryService.class);
		context.bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
		//Create a thread that talks to App Layer of BraceForce framework
		threadAppClient = new Thread(){
			public void run(){
				try{
					while ( workRun  ){
						//ReturnSensorNodes
						if ( isBound ) {
							//boolean serviceInitialized = 
							Set<String> sensorNodeSet = myService.returnSensorNode();
							if ( sensorNodeSet != null ){
								boolean appServiceStarted = myService.isServiceInitialized();
								for ( String sensorNodeName: sensorNodeSet){
									String sensorUID = myService.findSpecificSensorFromSensorNode(sensorNodeName, localSensorID);
									if ( sensorUID != null ){
										myService.subscribeEventData(sensorUID, thisClone, sensorNodeName);
									}
								}
							}
						}
						try{
							Thread.sleep(500);
						}catch (Exception ex){
							//not important, not interested in thread competition at this stage
						}
					}
				} catch ( Exception e ){
					Log.d("ThreadAppClient", e.toString());
				}
			}
		};
		threadAppClient.start();
		
		
	}
	
	public static void subscribeSensorEvent(String sensorID, Context context, SensorDataChangeListener listener,SensorAutoDetection detectionMode){
		sensorMgr = new AndroidSensorDataManager(context, detectionMode );
		provider = new AndroidSensorDataContentProviderImpl();
		
		//2634227263b66cfb
//		sensorMgr.startSensorDataAcquistionEventModel("ACCELEROMETER-On-5a04bec542cf5801", 
//				provider,  this);
		//05-28 21:36:49.359: D/AndroidSensorDataManager(7048): Found sensor ACCELEROMETER-On-adff4acf56beebdf
		//Samsung
	//	sensorMgr.startSensorDataAcquistionEventModel("ACCELEROMETER-On-adff4acf56beebdf", 
		//		provider,  this);
		
		//Motorola 05-29 12:19:12.504: D/AndroidSensorDataManager(5262): Found sensor ACCELEROMETER-On-2634227263b66cfb
		
		String phoneUniqueID = "Brace";
		try{
			phoneUniqueID =  Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
		}
		catch (Exception ex){
			
		}
		String id = sensorID + "-On-" + phoneUniqueID;
		sensorMgr.startSensorDataAcquistionEventModel(id, 
				provider,  listener, true);
	}
	
	public static void subscribeSensorEvent(String sensorID, Context context, SensorDataChangeListener listener){
		sensorMgr = new AndroidSensorDataManager(context );
		provider = new AndroidSensorDataContentProviderImpl();
		
		//2634227263b66cfb
//		sensorMgr.startSensorDataAcquistionEventModel("ACCELEROMETER-On-5a04bec542cf5801", 
//				provider,  this);
		//05-28 21:36:49.359: D/AndroidSensorDataManager(7048): Found sensor ACCELEROMETER-On-adff4acf56beebdf
		//Samsung
	//	sensorMgr.startSensorDataAcquistionEventModel("ACCELEROMETER-On-adff4acf56beebdf", 
		//		provider,  this);
		
		//Motorola 05-29 12:19:12.504: D/AndroidSensorDataManager(5262): Found sensor ACCELEROMETER-On-2634227263b66cfb
		
		String phoneUniqueID = "Brace";
		try{
			phoneUniqueID =  Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
		}
		catch (Exception ex){
			
		}
		String id = sensorID + "-On-" + phoneUniqueID;
		sensorMgr.startSensorDataAcquistionEventModel(id, 
				provider,  listener);
	}
	
	public static Bundle retrieveSensorData(SensorDataChangeEvent event, boolean ignoreTimestamp){
		
		
		Bundle currentData = null;
		boolean dataFound = false;
		
		List<Bundle> sensorDataBundle = null;
		int counter = 0;
		
		while ( counter <3 && !dataFound ) {
				counter++;
					//using data provider's data
					//instead of expensively requery the sensor
					//which might be also wrong
				sensorDataBundle = provider.getSensorDataReading();
				//List<Bundle> 
				if ( sensorDataBundle.isEmpty() )
			    {
					sensorDataBundle = sensorMgr.returnSensorDataAndroidAfterInitialization(event.getSensorID(), false);
				}
				
				if ( sensorDataBundle == null )
					continue;
				
				for ( Bundle curData : sensorDataBundle) {
					//if ( curData.getLong(Time_Stamp) == event.getSensorDataPacket().getTime()  ) {
						currentData = curData;
						dataFound = true;
					//}
				}
				
				
				
		}

		if ( !dataFound ){
			return null;
		}
		else {
			return currentData;
		}
	}
	
	public static Bundle retrieveSensorData(SensorDataChangeEvent event){
		
		
		Bundle currentData = null;
		boolean dataFound = false;
		
		List<Bundle> sensorDataBundle = null;
		int counter = 0;
		
		while ( counter <3 && !dataFound ) {
				counter++;
					//using data provider's data
					//instead of expensively requery the sensor
					//which might be also wrong
				sensorDataBundle = provider.getSensorDataReading();
				//List<Bundle> 
				if ( sensorDataBundle.isEmpty() )
			    {
					sensorDataBundle = sensorMgr.returnSensorDataAndroidAfterInitialization(event.getSensorID(), false);
				}
				
				if ( sensorDataBundle == null )
					continue;
				
				for ( Bundle curData : sensorDataBundle) {
					if ( curData.getLong(Time_Stamp) == event.getSensorDataPacket().getTime()  ) {
						currentData = curData;
						dataFound = true;
					}
				}
				
				
				
		}

		if ( !dataFound ){
			return null;
		}
		else {
			return currentData;
		}
	}
	
}
