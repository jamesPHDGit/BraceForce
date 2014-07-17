package BraceForce.SensorData.Android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import braceForce.Drivers.Android.AndroidDriverCommunicator;
import braceForce.Drivers.Android.AndroidSensorDataPacket;


import BraceForce.Drivers.ParameterMissingException;
import BraceForce.Drivers.SensorDataPacket;
import BraceForce.SensorData.BraceSensorDataManager;
import BraceForce.SensorData.GenericSensorDataContentProvider;
import BraceForce.SensorData.SensorDataChangeListener;
import BraceForce.SensorData.SensorMetaData;
import BraceForce.SensorData.SensorRetrievalMode;
import BraceForce.SensorData.SensorStateMachine;
import BraceForce.SensorData.Manager.DatabaseManager;
import BraceForce.SensorLink.BraceForceSensorLinkManager;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.DetailedSensorState;
import BraceForce.SensorLink.DriverType;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.SensorNotStartException;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;
import BraceForce.SensorLink.Android.BuiltIn.BraceBuiltInSensor;
import BraceForce.SensorLink.Android.BuiltIn.BuiltInSensorType;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

public class AndroidSensorDataManager implements BraceSensorDataManager, AndroidDataManagerAddonInterface {

		private static final String TAG = "AndroidSensorDataManager";
		private DatabaseManager databaseManager;
		
		private Thread workerThread;
		private Context svcContext;
		
		private Map<String, AndroidSensorLinkManager> sensors;
		private List<DriverType> driverTypes;
		private Map<CommunicationChannelType,AndroidChannelManager> channelManagers;
		private List<AndroidSensorThread> sensorThreads = new ArrayList<AndroidSensorThread>();;
		
		public enum SensorAutoDetection{
			USB,
			BLUETOOTH,
			NFC,
			ALL,
			None
		}
		
		
		public AndroidSensorDataManager(Context context ){
			this(context, 
					new DatabaseManager(context), new AndroidBluetoothManager(context), new USBManager(context) );
		}
		
		//overload this method with auto detection of external sensors
		public AndroidSensorDataManager(Context context, SensorAutoDetection detectionMode ){
			this(context, 
					new DatabaseManager(context), new AndroidBluetoothManager(context), new USBManager(context), detectionMode );
		}
		
