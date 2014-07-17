package braceForce.distribution;

import BraceForce.Network.Android.D2D;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SensorNodeDiscoveryService extends Service {


	public static final String PARAM_UDP_PORT_MSG = "udpMsg";
	public static final String PARAM_DISCOVERY_PERIOD_MSG = "discoveryMsg";
	public static final String PARAM_UDP_SOCKET_TIMEOUT_MSG = "socketTimeoutMsg";
	public static final String PARAM_TCP_PORT_MSG = "tcpMsg";
	public static final String PARAM_SENSORNODE_OBJECTIDMSG = "sensorNodeIDMsg";
	
	@Override
	public void onCreate() {
		
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.d("BraceForce", "SensorNodeService Started");
		// TODO Auto-generated method stub
		//55555 is the UDP port for all sensor nodes
			int udpPort = intent.getIntExtra(PARAM_UDP_PORT_MSG, 55555);
			//-1 means always run the discovery mode
			int discoveryPeriod = intent.getIntExtra(PARAM_DISCOVERY_PERIOD_MSG, -1);
			//udp socket times out in 10 seconds
			int udpSocketTimeout = intent.getIntExtra(PARAM_UDP_SOCKET_TIMEOUT_MSG, 10000);
			
			D2D.runUdpServer(getApplicationContext(), udpPort, discoveryPeriod, udpSocketTimeout);
			//55554 is the TCP port for all sensor nodes
			//38 is the object ID represents the SensorNodeDistribution object
			//for all sensor nodes
			int tcpPort = intent.getIntExtra(PARAM_TCP_PORT_MSG, 55554);
			int sensorRMIObjectID = intent.getIntExtra(PARAM_SENSORNODE_OBJECTIDMSG, 38);
			D2D.runTCPServerOnSensorNode(tcpPort, sensorRMIObjectID);
	
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();	
	    AndroidSensorDataManagerSingleton.cleanup();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
