package BraceForce.Network.Android;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import BraceForce.Distribution.AppNodeD2DManager;
import BraceForce.Distribution.DataCacheNodeSensorD2DManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;

public class D2D {
	

	public static void runTCPServer(int ipAddress, int objectID){
		final int localIPAddress = ipAddress;
		final int localObjectID = objectID;
		try{
			Thread tcpServerThread = new Thread(){
				  @Override
		            public void run()
		            {
					 AppNodeServer server = new AppNodeServer();
						try {
							while ( true ){
								server.startAppServer(localIPAddress, localObjectID);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("TCP" + e.toString());
						}
		            }
			};
			tcpServerThread.start();
		}
		 catch (Exception E)
		    {
		    	System.out.println("r: " + E.getMessage())  ;
		    }

	}
	
	public static void discoverNodesByUDP(Context context, int udpPort, DiscoverMode mode){
		final Context localContext = context;
		final int portNumber = udpPort;
		final DiscoverMode localDiscoverMode = mode;
		try{
			Thread udpDiscoverThread = new Thread(){
				  @Override
		            public void run()
		            {
					 
					   while ( true ){
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
								DatagramSocket socket = null;
							    try 
						        {
							    	socket = new DatagramSocket();
									socket.setBroadcast(true);
							        socket.setReceiveBufferSize(64 * 1024);
							        socket.setSendBufferSize(64 * 1024);
							     
							        int str_size = 1;
							        StringBuffer str = new StringBuffer();
							        while (str.length() < str_size)
							            str.append("h");
							        str.setLength(str_size);
							        byte buf[] = str.toString().getBytes("ISO-8859-1");
							     
							        InetAddress address = InetAddress.getByName("255.255.255.255");
							     
							        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portNumber);
							        socket.send(packet);
							      //  socket.send(packet);
							        socket.setSoTimeout(60000);
							        
							//    	while (true) {
							//		DatagramPacket packet = new DatagramPacket(new byte[0], 0);
							//		try {
							//			socket.receive(packet);
							//		} catch (SocketTimeoutException ex) {
							//			if (INFO) info("kryonet", "Host discovery timed out.");
							//			return hosts;
							//		}
							//		if (INFO) info("kryonet", "Discovered server: " + packet.getAddress());
							//		hosts.add(packet.getAddress());
							//	}
							        while ( true ) {
								        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
								        try {
								            socket.receive(receivePacket);
								        } catch (SocketTimeoutException ex) {
								            Log.d("kryonet", "Host discovery timed out.");
								        }
								        Log.d("kryonet", "Discovered server: " + receivePacket.getAddress());
								        if ( localDiscoverMode == DiscoverMode.DataCacheNode ){
								        	AppNodeD2DManager.addNewDataCacheNode(receivePacket.getAddress(), new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8"), "DataCacheNode-" + receivePacket.getData(), null, Integer.toString(receivePacket.getPort()));
								        }
								        else if ( localDiscoverMode == DiscoverMode.SensorNode ){
								        	DataCacheNodeSensorD2DManager.addNewSensorNode(receivePacket.getAddress(), new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8"), "SensorNode-" + receivePacket.getData(), null, Integer.toString(receivePacket.getPort()));
								        }
							        }
						        }
						        catch (Exception ex ){
						        	Log.d("kryonet", "Host discovery failure " + ex.toString());
						        	
						        }
						        finally{
						        	  if ( socket != null ){
						        		   socket.close();
						        	   }
						        	   if ( mlock != null )  {
						        		   mlock.release();
						        	   }
						        }
						        //detect sensor nodes every minute
						        try {
									Thread.sleep(60000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					   }
					  
		            }
			};
			udpDiscoverThread.start();
		}
		 catch (Exception E)
		    {
		    	System.out.println("r: " + E.getMessage())  ;
		    }

	}
	
	public static List<InetAddress> discoverUdpServers( Context localContext, int portNumber) throws IOException{

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
		DatagramSocket socket = new DatagramSocket();
		List<InetAddress> hosts = new ArrayList<InetAddress>();
        try 
        {
			socket.setBroadcast(true);
	        socket.setReceiveBufferSize(64 * 1024);
	        socket.setSendBufferSize(64 * 1024);
	     
	        int str_size = 1;
	        StringBuffer str = new StringBuffer();
	        while (str.length() < str_size)
	            str.append("h");
	        str.setLength(str_size);
	        byte buf[] = str.toString().getBytes("ISO-8859-1");
	     
	        InetAddress address = InetAddress.getByName("255.255.255.255");
	     
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portNumber);
	        socket.send(packet);
	      //  socket.send(packet);
	        socket.setSoTimeout(60000);
	        
	//    	while (true) {
	//		DatagramPacket packet = new DatagramPacket(new byte[0], 0);
	//		try {
	//			socket.receive(packet);
	//		} catch (SocketTimeoutException ex) {
	//			if (INFO) info("kryonet", "Host discovery timed out.");
	//			return hosts;
	//		}
	//		if (INFO) info("kryonet", "Discovered server: " + packet.getAddress());
	//		hosts.add(packet.getAddress());
	//	}
	        while ( true ) {
		        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
		        try {
		            socket.receive(receivePacket);
		        } catch (SocketTimeoutException ex) {
		            Log.d("kryonet", "Host discovery timed out.");
		            return hosts;
		        }
		        Log.d("kryonet", "Discovered server: " + receivePacket.getAddress());
		        hosts.add(receivePacket.getAddress());
	        }
        }
        catch (Exception ex ){
        	Log.d("kryonet", "Host discovery failure " + ex.toString());
        	return hosts;
        }
        finally{
        	  if ( socket != null ){
        		   socket.close();
        	   }
        	   if ( mlock != null )  {
        		   mlock.release();
        	   }
        }

        
	}
	
	public static InetAddress discoverUdpServer( Context localContext, int portNumber) throws IOException{

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
		DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        socket.setReceiveBufferSize(64 * 1024);
        socket.setSendBufferSize(64 * 1024);
     
        int str_size = 1;
        StringBuffer str = new StringBuffer();
        while (str.length() < str_size)
            str.append("h");
        str.setLength(str_size);
        byte buf[] = str.toString().getBytes("ISO-8859-1");
     
        InetAddress address = InetAddress.getByName("255.255.255.255");
     
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portNumber);
        socket.send(packet);
       
        socket.setSoTimeout(60000);
        DatagramPacket receivePacket = new DatagramPacket(new byte[0], 0);
        try {
            socket.receive(receivePacket);
        } catch (SocketTimeoutException ex) {
            Log.d("kryonet", "Host discovery timed out.");
           
        }
        finally{
        	  if ( socket != null ){
        		   socket.close();
        	   }
        	   if ( mlock != null )  {
        		   mlock.release();
        	   }
        }
        Log.d("kryonet", "Discovered server: " + receivePacket.getAddress());
        
        return receivePacket.getAddress();
        
	}
	
	public static void runUdpServer( Context mContext, int portNumber, int discoveryPeriod,  int socketTimout) {

		final Context localContext = mContext;
		final int localPort = portNumber;
		//Discovery period in minutes
		final int localDiscovery = discoveryPeriod;
		final int localSocketTimeout = socketTimout;
		// server will listen to one client
	    try
	    {
	        Thread udpServerThread = new Thread()
	        {
	            @Override
	            public void run()
	            {
	               
	                //   Retrieve the ServerName 
	                	Calendar timeout = Calendar.getInstance();
	                	timeout.setTimeInMillis( System.currentTimeMillis() + localDiscovery * 60000  );
	                	
	                	DatagramSocket socket = null;
	                    // Create new UDP-Socket 
                    	//Acquire the lock for wifimanager, otherwise the WIfi stack
                 		//filters out packets not explicitly addressed to this device.
                 		//Acquiring a MulticastLock will cause the stack to receive packets 
                 		//addressed to multicast addresses
                 		//Processing these extra packets can cause a noticable
                 		//battery drain and should be disabled when not needed.
                 		WifiManager wifi = (WifiManager)localContext.getSystemService( Context.WIFI_SERVICE);
                    	WifiManager.MulticastLock mlock = null;
	                    	 try
	     	                {
	                    	System.out.println("UDP S: Connecting...");
	
	                    	 // Create new UDP-Socket 
	                    	if ( wifi != null ){
	            				mlock = wifi.createMulticastLock("Log_Tag");
	            				mlock.acquire();
	            				Log.d("kryonet", "Acquired wifi lock");
	            			}
		                    socket = new DatagramSocket(localPort);
		                    socket.setBroadcast(true);
		                    socket.setReuseAddress(true);
		                    socket.setSoTimeout(localSocketTimeout);
		                    socket.setReceiveBufferSize(64*1024);
		                    socket.setSendBufferSize(64*1024);
		                    byte[] buf = new byte[1024];
	
		                    // * Prepare a UDP-Packet that can contain the data we
		                    // * want to receive
		                    while ( System.currentTimeMillis() < timeout.getTimeInMillis() )  {
		                    	 
		                    DatagramPacket packet = new DatagramPacket(buf,
		                            buf.length);
		                    System.out.println("UDP S: Receiving...");
	
		                  
		                //   wait to Receive the UDP-Packet 
		                    try{
		                    socket.receive(packet);
		                    System.out.println("UDP S: Received: '" + packet.getAddress() + " "
		                            + new String(packet.getData()) + "'");
	
		                    
		                    //send response back
		                    socket.send(packet);
		                  
		                   
		                    String acceptedMsg = new String(packet.getData());
	
		                    System.out.println("UDP S: Done.");
		                    }catch (Exception ex){}
	     	               }
	                    } 
	                    	 catch (Exception e)
	   	                {
	     	            	   
     	            	  System.out.println("UDP S: Error" + e.toString() );
	   	                }
                    	 finally{
                    		 try
	     	            	   {
                    			 if ( socket != null ){
		     	            		   socket.close();
		     	            	   }
		     	            	   if ( mlock != null )  {
		     	            		   mlock.release();
		     	            	   }
		     	            	 
	     	            	   }
	     	            	   catch (Exception ex){}
                    	 }
	                    System.out.println("UDP S: End discovery period");
	            }

	        };
	        udpServerThread.start();

	    }

	    catch (Exception E)
	    {
	    	System.out.println("r: " + E.getMessage())  ;
	    }

	}

//	public static void runUdpServer(Context mContext, int portNumber, int discoveryPeriod) {
//
//		final Context localContext = mContext;
//		final int localPort = portNumber;
//		//Discovery period in minutes
//		final int localDiscovery = discoveryPeriod;
//		// server will listen to one client
//	    try
//	    {
//	        Thread udpServerThread = new Thread()
//	        {
//	            @Override
//	            public void run()
//	            {
//	               
//	                //   Retrieve the ServerName 
//	                	Calendar timeout = Calendar.getInstance();
//	                	timeout.setTimeInMillis( System.currentTimeMillis() + localDiscovery * 60000  );
//	                	
//	                    while ( System.currentTimeMillis() < timeout.getTimeInMillis() )  {
//	                    	 DatagramSocket socket = null;
//	                    	//Acquire the lock for wifimanager, otherwise the WIfi stack
//	                 		//filters out packets not explicitly addressed to this device.
//	                 		//Acquiring a MulticastLock will cause the stack to receive packets 
//	                 		//addressed to multicast addresses
//	                 		//Processing these extra packets can cause a noticable
//	                 		//battery drain and should be disabled when not needed.
//	                 		WifiManager wifi = (WifiManager)localContext.getSystemService( Context.WIFI_SERVICE);
//	                    	 WifiManager.MulticastLock mlock = null;
//	                    	 try
//	     	                {
//	                    	Log.d("UDP", "S: Connecting...");
//		                    // Create new UDP-Socket 
//	                    	if ( wifi != null ){
//	            				mlock = wifi.createMulticastLock("Log_Tag");
//	            				mlock.acquire();
//	            				Log.d("kryonet", "Acquired wifi lock");
//	            			}
//		                    socket = new DatagramSocket(localPort);
//		                    socket.setBroadcast(true);
//		                    socket.setReuseAddress(true);
//		                    socket.setSoTimeout(5000);
//		                    byte[] buf = new byte[17];
//	
//		                    // * Prepare a UDP-Packet that can contain the data we
//		                    // * want to receive
//	
//		                    DatagramPacket packet = new DatagramPacket(buf,
//		                            buf.length);
//		                    Log.d("UDP", "S: Receiving...");
//	
//		                 
//		                    
//		                //   wait to Receive the UDP-Packet 
//		                    socket.receive(packet);
//		                    Log.d("UDP", "S: Received: '" + packet.getAddress() + " "
//		                            + new String(packet.getData()) + "'");
//	
//		                    
//		                    //send response back
//		                    socket.send(packet);
//		                    
//		                 //   mlock.release();
//		                 //   socket.close();
//		                    String acceptedMsg = new String(packet.getData());
//	
//		                    Log.d("UDP", "S: Done.");
//	     	               } catch (Exception e)
//		   	                {
//	     	            	   
//		   	                    Log.e("UDP", "S: Error", e);
//		   	                }
//	                    	 finally{
//	                    		 try
//		     	            	   {
//			     	            	   if ( socket != null ){
//			     	            		   socket.close();
//			     	            	   }
//			     	            	   if ( mlock != null )  {
//			     	            		   mlock.release();
//			     	            	   }
//		     	            	   }
//		     	            	   catch (Exception ex){}
//	                    	 }
//	                    }
//	                    Log.d("UDP", "S: End discovery period");
//	            }
//
//	        };
//	        udpServerThread.start();
//
//	    }
//
//	    catch (Exception E)
//	    {
//	     Log.e("r",E.getMessage())  ;
//	    }
//
//	}
	
	public static InetAddress connectToServer(Context localContext, int UDPPort, int timeout, String givenBroadcastAddress){
		try
		{
		Client client = new Client();
		

	    client.start();
	    InetAddress serverAddress = client.discoverHost(localContext, UDPPort, timeout, givenBroadcastAddress);
	   // try {
	    	Log.d("UDP","server discoverd: " + serverAddress);
	    	return serverAddress;
			//client.connect(5000,  InetAddress.getByAddress(serverAddress.getAddress()), 54555, 54777);
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	System.out.println(  "server discover failure  " + e.getMessage());
			//e.printStackTrace();
		//}
	    
	    
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
//	    			System.out.println(  "server request Data:  " + request.sensorId);
//	    		}
//	    	}
//	    }
//	    	);
	}
		catch ( Exception ex ){
			Log.d("UDP", "server request failure: " + ex.getMessage());
			return null;
		}
	    
	}

}
