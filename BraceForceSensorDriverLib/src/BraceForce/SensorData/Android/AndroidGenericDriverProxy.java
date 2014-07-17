package BraceForce.SensorData.Android;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import braceForce.Drivers.Android.AndroidDriverCommunicator;
import braceForce.Drivers.Android.AndroidSensorDataPacket;
import braceForce.Drivers.Android.AndroidSensorDataParseResponse;
import braceForce.Drivers.Android.AndroidSensorParameter;

import BraceForce.Drivers.Driver;
import BraceForce.Drivers.ParameterMissingException;
import BraceForce.Drivers.SensorDataPacket;
import BraceForce.Drivers.SensorParameter;
import braceForce.Drivers.Android.AndroidDeviceDrivers;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */	
public class AndroidGenericDriverProxy implements ServiceConnection, AndroidDriverCommunicator {

	public static final String TAG = "GenericDriverProxy";
	private Context componentContext;
	private AndroidDeviceDrivers sensorDriverProxy;
	//private boolean isBoundToService;
	private AtomicBoolean isBoundToService; 

	public AndroidGenericDriverProxy(String packageName, String className,
			Context context) {
		try {
			componentContext = context;
			isBoundToService = new AtomicBoolean(false);
			Intent bind_intent = new Intent();
			// XXX make sure classname used in the intent is the fully qualified
			// class name
			Log.d(TAG,"binding to sensor driver: pkg: " + packageName + " className: "+ className);
			bind_intent.setClassName(packageName, className);
			componentContext.bindService(bind_intent, this,
					Context.BIND_AUTO_CREATE);
		}
		catch (Exception ex){
			Log.d(TAG, "Error in connectin to Driver Service: " + ex.toString() );
		}
	}


	@Override
	public void shutdown() {
		if( isBoundToService.get() ) {
			try {
				componentContext.unbindService(this);
			}
			catch(Exception ex) {
				Log.d(TAG,"shutdown threw exception");
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		Log.d(TAG, "Bound to SensorDriver");
		sensorDriverProxy = AndroidDeviceDrivers.Stub.asInterface(service);
		isBoundToService.set(true);
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		Log.d(TAG, "unbound to sensor driver");
		isBoundToService.set(false);
	}

	// dummy impl of getSensorData in V2
	public List<Bundle> getSensorData(long maxNumReadings) {
		return new ArrayList<Bundle>();
	}

	@Override
	public AndroidSensorDataParseResponse getSensorData(long maxNumReadings,
			List<AndroidSensorDataPacket> rawSensorData, byte [] remainingData) {

		if (isBoundToService.get() ) {
			try {
//				Log.d(TAG, " calling getSensorDataV2");
				AndroidSensorDataParseResponse sdp = sensorDriverProxy.getSensorData(maxNumReadings,
						rawSensorData, remainingData);
				return sdp;
			} catch (Exception ex) {
				String exception = ex.toString();
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public byte[] encodeDataToSendToSensor(Bundle dataToFormat) {
		byte [] encodedSensorData = null;
		if (isBoundToService.get()) {
			try {
				encodedSensorData = sensorDriverProxy.encodeDataToSendToSensor(dataToFormat);
			} catch (RemoteException rex) {
				rex.printStackTrace();
			}
		}
		return encodedSensorData;
	}

	@Override
	public byte[] configureCmd(String setting, Bundle params) {
		byte[] cmd = null;
		if (isBoundToService.get()) {
			try {
				cmd = sensorDriverProxy.configureCmd(setting, params);
			} catch (RemoteException rex) {
				rex.printStackTrace();
			}
		}
		return cmd;
	}

	@Override
	public byte[] getSensorDataCmd() {
		byte[] cmd = null;
		if (isBoundToService.get()) {
			try {
				cmd = sensorDriverProxy.getSensorDataCmd();
			} catch (RemoteException rex) {
				rex.printStackTrace();
			}
		}
		return cmd;
	}

	@Override
	public byte[] startCmd() {
		byte[] cmd = null;
		if (isBoundToService.get()) {
			try {
				cmd = sensorDriverProxy.startCmd();
			} catch (RemoteException rex) {
				rex.printStackTrace();
			}
		}
		return cmd;
	}

	@Override
	public byte[] stopCmd() {
		byte[] cmd = null;
		try {
			cmd = sensorDriverProxy.stopCmd();
		} catch (RemoteException rex) {
			rex.printStackTrace();
		}
		return cmd;
	}


	@Override
	public List<AndroidSensorParameter> getDriverParameters() {
		List<AndroidSensorParameter> list = null;
		try {
			list = sensorDriverProxy.getDriverParameters();
		} catch (RemoteException rex) {
			rex.printStackTrace();
		}
		return list;
	}


	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}


}
