package braceForce.distribution;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SensorNodeD2DManager extends BraceNodeD2DManager {
	
	private static ConcurrentLinkedQueue<BraceNode> dataCacheNodeList = new ConcurrentLinkedQueue<BraceNode>();
    
    private static ConcurrentHashMap<String, String> sensorCacheNodeMapping = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, BraceNode> cacheNodeLookup = new ConcurrentHashMap<String, BraceNode>();
    private static ConcurrentHashMap<String, Boolean> sensorNodeSuppressionMode = new ConcurrentHashMap<String, Boolean>();
    private static ConcurrentHashMap<String, Hashtable> sensorTemporalReading = new ConcurrentHashMap<String, Hashtable>();
   
    public static void storeTemporalReading(String sensorID, Hashtable latestData){
    	if ( sensorTemporalReading.containsKey(sensorID)) {
    		sensorTemporalReading.replace(sensorID, latestData);
		}
		else{
			if ( latestData != null )
			sensorTemporalReading.put(sensorID, latestData);
		}
    }
    
    public static Hashtable getTemporalReading(String sensorID){
    	if ( sensorTemporalReading.containsKey(sensorID)){
    		return sensorTemporalReading.get(sensorID);
    	}
    	return null;
    }
    //Change suppression mode for one specific sensor
    public static void changeSuppressionMode(String sensorID, boolean suppressed ){
    	if ( sensorNodeSuppressionMode.containsKey(sensorID)){
    		sensorNodeSuppressionMode.replace(sensorID, suppressed);
    	}
    	else {
    		sensorNodeSuppressionMode.put(sensorID, suppressed);
    	}
    }
    
    public static boolean isSensorSuppressed(String sensorID){
    	if ( sensorNodeSuppressionMode.containsKey(sensorID )) {
    		return sensorNodeSuppressionMode.get(sensorID);
    	}
    	else {
    		return false; //by default the sensor is not suppressed
    	}
    }
    
    //Binds a sensor to a data cache node
    //once again can bind a sensor to a unique data cache node
    public static boolean registerSensorWithDataCacheNode(String sensorID, String dataCacheNodeID){
    	if ( sensorCacheNodeMapping.containsKey(sensorID) ){
    		sensorCacheNodeMapping.replace(sensorID, dataCacheNodeID);
    		return true;
    	}
    	else {
    		sensorCacheNodeMapping.put(sensorID, dataCacheNodeID);
    		return false;
    	}
    }
    
    public static String findDataCacheNodeBound(String sensorID){
    	return sensorCacheNodeMapping.get(sensorID);
    }
    
    public static Set<String> returnActivatedSensorWithEventDrivenModel(){
    	Set<String> keyset = sensorCacheNodeMapping.keySet();
    	return keyset;
    }
	
	public static void addNewDataCacheNode(BraceNode node){
		addNewHostNode( dataCacheNodeList, node );
		updateCacheNodeMapping(node);
	}
	
	public static boolean addNewDataCacheNode(InetAddress hostAddress, String nodeID, String nodeName, String tcpPort, String udpPort ){
		boolean addResult = addNewHostNode( dataCacheNodeList, hostAddress, false, nodeID, nodeName, tcpPort, udpPort );
		if ( addResult ){
			BraceNode newNode = new BraceNode();
			newNode.setNodeID(nodeID);
			newNode.setNodeAddress(hostAddress);
			newNode.setTcpPort(tcpPort);
			newNode.setUdpPort(udpPort);
			updateCacheNodeMapping(newNode);
		}
		
		return addResult;
	}
	
	private static void updateCacheNodeMapping(BraceNode node) {
		if ( cacheNodeLookup.containsKey(node.getNodeID())) {
			cacheNodeLookup.replace(node.getNodeID(), node);
		}
		else {
			cacheNodeLookup.put(node.getNodeID(), node);
		}
	}
	
	
	public static BraceNode findCacheNode(String cacheNodeID){
		return cacheNodeLookup.get(cacheNodeID);
	}
	
	public static boolean isDataCacheNodeRegistered(String getHostAddress){
		return isHostNodeRegistered( dataCacheNodeList, getHostAddress);
	}
	
	public static List<BraceNode> getActiveDataCacheNodes() {
		return getActiveBraceNodes( dataCacheNodeList );
	}
	
}
