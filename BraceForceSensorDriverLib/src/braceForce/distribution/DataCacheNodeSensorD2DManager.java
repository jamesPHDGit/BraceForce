package braceForce.distribution;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataCacheNodeSensorD2DManager extends BraceNodeD2DManager {
	
	private static ConcurrentLinkedQueue<BraceNode> sensorNodeList = new ConcurrentLinkedQueue<BraceNode>();
    private static ConcurrentHashMap<String, ArrayList> sensorNodeSensorList = new ConcurrentHashMap<String,ArrayList>();
    private static ConcurrentHashMap<String,String> sensorNodeMapping = new ConcurrentHashMap<String,String>();
    private static ConcurrentHashMap<String, BraceNode> sensorNodeLookup = new ConcurrentHashMap<String, BraceNode>();
    private static ConcurrentHashMap<String, ArrayList> sensorSpatialMap = new ConcurrentHashMap<String, ArrayList>();
    private static ConcurrentHashMap<String, Hashtable> sensorTemporalReading = new ConcurrentHashMap<String, Hashtable>();
    
    public static void storeTemporalReading(String sensorID, Hashtable latestData){
    	if ( sensorTemporalReading.containsKey(sensorID)) {
    		sensorTemporalReading.replace(sensorID, latestData);
		}
		else{
			sensorTemporalReading.put(sensorID, latestData);
		}
    }
    
    public static Hashtable getTemporalReading(String sensorID){
    	if ( sensorTemporalReading.containsKey(sensorID)){
    		return sensorTemporalReading.get(sensorID);
    	}
    	return null;
    }
    
    //iterate through all sensor nodes
    //iterate through temperature and light sensor for each sensor node
    //construct spatial sensor list based on sensor type - temperature and light
    public static void generateSpatialSensorList(){
    	//sensor nodes are discovered automatically
    	//rudimenatry spatial sensors
    	ArrayList temperatureList = new ArrayList();
    	ArrayList lightList = new ArrayList();
    	if ( !sensorNodeList.isEmpty()){
    		for( BraceNode sensorNode : sensorNodeList ){
    			if ( sensorNodeSensorList.containsKey(sensorNode.getNodeID())) {
    				//Sensor lists are there for sensor node
    				//walk through each sensors and construct the spatial sensor list
    				for ( Object sensorListObject : sensorNodeSensorList.get(sensorNode.getNodeID())) {
    					if ( sensorListObject != null ){
    						ArrayList sensorList = (ArrayList)sensorListObject;
    						for (Object sensorObj : sensorList ){
    				    		SensorMetaInfo sensorMetaInfo = (SensorMetaInfo)sensorObj;
    				    		if ( sensorMetaInfo.getSensorType().toUpperCase().contains("TEMPERATURE")) {
    				    			temperatureList.add(sensorMetaInfo.getSensorID());
    				    		}
    				    		else if ( sensorMetaInfo.getSensorType().toUpperCase().contains("LIGHT")) {
    				    			 lightList.add(sensorMetaInfo.getSensorID());
    				    		}
    				    			
    						}
    					}
    				}
    			}
    			else {
    				getSensorList(sensorNode.getNodeID()); //prepare the data for next run
    				//don't consume too much time for this
    			}
    		}
    	}
    	
    	//Maintain spatial sensor list based on sensor type
    	//here rudimentary implementation
    	//for temperature and light
    	if ( sensorSpatialMap.containsKey("TEMPERATURE")) {
    		sensorSpatialMap.put("TEMPERATURE", temperatureList);
    	}
    	else if ( sensorSpatialMap.containsKey("LIGHT")) {
    		sensorSpatialMap.put("LIGHT", lightList);
    	}
    }
    
    //walk through sensor spatial list, retrieves the latest data
    //determins whether specific sensor node sensor data can be suppressed
    //rudimentary implementation to check for node 1, 3 and 5 to determine node 2, 4 only
    public static void checkSuppression(String sensorType, String mainVariableName, float delta){
    	//walk through temperature spatial list
    	if ( sensorSpatialMap.containsKey(sensorType)) {
    		ArrayList sensorList  = sensorSpatialMap.get(sensorType);
    		int sensorCounter = 0;
    		int numberOfSensors = sensorList.size();
    		if ( numberOfSensors < 3 ){
    			return;  //not enough sensors to play this game
    		}
    		float[] sensorValue = new float[numberOfSensors];
    		String[] sensorIDs = new String[numberOfSensors];
    		for ( Object sensor : sensorList ){
    			String sensorID = (String)sensor;
    			Hashtable temporalReading = getTemporalReading(sensorID);
    			if ( temporalReading != null ){
    				sensorValue[sensorCounter] = (Float)temporalReading.get(mainVariableName);
    			}
    			else {
    				sensorValue[sensorCounter] = Float.NEGATIVE_INFINITY; //not very meaningful for no data
    			}
    			sensorIDs[sensorCounter] = sensorID;
    			sensorCounter++;
    			
    		}
    		
    		int baseSensorCounter = 0;
    		while ( baseSensorCounter < numberOfSensors ){
    			float leftNeighborReading = sensorValue[baseSensorCounter];
    			float candidateReading = sensorValue[baseSensorCounter+1];
    			float rightNeighborReading = sensorValue[baseSensorCounter+2];
    			float averageNeighborReading = (rightNeighborReading + leftNeighborReading)/2;
    			if ( Math.abs(candidateReading - averageNeighborReading) < delta ) {
    			  //suppress candidate node
    			  DataCacheNodeDistributionImpl nodeDistribution = new DataCacheNodeDistributionImpl();
    			  nodeDistribution.suppressSensorDataReport(sensorIDs[baseSensorCounter+1], true);
    			}
    			else {
    				//dont suppress candidate node
    			  DataCacheNodeDistributionImpl nodeDistribution = new DataCacheNodeDistributionImpl();
       			  nodeDistribution.suppressSensorDataReport(sensorIDs[baseSensorCounter+1], false);
    			}
    			baseSensorCounter = baseSensorCounter + 3;
    		}
    		
    	}
    	
    }
    
    //a unique sensor can be moved from one station to another
    //check mapping between sensor and sensor node is time consuming
    //as application node does not care which sensor node this sensor is attached to
    //it ONLY cares to get the sensor data within specified time or subscribe the sensor data (Event-Driven data acquisition)
    //instead to find out the mapping between sensor and sensor node, when register sensorlist with
    //sensor node, does the mapping. 
    //each sensor has a unique ID and it can move from one sensor node to another
    //for instance bluetooth sensor
    public static void registerSensorsForSensorNode(String sensorNodeID, ArrayList sensorList){
    	if ( sensorNodeSensorList.contains(sensorNodeID) ){
    		sensorNodeSensorList.replace(sensorNodeID, sensorList);
    	}
    	else {
    		if ( sensorList != null )
    		sensorNodeSensorList.put(sensorNodeID, sensorList);
    	}
    	
    	if ( sensorList != null ) {
	    	for (Object sensorObj : sensorList ){
	    		SensorMetaInfo sensorMetaInfo = (SensorMetaInfo)sensorObj;
	    		if ( sensorNodeMapping.contains(sensorMetaInfo.getSensorID() ) ) {
	    			sensorNodeMapping.replace(sensorMetaInfo.getSensorID(), sensorNodeID);
	    		}
	    		else{
	    			sensorNodeMapping.put(sensorMetaInfo.getSensorID(), sensorNodeID);
	    		}
	    	}
    	}
    }
    
    //To find out what sensor node this unique sensor is attached
    //becomes O(1), constant time operation instead of O(number of sensor nodes * number of sensors) 
    //BruceForce search
    //which is important
    public static String findSensorNodeAttached (String sensorID){
    	return sensorNodeMapping.get(sensorID);
    }
	
	
    //once again for the same sensor node,
    //the meta information might change
    //to make sensor node look up constant when searching
	public static void addNewSensorNode(BraceNode node){
		addNewHostNode( sensorNodeList, node );
		updateSensorNodeMapping(node);
	}

	private static void updateSensorNodeMapping(BraceNode node) {
		if ( sensorNodeLookup.contains(node.getNodeID())) {
			sensorNodeLookup.replace(node.getNodeID(), node);
		}
		else {
			sensorNodeLookup.put(node.getNodeID(), node);
		}
	}
	
	public static boolean addNewSensorNode(InetAddress hostAddress,  String nodeID, String nodeName, String tcpPort, String udpPort ){
		
		boolean addResult = addNewHostNode( sensorNodeList, hostAddress, true, nodeID, nodeName, tcpPort, udpPort );
		if ( addResult ){
			BraceNode newNode = new BraceNode();
			newNode.setNodeID(nodeID);
			newNode.setNodeAddress(hostAddress);
			newNode.setTcpPort(tcpPort);
			newNode.setUdpPort(udpPort);
			updateSensorNodeMapping(newNode);
		}
		
		return addResult;
	}
	
	public static BraceNode findSensorNodeInfo(String nodeID){
		return sensorNodeLookup.get(nodeID);
	}
	
	public static boolean isSensorNodeRegistered(String getHostAddress){
		return isHostNodeRegistered( sensorNodeList, getHostAddress);
	}
	
	public static List<BraceNode> getActiveSensorNodes() {
		return getActiveBraceNodes( sensorNodeList );
	}
	
	public static ArrayList getSensorList(String nodeID){
		return sensorNodeSensorList.get(nodeID);
	}
	
}
