package com.example.bracedatacacheservicetierclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import braceForce.distribution.CacheNodeDiscoveryService;

public class DataNodeAutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Start sensor node service
//		Intent datacacheNodeServiceIntent = new Intent(context, CacheNodeDiscoveryService.class);
//		//sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_DISCOVERY_PERIOD_MSG, 600000);
//		//	datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_DISCOVERY_PERIOD_MSG, -1);
//		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_CACHENODE_OBJECTID_MSG, 40);
//		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_TCP_PORT_MSG, 56554);
//		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_UDP_PORT_MSG, 56555);
//		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_UDP_SOCKET_TIMEOUT_MSG, 30000);
//		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_SENSORNODE_UDP_PORT_MSG, 55555);
//		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_PREDICTION_CYCLE, 30000);
//		context.startService(datacacheNodeServiceIntent);
//		Log.d("DataServer", "autoStarted");
//		Toast.makeText(context, "Data Node Service Start........", Toast.LENGTH_LONG).show();
	}

}
