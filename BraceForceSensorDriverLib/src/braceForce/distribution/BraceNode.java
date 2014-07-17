package braceForce.distribution;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BraceNode {

	private String nodeID;
	private String nodeName;
	private InetAddress nodeAddress;
	private String tcpPort;
	private String udpPort;
	private long discoveredTime;
	private boolean activeDiscovered;
	private boolean passiveDiscovered;
	public String getNodeID() {
		return nodeID;
	}
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public InetAddress getNodeAddress() {
		return nodeAddress;
	}
	public void setNodeAddress(InetAddress nodeAddress) {
		this.nodeAddress = nodeAddress;
	}
	public String getTcpPort() {
		return tcpPort;
	}
	public void setTcpPort(String tcpPort) {
		this.tcpPort = tcpPort;
	}
	public String getUdpPort() {
		return udpPort;
	}
	public void setUdpPort(String udpPort) {
		this.udpPort = udpPort;
	}
	public long getDiscoveredTime() {
		return discoveredTime;
	}
	public void setDiscoveredTime(long discoveredTime) {
		this.discoveredTime = discoveredTime;
	}
	public boolean isActiveDiscovered() {
		return activeDiscovered;
	}
	public void setActiveDiscovered(boolean activeDiscovered) {
		this.activeDiscovered = activeDiscovered;
	}
	public boolean isPassiveDiscovered() {
		return passiveDiscovered;
	}
	public void setPassiveDiscovered(boolean passiveDiscovered) {
		this.passiveDiscovered = passiveDiscovered;
	}
	
	
	
}
