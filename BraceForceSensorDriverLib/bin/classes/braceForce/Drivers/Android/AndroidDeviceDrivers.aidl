package braceForce.Drivers.Android;

import java.util.List;
import android.os.Bundle;
import braceForce.Drivers.Android.AndroidSensorDataPacket;
import braceForce.Drivers.Android.AndroidSensorDataParseResponse;
import braceForce.Drivers.Android.AndroidSensorParameter;

interface AndroidDeviceDrivers {
  
	/*
	 * Get the sensor-specific command buffer to configure parameters. 
	 * return 0-length buffer if sensor does not have a start command
	 * @param configData Data for configuration.
	 */
	byte [] configureCmd(in String setting, in Bundle configInfo);
	
	/*
	 * Some sensors (e.g. WaterUse sensor) return data only when queried
	 * This method returns the command buffer needed to retrieve data from the sensor
	 * return 0-length buffer if a query command is not needed for the sensor
	 */
	 
	byte [] getSensorDataCmd();	
	
	AndroidSensorDataParseResponse getSensorData(in long maxNumReadings, in List<AndroidSensorDataPacket> rawSensorData, in byte [] remainingData);
	
	byte[] encodeDataToSendToSensor(in Bundle dataToFormat);
	
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


	/*
	 * Get the list of driver parameters 
	 */
	List<AndroidSensorParameter> getDriverParameters();
}