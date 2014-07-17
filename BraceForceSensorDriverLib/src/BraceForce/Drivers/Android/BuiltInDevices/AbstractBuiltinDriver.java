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
package BraceForce.Drivers.Android.BuiltInDevices;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import braceForce.Drivers.Android.AndroidSensorParameter;



import BraceForce.Drivers.ParameterMissingException;
import BraceForce.Drivers.SensorDataPacket;
import BraceForce.Drivers.SensorDataResponse;
import braceForce.Drivers.Android.AndroidDeviceDrivers;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public abstract class AbstractBuiltinDriver implements AndroidDeviceDrivers {

	protected  static final String timestamp_param = "timestamp";
	protected List<AndroidSensorParameter> sensorParams = new ArrayList<AndroidSensorParameter>();
	
	@Override
	public byte[] configureCmd(String setting, Bundle config) {
		return new byte[0];
	}

	@Override
	public byte[] getSensorDataCmd() {
		return new byte[0];
	}

	@Override
	public byte[] startCmd() {
		return new byte[0];
	}

	@Override
	public byte[] stopCmd() {
		return new byte[0];
	}
	
	public byte[] sendDataToSensor(Bundle dataToFormat) {
		return null;
	}	
	
	@Override
	public List<AndroidSensorParameter> getDriverParameters() {
		return sensorParams;
	}
	
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	

	public byte[] sendDataToSensor(Hashtable dataToFormat) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public byte[] encodeDataToSendToSensor(Bundle dataToFormat)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
