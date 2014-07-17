package braceForce.Drivers.Android;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import braceForce.Drivers.Android.AndroidDeviceDrivers;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public abstract class AbstractAndroidExternalDriver implements AndroidDeviceDrivers {

	protected  static final String timestamp_param = "timestamp";
	protected List<AndroidSensorParameter> sensorParams = new ArrayList<AndroidSensorParameter>();
	
	@Override
	public byte[] configureCmd(String setting, Bundle config) {
		return null;
	}

	@Override
	public byte[] getSensorDataCmd() {
		return null;
	}

	@Override
	public byte[] startCmd() {
		return null;
	}

	@Override
	public byte[] stopCmd() {
		return null;
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
