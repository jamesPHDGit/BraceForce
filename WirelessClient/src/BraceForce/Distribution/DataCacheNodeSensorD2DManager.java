package BraceForce.Distribution;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataCacheNodeSensorD2DManager extends BraceNodeD2DManager {
	
	private static ConcurrentLinkedQueue<BraceNode> sensorNodeList = new ConcurrentLinkedQueue<BraceNode>();
    
	public static void addNewSensorNode(BraceNode node){
		addNewHostNode( sensorNodeList, node );
	}
	
	public static boolean addNewSensorNode(InetAddress hostAddress,  String nodeID, String nodeName, String tcpPort, String udpPort ){
		return addNewHostNode( sensorNodeList, hostAddress, true, nodeID, nodeName, tcpPort, udpPort );
		
	}
	
	public static boolean isSensorNodeRegistered(String getHostAddress){
		return isHostNodeRegistered( sensorNodeList, getHostAddress);
	}
	
}
