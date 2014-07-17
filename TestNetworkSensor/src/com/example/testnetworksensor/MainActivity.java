package com.example.testnetworksensor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import BraceForce.SensorData.Android.BraceSensorManager;
import BraceForce.SensorData.Android.AndroidSensorDataManager.SensorAutoDetection;
import BraceForce.SensorLink.Android.BuiltIn.BuiltInSensorType;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import braceForce.distribution.AppNodeDiscoveryService;
import braceForce.distribution.AppNodeDiscoveryService.MyLocalBinder;
import braceForce.distribution.ISensorEventSubscriber;

public class MainActivity extends Activity  implements ISensorEventSubscriber {

	
	//private int msgID = 1014;
	
	//create a handler in the UI thread which talks to a separate thread
	/*private Handler myUIHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if ( msg.what == msgID ) {
				//update UI
				(Hashtable) msg.obj 
			}
		}
	}*/
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		
//		workRun = true;
//		final ISensorEventSubscriber thisClone = this;
//		Intent intent = new Intent(this, AppNodeDiscoveryService.class);
//		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
//		//Create a thread that talks to App Layer of BraceForce framework
//		threadAppClient = new Thread(){
//			public void run(){
//				try{
//					while ( workRun  ){
//						//ReturnSensorNodes
//						if ( isBound ) {
//							//boolean serviceInitialized = 
//							Set<String> sensorNodeSet = myService.returnSensorNode();
//							if ( sensorNodeSet != null ){
//								boolean appServiceStarted = myService.isServiceInitialized();
//								for ( String sensorNodeName: sensorNodeSet){
//									String sensorUID = myService.findSpecificSensorFromSensorNode(sensorNodeName, BuiltInSensorType.ACCELEROMETER.name());
//									if ( sensorUID != null ){
//										myService.subscribeEventData(sensorUID, thisClone, sensorNodeName);
//									}
//								}
//							}
//						}
//						try{
//							Thread.sleep(500);
//						}catch (Exception ex){
//							//not important, not interested in thread competition at this stage
//						}
//					}
//				} catch ( Exception e ){
//					Log.d("ThreadAppClient", e.toString());
//				}
//			}
//		};
//		threadAppClient.start();
		
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//subscription
		BraceSensorManager.subscribeSensorEvent( BuiltInSensorType.ACCELEROMETER.name(), this, this, true);

	}
	 @Override
   protected void onStop() {
   	// TODO Auto-generated method stub
   	super.onStop();
   	//BraceForce cleanup
   	BraceSensorManager.cleanup(this,true);
   }

	 @Override
	    protected void onPause() {
	    	// TODO Auto-generated method stub
	    	super.onPause();
	    	//BraceForce cleanup
	    	BraceSensorManager.cleanup(this,true);
	    }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onSensorDataChanged(String sensorID, Hashtable sensorData) {
		// TODO Auto-generated method stub
		//Braceforce get sensor data
		final Hashtable sensorDataReading = sensorData;
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				synchronized(this){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							TextView sensorReading = (TextView)findViewById(R.id.sensorReadingBraceForce);
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
