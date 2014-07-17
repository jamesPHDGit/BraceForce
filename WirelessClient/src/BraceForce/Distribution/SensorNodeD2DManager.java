package BraceForce.Distribution;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SensorNodeD2DManager extends BraceNodeD2DManager {
	
	private static ConcurrentLinkedQueue<BraceNode> dataCacheNodeList = new ConcurrentLinkedQueue<BraceNode>();
    
	public static void addNewDataCacheNode(BraceNode node){
		addNewHostNode( dataCacheNodeList, node );
	}
	
	public static boolean addNewDataCacheNode(InetAddress hostAddress, String nodeID, String nodeName, String tcpPort, String udpPort ){
		return addNewHostNode( dataCacheNodeList, hostAddress, false, nodeID, nodeName, tcpPort, udpPort );
		
	}
	
	public static boolean isDataCacheNodeRegistered(String getHostAddress){
		return isHostNodeRegistered( dataCacheNodeList, getHostAddress);
	}
	
}
