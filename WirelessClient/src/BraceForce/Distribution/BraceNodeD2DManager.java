package BraceForce.Distribution;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BraceNodeD2DManager {
	

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
				return true;
			}
		}
		return false;
	}

}
