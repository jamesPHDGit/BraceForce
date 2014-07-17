package com.example.braceforcesensoronedriver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import braceForce.distribution.CacheNodeDiscoveryService;
import braceForce.distribution.SensorNodeDiscoveryService;


public class SensorDataNodeAutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent sensorNodeServiceIntent = new Intent(context, SensorNodeDiscoveryService.class);
		//sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_DISCOVERY_PERIOD_MSG, 600000);
		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_SENSORNODE_OBJECTIDMSG, 38);
		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_TCP_PORT_MSG, 55554);
		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_UDP_PORT_MSG, 55555);
		sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_UDP_SOCKET_TIMEOUT_MSG, 30000);
		context.startService(sensorNodeServiceIntent);
		
		Intent datacacheNodeServiceIntent = new Intent(context, CacheNodeDiscoveryService.class);
		//sensorNodeServiceIntent.putExtra(SensorNodeDiscoveryService.PARAM_DISCOVERY_PERIOD_MSG, 600000);
		//	datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_DISCOVERY_PERIOD_MSG, -1);
		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_CACHENODE_OBJECTID_MSG, 40);
		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_TCP_PORT_MSG, 56554);
		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_UDP_PORT_MSG, 56555);
		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_UDP_SOCKET_TIMEOUT_MSG, 30000);
		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_SENSORNODE_UDP_PORT_MSG, 55555);
		datacacheNodeServiceIntent.putExtra(CacheNodeDiscoveryService.PARAM_PREDICTION_CYCLE, 30000);
		context.startService(datacacheNodeServiceIntent);
	}

}
