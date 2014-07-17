package BraceForce.SensorData;

import java.util.List;


import BraceForce.SensorLink.BraceForceSensorLinkManager;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.DiscoverableDevice;
import BraceForce.SensorLink.DriverType;
import BraceForce.SensorLink.SensorNotFoundException;


public interface ChannelManager {

	// Set the sensor manager
	public void setSensorManager(BraceSensorDataManager sensorManager);
	
	public void initializeSensors();
	
	// Get sensor status
	public SensorStateMachine getSensorStatus(String id);
	
	// Register sensor
	public boolean sensorRegister(String id, DriverType sensorDriver);
	
	public List<DiscoverableDevice> getDiscoverableSensor();
	
	// List all available sensors
	public List<BraceForceSensorLinkManager> getRegisteredSensorLinkManagerList(CommunicationChannelType commType);
	
	// Connect to sensor
	public void sensorConnect(String id) throws SensorNotFoundException;
	
	// Disconnect from sensor
	public void sensorDisconnect(String id) throws SensorNotFoundException;
	
	//start getting data from sensor
	public void startSensorDataAcquisition(String id, byte[] command);
	
	//stop getting data from sensor
	public void stopSensorDataAcquisition(String id, byte[] command);
	
	// Write data to sensor
	public void sensorWrite(String id, byte[] message);
	
	// Search for available sensors
	public void searchForSensors();
	
	//shutdown channel manager
	public void shutdown();
	
	public CommunicationChannelType getCommChannelType();
	
	public void removeAllSensors();		
}
