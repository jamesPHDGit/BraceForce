package braceForce.distribution;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import BraceForce.Network.Android.DataCacheNodeServer;
import BraceForce.SensorData.SensorDataChangeEvent;
import BraceForce.SensorData.SensorDataChangeListener;
import BraceForce.SensorData.Android.AndroidSensorDataContentProvider;
import BraceForce.SensorData.Android.AndroidSensorDataManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class SensorEventDrivenListener implements SensorDataChangeListener {

	private int dataCacheRMIObjectID = 40;
	private AndroidSensorDataContentProvider dataProvider;
	
	@Override
	public void onDataChanged(SensorDataChangeEvent event) {
		// TODO Auto-generated method stub
		String Time_Stamp = "timestamp";
		Context svcContext = event.getBraceForceContext();
		//event shall have a context
		AndroidSensorDataManager androidSensorDataManager = AndroidSensorDataManagerSingleton.getInstance(svcContext);
		List<Bundle> sensorDataBundle = null;
		boolean dataFound = false;
		boolean useDataCache = false;
		Bundle currentData = null;
		Hashtable bundleHT = null;
		//while ( counter <3 && !dataFound ) {
				if ( dataProvider != null ){
					//using data provider's data
					//instead of expensively requery the sensor
					//which might be also wrong
					sensorDataBundle = dataProvider.getSensorDataReading();
					useDataCache = true;
				}
				
				if ( sensorDataBundle.isEmpty() )
			    {
					sensorDataBundle = androidSensorDataManager.returnSensorDataAndroidAfterInitialization(event.getSensorID(), false);
				}
				//List<Bundle> 
				
				
				for ( Bundle curData : sensorDataBundle) {
					if ( curData.getLong(Time_Stamp) == event.getSensorDataPacket().getTime()  ) {
						currentData = curData;
						bundleHT = convertBundleToHash(currentData);
						dataFound = true;
					}
				}
				
	
		if ( dataFound ){
			
			
			//Determine whether to report sensor data change or not depending on suppression mode
			//this mode is passed from data aggregation node which runs spatial data correlation
			if ( SensorNodeD2DManager.isSensorSuppressed(event.getSensorID())){
				Log.d("SensorEventDrivenListener", "Data suppressed by spatial data correlation");
				return;
			}
			
			//Determine whether to report based on temporal mode which specifies a delta
			//Here provides rudimentary sensor type detection
			String sensorID = event.getSensorID();
			
			
			
			Hashtable historicalData = SensorNodeD2DManager.getTemporalReading(sensorID);
			
			
			//suppress logics
			/*
			long timeDelta = 20;
			float dataDelta = 0;
			String mainParameterName = "";
			if ( historicalData != null ){
				if ( sensorID.toUpperCase().contains("LIGHT")){
					//LIGHT SENSOR
					//VALUE DELTA
					//TEMPORAL DELTA
					//DELTA IS RUDIMENTARY 
					//20
					//light-level
					//timestamp
					//5 seconds
					mainParameterName = "light-level";
					timeDelta = 5000;
					dataDelta = 20;
					
				}
				else if ( sensorID.toUpperCase().contains("TEMPERATURE")){
					//TEMPERATURE SENSOR
					//delta is RUDIMENTARY
					//5
					//temperature
					//timestamp
					mainParameterName = "temperature";
					timeDelta = 5000;
					dataDelta = 5;
				}
			}
			
			float dataLastTime = (Float)historicalData.get(mainParameterName);
			float dataThisTime = (Float)bundleHT.get(mainParameterName);
			long accessTime = (Long)historicalData.get("timestamp");
			long currentTime = System.currentTimeMillis();
			if ( currentTime - accessTime < timeDelta ){
				if ( Math.abs( dataThisTime - dataLastTime ) < dataDelta ) {
					Log.d("SensorEventDrivenListener", "Data suppressed by temporal data correlation");
					return;
				}
			}
			*/
			
			
			//register this historical data
			SensorNodeD2DManager.storeTemporalReading(sensorID, bundleHT);
			
			//data is gathered for the given sensor
			//now shall report to the data cache node bound to it
			// TODO Auto-generated method stub
			//Find the cache node which subscribes to this sensor data change event
			String cacheNodeID = SensorNodeD2DManager.findDataCacheNodeBound(event.getSensorID());
		
			//find out InetAddress and TCP for this cache node
			BraceNode cacheNodeInfo = SensorNodeD2DManager.findCacheNode(cacheNodeID);
					
			//report the data to cache node
			DataCacheNodeServer cacheServer = new DataCacheNodeServer();
			
			
			try {
				cacheServer.RMIReportSensorDataChange( cacheNodeInfo.getNodeAddress(), Integer.parseInt(cacheNodeInfo.getTcpPort()),
						getDataCacheRMIObjectID(), event.getSensorID(), bundleHT);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				System.out.println("DataCacheNodeDistribution Error: " + e.toString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("DataCacheNodeDistribution Error: " + e.toString());
			}
		}
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

	public int getDataCacheRMIObjectID() {
		return dataCacheRMIObjectID;
	}

	public void setDataCacheRMIObjectID(int dataCacheRMIObjectID) {
		this.dataCacheRMIObjectID = dataCacheRMIObjectID;
	}

	@Override
	public void bindDataProvider(
			AndroidSensorDataContentProvider androidProvider) {
		// TODO Auto-generated method stub
		dataProvider = androidProvider;
	}

}
