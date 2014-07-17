package braceForce.distribution;

import java.util.Set;

import BraceForce.Network.Android.D2D;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class AppNodeDiscoveryService extends Service {

	//return reference to itself
	private IBinder myBinder;

	public static final String PARAM_UDP_PORT_MSG = "udpMsg";
	public static final String PARAM_DISCOVERY_PERIOD_MSG = "discoveryMsg";
	public static final String PARAM_UDP_SOCKET_TIMEOUT_MSG = "socketTimeoutMsg";
	public static final String PARAM_TCP_PORT_MSG = "tcpMsg";
	public static final String PARAM_APPNODE_OBJECTIDMSG = "appNodeIDMsg";
	public static final String PARAM_CACHENODE_UDP_PORT_MSG = "dataCacheNodeUDPMsg";
	public static final String PARAM_CACHENODE_TCP_PORT_MSG = "dataCacheNodeTCPMsg";
	public static final String PARAM_CACHENODE_OBJECTIDMSG = "cacheNodeIDMsg";
	public static final String PARAM_APPNODE_DATAREFRESHINTERVAL = "appThreadIntervalMsg";
	

	@Override
	public void onStart(Intent intent, int startid) {
	
	
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		serviceInit(intent);
		return myBinder;
	}
	
	@Override
	public void onCreate() {
		
		
		
		 myBinder = new MyLocalBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
	
		
		return START_STICKY;
	}

	private void serviceInit(Intent intent) {
		AppNodeD2DManager.setServiceStarted();
// TODO Auto-generated method stub
//57555 is the UDP port for all data cache nodes
		Log.d("BraceForce", "AppNodeService Started");
		int udpPort = intent.getIntExtra(PARAM_UDP_PORT_MSG, 57555);
		//-1 means always run the discovery mode
		int discoveryPeriod = intent.getIntExtra(PARAM_DISCOVERY_PERIOD_MSG, -1);
		//udp socket times out in 10 seconds
		int udpSocketTimeout = intent.getIntExtra(PARAM_UDP_SOCKET_TIMEOUT_MSG, 10000);
		
		D2D.runUdpServer(getApplicationContext(), udpPort, discoveryPeriod, udpSocketTimeout);
		//57554 is the TCP port for all sensor nodes
		//42 is the object ID represents the SensorNodeDistribution object
		//for all sensor nodes
		int tcpPort = intent.getIntExtra(PARAM_TCP_PORT_MSG, 57554);
		int appRMIObjectID = intent.getIntExtra(PARAM_APPNODE_OBJECTIDMSG, 42);
		D2D.runTCPServerOnAppNode(tcpPort, appRMIObjectID);
		
		//discover data cache nodes dynamically
		int cacheNodeUDPPort = intent.getIntExtra(PARAM_CACHENODE_UDP_PORT_MSG, 56555);
		//D2D.discoverNodesByUDP(getApplicationContext(), cacheNodeUDPPort, DiscoverMode.DataCacheNode);
		
		//Gather sensor nodes and sensor list for each node periodically
		int cacheServerTCPPort = intent.getIntExtra(PARAM_CACHENODE_TCP_PORT_MSG, 56554);
		int cacheServerObjectID = intent.getIntExtra(PARAM_CACHENODE_OBJECTIDMSG, 40);
		int gatherInterval = intent.getIntExtra(PARAM_APPNODE_DATAREFRESHINTERVAL, 1);
		
		//D2D.runDataGatherOnAppNode(gatherInterval, cacheServerTCPPort, cacheServerObjectID);
		
		D2D.runErrandOnAppNode(getApplicationContext(), cacheNodeUDPPort, cacheServerTCPPort, cacheServerObjectID );
	}
	
	@Override
	public void onDestroy() {
	
	}
	
	//Expose methods
	public Set<String> returnSensorNode() {
		return AppNodeD2DManager.returnSensorNodes();
	}
	
	public String findSpecificSensorFromSensorNode(String sensorNodeID, String sensorType) {
		return AppNodeD2DManager.findSpecificSensorFromSensorNode(sensorNodeID, sensorType);
	}
	
	public void subscribeEventData(String sensorID, ISensorEventSubscriber subscriber, String sensorNodeName ){
		AppNodeD2DManager.subscribeEventData(sensorID, subscriber, sensorNodeName );
	}
	
	public boolean isServiceInitialized(){
		return AppNodeD2DManager.getServiceStarted();
	}
	
	
	public class MyLocalBinder extends Binder {
		public AppNodeDiscoveryService getService() {
			return AppNodeDiscoveryService.this;
		}
	}

}
