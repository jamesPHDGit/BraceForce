package braceForce.distribution;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


import BraceForce.SensorData.Android.AndroidSensorDataContentProviderImpl;
import BraceForce.SensorData.Android.AndroidSensorDataManager;
import BraceForce.SensorLink.BraceForceSensorLinkManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;

public class SensorNodeDistributionImpl implements SensorNodeDistribution {

	@Override
	public ArrayList getListOfSensors() {
		try{
			Log.d("SensorNodeServer", "getListOfSensors is called remotely");
			return getListOfSensors(MyApplication.getAppContext());
		}
		catch (Exception ex){
			Log.d("SensorNodeServer", "getListOfSensors error " + ex.toString());
			return null;
		}
		
	}

	


	@Override
	public Hashtable getSensorData(String sensorID, long dateTime,
			long timeDifference) {
		// TODO Auto-generated method stub
		//return null;
		return getSensorData(MyApplication.getAppContext(), sensorID, dateTime, timeDifference);
	}
	
	//test local method
	public Hashtable getSensorData(Context androidContext, String sensorID, long dateTime,
			long timeDifference, int pollingWindow) {
		// TODO Auto-generated method stub
		String Time_Stamp = "timestamp";
		AndroidSensorDataManager androidSensorDataManager = AndroidSensorDataManagerSingleton.getInstance(androidContext);
		//androidSensorDataManager.startCollectSensorUsingThreads();
		AndroidSensorDataContentProviderImpl  dataProvider = new AndroidSensorDataContentProviderImpl();
		androidSensorDataManager.startSensorDataAcquistionPollingMode(sensorID, dataProvider, null, pollingWindow);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean dataFound = false;
		int counter = 0;
		Bundle currentData = null;
		Hashtable bundleHT = null;
		List<Bundle> sensorDataBundle;
		while ( counter <3 && !dataFound ) {
				counter++;
				
				//using data provider's data
				//instead of expensively requery the sensor
				//which might be also wrong
				sensorDataBundle = dataProvider.getSensorDataReading();
				
			    if ( sensorDataBundle.isEmpty() ){
//			    //	sensorDataBundle = androidSensorDataManager.get
		    	sensorDataBundle = androidSensorDataManager.returnSensorDataAndroidAfterInitialization(sensorID, false);
		    }
			    if ( sensorDataBundle.isEmpty() ){
			    	//something very slow regarding with sensor reading
			    	try {
						Thread.sleep(500*counter);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
				for ( Bundle curData : sensorDataBundle) {
					//if ( curData.getLong(Time_Stamp) >= dateTime - timeDifference && curData.getLong(Time_Stamp) <= dateTime + timeDifference ) {
						currentData = curData;
					    bundleHT = convertBundleToHash(currentData);
						dataFound = true;
					//}
				}
				
		}
		return bundleHT;
		
		
	}
	
	@Override
	public Hashtable getSensorData(Context androidContext, String sensorID, long dateTime,
			long timeDifference) {
		// TODO Auto-generated method stub
		String Time_Stamp = "timestamp";
		AndroidSensorDataManager androidSensorDataManager = AndroidSensorDataManagerSingleton.getInstance(androidContext);
		//androidSensorDataManager.startCollectSensorUsingThreads();
		AndroidSensorDataContentProviderImpl  dataProvider = new AndroidSensorDataContentProviderImpl();
		androidSensorDataManager.startSensorDataAcquistionPollingMode(sensorID, dataProvider, null);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean dataFound = false;
		int counter = 0;
		Bundle currentData = null;
		Hashtable bundleHT = null;
		List<Bundle> sensorDataBundle;
		while ( counter <3 && !dataFound ) {
				counter++;
				
				//using data provider's data
				//instead of expensively requery the sensor
				//which might be also wrong
				sensorDataBundle = dataProvider.getSensorDataReading();
				
			    if ( sensorDataBundle.isEmpty() ){
//			    //	sensorDataBundle = androidSensorDataManager.get
		    	sensorDataBundle = androidSensorDataManager.returnSensorDataAndroidAfterInitialization(sensorID, false);
		    }
			    if ( sensorDataBundle.isEmpty() ){
			    	//something very slow regarding with sensor reading
			    	try {
						Thread.sleep(500*counter);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
				for ( Bundle curData : sensorDataBundle) {
					//if ( curData.getLong(Time_Stamp) >= dateTime - timeDifference && curData.getLong(Time_Stamp) <= dateTime + timeDifference ) {
						currentData = curData;
					    bundleHT = convertBundleToHash(currentData);
						dataFound = true;
					//}
				}
				
		}
		return bundleHT;
		
		
	}

	private Hashtable convertBundleToHash(Bundle currentData) {
		Hashtable bundleHT = new Hashtable();
		 Set<String> keysets = currentData.keySet();
		 for ( String bundleKey : keysets ){
			 Object bundleValue = currentData.get(bundleKey);
			 bundleHT.put(bundleKey, bundleValue);
		 }
		return bundleHT;
	}

	@Override
	public ArrayList getListOfSensors(Context androidContext) {
		// TODO Auto-generated method stub
		try{
			AndroidSensorDataManager androidSensorDataManager = AndroidSensorDataManagerSingleton.getInstance(androidContext);

			List<BraceForceSensorLinkManager> linkManager = androidSensorDataManager.getAllRegisteredSensorLinkManagers();
			ArrayList sensorList = new ArrayList();
			for ( BraceForceSensorLinkManager sensorManager : linkManager){
				SensorMetaInfo meta = new SensorMetaInfo();
				meta.setSensorID(sensorManager.getSensorID());
				meta.setSensorType(sensorManager.getCommunicationChannelType().name());
				sensorList.add(meta);
			}
			return sensorList;
		}
		catch (Exception ex){
			Log.d("Sensor Node", "Get sensor list error " + ex.toString());
			return null;
		}
	}

	@Override
	public void subScribeSensorEvent(Context androidContext, String sensorID,
			String dataCacheStationID) {
		// TODO Auto-generated method stub
		
	}
	
	//to test front end
	//to fool the android front ened
	public void shutdown(){
		AndroidSensorDataManagerSingleton.cleanup();
	}

	@Override
	public void suppressSensorEvent(String sensorID, boolean suppressMode) {
		// TODO Auto-generated method stub
		SensorNodeD2DManager.changeSuppressionMode(sensorID, suppressMode);
	}

	@Override
	public void subScribeSensorEvent(String sensorID,
			String dataCacheStationID, String hostAddress, String hostName,
			int hostPort) {
		// TODO Auto-generated method stub
		subScribeSensorEvent(MyApplication.getAppContext(), sensorID, dataCacheStationID, hostAddress,
				hostName, hostPort);
	}

	@Override
	public void subScribeSensorEvent(Context androidContext, String sensorID,
			String dataCacheStationID, String hostAddress, String hostName,
			int hostPort) {
		// TODO Auto-generated method stub
		try {
			InetAddress remoteAddress = InetAddress.getByName(hostName);
			
			
			SensorNodeD2DManager.addNewDataCacheNode(remoteAddress,
					hostAddress,  hostName, 
					  Integer.toString(hostPort), null);
			boolean registeredBefore = SensorNodeD2DManager.registerSensorWithDataCacheNode(sensorID, dataCacheStationID);
			
			if ( !registeredBefore ){
				AndroidSensorDataManager androidSensorDataManager = AndroidSensorDataManagerSingleton.getInstance(androidContext);
				//need to register every single sensor with event driven data model turned on
				//when sensor service turned off, they can be all properly shut down
				AndroidSensorDataContentProviderImpl  dataProvider = new AndroidSensorDataContentProviderImpl();
				SensorEventDrivenListener listener = new SensorEventDrivenListener();
				listener.bindDataProvider(dataProvider);
				androidSensorDataManager.startSensorDataAcquistionEventModel(sensorID, dataProvider, listener);
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//de-commision these two methods which causes stack overflow due to serialization issue
	//of Connection object

	/*
	@Override
	public void subScribeSensorEvent(String sensorID, String dataCacheStationID, Connection connection) {
		// TODO Auto-generated method stub
		subScribeSensorEvent(MyApplication.getAppContext(), sensorID, dataCacheStationID, connection);
	}
	

	@Override
	public void subScribeSensorEvent(Context androidContext, String sensorID, String dataCacheStationID, Connection connection) {
		// TODO Auto-generated method stub
		
		try {
			InetAddress remoteAddress = InetAddress.getByName(connection.getRemoteAddressTCP().getAddress().getHostName());
			String hostAddress = connection.getRemoteAddressTCP().getAddress().getHostAddress();
			String hostName =  connection.getRemoteAddressTCP().getAddress().getHostName();
			int hostPort = connection.getRemoteAddressTCP().getPort();
			
			
			SensorNodeD2DManager.addNewDataCacheNode(remoteAddress,
					hostAddress,  hostName, 
					  Integer.toString(hostPort), null);
			SensorNodeD2DManager.registerSensorWithDataCacheNode(sensorID, dataCacheStationID);
			
			AndroidSensorDataManager androidSensorDataManager = AndroidSensorDataManagerSingleton.getInstance(androidContext);
			//need to register every single sensor with event driven data model turned on
			//when sensor service turned off, they can be all properly shut down
			AndroidSensorDataContentProviderImpl  dataProvider = new AndroidSensorDataContentProviderImpl();
			SensorEventDrivenListener listener = new SensorEventDrivenListener();
			listener.bindDataProvider(dataProvider);
			androidSensorDataManager.startSensorDataAcquistionEventModel(sensorID, dataProvider, listener);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		
		
	}
	*/

}
