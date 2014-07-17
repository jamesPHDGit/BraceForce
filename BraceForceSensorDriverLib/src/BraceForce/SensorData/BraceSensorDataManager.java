package BraceForce.SensorData;


import java.util.Hashtable;
import java.util.List;

import BraceForce.Drivers.SensorDataPacket;
import BraceForce.SensorLink.BraceForceSensorLinkManager;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.DetailedSensorState;
import BraceForce.SensorLink.DriverType;

public interface BraceSensorDataManager {
	
	
	//Continuous way of retrieving sensor data
	void startPollingSensor(String id, GenericSensorDataContentProvider dataProvider );
	
	//retrieve sensor data in once off manner
	List<Hashtable> returnSensorData( String id, boolean clearDataBuffer);
	
	
	
	//link sensor with eventModel for real time data sensing
	void startSensorDataAcquistionEventModel(String id, GenericSensorDataContentProvider dataProvider, SensorDataChangeListener listener);
	
	//Get list of sensors registered with current node
	List<DriverType> getDriverTypes();
	
	
	//Internal Methods
	BraceForceSensorLinkManager getSensorLinkManager(String id);
	SensorStateMachine getSensorState(String id); 
	boolean addSensor(String id, DriverType driver);
	void addSensorDataPacket(String id, SensorDataPacket sdp); 
	void updateSensorState(String id, DetailedSensorState state);
	DetailedSensorState querySensorState(String id);
	
	void initializeRegisteredSensors();
	List<BraceForceSensorLinkManager> getRegisteredSensorLinkManagers(CommunicationChannelType channelType);
	
	void shutdown();
	
	List<BraceForceSensorLinkManager> getAllRegisteredSensorLinkManagers();	
	void removeAllSensors();
	void startCollectSensorUsingThreads();

}
