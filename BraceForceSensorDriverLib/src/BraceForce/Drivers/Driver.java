package BraceForce.Drivers;

import java.util.Hashtable;
import java.util.List;

import android.os.Bundle;

/**
 * 
 * @author jameszhengxi1979@gmail.com
 * 
 */

public interface Driver {

	byte[] getSensorConfigCmd(String setting, Hashtable configParams) throws ParameterMissingException;
	
	/*
	 * Some sensors (e.g. WaterUse sensor) return data only when queried
	 * This method returns the command buffer needed to retrieve data from the sensor
	 * return 0-length buffer if a query command is not needed for the sensor
	 */
	byte [] getSensorDataCmd();
	
	/*
	 * Get the sensor-specific start command. 
	 * return 0-length byte if sensor doesnot have a start command
	 */
	byte[] startCmd();

	/*
	 * Get the sensor-specific stop command. 
	 * return 0-length byte if sensor doesnot have a start command
	 */
	byte[] stopCmd();

	SensorDataResponse getSensorData(long maxNumReadings, List<SensorDataPacket> rawSensorData, byte [] remainingData);
	byte[] sendDataToSensor(Hashtable dataToFormat);
	void shutdown();
}
