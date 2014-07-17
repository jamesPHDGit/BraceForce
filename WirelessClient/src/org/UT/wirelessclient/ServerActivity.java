package org.UT.wirelessclient;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import BraceForce.Network.Android.AppNodeServer;
import BraceForce.Network.Android.D2D;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.Menu;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class ServerActivity extends Activity {

	
	private ArrayList<Thread> threads = new ArrayList();
	ArrayList<EndPoint> endPoints = new ArrayList();
	private Timer timer;
	boolean fail;
	
	
	
	private void runUdpServer(Context mContext) {

		final Context localContext = mContext;
		// server will listen to one client
	    try
	    {
	        Thread udpServerThread = new Thread()
	        {
	            @Override
	            public void run()
	            {
	                try
	                {
	                //   Retrieve the ServerName 

	                       
	                    Log.d("UDP", "S: Connecting...");
	                    // Create new UDP-Socket 
	                    DatagramSocket socket = new DatagramSocket(54777);






	                    byte[] buf = new byte[17];


	                    // * Prepare a UDP-Packet that can contain the data we
	                    // * want to receive

	                    DatagramPacket packet = new DatagramPacket(buf,
	                            buf.length);
	                    Log.d("UDP", "S: Receiving...");

	                  //Acquire the lock for wifimanager, otherwise the WIfi stack
						//filters out packets not explicitly addressed to this device.
						//Acquiring a MulticastLock will cause the stack to receive packets 
						//addressed to multicast addresses
						//Processing these extra packets can cause a noticable
						//battery drain and should be disabled when not needed.
						WifiManager wifi = (WifiManager)localContext.getSystemService( Context.WIFI_SERVICE);
						WifiManager.MulticastLock mlock = null;
						if ( wifi != null ){
							mlock = wifi.createMulticastLock("Log_Tag");
							mlock.acquire();
							Log.d("kryonet", "Acquired wifi lock");
						}
	                    
	                //   wait to Receive the UDP-Packet 
	                    socket.receive(packet);
	                    Log.d("UDP", "S: Received: '" + packet.getAddress() + " "
	                            + new String(packet.getData()) + "'");

	                    
	                    //send response back
	                    socket.send(packet);
	                    
	                    mlock.release();
	                    socket.close();
	                    String acceptedMsg = new String(packet.getData());


	             


	                    Log.d("UDP", "S: Done.");
	                } catch (Exception e)
	                {
	                    Log.e("UDP", "S: Error", e);
	                }


	            }

	        };
	        udpServerThread.start();

	    }

	    catch (Exception E)
	    {
	     Log.e("r",E.getMessage())  ;
	    }

	}
	
	
	
	public void waitForThreads (int stopAfterMillis) {
		if (stopAfterMillis > 10000) throw new IllegalArgumentException("stopAfterMillis must be < 10000");
		stopEndPoints(stopAfterMillis);
		waitForThreads();
	}
	
	public void stopEndPoints () {
		stopEndPoints(0);
	}

	
	public void stopEndPoints (int stopAfterMillis) {
		timer.schedule(new TimerTask() {
			public void run () {
				for (EndPoint endPoint : endPoints)
					endPoint.stop();
				endPoints.clear();
			}
		}, stopAfterMillis);
	}

	public void waitForThreads () {
		fail = false;
		TimerTask failTask = new TimerTask() {
			public void run () {
				Log.d("Server Socket","Start to stop listen:");
				stopEndPoints();
				fail = true;
			}
		};
		timer.schedule(failTask, 110000);
		while (true) {
			for (Iterator iter = threads.iterator(); iter.hasNext();) {
				Thread thread = (Thread)iter.next();
				if (!thread.isAlive()) iter.remove();
			}
			if (threads.isEmpty()) break;
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
			}
		}
		failTask.cancel();
		// Give sockets a chance to close before starting the next test.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}
	

	
	public void startEndPoint (EndPoint endPoint) {
		endPoints.add(endPoint);
		Thread thread = new Thread(endPoint, endPoint.getClass().getSimpleName());
		threads.add(thread);
		thread.start();
		Log.d("Server Socket","Start to listen:");
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		
		//ThreadPolicy tp = ThreadPolicy.LAX;
		//StrictMode.setThreadPolicy(tp);
	
		
		
		D2D.runUdpServer(this, 54777, 5,  30000);
		
		D2D.runTCPServer(55551, 42);
		
		
//		try {
//			startServer();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			Log.d("Server Socket","Fail to start server: " + e.toString());
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server, menu);
		return true;
	}

}
