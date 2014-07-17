package com.example.testnetworksensor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import braceForce.distribution.AppNodeD2DManager;
import braceForce.distribution.ISensorEventSubscriber;
import BraceForce.Network.Android.D2D;
import BraceForce.SensorLink.Android.BuiltIn.BuiltInSensorType;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DebugMainActivity extends Activity implements ISensorEventSubscriber {

	
	private Thread threadAppClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug_main);
		
		AppNodeD2DManager.setServiceStarted();
		// TODO Auto-generated method stub
		//57555 is the UDP port for all data cache nodes
		Log.d("BraceForce", "AppNodeService Started");
		int udpPort =  57555;
		//-1 means always run the discovery mode
		int discoveryPeriod =  -1;
		//udp socket times out in 10 seconds
		int udpSocketTimeout = 10000;
		
		final ISensorEventSubscriber thisClone = this;
		
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
		
		//Create a thread that talks to App Layer of BraceForce framework
		threadAppClient = new Thread(){
			public void run(){
				try{
					while ( true  ){
						//ReturnSensorNodes
						
						//boolean serviceInitialized = 
						Set<String> sensorNodeSet = AppNodeD2DManager.returnSensorNodes();
						if ( sensorNodeSet != null ){
							for ( String sensorNodeName: sensorNodeSet){
								String sensorUID = AppNodeD2DManager.findSpecificSensorFromSensorNode(sensorNodeName, BuiltInSensorType.ACCELEROMETER.name());
								if ( sensorUID != null ){
									AppNodeD2DManager.subscribeEventData(sensorUID, thisClone, sensorNodeName);
								}
							}
						}
						
						try{
							Thread.sleep(500);
						}catch (Exception ex){
							//not important, not interested in thread competition at this stage
						}
					}
				} catch ( Exception e ){
					Log.d("ThreadAppClient", e.toString());
				}
			}
		};
		threadAppClient.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.debug_main, menu);
		return true;
	}

	@Override
	public void onSensorDataChanged(String sensorID, Hashtable sensorData) {
		// TODO Auto-generated method stub
		final Hashtable sensorDataReading = sensorData;
		Thread thread = new Thread() {
			@Override
			public void run() {
				synchronized(this){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							TextView sensorReading = (TextView)findViewById(R.id.sensorReadingBraceForce1);
							StringBuilder sbNewReading = new StringBuilder();
							if ( sensorDataReading != null && !sensorDataReading.isEmpty() ) {
								//update UI
								Enumeration keys = sensorDataReading.keys();
								while ( keys.hasMoreElements() ){
									Object key = keys.nextElement();
									Object value = sensorDataReading.get(key);
									sbNewReading.append(key.toString() + ":" + value.toString() + "|");
								}
								sensorReading.setText(sbNewReading.toString());
							}
							
							
							
							
							
							
						}
					});
				}
			};
	};
	
	thread.start();
	}
	
}
