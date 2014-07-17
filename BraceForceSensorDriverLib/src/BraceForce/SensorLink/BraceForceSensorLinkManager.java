package BraceForce.SensorLink;

import java.util.Hashtable;
import java.util.List;

import BraceForce.Drivers.ParameterMissingException;
import BraceForce.Drivers.SensorDataPacket;


public interface BraceForceSensorLinkManager {
	public abstract void connect()
			throws SensorNotFoundException;
	
	public abstract CommunicationChannelType getCommunicationChannelType();
	
	public abstract void disconnect() throws SensorNotFoundException;
	
	/**
	 * Sends a message to the sensor to configure it.
	 * @param setting TODO
	 * @param configData Data for configuration.
	 */
	public abstract void configure(String setting, Hashtable params)
			throws ParameterMissingException;

	public abstract List<Hashtable> getSensorDatainCollection(long maxNumReadings);

	public abstract void sendDataToSensor(Hashtable dataToEncode);

	public abstract boolean startSensor();

	public abstract boolean stopSensor();

	public abstract String getSensorID();
	
	/**
	 * Adds a new data sample packet for a sensor. 
	 * @param packet
	 *            sensor data packet
	 */
	public abstract void addSensorDataPacket(SensorDataPacket packet);

	
	/**
	 * Deletes any existing temporary buffers for a sensor. This should be
	 * called by a sensor prior to be activated and buffering data to clear any
	 * previous data.
	 * 
	 */
	public abstract void dataBufferReset();


	public abstract void shutdown() throws SensorNotFoundException;

}
