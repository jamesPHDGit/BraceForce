package com.example.bracedatacacheservicetierclient;

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
		
		
		
		Log.d("BraceForce", "DataCacheNodeService  Started");
		// TODO Auto-generated method stub
		//56555 is the UDP port for all data cache nodes
			int udpPort = 56555;
			//-1 means always run the discovery mode
			int discoveryPeriod = -1;
			//udp socket times out in 10 seconds
			int udpSocketTimeout =  10000;
			
			D2D.runUdpServer(getApplicationContext(), udpPort, discoveryPeriod, udpSocketTimeout);
			//56554 is the TCP port for all data cache node nodes
			//40 is the object ID represents the SensorNodeDistribution object
			//for all sensor nodes
			int tcpPort = 56554;
			int cacheRMIObjectID =  40;
			D2D.runTCPServerOnDataCacheNode(tcpPort, cacheRMIObjectID);
			
			
			//discover data cache nodes dynamically
			int cacheNodeUDPPort =55555;
			//D2D.discoverNodesByUDP(this, cacheNodeUDPPort, DiscoverMode.DataCacheNode);
	
		
			//Gather sensor list for each node periodically
			int sensorServerTCPPort =  55554;
			int sensorServerObjectID = 38;
			int gatherInterval =  1;
			
		//	D2D.runDataGatherOnCacheNode(gatherInterval, sensorServerTCPPort, sensorServerObjectID);

			int predictionSuppressionCycle =  30000;
		//	D2D.runSpatialDataPredictionOnDataNode(predictionSuppressionCycle);
			
			D2D.runErrandsOnCacheNode(getApplicationContext(),  cacheNodeUDPPort, tcpPort, sensorServerTCPPort, sensorServerObjectID);
			
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
