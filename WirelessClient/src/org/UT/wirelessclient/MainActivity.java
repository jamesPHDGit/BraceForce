package org.UT.wirelessclient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.List;

import BraceForce.Network.Android.AppNodeServer;
import BraceForce.Network.Android.D2D;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;



public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ThreadPolicy tp = ThreadPolicy.LAX;
		StrictMode.setThreadPolicy(tp);
		
		discoverServers();
		//connectToServer();
	}
	
	private void discoverServers(){
		try{
			List<InetAddress> discoveredServers = D2D.discoverUdpServers(this, 54777);
			StringBuilder sb = new StringBuilder();
			for (InetAddress address : discoveredServers ){
				//Log.d("UDP Servers", address.getHostName());
				sb.append(address.getHostAddress());
				sb.append(";");
			}
			
		    TextView tvX= (TextView)findViewById(R.id.Message_TV);
			tvX.setText(sb.toString());
		}
		catch (Exception ex){
			
		}
	}
	
	private void connectToServer(){
	
		try {
		
		    
		    InetAddress serverAddress = D2D.discoverUdpServer(this,54777);
//		    TextView tvX= (TextView)findViewById(R.id.Message_TV);
//			tvX.setText(serverAddress.getHostAddress());
//		    Log.d("UDP Client", "server discovered: " + serverAddress.getHostAddress());
			 if ( serverAddress != null ){
				 AppNodeServer server = new AppNodeServer();
				 Hashtable ht = new Hashtable();
				 ht.put("data", "test sensor data");
		
				 server.connectToServerUsingTCP(InetAddress.getByName("192.168.1.4"), 55551, "Sensor-1", 42, ht);
			 }
			 else {
				 AppNodeServer server = new AppNodeServer();
				 Hashtable ht = new Hashtable();
				 ht.put("data", "test sensor data");
				 server.connectToServerUsingTCP(InetAddress.getByName("128.62.212.18"), 55551, "Sensor-1", 42, ht);
				 
			 }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("UDP Client", "Exception: " + e.toString());
		}
		
//	    try {
//			client.connect(5000,  InetAddress.getByAddress(serverAddress.getAddress()), 54555, 54777);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			Log.d("server request", "server discover failure  " + e.getMessage());
//			//e.printStackTrace();
//		}
//	    
//	    SensorResponse response = new SensorResponse();
//	    response.sensorData = "data-sensor";
//	    response.timeStamp = System.currentTimeMillis();
//	    client.sendTCP(response);
//	    
//	    client.addListener( new Listener() {
//	    	public void received(Connection conn, Object object){
//	    		if ( object instanceof SensorRequest){
//	    			SensorRequest request = (SensorRequest)object;
//	    			String responseID = request.sensorId;
//	    			
//	    			
//	    			TextView tvX= (TextView)findViewById(R.id.Message_TV);
//	    			tvX.setText(responseID);
//	    			Log.d("server request", "server request Data:  " + request.sensorId);
//	    		}
//	    	}
//	    }
//	    	);
//		}
//		catch ( Exception ex ){
//			Log.d("server request", "server request failure: " + ex.getMessage());
//		}
//	    
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}

	    @Override
	    protected void onPostExecute(String result) {
//	          TextView txt = (TextView) findViewById(R.id.output);
//	          txt.setText("Executed"); // txt.setText(result);
	          //might want to change "executed" for the returned string passed into onPostExecute() but that is upto you
	    }

	    @Override
	    protected void onPreExecute() {
	    }

	    @Override
	    protected void onProgressUpdate(Void... values) {
	    }

	
	}   


}

