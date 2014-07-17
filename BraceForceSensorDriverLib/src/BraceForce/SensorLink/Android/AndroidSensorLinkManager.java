package BraceForce.SensorLink.Android;

import BraceForce.Drivers.ParameterMissingException;
import BraceForce.SensorLink.BraceForceSensorLinkManager;

import java.util.List;

import braceForce.Drivers.Android.AndroidSensorDataPacket;
import braceForce.Drivers.Android.AndroidSensorParameter;
import android.os.Bundle;

public interface AndroidSensorLinkManager extends BraceForceSensorLinkManager {

	public boolean isBuiltin();
	public void configure(String setting, Bundle params) throws ParameterMissingException; 
	public abstract List<Bundle> getSensorData(long maxNumReadings);

	public abstract List<AndroidSensorParameter> returnDriverParameters();
	/**
	 * Adds a new data sample packet for a sensor. 
	 * @param packet
	 *            sensor data packet
	 */
	public abstract void addSensorDataPacket(AndroidSensorDataPacket packet);

	
	public abstract boolean usingContentProvider();


}

