package BraceForce.SensorData.Android;
import java.util.List;

import BraceForce.Drivers.ParameterMissingException;
import BraceForce.SensorData.BraceSensorDataManager;
import BraceForce.SensorData.SensorDataChangeListener;
import BraceForce.SensorData.SensorRetrievalMode;
import BraceForce.SensorLink.BraceForceSensorLinkManager;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.SensorNotStartException;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;
import BraceForce.SensorLink.Android.BuiltIn.BraceBuiltInSensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;



public class AndroidSensorThread extends Thread {
	private static final String TAG = "SensorThread";
	private boolean isRunning;
	
	private BraceSensorDataManager sensorManager;
	private AndroidSensorLinkManager braceSensor;
	private BraceBuiltInSensor braceBuiltInSensor;
	private AndroidExternalSensorLinkManager braceExternalSensor;
	private AndroidSensorDataContentProvider dataProvider;
	private String sensorID;
	private SensorRetrievalMode sensMode;
	private boolean isBuiltinSensor;
	private SensorDataChangeListener threadEventListener;
	private int pollingWindow = 300; //default 300 mill-seconds
	
	public void overridePollingWindow(int overrideWindowSize){
		pollingWindow = overrideWindowSize;
	}
	
	public AndroidSensorThread( AndroidSensorDataManager manager, AndroidSensorDataContentProvider provider, String setting, Bundle configParam, String id, SensorRetrievalMode sensorMode, SensorDataChangeListener listener) throws SensorNotFoundException, ParameterMissingException, SensorNotStartException {
		super("SensorThread");
		isRunning = true;
		sensorManager = manager;
		sensorID = id;
		braceSensor = manager.getSensor(id);
		isBuiltinSensor = braceSensor.isBuiltin();
		threadEventListener = listener;
		if ( isBuiltinSensor ){
			braceBuiltInSensor = (BraceBuiltInSensor)manager.getSensor(id);
		}
		else{
			braceExternalSensor = (AndroidExternalSensorLinkManager)manager.getSensor(id);
		}
		dataProvider = provider;
		sensMode = sensorMode;
		
		//connect to sensor
		try {
			if ( isBuiltinSensor ){
				braceBuiltInSensor.connect();
//				if ( configParam != null ){
//					braceBuiltInSensor.configure( setting, configParam);
//				}
				Bundle sensorRate = new Bundle();
				sensorRate.putInt("rate", SensorManager.SENSOR_DELAY_NORMAL );
				
				braceBuiltInSensor.configure("rate", sensorRate);
				
				
				braceBuiltInSensor.dataBufferReset();
				
				//start the sensor
				if ( !braceBuiltInSensor.startSensor() ){
					throw new SensorNotStartException("could not start sensor!");
				}
				
				if ( sensMode == SensorRetrievalMode.PUSH ) {
					braceBuiltInSensor.addRealtimeChangeListener(threadEventListener);
				}
			}
			else {
				braceExternalSensor.connect();
				if ( configParam != null ){
					braceExternalSensor.configure( setting, configParam);
				}
				
				braceExternalSensor.dataBufferReset();
				
				//start the sensor
				if ( !braceExternalSensor.startSensor() ){
					throw new SensorNotStartException("could not start sensor!");
				}
				if ( sensMode == SensorRetrievalMode.PUSH ) {
					braceExternalSensor.addRealtimeChangeListener(threadEventListener);
				}
				
			}
			
		} catch (SensorNotFoundException e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}

	public void stopthread() throws SensorNotFoundException {
		isRunning = false;
//		braceSensor.stopSensor();
//		braceSensor.disconnect();
		if ( isBuiltinSensor ){
			braceBuiltInSensor.stopSensor();
			braceBuiltInSensor.disconnect();
		}
		else{
			braceExternalSensor.stopSensor();
			braceExternalSensor.disconnect();
		}
		this.interrupt();
	}

	@Override
	public void run() {
		//Log.d(TAG, "worker thread started");		
		//Log.e(TAG,"resolver.getType: " + resolver.getType(uri));
		
		while(isRunning) {

			

			try {
				
				if ( isBuiltinSensor ){
					//Push is via Listener
				//	if ( sensMode != SensorRetrievalMode.PUSH ){
						Log.d("SensorNode", "pushing data into dataprovider structure");
						dataProvider.addSensorDataList(braceBuiltInSensor.getSensorData(0));
				//	}
				}
				else{
					//Push is via Listener
					//if ( sensMode != SensorRetrievalMode.PUSH ){
					List<Bundle> externalSensorData = braceExternalSensor.getSensorData(0);
					if ( externalSensorData != null )
						dataProvider.addSensorDataList(externalSensorData);
					//}
				
				}
				
				
				//dataProvider.addSensorData(braceSensor.getSensorDatainCollection(0));
				
				Thread.sleep(pollingWindow);
			}
			catch(InterruptedException iex) {
				iex.printStackTrace();
			}
		}
	}

	private void moveSensorDataToDB(BraceForceSensorLinkManager aSensor) {
		//keep for the second version
//		if (aSensor != null) {			
//			List<Hashtable> bundles = aSensor.getSensorDatainCollection(0);//XXX for now this gets all data fm sensor			
//			if(bundles == null) {
//				Log.e(TAG,"WTF null list of bundles~");
//			}
//			else {
//				Iterator<Bundle> iter = bundles.iterator();
//				ContentValues values = new ContentValues();
//				while(iter.hasNext()) {
//					Bundle aBundle = iter.next();	
//					String sensorID = aSensor.getSensorID();
//					String msgType = aBundle.getString(DataSeries.MSG_TYPE);
//					String sensorType = aBundle.getString(DataSeries.SENSOR_TYPE);
//					long ts = aBundle.getLong(DataSeries.SERIES_TIMESTAMP);					
//					int numSamples = aBundle.getInt(DataSeries.NUM_SAMPLES); 										
//					String tempReport = aBundle.getString(DataSeries.DATA_AS_CSV);		
//
//					Log.d(TAG, "sensorID: " + sensorID + " ts: " + ts + "numSamples : " + numSamples + 
//							"msgType: " + msgType + " sensorType: " + sensorType);
//					Log.d(TAG,"sensor data: " + tempReport);
//
//					values.put(Constants.KEY_TIMESTAMP, ts);							
//					values.put(Constants.KEY_SENSOR_ID, sensorID);
//					values.put(Constants.KEY_MSG_TYPE, msgType);							
//					values.put(Constants.KEY_SENSOR_TYPE, sensorType);
//					values.put(Constants.KEY_DATA, tempReport);
//					Uri newuri = resolver.insert(uri, values);
//					Log.e(TAG,"new uri: "+ newuri.toString());
//				}
//			}
//		}
	}
}
