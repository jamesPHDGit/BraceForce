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
package BraceForce.SensorData.Manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import braceForce.Drivers.Android.AndroidSensorDataPacket;

import BraceForce.Drivers.SensorDataPacket;
import BraceForce.Global.Constants;
import BraceForce.SensorData.Android.AndroidSensorDataManager;
import BraceForce.SensorData.USB.USBDiscoverableDevice;
import BraceForce.SensorLink.DetailedSensorState;
import BraceForce.SensorLink.DiscoverableDevice;
import BraceForce.SensorLink.DriverType;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.USBCommSubChannel;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class AndroidFTDISubChannel implements USBCommSubChannel {
	
	private static final String TAG = "FTDIChannel";

	private static final String FIXED_ID = "FTDI";
	
	private static final int MAX_BUFFER_SIZE = 64;

	private static final int FTDI_VENDOR = 1027;

	private static final int FTDI_PRODUCT = 24577;

	// private static final int IN_REQ_TYPE = UsbConstants.USB_TYPE_VENDOR
	// | UsbConstants.USB_DIR_IN;

	private static final int OUT_REQ_TYPE = UsbConstants.USB_TYPE_VENDOR
			| UsbConstants.USB_DIR_OUT;

	private static UsbManager androidUsbManager;

	private UsbDeviceConnection ftdiConnection;

	private static UsbDevice ftdiDevice;

	private UsbInterface ftdiInterface;

	private UsbEndpoint ftdiIn;

	private UsbEndpoint ftdiOut;

	private boolean usbDeviceClaimed;
	
	private DataProcessor processor;
	private AndroidSensorDataManager mSensorManager;
	private Context mContext;
	private List<DiscoverableDevice> mDiscoverableDeviceList;

	public AndroidFTDISubChannel(Context context, AndroidSensorDataManager sensorManager) {
		usbDeviceClaimed = false;
		mContext = context;
		mSensorManager = sensorManager;

		mDiscoverableDeviceList = new ArrayList<DiscoverableDevice>();
	}

	public static boolean scanForDevice(Context svcContext) {
		boolean found = false;
		Map<String, UsbDevice> devices = ((UsbManager) svcContext
				.getSystemService(Context.USB_SERVICE)).getDeviceList();

		if (!devices.isEmpty()) {
			Log.e(TAG, "DEVICES Found");

			for (UsbDevice device : devices.values()) {

				if (device.getVendorId() == FTDI_VENDOR
						&& device.getProductId() == FTDI_PRODUCT) {
					Log.e(TAG, "FTDI DEVICE Found");
					found = true;
					ftdiDevice = device;
					break;
				}
			}
		}
		// else {
		// Log.e(TAG, "DEVICES NOT Found");
		// }
		return found;
	}

	public static void authorize(Context svcContext,
			PendingIntent usbAuthPendingIntent) {
		androidUsbManager = ((UsbManager) svcContext
				.getSystemService(Context.USB_SERVICE));

		if (!androidUsbManager.hasPermission(ftdiDevice)) {
			androidUsbManager.requestPermission(ftdiDevice,
					usbAuthPendingIntent);
		}
	}

	public void initializeSensors() {
		getDiscoverableSensor();
	}

	public void searchForSensors() {
		getDiscoverableSensor();
	}

	public List<DiscoverableDevice> getDiscoverableSensor() {
		List<DiscoverableDevice> deviceList = new ArrayList<DiscoverableDevice>();

		Map<String, UsbDevice> devices = androidUsbManager.getDeviceList();
		mDiscoverableDeviceList.clear();

		if (devices.isEmpty()) {
			Log.e(TAG, "NO Devices Found");
		} else {
			Log.e(TAG, "DEVICES Found");

			for (UsbDevice device : devices.values()) {

				if (device.getVendorId() == FTDI_VENDOR
						&& device.getProductId() == FTDI_PRODUCT) {

					Log.e(TAG, "FOUND FTDI Device");
					//String sensorID = String.valueOf(device.getDeviceId());
					USBDiscoverableDevice newDiscoverableDevice = new USBDiscoverableDevice(
							FIXED_ID, this.mSensorManager);
					newDiscoverableDevice.connectionLost = false;

					deviceList.add(newDiscoverableDevice);
					mDiscoverableDeviceList.add(newDiscoverableDevice);

				} else {
					Log.d(TAG, "USB DEVICE: " + device.getDeviceName());
					Log.d(TAG, "Vendor: " + device.getVendorId());
					Log.d(TAG, "Product: " + device.getProductId());
					Log.d(TAG, "ID: " + device.getDeviceId());
					Log.d(TAG, "Device Protocol: " + device.getDeviceProtocol());
					Log.d(TAG, "Num Interfaces: " + device.getInterfaceCount());
				}
			}
		}
		return deviceList;
	}

	/*
	 * Sensor connection methods
	 */

	/**
	 * Sensor register
	 * 
	 * @return True if sensor is successfully registered or has already been
	 *         registered
	 */
	public boolean sensorRegister(String id_to_add, DriverType sensorType) {
		Log.d(TAG, "In sensor register.");
		if (sensorType == null) {
			Log.d(TAG, "Did not find sensor type for sensor " + id_to_add);
			return false;
		}

		if (mDiscoverableDeviceList == null
				|| mDiscoverableDeviceList.size() == 0) {
			// re init once again
			initializeSensors();
		}

		if (mDiscoverableDeviceList == null
				|| mDiscoverableDeviceList.size() == 0) {
			// still no sensors?
			Log.e(TAG,
					"No device ids, check usb connection!  FAILED to register '"
							+ sensorType + "' sensor with passed id '"
							+ id_to_add + "'.");
			return false;
		}

		boolean foundSensor = false;

		Iterator<DiscoverableDevice> discoverableDeviceIterator = mDiscoverableDeviceList
				.iterator();
		while (discoverableDeviceIterator.hasNext()) {
			USBDiscoverableDevice newDevice = (USBDiscoverableDevice) discoverableDeviceIterator
					.next();
			if (newDevice.getDeviceId().equals(id_to_add)
					&& newDevice.connectionLost == false) {
				foundSensor = true;
				break;
			}
		}

		if (foundSensor) {
			if (mSensorManager.getSensor(id_to_add) != null)
				return true;
			// Need to add the sensor
			if (mSensorManager.addSensor(id_to_add, sensorType)) {
				Log.d(TAG, "Added usb sensor to sensor manager.");

				Intent i = new Intent();
				i.setAction(Constants.USB_STATE_CHANGE);
				mContext.sendBroadcast(i);
				return true;
			} else {
				Log.e(TAG, "Failed to add usb sensor to sensor manager");
			}
		}

		Log.d(TAG, "Can't find sensor: '" + id_to_add + "' of type '"
				+ sensorType + "' registration failed.");
		return false;
	}

	public void sensorConnect(String id) throws SensorNotFoundException {
		if (mDiscoverableDeviceList == null
				|| mDiscoverableDeviceList.size() == 0) {
			// re init once again
			getDiscoverableSensor();
		}

		if (ftdiDevice != null) {
				// && id.equals(String.valueOf(ftdiDevice.getDeviceId()))) {

			Log.e(TAG, "Number of interfaces:" + ftdiDevice.getInterfaceCount());
			ftdiInterface = ftdiDevice.getInterface(0);
			if (ftdiInterface == null) {
				Log.e(TAG, "NO USB INTERFACE! STOPPING!!!!!!!!!!!!!!!!!!!!");
				return;
			}

			if (ftdiInterface.getEndpointCount() == 2) {
				Log.e(TAG,
						"Number of endpoints:"
								+ ftdiInterface.getEndpointCount());
				UsbEndpoint endpoint0 = ftdiInterface.getEndpoint(0);
				UsbEndpoint endpoint1 = ftdiInterface.getEndpoint(1);
				if (endpoint0.getDirection() == UsbConstants.USB_DIR_IN
						&& endpoint1.getDirection() == UsbConstants.USB_DIR_OUT) {
					ftdiIn = endpoint0;
					ftdiOut = endpoint1;
				} else if (endpoint0.getDirection() == UsbConstants.USB_DIR_OUT
						&& endpoint1.getDirection() == UsbConstants.USB_DIR_IN) {
					ftdiIn = endpoint1;
					ftdiOut = endpoint0;
				} else {
					Log.e(TAG,
							"CAN'T FIGURE OUT DIRECTION of endpoints! STOPPING!!!!!!!!!!!!!!!!!!!!");
					return;
				}

			} else {
				Log.e(TAG,
						"INCORRECT Number of endpoints! STOPPING!!!!!!!!!!!!!!!!!!!!");
				return;
			}

			Log.e(TAG, "Opening Device");
			ftdiConnection = androidUsbManager.openDevice(ftdiDevice);
			usbDeviceClaimed = ftdiConnection.claimInterface(ftdiInterface,
					true);

			if (usbDeviceClaimed) {
				ftdiConnection.controlTransfer(OUT_REQ_TYPE, 0x00, 0x00, 0,
						null, 0, 10000);
				ftdiConnection.controlTransfer(OUT_REQ_TYPE, 0x01, 0x00, 0,
						null, 0, 10000);
				ftdiConnection.controlTransfer(OUT_REQ_TYPE, 0x02, 0x00, 0,
						null, 0, 10000);

				// configure device
				// set baud rate
				ftdiConnection.controlTransfer(OUT_REQ_TYPE, 0x03, 312, 4,
						null, 0, 10000);
				// set data bits, stop bits, and parity
				int config = 0x08; // (8, 0, 0)
				ftdiConnection.controlTransfer(OUT_REQ_TYPE, 0x04, config, 0,
						null, 0, 10000);

				mSensorManager.updateSensorState(id,
						DetailedSensorState.CONNECTED);
			} else {
				mSensorManager.updateSensorState(id,
						DetailedSensorState.DISCONNECTED);
			}
		}

	}

	public void sensorDisconnect(String id) throws SensorNotFoundException {
		closeFtdiConnection();
		
		AndroidSensorLinkManager sensor = mSensorManager.getSensor(id);
		if (sensor != null) {
			mSensorManager.updateSensorState(id,
					DetailedSensorState.DISCONNECTED);
		}
	}

	public void sensorWrite(String id, byte[] message) {
		AndroidSensorLinkManager sensor = mSensorManager.getSensor(id);
		if (sensor != null) {
			// bulk transfer on ftdiout
			int bytesTransfered = ftdiConnection.bulkTransfer(ftdiOut, message,
					message.length, 500);

			if (bytesTransfered < 0) {
				Log.e(TAG, "error writing data to ftdi connection");
			}
		}
	}

	public void startSensorDataAcquisition(String id, byte[] command) {
		if(!usbDeviceClaimed) {
			return;
		}
		
		AndroidSensorLinkManager sensor = mSensorManager.getSensor(id);
		if (sensor != null) {
			sensor.dataBufferReset();
		}
		
		if(processor == null || processor.getState() == Thread.State.TERMINATED) {
			processor = new DataProcessor(this);
			processor.start();
		}
	}

	public void stopSensorDataAcquisition(String id, byte[] command) {
		if (processor != null) {
			processor.shutdownThread();
			processor = null;
		}
	}

	public void shutdown() {
		if (processor != null) {
			processor.shutdownThread();
			processor = null;
		}
		
		closeFtdiConnection();
	}

	private void closeFtdiConnection() {
		if (ftdiConnection != null) {
			if (ftdiInterface != null && usbDeviceClaimed) {
				ftdiConnection.releaseInterface(ftdiInterface);
				usbDeviceClaimed = false;
			}
			
			ftdiConnection.close();
		}
	}

	public synchronized void removeAllSensors() {
		if (mDiscoverableDeviceList != null) {
			Iterator<DiscoverableDevice> discoverableDeviceIterator = mDiscoverableDeviceList
					.iterator();
			while (discoverableDeviceIterator.hasNext())
				discoverableDeviceIterator.next().connectionLost();
		}
		mDiscoverableDeviceList = new ArrayList<DiscoverableDevice>();
	}

	public void processData() {
		byte[] inputBuffer = new byte[MAX_BUFFER_SIZE];

		int bytesTransfered = ftdiConnection.bulkTransfer(ftdiIn, inputBuffer,
				MAX_BUFFER_SIZE, 500);
		if (bytesTransfered >= 2) {
			if (bytesTransfered == MAX_BUFFER_SIZE) {
				Log.e(TAG, "OVERFLOW");
				return;
			}
			// Log.e(TAG, "HEADER: " + inputBuffer[0] + " " + inputBuffer[1]);
			//Log.v(TAG, "Channel Bytes recv'd:" + (bytesTransfered - 2));

			// StringBuffer str = new StringBuffer();
			// for(int i = 2; i < bytesTransfered; i++) {
			// str.append(inputBuffer[i] + " ");
			// }
			//
			// Log.v(TAG, str.toString());

			byte[] sensorData = new byte[bytesTransfered - 2];
			// for(int tmp = 0; tmp < bytesTransfered - 2; tmp++) {
			// sensorData[tmp] = (byte)(tmp + 1);
			// }
			System.arraycopy(inputBuffer, 2, sensorData, 0, sensorData.length);
			AndroidSensorDataPacket sdp = new AndroidSensorDataPacket(sensorData,
					System.currentTimeMillis());

			mSensorManager.addSensorDataPacket(FIXED_ID, sdp);

			// Log.e(TAG, output.toString());
		}
	}

	private class DataProcessor extends Thread {

		private AndroidFTDISubChannel channel;
		private final AtomicBoolean killThread = new AtomicBoolean(false);

		DataProcessor(AndroidFTDISubChannel channel) {
			super("FTDIChannel DataProcessor");
			this.channel = channel;
		}

		public void shutdownThread() {
			killThread.set(true);
			interrupt();
		}
		
		@Override
		public void run() {
			while (!killThread.get()) {
				channel.processData();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
