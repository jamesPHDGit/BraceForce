package com.example.braceforceappservicetierclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import braceForce.distribution.AppNodeDiscoveryService;
import braceForce.distribution.CacheNodeDiscoveryService;

public class AppNodeAutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Start sensor node service
		Intent appNodeServiceIntent = new Intent(context, AppNodeDiscoveryService.class);
		appNodeServiceIntent.putExtra(AppNodeDiscoveryService.PARAM_DISCOVERY_PERIOD_MSG, 600000);
		appNodeServiceIntent.putExtra(AppNodeDiscoveryService.PARAM_APPNODE_OBJECTIDMSG, 42);
		appNodeServiceIntent.putExtra(AppNodeDiscoveryService.PARAM_TCP_PORT_MSG, 55534);
		appNodeServiceIntent.putExtra(AppNodeDiscoveryService.PARAM_UDP_PORT_MSG, 55535);
		appNodeServiceIntent.putExtra(AppNodeDiscoveryService.PARAM_UDP_SOCKET_TIMEOUT_MSG, 30000);
		appNodeServiceIntent.putExtra(AppNodeDiscoveryService.PARAM_CACHENODE_UDP_PORT_MSG, 55545);
		context.startService(appNodeServiceIntent);
		Toast.makeText(context, "Service Start........", Toast.LENGTH_LONG).show();
	}

}
