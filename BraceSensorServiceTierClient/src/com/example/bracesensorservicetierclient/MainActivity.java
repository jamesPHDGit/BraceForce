package com.example.bracesensorservicetierclient;

import BraceForce.Network.Android.D2D;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int udpPort =  55555;
		//-1 means always run the discovery mode
		int discoveryPeriod =  -1;
		//udp socket times out in 10 seconds
		int udpSocketTimeout = 10000;
		
		D2D.runUdpServer(getApplicationContext(), udpPort, discoveryPeriod, udpSocketTimeout);
		//55554 is the TCP port for all sensor nodes
		//38 is the object ID represents the SensorNodeDistribution object
		//for all sensor nodes
		int tcpPort =  55554;
		int sensorRMIObjectID = 38;
		D2D.runTCPServerOnSensorNode(tcpPort, sensorRMIObjectID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
