package com.example.braceforceappservicetierclient;

import braceForce.distribution.AppNodeD2DManager;
import BraceForce.Network.Android.D2D;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AppNodeD2DManager.setServiceStarted();
		// TODO Auto-generated method stub
		//57555 is the UDP port for all data cache nodes
		Log.d("BraceForce", "AppNodeService Started");
		int udpPort =  57555;
		//-1 means always run the discovery mode
		int discoveryPeriod =  -1;
		//udp socket times out in 10 seconds
		int udpSocketTimeout = 10000;
		
		D2D.runUdpServer(getApplicationContext(), udpPort, discoveryPeriod, udpSocketTimeout);
		//57554 is the TCP port for all sensor nodes
		//42 is the object ID represents the SensorNodeDistribution object
		//for all sensor nodes
		int tcpPort =  57554;
		int appRMIObjectID =  42;
		D2D.runTCPServerOnAppNode(tcpPort, appRMIObjectID);
		
		//discover data cache nodes dynamically
		int cacheNodeUDPPort =  56555;
		//D2D.discoverNodesByUDP(getApplicationContext(), cacheNodeUDPPort, DiscoverMode.DataCacheNode);
		
		//Gather sensor nodes and sensor list for each node periodically
		int cacheServerTCPPort = 56554;
		int cacheServerObjectID =  40;
		int gatherInterval = 1;
		
		//D2D.runDataGatherOnAppNode(gatherInterval, cacheServerTCPPort, cacheServerObjectID);
		
		D2D.runErrandOnAppNode(getApplicationContext(), cacheNodeUDPPort, cacheServerTCPPort, cacheServerObjectID );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
