/*
 * Copyright (C) 2013 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package BraceForce.SensorData.Android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import BraceForce.Global.Constants;
import BraceForce.SensorData.BraceSensorDataManager;
import BraceForce.SensorLink.BraceForceSensorLinkManager;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.DetailedSensorState;
import BraceForce.SensorLink.DiscoverableDevice;
import BraceForce.SensorLink.DriverType;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class AndroidBluetoothManager extends AndroidAbstractChannelManagerBase {

	// logging
	private static final String LOGTAG = "BTManager";

	// bluetooth
	private BluetoothAdapter mBtAdapter;

	// sensor map
	private HashMap<String, AndroidBluetoothSensor> mSensorMap;

	// discoverable device map
	private HashMap<String, AndroidBluetoothDiscoverableDevice> mDiscoverableDeviceMap;
	
	private boolean receiversRegistered = false;


	/**
	 * Constructor
	 * 
	 * @param context
	 *            application context
	 * @param database
	 *            service database
	 */
	public AndroidBluetoothManager(Context context) {
		super(context, CommunicationChannelType.BLUETOOTH);

		// Create state data structures
		mSensorMap = new HashMap<String, AndroidBluetoothSensor>();
		mDiscoverableDeviceMap = new HashMap<String, AndroidBluetoothDiscoverableDevice>();

		// register broadcast receivers
		 
		// ACTION_FOUND
		IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mContext.registerReceiver(mBluetoothStateChangeReceiver, f1);

		// BOND STATE CHANGE
		IntentFilter f2 = new IntentFilter(
				BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		mContext.registerReceiver(mBluetoothStateChangeReceiver, f2);

		// DISCOVERY FINISHED
		IntentFilter f3 = new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mContext.registerReceiver(mDiscoveryFinished, f3);

		// Get Reference To Local BluetoothAdapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		receiversRegistered = true;
		
	}


	@Override
	public void initializeSensors() {
		if (mBtAdapter != null) {
			Set<BluetoothDevice> btds = mBtAdapter.getBondedDevices();
			Iterator<BluetoothDevice> itr = btds.iterator();
			while (itr.hasNext()) {
				BluetoothDevice device = itr.next();
				if(device != null) {
					AndroidBluetoothDiscoverableDevice btDevice = new AndroidBluetoothDiscoverableDevice(device, mSensorManager);
					mDiscoverableDeviceMap.put(device.getAddress(), btDevice);
				}
			}
		}
		
	}

	public synchronized List<DiscoverableDevice> getDiscoverableSensor() {
		Collection<AndroidBluetoothDiscoverableDevice> collection = mDiscoverableDeviceMap.values();
		List<DiscoverableDevice> deviceList = new ArrayList<DiscoverableDevice>();
		deviceList.addAll(collection);
		return deviceList;
	}
	
	/**
	 * Used By SensorService To Manipulate Individual Sensors
	 * 
	 * @return bluetooth sensor
	 */
	public synchronized AndroidBluetoothSensor getSensor(String id) {
		return mSensorMap.get(id);
	}

	/**
	 * Cleanup Method For Bluetooth Manager
	 */
	@Override
	public synchronized void shutdown() {

		// Cleanup All Of The Bluetooth Sensor Connections
		if (mSensorMap != null) {
			Collection<AndroidBluetoothSensor> c = mSensorMap.values();
			Iterator<AndroidBluetoothSensor> it = c.iterator();

			while (it.hasNext())
				it.next().kill();
		}

		stopSearchingForSensors();

		if (receiversRegistered ) {
			mContext.unregisterReceiver(mBluetoothStateChangeReceiver);
			mContext.unregisterReceiver(mDiscoveryFinished);
		}
		
		receiversRegistered = false;
	}

	/**
	 * Search For Sensors -- Bluetooth discovery
	 */
	public synchronized void searchForSensors() {
		if (mBtAdapter != null) {
			if (mBtAdapter.isDiscovering())
				mBtAdapter.cancelDiscovery();
			mBtAdapter.startDiscovery();
		}
	}

	/**
	 * Stop Searching For Sensors -- Bluetooth discovery
	 */
	public synchronized void stopSearchingForSensors() {
		if (mBtAdapter != null) {
			if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
			}
		}
	}

	/**
	 * Sensor register
	 * 
	 * @return True If That Is The Case, Otherwise Returns False
	 */
	public synchronized boolean sensorRegister(String id, DriverType sensorType) {
		Log.d(LOGTAG, "In sensor register.");
		
		if(id == null || sensorType == null) {
			Log.d(LOGTAG, "registration FAILED. sensorID or sensorType is null");
			return false;
		}

		if (mBtAdapter != null) {
			Log.d(LOGTAG, "Has BTAdapter");
			BluetoothDevice device = mBtAdapter.getRemoteDevice(id);
			if (device == null) {
				Log.d(LOGTAG, "FAILED to get BT device to register!");
				return false;
			}

			Log.d(LOGTAG, "Got BT device " + device.getAddress());

			AndroidBluetoothSensor bs = getSensor(id);
			if (bs == null) {
				Log.d(LOGTAG, "BS is null, ADDING sensor to sensor manager");

				mSensorManager.addSensor(id, sensorType);
//				mDatabaseManager.sensorUpdateRegState(id.toString(),
//						SensorRegistrationState.REGISTERED, sensorType.getSensorType());
				Log.d(LOGTAG, "Added bluetooth sensor to sensor manager.");
				Intent i = new Intent();
				i.setAction(Constants.BT_STATE_CHANGE);
				mContext.sendBroadcast(i);
			}

			if (bs != null) {
				stopSearchingForSensors();
				return true;
			}
			return true;
		}
		return false;
	}

	/**
	 * Start sensor recording
	 */
	public synchronized void startSensorDataAcquisition(String id, byte[] cmd) {
		Log.d(LOGTAG, "Sensor record: " + id);

		// clear any previous buffer data
		AndroidSensorLinkManager sensor = mSensorManager.getSensor(id);
		sensor.dataBufferReset();

		// inform bluetooth manager to start recording a sensor
		AndroidBluetoothSensor bts = getSensor(id);

		if (bts != null) {
			bts.activiate();

			if(cmd != null && cmd.length > 0)
				sensorWrite(id,cmd);
		}
	}

	/**
	 * Connect A Sensor If Not Already Connected
	 */
	public synchronized void sensorConnect(String id) throws SensorNotFoundException {
		Log.d(LOGTAG, "Sensor connect: " + id);
		AndroidBluetoothSensor bts = getSensor(id);

		if (bts == null || bts.getState() == Thread.State.TERMINATED) {
			AndroidSensorLinkManager sensor = mSensorManager.getSensor(id);
			if (sensor == null) {
				throw new SensorNotFoundException(
				"Sensor not found in sensor manager");
			}
			Log.d(LOGTAG, "Bluetooth sensor created");
			BluetoothDevice device = mBtAdapter.getRemoteDevice(id);

			bts = new AndroidBluetoothSensor(sensor, device,this);
			mSensorMap.put(id, bts);
		}
		
		if (bts != null) {
			Log.d(LOGTAG, "Connecting to physical sensor");
			bts.connect();
		}
	}

	public synchronized void sensorWrite(String id, byte[] message) {
		Log.d(LOGTAG, "Sensor write: " + id + ", message: " + new String(message));
		AndroidBluetoothSensor bts = getSensor(id);

		if (bts != null)
			bts.write(new String(message));
	}

	/**
	 * Stop sensor recording
	 */
	public synchronized void stopSensorDataAcquisition(String id, byte[] cmd) {
		Log.d(LOGTAG, "Sensor record stop: " + id);

		AndroidBluetoothSensor bts = getSensor(id);

		// inform bluetooth manager to stop recording a sensor
		if (bts != null) {
			if(cmd != null && cmd.length > 0)
				sensorWrite(id,cmd);
			
			bts.deactivate();
			
		}
	}

	@Override
	public synchronized void sensorDisconnect(String id) throws SensorNotFoundException {
		AndroidBluetoothSensor bts = getSensor(id);

		if (bts != null) {
			Log.d(LOGTAG, "Disconnecting from physical sensor");
			bts.kill();
		}
	}

	@Override
	public synchronized void removeAllSensors() {
		// Cleanup All Of The Bluetooth Sensor Connections
		if (mSensorMap != null) {
			Collection<AndroidBluetoothSensor> c = mSensorMap.values();
			Iterator<AndroidBluetoothSensor> it = c.iterator();

			while (it.hasNext()) {
				it.next().kill();
			}
		}

		mSensorMap = new HashMap<String, AndroidBluetoothSensor>();
	}
	
	public void updateSensorStateInDb(String sensorID, DetailedSensorState state) {
		this.mSensorManager.updateSensorState(sensorID, state);
	}

	
	/**
	 * Bluetooth Broadcast State Change
	 */
	private final BroadcastReceiver mBluetoothStateChangeReceiver = new BroadcastReceiver() {

		/**
		 * Called when broadcast is received.
		 * 
		 * @param context
		 *            application context
		 * @param intent
		 *            broadcast intent
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			BluetoothDevice parceledDevice = intent
			.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			String id = parceledDevice.getAddress();
			AndroidBluetoothDiscoverableDevice bdd = mDiscoverableDeviceMap.get(id);
			BluetoothDevice deviceObj = mBtAdapter.getRemoteDevice(id);
			
			if(deviceObj == null) {
				// TODO: if it's null maybe we should signal a disconnect
				return;
			}
			Log.d(LOGTAG, "Bluetooth Device State Was Updated " + id);
			if(bdd == null) {
				bdd = new AndroidBluetoothDiscoverableDevice(deviceObj, mSensorManager);
				mDiscoverableDeviceMap.put(id, bdd);
			} else {
				bdd.connectionRestored();
				bdd.updateBluetoothDeviceObject(deviceObj);
			}
			
			Intent i = new Intent();
			i.setAction(Constants.BT_STATE_CHANGE);
			mContext.sendBroadcast(i);
		}
	};

	/**
	 * Bluetooth Broadcast Discovery Finished Receiver
	 */
	private final BroadcastReceiver mDiscoveryFinished = new BroadcastReceiver() {

		/**
		 * Called when broadcast is received.
		 * 
		 * @param context
		 *            application context
		 * @param intent
		 *            broadcast intent
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent i = new Intent();
			i.setAction(Constants.ACTION_SCAN_FINISHED);
			mContext.sendBroadcast(i);
		}
	};


	@Override
	public void setSensorManager(BraceSensorDataManager sensorManager) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<BraceForceSensorLinkManager> getRegisteredSensorLinkManagerList(
			CommunicationChannelType commType) {
		// TODO Auto-generated method stub
		return null;
	}
}
