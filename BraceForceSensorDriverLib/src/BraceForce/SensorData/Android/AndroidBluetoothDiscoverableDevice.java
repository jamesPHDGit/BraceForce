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

import java.util.concurrent.atomic.AtomicBoolean;

import BraceForce.SensorLink.DiscoverableDevice;
import BraceForce.SensorLink.DiscoverableDeviceState;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;
import android.bluetooth.BluetoothDevice;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class AndroidBluetoothDiscoverableDevice implements DiscoverableDevice {
	
	private BluetoothDevice btDevice;
	
	private AndroidSensorDataManager sensorMgr;
	
	private AtomicBoolean connectionLost;
	
	public AndroidBluetoothDiscoverableDevice(BluetoothDevice device, AndroidSensorDataManager sensorManager) {
		this.btDevice = device;
		this.sensorMgr = sensorManager;		
		this.connectionLost = new AtomicBoolean(false); 				
	}
	
	public synchronized void updateBluetoothDeviceObject(BluetoothDevice updatedBtDevice) {
		this.btDevice = updatedBtDevice;
	}
	
	@Override
	public synchronized String getDeviceId() {
		return btDevice.getAddress();
	}
	
	@Override
	public synchronized DiscoverableDeviceState getDeviceState() {
		if(connectionLost.get())
			return DiscoverableDeviceState.OUT_OF_RANGE;
		
		boolean bonded = (btDevice.getBondState() == BluetoothDevice.BOND_BONDED);
		if(!bonded)
			return DiscoverableDeviceState.UNPAIRED;
		
		AndroidSensorLinkManager sensor = sensorMgr.getSensor(getDeviceId());
		if(sensor != null)
			return DiscoverableDeviceState.REGISTERED;
		
		return DiscoverableDeviceState.PAIRED;
	}
	
	@Override
	public void connectionLost() {
		connectionLost.set(true);
	}
	
	@Override
	public void connectionRestored() {
		connectionLost.set(false);
	}

	@Override
	public synchronized String getDeviceName() {
		return btDevice.getName();
	}
	
}