		public AndroidSensorDataManager(Context context, DatabaseManager dbManager,AndroidBluetoothManager btManager,
				USBManager usbManager, SensorAutoDetection detectionMode) {
			
			svcContext = context;
			this.databaseManager = dbManager;

			sensors = new Hashtable<String, AndroidSensorLinkManager>();
			channelManagers = new HashMap<CommunicationChannelType,AndroidChannelManager>();	
			
			channelManagers.put(btManager.getCommChannelType(), btManager);
			channelManagers.put(usbManager.getCommChannelType(), usbManager);
			
			if ( detectionMode == SensorAutoDetection.ALL ){
				btManager.setSensorManager(this);
				usbManager.setSensorManager(this);
				btManager.initializeSensors();
				usbManager.initializeSensors();
				
			}
			else if ( detectionMode == SensorAutoDetection.BLUETOOTH ) {
				btManager.setSensorManager(this);
				btManager.initializeSensors();
				
			}
			else if ( detectionMode == SensorAutoDetection.USB) {
				usbManager.setSensorManager(this);
				usbManager.initializeSensors();
				
			}
			
			queryNupdateSensorDriverTypes();
			
			if ( detectionMode == SensorAutoDetection.USB ){
				//has to find USB or report failure
				//give 5 seconds to detect the device
				
				//Wait for the device to be detected
				long startTime = System.currentTimeMillis();
				long endTime = startTime;
				boolean isFound = false;
				while ( (endTime - startTime) < 5000 &&  !isFound ) {
					
					isFound = allocateDynamicDriverToDynamicDetectedSensor(usbManager);
					endTime = System.currentTimeMillis();
					if ( !isFound ){
						try {
							Thread.sleep(1000);//wait for 1 second
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				
			}
			
			//startCollectSensorUsingThreads();
			
		}

		private boolean allocateDynamicDriverToDynamicDetectedSensor(
				USBManager usbManager) {
			try{
				List<BraceForce.SensorLink.DiscoverableDevice> sensorList = usbManager.getDiscoverableSensor();
				if(sensorList != null) {
					// load sensor list into list adapter
					for (BraceForce.SensorLink.DiscoverableDevice device : sensorList) {
						String id = device.getDeviceId();
						BraceForce.SensorLink.DiscoverableDeviceState state = device.getDeviceState();
						Log.d("BraceForce device","Id: "+id+"  state: "+state);
						String name = device.getDeviceName();
	
						List<DriverType> drivers = driverTypes;
						
						for(DriverType driver: drivers) {
							if ( driver.getCommunicationChannelType() == CommunicationChannelType.USB ) {
								//allocate the first one as blind binding
								//to do-> find a good allocation algorithm to map dynamic driver to
								//dynamically detected USB driver
								addSensor(id, driver);
								return true;
							}
						}
					}
				}
			}
			catch (Exception ex){
				return false;
			}
			return false;
		}			
		
		public AndroidSensorDataManager(Context context, DatabaseManager dbManager,AndroidBluetoothManager btManager,
				USBManager usbManager ) {
			
			this(context, dbManager, btManager, usbManager, SensorAutoDetection.None );
			
		}			
		
		public void startCollectSensorUsingThreads(){
			workerThread = new AndroidWorkerThread(svcContext, this);
			workerThread.start();
		}
		
		public void initializeRegisteredSensors() {
			// discover built in sensors
			android.hardware.SensorManager builtInSensorManager = (android.hardware.SensorManager) svcContext.getSystemService(Context.SENSOR_SERVICE);
			if(builtInSensorManager != null) {
				List<android.hardware.Sensor> deviceSensors = builtInSensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL);
				for(android.hardware.Sensor hwSensor : deviceSensors) {
					BuiltInSensorType sensorType = BuiltInSensorType.convertToBuiltInSensor(hwSensor.getType());
					if(sensorType != null) {
						try {
							String phoneUniqueID = "Brace";
							try{
								phoneUniqueID =  Secure.getString(svcContext.getContentResolver(),
                                        Secure.ANDROID_ID);
							}
							catch (Exception ex){
								
							}
							String id = sensorType.name() + "-On-" + phoneUniqueID;
							Log.d(TAG,"Found sensor "+ id);
							AndroidSensorLinkManager sensor = new BraceBuiltInSensor(sensorType, builtInSensorManager, id, svcContext);			
							sensors.put(id, sensor);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			// load sensors from the database		
			for(AndroidChannelManager channelManager : channelManagers.values()) {
				CommunicationChannelType type = channelManager.getCommChannelType();
				Log.d(TAG, "Load from DB:" + type.name());

				List<SensorMetaData> savedSensorList =  databaseManager.sensorList(type);

				for(SensorMetaData sensorData : savedSensorList) {
					Log.d(TAG, "Sensor in DB:" + sensorData.id + " Type:" + sensorData.type);
					DriverType driverType = getDriverType(sensorData.type);

					if(driverType != null) {
						Log.d(TAG,"initing sensor from DB: id: " + sensorData.id + 
								" driverType: " + sensorData.type + " state " + sensorData.state);

						if(connectToDriver(sensorData.id,driverType)) {						
							Log.d(TAG,sensorData.id + " connected to driver " + sensorData.type);

							if(sensorData.state == DetailedSensorState.CONNECTED) {
								try {
									channelManager.sensorConnect(sensorData.id);
//									updateSensorState(sensorData.id, DetailedSensorState.CONNECTED);
									Log.d(TAG,"connected to sensor " + sensorData.id  + " over " + channelManager.getCommChannelType());
								}
								catch(SensorNotFoundException snfe) {
									updateSensorState(sensorData.id, DetailedSensorState.DISCONNECTED);
									Log.d(TAG,"SensorNotFoundException. unable to connect to sensor " + sensorData.id + " over " + channelManager.getCommChannelType());
								}
							}
						}
					}
					else {
						Log.e(TAG,"driver NOT FOUND for type : " + sensorData.type);
					}
				}					
			}		
		}
		
		
		private boolean connectToDriver(String id,DriverType driver) {
			// create the sensor		
			AndroidExternalSensorLinkManager sensorFacade = null;
			try {
				AndroidDriverCommunicator sensorDriver = new AndroidGenericDriverProxy(driver.getSensorPackageName(), driver.getSensorDriverAddress(), this.svcContext);
				sensorFacade = new AndroidExternalSensorLinkManager(id,sensorDriver,channelManagers.get(driver.getCommunicationChannelType()), svcContext);			
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			//put facade instead of driver
			sensors.put(id, sensorFacade);
			return true;		
		}
		
		private void shutdownAllSensors() {
			for (AndroidSensorLinkManager sensor : sensors.values()) {
				try {
					sensor.shutdown();
				}
				catch(SensorNotFoundException snfe) {
					snfe.printStackTrace();
				}
			}
		}
		

		public void queryNupdateSensorDriverTypes() {
			List<DriverType> allDrivers = new ArrayList<DriverType>();
			List<DriverType> btDrivers = AndroidSensorDriverDiscovery.getAllDriversForChannel(svcContext, CommunicationChannelType.BLUETOOTH );
			List<DriverType> usbDrivers = AndroidSensorDriverDiscovery.getAllDriversForChannel(svcContext, CommunicationChannelType.USB);
			allDrivers.addAll(btDrivers);
			allDrivers.addAll(usbDrivers);
			driverTypes = allDrivers;
		}

		public DriverType getDriverType(String type) {
			DriverType foundDriverType = null;
			for(DriverType driverType : driverTypes) {
				if(driverType.getSensorType().equals(type)) {
					foundDriverType = driverType;
					break;
				}
			}		
			return foundDriverType;
		}


		public AndroidSensorLinkManager getSensor(String id) {
			return sensors.get(id);
		}

		/**
		 * Get the sensor status
		 * @param id Sensor id.
		 * @return SensorState as determined by communication manager.
		 */

		public SensorStateMachine getSensorState(String id) {		
			Log.d(TAG,"getting sensor state");
			AndroidSensorLinkManager sensor = sensors.get(id);
			if (sensor == null) {
				Log.e(TAG, "Can't find sensor type");
				return null;
			}
			
			AndroidChannelManager cm = channelManagers.get(sensor.getCommunicationChannelType());		
			if (cm == null) {
				Log.e(TAG, "unkown channel type: " + sensor.getCommunicationChannelType());
				return null;
			}
			
			return cm.getSensorStatus(id);		
		}

		public void addSensorDataPacket(String id, SensorDataPacket sdp) {
			AndroidSensorLinkManager sensor = sensors.get(id);
			if (sensor != null) {
				sensor.addSensorDataPacket(sdp);
			} else {
				Log.e(TAG, "can't route data for sensor ID: " + id);
			}
		}
		
		public void addSensorDataPacket(String id, AndroidSensorDataPacket sdp) {
			AndroidSensorLinkManager sensor = sensors.get(id);
			if (sensor != null) {
				sensor.addSensorDataPacket(sdp);
			} else {
				Log.e(TAG, "can't route data for sensor ID: " + id);
			}
		}

		public void shutdown() {
			try
			{
				shutdownAllSensors(); 
				this.databaseManager.closeDb();
				//try{((AndroidWorkerThread) workerThread).stopthread();	}catch(Exception ex){}
				
				for ( AndroidSensorThread thread: sensorThreads ){
					try{thread.stopthread();}catch(Exception ex){}
				}
				Iterator it = this.channelManagers.entrySet().iterator();
				while ( it.hasNext() ) {
					Map.Entry pairs = (Map.Entry)it.next();
					AndroidChannelManager acm = (AndroidChannelManager)pairs.getValue();
					acm.shutdown();
				}
			}
			catch (Exception ex){
				
			}
		}		

		public boolean addSensor(String id, DriverType driver) {

			if(driver == null) {
				return false;
			}

			Log.d(TAG,"sensor type: " + driver);		
			connectToDriver(id,driver);
		
			databaseManager.sensorInsert(id, driver.getSensorType(), driver.getSensorType(), 
					DetailedSensorState.DISCONNECTED, 
					driver.getCommunicationChannelType());
			
			return true;
		}
		
		public List<AndroidSensorLinkManager> getSensorsUsingContentProvider() {
			List<AndroidSensorLinkManager> sensorList = new ArrayList<AndroidSensorLinkManager>();

			for (AndroidSensorLinkManager sensor : sensors.values()) {
				if (sensor.usingContentProvider()) {
					sensorList.add(sensor);
				}
			}
			return sensorList;
		}


		public void removeAllSensors() {
			shutdownAllSensors();
			sensors = new Hashtable<String, AndroidSensorLinkManager>();
			
			databaseManager.deleteAllSensors();
			
			
		}
		

		

		public List<DriverType> getDriverTypes() {
			return driverTypes;
		}


		public List<AndroidSensorLinkManager> getRegisteredSensors(CommunicationChannelType channelType) {
			List<AndroidSensorLinkManager> sensorList = new ArrayList<AndroidSensorLinkManager>();

			for (AndroidSensorLinkManager sensor : sensors.values()) {
				if (sensor.getCommunicationChannelType() == channelType) {
					sensorList.add(sensor);
				}
			}
			return sensorList;
		}
		
		public List<AndroidSensorLinkManager> getAllAndroidSensors(CommunicationChannelType channelType) {
			detectSensorInit();
			List<AndroidSensorLinkManager> sensorList = new ArrayList<AndroidSensorLinkManager>();

			for (AndroidSensorLinkManager sensor : sensors.values()) {
				if (sensor.getCommunicationChannelType() == channelType) {
					sensorList.add(sensor);
				}
			}
			return sensorList;
		}
		
		public List<AndroidSensorLinkManager> getAllAndroidSensors() {
			detectSensorInit();
			List<AndroidSensorLinkManager> sensorList = new ArrayList<AndroidSensorLinkManager>();

			for (AndroidSensorLinkManager sensor : sensors.values()) {
			//	if (sensor.getCommunicationChannelType() == channelType) {
					sensorList.add(sensor);
				//}
			}
			return sensorList;
		}

		private void detectSensorInit() {
			if ( sensors.isEmpty() ){
				initializeRegisteredSensors();
			}
		}
		
		private void detectSensorInit(boolean override) {
			
			if ( override ) { sensors.clear();}
			if ( sensors.isEmpty() ){
				initializeRegisteredSensors();
			}
		}

		public void updateSensorState(String id, DetailedSensorState state) {
			databaseManager.sensorUpdateState(id, state);		
		}
		

		public DetailedSensorState querySensorState(String id) {
			return databaseManager.sensorQuerySensorState(id);
		}

		@Override
		public BraceForceSensorLinkManager getSensorLinkManager(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<BraceForceSensorLinkManager> getRegisteredSensorLinkManagers(
				CommunicationChannelType channelType) {
			// TODO Auto-generated method stub
			List<AndroidSensorLinkManager> androidLinkManagers =  getAllAndroidSensors(channelType);
			return (List<BraceForceSensorLinkManager>)(List<?>)androidLinkManagers;
		}

		@Override
		public List<BraceForceSensorLinkManager> getAllRegisteredSensorLinkManagers() {
			// TODO Auto-generated method stub
			List<AndroidSensorLinkManager> androidLinkManagers =  getAllAndroidSensors();
			return (List<BraceForceSensorLinkManager>)(List<?>)androidLinkManagers;
		}
		

		@Override
		public void startPollingSensor(String id,
				GenericSensorDataContentProvider dataProvider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<Hashtable> returnSensorData(String id,  boolean clearDataBuffer){
			 List<Bundle> androidData = returnSensorDataAndroid(id, clearDataBuffer);
			 List<Hashtable> standardJavaData = new ArrayList<Hashtable>();
			 for ( Bundle bundleObj : androidData ){
				 Hashtable bundleHT = new Hashtable();
				 Set<String> keysets = bundleObj.keySet();
				 for ( String bundleKey : keysets ){
					 Object bundleValue = bundleObj.get(bundleKey);
					 bundleHT.put(bundleKey, bundleValue);
				 }
				 standardJavaData.add(bundleHT);
			 }
			 return standardJavaData;
		}
		
		
		@Override
		public void startSensorDataAcquistionEventModel(String id,
				GenericSensorDataContentProvider dataProvider,
				SensorDataChangeListener listener) {
			
		}

		
		public void startPollingSensor(String id,
				AndroidSensorDataContentProvider dataProvider, boolean override) {
			// TODO Auto-generated method stub
			detectSensorInit(override);
			
			try {
				AndroidSensorThread sensorThread = new AndroidSensorThread(this, dataProvider, null, null, id, 
						SensorRetrievalMode.PULLCONTINUOUS, null);
				sensorThreads.add(sensorThread);
				sensorThread.start();
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParameterMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SensorNotStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		@Override
		public void startPollingSensor(String id,
				AndroidSensorDataContentProvider dataProvider) {
			// TODO Auto-generated method stub
			detectSensorInit();
			
			try {
				AndroidSensorThread sensorThread = new AndroidSensorThread(this, dataProvider, null, null, id, 
						SensorRetrievalMode.PULLCONTINUOUS, null);
				sensorThreads.add(sensorThread);
				sensorThread.start();
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParameterMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SensorNotStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		
		
		public void startSensorDataAcquistionPollingMode(String id,
				AndroidSensorDataContentProvider dataProvider,
				SensorDataChangeListener listener, int pollingWindow, boolean override) {
			// TODO Auto-generated method stub
			detectSensorInit(override);
			
			try {
				AndroidSensorThread sensorThread = new AndroidSensorThread(this, dataProvider, null, null, id, 
						SensorRetrievalMode.PULLONCE, listener );
				sensorThread.overridePollingWindow(pollingWindow);
				sensorThreads.add(sensorThread);
				sensorThread.start();
				
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParameterMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SensorNotStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}
		
		//override method with polling time window specified
		public void startSensorDataAcquistionPollingMode(String id,
				AndroidSensorDataContentProvider dataProvider,
				SensorDataChangeListener listener, int pollingWindow) {
			// TODO Auto-generated method stub
			detectSensorInit();
			
			try {
				AndroidSensorThread sensorThread = new AndroidSensorThread(this, dataProvider, null, null, id, 
						SensorRetrievalMode.PULLONCE, listener );
				sensorThread.overridePollingWindow(pollingWindow);
				sensorThreads.add(sensorThread);
				sensorThread.start();
				
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParameterMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SensorNotStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}
		public void startSensorDataAcquistionPollingMode(String id,
				AndroidSensorDataContentProvider dataProvider,
				SensorDataChangeListener listener) {
			// TODO Auto-generated method stub
			detectSensorInit();
			
			try {
				AndroidSensorThread sensorThread = new AndroidSensorThread(this, dataProvider, null, null, id, 
						SensorRetrievalMode.PULLONCE, listener );
				sensorThreads.add(sensorThread);
				sensorThread.start();
				
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParameterMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SensorNotStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}
		
		public void startSensorDataAcquistionEventModel(String id,
				AndroidSensorDataContentProvider dataProvider,
				SensorDataChangeListener listener, boolean override) {
			// TODO Auto-generated method stub
			detectSensorInit(override);
			
			try {
				AndroidSensorThread sensorThread = new AndroidSensorThread(this, dataProvider, null, null, id, 
						SensorRetrievalMode.PUSH, listener );
				sensorThreads.add(sensorThread);
				sensorThread.start();
				
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParameterMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SensorNotStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}
		
		@Override
		public void startSensorDataAcquistionEventModel(String id,
				AndroidSensorDataContentProvider dataProvider,
				SensorDataChangeListener listener) {
			// TODO Auto-generated method stub
			detectSensorInit();
			
			try {
				AndroidSensorThread sensorThread = new AndroidSensorThread(this, dataProvider, null, null, id, 
						SensorRetrievalMode.PUSH, listener );
				sensorThreads.add(sensorThread);
				sensorThread.start();
				
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParameterMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SensorNotStartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}

		public List<Bundle> returnSensorDataAndroidAfterInitialization(String id, boolean clearDataBuffer) {
			AndroidSensorLinkManager braceSensor = getSensor(id);
			Log.d("Sensor node", "get sensor data in event change");
			return returnSensorDataAndroid(braceSensor, clearDataBuffer);
		}
		
		public List<Bundle> returnSensorDataAndroid(AndroidSensorLinkManager androidSensor, boolean clearDataBuffer){
			List<Bundle> dataGathered=	androidSensor.getSensorData(0);
			if ( clearDataBuffer ){
				androidSensor.dataBufferReset();
			}
			return dataGathered;
		}

		public List<Bundle> returnSensorDataAndroid(String id, boolean clearDataBuffer) {
			// TODO Auto-generated method stub
			detectSensorInit();
			AndroidSensorLinkManager braceSensor = getSensor(id);
			
			//connect to sensor
			try {
				
				braceSensor.connect();
				
			
					
					//start the sensor
					if ( !braceSensor.startSensor() ){
						try {
							throw new SensorNotStartException("could not start sensor!");
						} catch (SensorNotStartException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				List<Bundle> dataGathered=	braceSensor.getSensorData(0);
				if ( clearDataBuffer ){
					braceSensor.dataBufferReset();
				}
				braceSensor.disconnect();
				braceSensor.shutdown();
				return dataGathered;
				
			} catch (SensorNotFoundException e) {
				// TODO Auto-generated catch block
				
				Log.d("Sensor retrieval", "Exception: " + e.toString());
				return null;
			}
		}

		
	}

