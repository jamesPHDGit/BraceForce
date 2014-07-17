package BraceForce.SensorData;


import java.util.Hashtable;

import BraceForce.Drivers.ParameterMissingException;
import BraceForce.SensorLink.BraceForceSensorLinkManager;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.SensorNotStartException;



public class SensorThread extends Thread {
	private static final String TAG = "SensorThread";
	private boolean isRunning;
	
	private BraceSensorDataManager sensorManager;
	private BraceForceSensorLinkManager braceSensor;
	private GenericSensorDataContentProvider dataProvider;
	private String sensorID;
	private SensorRetrievalMode sensMode;

	public SensorThread( BraceSensorDataManager manager, GenericSensorDataContentProvider provider, String setting, Hashtable configParam, String id, SensorRetrievalMode sensorMode) throws SensorNotFoundException, ParameterMissingException, SensorNotStartException {
		super("SensorThread");
		isRunning = true;
		sensorManager = manager;
		sensorID = id;
		braceSensor = manager.getSensorLinkManager(id);
		dataProvider = provider;
		sensMode = sensorMode;
		//connect to sensor
		try {
			//connect to the sensor
			braceSensor.connect();
			//config the sensor if there is configuration
			if ( configParam != null ){
				braceSensor.configure(setting, configParam);
			}
			//start the sensor
			if ( !braceSensor.startSensor() ){
				throw new SensorNotStartException("could not start sensor!");
			}
			
		} catch (SensorNotFoundException e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}

	public void stopthread() throws SensorNotFoundException {
		isRunning = false;
		braceSensor.stopSensor();
		braceSensor.disconnect();
		this.interrupt();
	}

	@Override
	public void run() {
		//Log.d(TAG, "worker thread started");		
		//Log.e(TAG,"resolver.getType: " + resolver.getType(uri));
		
		while(isRunning) {

			

			try {
				dataProvider.addSensorData(braceSensor.getSensorDatainCollection(0));
				
				Thread.sleep(300);
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
