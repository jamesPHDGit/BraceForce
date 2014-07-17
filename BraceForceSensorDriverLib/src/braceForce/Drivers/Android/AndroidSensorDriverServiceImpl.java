package braceForce.Drivers.Android;

import java.lang.reflect.Constructor;
import java.util.List;


import BraceForce.Drivers.ParameterMissingException;
import braceForce.Drivers.Android.AndroidDeviceDrivers;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;


public class AndroidSensorDriverServiceImpl extends AndroidDeviceDrivers.Stub {

	private static final String TAG = "SensorDriverServiceInterface"; 

	private AndroidDeviceDrivers sensorDriver;
	
	public AndroidSensorDriverServiceImpl(Context context)	
	{
		if(context == null)			
			Log.d(TAG,"passed in null context!");			
		try {		
			ApplicationInfo ai = 
					context.getPackageManager().getApplicationInfo(context.getPackageName(), 
					PackageManager.GET_META_DATA);
			String classNameStr  = ai.metaData.getString(ManifestMetadata.DRIVER_IMPL_CLASSNAME);
			Log.d(TAG, "class name read: " + classNameStr);
			
			Constructor<? extends AndroidDeviceDrivers> driverConstructor;	
			Class<? extends AndroidDeviceDrivers> driverClass = Class.forName(classNameStr).asSubclass(AndroidDeviceDrivers.class);	
			
			driverConstructor = driverClass.getConstructor();
			sensorDriver = driverConstructor.newInstance();			
		}
		catch(Exception ex) {
			ex.printStackTrace();			
		}
	}
	

	public byte [] configureCmd (String setting, Bundle configInfo) throws RemoteException
	{
		byte[] result;
		Log.d(TAG,"configuring");
		result = sensorDriver.configureCmd(setting, configInfo);
		Log.d(TAG,"configured");
		return result;	
	}

	public byte[] getSensorDataCmd () throws RemoteException
	{
		return sensorDriver.getSensorDataCmd();
	}

	public AndroidSensorDataParseResponse getSensorDataV2(long maxNumReadings,List<AndroidSensorDataPacket> rawData, byte [] remainingBytes) throws RemoteException
	{
//		Log.d(TAG," sensor driver get dataV2");
		return sensorDriver.getSensorData(maxNumReadings,rawData, remainingBytes);
	}
	
	@Override
	public byte[] encodeDataToSendToSensor(Bundle dataToFormat) throws RemoteException {		
		return sensorDriver.encodeDataToSendToSensor(dataToFormat);
	}

	public byte[] startCmd() throws RemoteException
	{
		return sensorDriver.startCmd();
	}

	public byte[] stopCmd() throws RemoteException
	{
		return sensorDriver.stopCmd();
	}


	@Override
	public List<AndroidSensorParameter> getDriverParameters() throws RemoteException {
		return sensorDriver.getDriverParameters();
	}


	@Override
	public AndroidSensorDataParseResponse getSensorData(long maxNumReadings,
			List<AndroidSensorDataPacket> rawSensorData, byte[] remainingData)
			throws RemoteException {
		// TODO Auto-generated method stub
		return sensorDriver.getSensorData(maxNumReadings, rawSensorData, remainingData);
	}	

}
