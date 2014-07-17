package braceForce.distribution;

import BraceForce.Network.Android.D2D;
import BraceForce.Network.Android.DiscoverMode;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CacheNodeDiscoveryService extends Service {


	public static final String PARAM_UDP_PORT_MSG = "udpMsg";
	public static final String PARAM_DISCOVERY_PERIOD_MSG = "discoveryMsg";
	public static final String PARAM_UDP_SOCKET_TIMEOUT_MSG = "socketTimeoutMsg";
	public static final String PARAM_TCP_PORT_MSG = "tcpMsg";
	public static final String PARAM_CACHENODE_OBJECTID_MSG = "cacheNodeIDMsg";
	public static final String PARAM_SENSORNODE_UDP_PORT_MSG = "sensorNodeUDPMsg";
	public static final String PARAM_SENSORNODE_TCP_PORT_MSG = "sensorNodeTCPMsg";
	public static final String PARAM_SENSORNODE_OBJECTIDMSG = "sensorNodeIDMsg";
	public static final String PARAM_CACHENODE_DATAREFRESHINTERVAL = "cacheThreadIntervalMsg";
	public static final String PARAM_PREDICTION_CYCLE = "predictionCycle";
	
	@Override
	public void onCreate(){
		Log.d("CacheNodeServer", "onCreate");
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
	
	}

	
	@Override
	public void onStart(Intent intent, int startId){
		
		Log.d("BraceForce", "DataCacheNodeService  Started");
		// TODO Auto-generated method stub
		//56555 is the UDP port for all data cache nodes
			int udpPort = intent.getIntExtra(PARAM_UDP_PORT_MSG, 56555);
			//-1 means always run the discovery mode
			int discoveryPeriod = intent.getIntExtra(PARAM_DISCOVERY_PERIOD_MSG, -1);
			//udp socket times out in 10 seconds
			int udpSocketTimeout = intent.getIntExtra(PARAM_UDP_SOCKET_TIMEOUT_MSG, 10000);
			
			D2D.runUdpServer(getApplicationContext(), udpPort, discoveryPeriod, udpSocketTimeout);
			//56554 is the TCP port for all sensor nodes
			//40 is the object ID represents the SensorNodeDistribution object
			//for all sensor nodes
			int tcpPort = intent.getIntExtra(PARAM_TCP_PORT_MSG, 56554);
			int cacheRMIObjectID = intent.getIntExtra(PARAM_CACHENODE_OBJECTID_MSG, 40);
			D2D.runTCPServerOnDataCacheNode(tcpPort, cacheRMIObjectID);
			
			
			//discover data cache nodes dynamically
			int cacheNodeUDPPort = intent.getIntExtra(PARAM_SENSORNODE_UDP_PORT_MSG, 55555);
			//D2D.discoverNodesByUDP(this, cacheNodeUDPPort, DiscoverMode.DataCacheNode);
	
		
			//Gather sensor list for each node periodically
			int sensorServerTCPPort = intent.getIntExtra(PARAM_SENSORNODE_TCP_PORT_MSG, 55554);
			int sensorServerObjectID = intent.getIntExtra(PARAM_SENSORNODE_OBJECTIDMSG, 38);
			int gatherInterval = intent.getIntExtra(PARAM_CACHENODE_DATAREFRESHINTERVAL, 1);
			
		//	D2D.runDataGatherOnCacheNode(gatherInterval, sensorServerTCPPort, sensorServerObjectID);

			int predictionSuppressionCycle = intent.getIntExtra(PARAM_PREDICTION_CYCLE, 30000);
		//	D2D.runSpatialDataPredictionOnDataNode(predictionSuppressionCycle);
			
			D2D.runErrandsOnCacheNode(getApplicationContext(), cacheNodeUDPPort, tcpPort, sensorServerTCPPort, sensorServerObjectID);
			
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
