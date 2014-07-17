package BraceForce.Distribution;

import java.net.InetAddress;

public class DataCacheNode {

	private String nodeID;
	private String nodeName;
	private InetAddress nodeAddress;
	private String tcpPort;
	private String udpPort;
	private long discoveredTime;
	private boolean activeDiscovered;
	private boolean passiveDiscovered;
	
}
