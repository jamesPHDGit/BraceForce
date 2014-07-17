package braceForce.distribution;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BraceNodeD2DManager {
	
	private static long nodeExpirationTime = 5; //3 minutes expired, of course it is dependent on how 
	//often device discovery runs which is pre-set internally as ever one minute

	public static void addNewHostNode(ConcurrentLinkedQueue<BraceNode> hostNodeList, BraceNode node){
		if ( !hostNodeList.contains(node)){
			hostNodeList.add(node);
		}
	}
	
	public static boolean addNewHostNode(ConcurrentLinkedQueue<BraceNode> hostNodeList, InetAddress hostAddress, boolean autoDiscover, String nodeID, String nodeName, String tcpPort, String udpPort ){
		try{
			if ( !isHostNodeRegistered( hostNodeList, hostAddress.getHostAddress())){
				BraceNode newNode = new BraceNode();
				newNode.setNodeID(nodeID);
				newNode.setNodeName(nodeName);
				newNode.setNodeAddress(hostAddress);
				newNode.setTcpPort(tcpPort);
				newNode.setUdpPort(udpPort);
				if ( autoDiscover ){
					newNode.setActiveDiscovered(true);
					newNode.setPassiveDiscovered(false);
				}
				else{
					newNode.setActiveDiscovered(false);
					newNode.setPassiveDiscovered(true);
				}
				newNode.setDiscoveredTime(System.currentTimeMillis());
				hostNodeList.add(newNode);
			}
			return true;
		}
		catch (Exception ex){
			System.out.println("register host failure " + ex.toString());
			return false;
		}
		
	}
	
	public static boolean isHostNodeRegistered(ConcurrentLinkedQueue<BraceNode> hostNodeList,String getHostAddress){
		Iterator<BraceNode> iter = hostNodeList.iterator();
		while ( iter.hasNext() ){
			BraceNode nextNode = iter.next();
			if ( nextNode.getNodeAddress().getHostAddress().equalsIgnoreCase(getHostAddress) ){
				//current node has not expired
				if ( nextNode.getDiscoveredTime() + nodeExpirationTime * 36000 > System.currentTimeMillis() ){
					return true;
				}
			}
		}
		return false;
	}
	
	public static List<BraceNode> getActiveBraceNodes(ConcurrentLinkedQueue<BraceNode> hostNodeList) {
		Iterator<BraceNode> iter = hostNodeList.iterator();
		List<BraceNode> list = new ArrayList<BraceNode>();
		List<BraceNode> toRemoved = new ArrayList<BraceNode>();
		while ( iter.hasNext() ) {
			BraceNode nextNode = iter.next();
			//lazy remove
			if (  nextNode.getDiscoveredTime() + nodeExpirationTime * 36000 < System.currentTimeMillis() ){
				toRemoved.add(nextNode);
			}
			else {
				list.add(nextNode);
			}
		}
		
		//lazy remove
		
		for ( BraceNode nodeToBeRemoved : toRemoved ){
			try{hostNodeList.remove(nodeToBeRemoved);}catch(Exception ex){}
		}
		
		return list;
	}

}
