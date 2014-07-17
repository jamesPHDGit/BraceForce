package BraceForce.SensorLink.Android.BuiltIn;

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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import braceForce.Drivers.Android.AndroidSensorDataPacket;
import braceForce.Drivers.Android.AndroidSensorDataParseResponse;
import braceForce.Drivers.Android.AndroidSensorParameter;

import BraceForce.Drivers.ParameterMissingException;
import BraceForce.Drivers.SensorDataPacket;
import braceForce.Drivers.Android.AndroidDeviceDrivers;
import BraceForce.Drivers.Android.BuiltInDevices.AbstractBuiltinDriver;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.SensorEventModel;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class BraceBuiltInSensor extends SensorEventModel implements AndroidSensorLinkManager,
		SensorEventListener {

	// logging
	private static final String LOGTAG = "BuiltInSensor";

	private static final String DELIMINATOR = "\n";


	// sensor description
	private final BuiltInSensorType sensorType;
	private final SensorManager mBuiltInSensorManager;
	private final String sensorId;
	private final AndroidDeviceDrivers sensorDriver;

	private Context mContext;
	
	// state
	private Queue<AndroidSensorDataPacket> buffer;
	private byte[] remainingBytes;

	private int rate;

	public BraceBuiltInSensor(BuiltInSensorType type,
			SensorManager builtInSensorManager, String sensorID, Context context) throws Exception {
		this.sensorType = type;
		this.mBuiltInSensorManager = builtInSensorManager;
		this.mContext = context;
		this.sensorId = sensorID;
		Class<? extends AbstractBuiltinDriver> sensorClass = sensorType
				.getDriverClass();
		Constructor<? extends AbstractBuiltinDriver> constructor;
		constructor = sensorClass.getConstructor();
		this.sensorDriver = constructor.newInstance();
		this.buffer = new ConcurrentLinkedQueue<AndroidSensorDataPacket>();
		this.rate = SensorManager.SENSOR_DELAY_FASTEST;
	}

	@Override
	public void connect() throws SensorNotFoundException {
	
		Sensor sensor = mBuiltInSensorManager.getDefaultSensor(sensorType
				.getType());
		if (sensor == null) {
			throw new SensorNotFoundException("Unable to locate sensor "
					+ sensorType.name());
		}
	}

	@Override
	public void disconnect() throws SensorNotFoundException {
		this.stopSensor();
		
	}

	@Override
	public void shutdown() throws SensorNotFoundException {
		this.disconnect();
	}

	public void configure(String setting, Bundle params) throws ParameterMissingException {
		if (setting.equals("rate")) {
			int tmpRate = params.getInt("rate");

			// TODO: remove check after application moves to higher API level
			// As of Android 3.0 (API Level 11) you can also specify the delay
			// as an absolute value (in microseconds).
			if (tmpRate == SensorManager.SENSOR_DELAY_NORMAL
					|| tmpRate == SensorManager.SENSOR_DELAY_UI
					|| tmpRate == SensorManager.SENSOR_DELAY_GAME
					|| tmpRate == SensorManager.SENSOR_DELAY_FASTEST) {
				rate = tmpRate;
			}
		}
	}

	@Override
	public boolean startSensor() {
	    Sensor sensor = mBuiltInSensorManager.getDefaultSensor(sensorType.getType());
		if (sensor != null) {
			mBuiltInSensorManager.registerListener(this, sensor, rate);
			Log.d("BraceInternalSensor", "Sensor started-listened attached");
			return true;
		} else {
			return false;
		}

	}

	@Override
	public boolean stopSensor() {
		mBuiltInSensorManager.unregisterListener(this);
		return true;
	}

	@Override
	public List<Bundle> getSensorData(long maxNumReadings) {
		
		ArrayList<AndroidSensorDataPacket> rawData = new ArrayList<AndroidSensorDataPacket>();
		
		//dont clear buffer blindly 
		if ( buffer.isEmpty() ){
			return new ArrayList<Bundle>();
		}
		
		rawData.addAll(buffer);
		buffer.clear();
		AndroidSensorDataParseResponse response;
		try {
			response = sensorDriver.getSensorData(
					maxNumReadings, rawData, remainingBytes);
			remainingBytes = response.getRemainingData();
			return response.getSensorData();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CommunicationChannelType getCommunicationChannelType() {
		return CommunicationChannelType.BUILTIN;
	}

	@Override
	public String getSensorID() {
		return sensorId;
	}

	

	@Override
	public void dataBufferReset() {
		Log.d(LOGTAG, "dataBufferReset: clearing buffer for sensor ");
		if (buffer != null) {
			buffer = new ConcurrentLinkedQueue<AndroidSensorDataPacket>();
		}
	}

	@Override
	public void addSensorDataPacket(AndroidSensorDataPacket packet) {
		buffer.add(packet);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// DO NOTHING YET

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == sensorType.getType()) {
			// encode the float array into a byte array
			String sdpValuesStr = "";
			for (float value : event.values) {
				sdpValuesStr += Float.toString(value) + DELIMINATOR;
			}
			Log.d(LOGTAG, "Raw sensor Data:  " + sdpValuesStr);
			// populate sensor data packet
			AndroidSensorDataPacket sdp = new AndroidSensorDataPacket(sdpValuesStr.getBytes(), event.timestamp);
			addSensorDataPacket(sdp);
			notifyListeners(getSensorID(), sdp, this.mContext  );
		}

	}


	public void sendDataToSensor(Bundle data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean usingContentProvider() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void configure(String setting, Hashtable params)
			throws ParameterMissingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Hashtable> getSensorDatainCollection(long maxNumReadings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendDataToSensor(Hashtable dataToEncode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSensorDataPacket(SensorDataPacket packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBuiltin() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<AndroidSensorParameter> returnDriverParameters() {
		// TODO Auto-generated method stub
		try {
			return sensorDriver.getDriverParameters();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}



}
