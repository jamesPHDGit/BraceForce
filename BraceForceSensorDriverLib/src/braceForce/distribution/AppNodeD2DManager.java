package braceForce.distribution;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import BraceForce.Network.Android.DataCacheNodeServer;

public class AppNodeD2DManager extends BraceNodeD2DManager {
	
	private static ConcurrentLinkedQueue<BraceNode> dataCacheNodeList = new ConcurrentLinkedQueue<BraceNode>();
	private static final int setInitialSize = 16;
    private static ConcurrentHashMap<String,ArrayList> sensorNodeHash = new ConcurrentHashMap<String,ArrayList>(setInitialSize );
    private static ConcurrentHashMap<String,ArrayList> sensorHash = new ConcurrentHashMap<String, ArrayList>(setInitialSize);
	private static ConcurrentHashMap<String, ISensorEventSubscriber> sensorEventSubscription = new ConcurrentHashMap<String, ISensorEventSubscriber>(setInitialSize);
    private static boolean appServiceStarted = false;
    private static String ownIPAddress = "";
    
    
    public static void setOwnIPAddress(String ipAddress ){
    	ownIPAddress = ipAddress;
    }
    
    public static String getOwnIPAddress(){
    	return ownIPAddress;
    }
    
    public static void setServiceStarted(){
    	appServiceStarted = true;
    }
	
    public static boolean getServiceStarted(){
    	return appServiceStarted;
    }
	
	public static void subscribeEventData(String sensorID, ISensorEventSubscriber subscriber, String sensorNodeName ){
		if ( sensorEventSubscription.containsKey(sensorID) ){
			sensorEventSubscription.replace(sensorID, subscriber);
		}
		else{
			//first time create subscription
			//activate call to cache node for subscription of sensor data
			DataCacheNodeServer cacheServer = new DataCacheNodeServer();
			
			//find datacacheServer associated with this sensorNode
			String dataCacheServerAddress =  findDataCacheNodeBySensorNode( sensorNodeName );
			int dataCacheTcpPort =  56554;
			int cacheRMIObjectID = 40;
			int appTcpPort = 57554;
			InetAddress dataCacheAddress = null;
			try {
				dataCacheAddress = InetAddress.getByName(dataCacheServerAddress);
				
				cacheServer.RMISubscribeSensorDataEvent(dataCacheAddress, appTcpPort, getOwnIPAddress(),
						dataCacheTcpPort, sensorID, cacheRMIObjectID );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
					
			sensorEventSubscription.put(sensorID, subscriber);
		}
	}
	
	public static ISensorEventSubscriber getSubscriberForTheSensor(String sensorID){
		if ( sensorEventSubscription.containsKey(sensorID) ){
			return sensorEventSubscription.get(sensorID);
		}
		else{
			return null;
		}
	}
	
    public static void registerSensorNode(String dataCacheNode, ArrayList sensorNodeList){
    	if ( sensorNodeHash.containsKey(dataCacheNode) ){
    		sensorNodeHash.replace(dataCacheNode, sensorNodeList);
    	}
    	else{
    		if ( sensorNodeList != null )
    			sensorNodeHash.put(dataCacheNode, sensorNodeList);
    	}
    }
    
    public static String findDataCacheNodeBySensorNode( String sensorNodeName ){
    	if ( sensorNodeHash.isEmpty() ){
    		return null;
    	}
    	
    	for (String dataCacheNode : sensorNodeHash.keySet() ) {
    		ArrayList<String> sensorNodeList = sensorNodeHash.get(dataCacheNode);
    		for ( String sensorNode : sensorNodeList ){
    			if ( sensorNode.toUpperCase().equals(sensorNodeName) ) {
    				return dataCacheNode; //find the very data cache node matched
    			}
    		}
    	}
    	return null;
    }
    
    public static void registerSensors(String sensorNode, ArrayList sensorList){
    	if ( sensorHash.containsKey(sensorNode) ){
    		sensorHash.replace(sensorNode, sensorList);
    	}
    	else{
    		if ( sensorList != null )
    		sensorHash.put(sensorNode, sensorList);
    	}
    }
	
    public static Set<String> returnSensorNodes(){
    	return sensorHash.keySet();
    }
    
    public static Set<String> returnDataCacheNodes(){
    	return sensorNodeHash.keySet();
    }
    
    public static String findSpecificSensorFromSensorNode(String sensorNodeID, String sensorType){
    	if ( sensorHash.containsKey(sensorNodeID)){
    		ArrayList sensorList = sensorHash.get(sensorNodeID);
    		for ( Object sensor : sensorList ){
    			SensorMetaInfo sensorInfo = (SensorMetaInfo)sensor;
    			if ( sensorInfo.getSensorID().toUpperCase().contains(sensorType)) {
    				return sensorInfo.getSensorID();
    			}
    		}
    	}
    	return null;
    }
    
    
	public static void addNewDataCacheNode(BraceNode node){
		addNewHostNode( dataCacheNodeList, node );
	}
	
	public static boolean addNewDataCacheNode(InetAddress hostAddress,  String nodeID, String nodeName, String tcpPort, String udpPort ){
		return addNewHostNode( dataCacheNodeList, hostAddress, true, nodeID, nodeName, tcpPort, udpPort );
		
	}
	
	public static boolean isDataCacheNodeRegistered(String getHostAddress){
		return isHostNodeRegistered( dataCacheNodeList, getHostAddress);
	}
	
	public static List<BraceNode> getActiveDataCacheNodes() {
		return getActiveBraceNodes( dataCacheNodeList );
	}
}
