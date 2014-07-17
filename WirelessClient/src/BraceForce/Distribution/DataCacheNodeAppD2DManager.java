package BraceForce.Distribution;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataCacheNodeAppD2DManager extends BraceNodeD2DManager {
	
	private static ConcurrentLinkedQueue<BraceNode> appNodeList = new ConcurrentLinkedQueue<BraceNode>();
    
	public static void addNewAppNode(BraceNode node){
		addNewHostNode( appNodeList, node );
	}
	
	public static boolean addNewAppNode(InetAddress hostAddress, String nodeID, String nodeName, String tcpPort, String udpPort ){
		return addNewHostNode( appNodeList, hostAddress, false, nodeID, nodeName, tcpPort, udpPort );
		
	}
	
	public static boolean isAppNodeRegistered(String getHostAddress){
		return isHostNodeRegistered( appNodeList, getHostAddress);
	}
	
}
