package braceForce.distribution;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataCacheNodeAppD2DManager extends BraceNodeD2DManager {
	
	private static ConcurrentLinkedQueue<BraceNode> appNodeList = new ConcurrentLinkedQueue<BraceNode>();
    private static ConcurrentHashMap<String,String> sensorEventSubscription = new ConcurrentHashMap<String,String>();
    private static ConcurrentHashMap<String, BraceNode> appNodeLookup = new ConcurrentHashMap<String, BraceNode>();
    
    
	public static void subscribeForSensorEvent(String appNodeID, String sensorID){
		if ( sensorEventSubscription.containsKey(sensorID) ) {
			sensorEventSubscription.replace(sensorID, appNodeID);
		}
		else{
			sensorEventSubscription.put(sensorID, appNodeID);
		}
	}
	
	public static String findSubscriber(String sensorID){
		if ( sensorEventSubscription.containsKey(sensorID) ) {
			return sensorEventSubscription.get(sensorID);
		}
		else{
			return null;
		}
	}
    
	public static void addNewAppNode(BraceNode node){
		addNewHostNode( appNodeList, node );
		updateAppNodeMapping( node );
	}
	
	private static void updateAppNodeMapping(BraceNode node) {
		if ( appNodeLookup.containsKey(node.getNodeID())) {
			appNodeLookup.replace(node.getNodeID(), node);
		}
		else {
			appNodeLookup.put(node.getNodeID(), node);
		}
	}
	
	public static boolean addNewAppNode(InetAddress hostAddress, String nodeID, String nodeName, String tcpPort, String udpPort ){
		boolean addResult = addNewHostNode( appNodeList, hostAddress, false, nodeID, nodeName, tcpPort, udpPort );
		
		if ( addResult ){
			BraceNode newNode = new BraceNode();
			newNode.setNodeID(nodeID);
			newNode.setNodeAddress(hostAddress);
			newNode.setTcpPort(tcpPort);
			newNode.setUdpPort(udpPort);
			updateAppNodeMapping(newNode);
		}
		
		return addResult;
	}
	
	public static BraceNode findAppNode(String appNodeID){
		return appNodeLookup.get(appNodeID);
	}
	
	public static boolean isAppNodeRegistered(String getHostAddress){
		return isHostNodeRegistered( appNodeList, getHostAddress);
	}
	
	
	public static List<BraceNode> getActiveAppNodes() {
		return getActiveBraceNodes( appNodeList );
	}
	
}
