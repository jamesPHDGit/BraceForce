package com.example.bracesensorservicetierclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import braceForce.distribution.SensorNodeDiscoveryService;

public class SensorNodeAutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Start sensor node service
//		Intent sensorNodeServiceIntent = new Intent(context, SensorNodeDiscoveryService.class);
//		//sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_DISCOVERY_PERIOD_MSG, 600000);
//		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_SENSORNODE_OBJECTIDMSG, 38);
//		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_TCP_PORT_MSG, 55554);
//		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_UDP_PORT_MSG, 55555);
//		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_UDP_SOCKET_TIMEOUT_MSG, 30000);
//		context.startService(sensorNodeServiceIntent);
//		Log.d("SensorServer", "autoStarted");
//		Toast.makeText(context, "Sensor Node Service Start........", Toast.LENGTH_LONG).show();
	}

}
