package BraceForce.Network.Android;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.util.Log;
import braceForce.distribution.BraceNode;
import braceForce.distribution.DataCacheNodeDistribution;
import braceForce.distribution.DataCacheNodeDistributionImpl;
import braceForce.distribution.SensorMetaInfo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

//Contains non-standard RMI client and server call for data cache node  

public class DataCacheNodeServer extends KyronetThread{

	 private  Client client;
	 private InetAddress localhost;
	 private int localtcpPort;
	 private Context localContext;
//	//call by app node
//	void subscribeSensorDataEvent(String sensorID);
//	Hashtable getSensorData(String sensorID, long Timestamp, long maxTimeDifference);
//	ArrayList getSensorNodeList();
//	ArrayList getSensorList(String sensorNodeID);
	
//	
//	//call by sensor node
//	void reportSensorDataChange(String sensorID, Hashtable sensorData);
	
	 
	 
	 
		//client call to establish TCP connection to remote data cache node and 
		//establish link between server and client
	 
	 
	 
	 
	  public void RMISubscribeSensorDataEvent(InetAddress host, int appNodePort, String appAddress, int tcpPort, String sensorID, int dataCacheNodeObjectID ) throws IOException{
		  	final  String sensorIDLocal = sensorID;
		  	final int localCacheNodeObjectID = dataCacheNodeObjectID;
		  	final int localAppNodePort = appNodePort;
		  	final String localAppNodeAddress = appAddress;
		  
		  //Connect to data cache server using TCP
		try{
		  	 client = new Client();
		  	 this.localhost = host;
		  	 this.localtcpPort = tcpPort;
			 register(client.getKryo());
			 
//			 ObjectSpace clientObjectSpace = new ObjectSpace(client);
//			 final AppNodeDistributionImpl clientObject = 
			 
			 startEndPoint(client);
			 client.addListener(
					 new Listener() {
						 public void connected(final Connection connection){
							 //access to data cache node's subscribeSensorDataEvent
							 subscribeSensorDataEvent(connection, localAppNodePort, localAppNodeAddress, localCacheNodeObjectID, sensorIDLocal );
						 }
						 
						 //two way communication
//						 
//						 public void received(Connection connection, Object object){
//							 if (!(object instanceof MessageWithTestObject)) return;
//                         MessageWithTestObject m = (MessageWithTestObject)object;
//                         System.out.println(clientTestObject.value);
//                         System.out.println(((TestObjectImpl)m.testObject).value);
//                         assertEquals(1234f, m.testObject.other());
//                         stopEndPoints(2000);
//
//						 }
					 }
					 
					 );
			 
			 		client.connect(30000, localhost, localtcpPort);
			 		Thread.sleep(10000);
			 		client.stop();
			 		 client.close();
		}
		catch (Exception ex){
			Log.d("Data Cache Server", "RMISubscribeSensorDataEvent error: " + ex.toString());
		}
			 
	  }
	  
	  //once connection to app node is established, make concrete RMI client call
	  static public void subscribeSensorDataEvent( final Connection connection, final int appNodePort, final String appNodeAddress, final int CacheNodeObjectID, final String sensorID){
		  new Thread(){
			  
			  public void run() {
				 
				  DataCacheNodeDistribution cacheServerObject = ObjectSpace.getRemoteObject(connection, CacheNodeObjectID, DataCacheNodeDistribution.class);
				  RemoteObject remoteObject = (RemoteObject)cacheServerObject;
				  //five seconds timeout
//				  remoteObject.setResponseTimeout(5000);
				  remoteObject.setNonBlocking(true);
				  remoteObject.setTransmitReturnValue(false);
//                m.text = "sometext";
//                m.testObject = ObjectSpace.getRemoteObject(connection, (short)id, TestObject.class);
//                connection.sendTCP(m);
				  Log.d("Data Cache Server", "Call subscribeSensorDataEvent");;
				  
				  //some advanced sample usage
//				  assertEquals(0f, test.other());
//                byte responseID = remoteObject.getLastResponseID();
//                assertEquals(other, remoteObject.waitForResponse(responseID));
//
//                // Non-blocking call that errors out
//                remoteObject.setTransmitReturnValue(false);
//                test.asplode();
//                assertEquals(remoteObject.waitForLastResponse().getClass(), UnsupportedOperationException.class);
//
//                // Call will time out if non-blocking isn't working properly
//                remoteObject.setTransmitExceptions(false);
//                test.moo("Mooooooooo", 3000);
//
//                // Test sending a reference to a remote object.
//                MessageWithTestObject m = new MessageWithTestObject();
//                m.number = 678;
//                m.text = "sometext";
//                m.testObject = ObjectSpace.getRemoteObject(connection, (short)id, TestObject.class);
//                connection.sendTCP(m);
				  Log.d("Data Cache Server", "Call subscribeSensorDataEvent");
				  cacheServerObject.subscribeSensorDataEvent(sensorID, appNodePort, appNodeAddress);
				  
			  }
			  
		  }.run();
	  }

	  
	  //reportSensorDataChange(String sensorID, Hashtable sensorData);
	  public void RMIReportSensorDataChange(InetAddress host, int tcpPort, int dataCacheNodeObjectID, String sensorID, Hashtable sensorData ) throws IOException{
		  	final  String localSensorID = sensorID;
		  	final Hashtable localSensorData = sensorData;
		  	final int localDataCacheNodeObjectID = dataCacheNodeObjectID;
		  	
		  //Connect to data cache server using TCP
		try{
		  	 client = new Client();
		  	 this.localhost = host;
		  	 this.localtcpPort = tcpPort;
			 register(client.getKryo());
			 
//			 ObjectSpace clientObjectSpace = new ObjectSpace(client);
//			 final AppNodeDistributionImpl clientObject = 
			 
			 startEndPoint(client);
			 client.addListener(
					 new Listener() {
						 public void connected(final Connection connection){
							 //access to data cache node's subscribeSensorDataEvent
							 reportSensorDataChange(connection, localSensorID, localSensorData, localDataCacheNodeObjectID );
						 }
						 
						 //two way communication
//						 
//						 public void received(Connection connection, Object object){
//							 if (!(object instanceof MessageWithTestObject)) return;
//                       MessageWithTestObject m = (MessageWithTestObject)object;
//                       System.out.println(clientTestObject.value);
//                       System.out.println(((TestObjectImpl)m.testObject).value);
//                       assertEquals(1234f, m.testObject.other());
//                       stopEndPoints(2000);
//
//						 }
					 }
					 
					 );
			 
			 		client.connect(30000, localhost, localtcpPort);
			 		Thread.sleep(10000);
			 		client.stop();
			 		 client.close();
			 	
		}
		catch (Exception ex){
			Log.d("Data Cache Server", "RMIReportSensorDataChange error " + ex.toString());
		}
			 
	  }
	  
	  //once connection to app node is established, make concrete RMI client call
	  static public void reportSensorDataChange(final Connection connection, final String localSensorID, final Hashtable localSensorData, final int CacheNodeObjectID){
		  new Thread(){
			  
			  public void run() {
				  DataCacheNodeDistribution cacheServerObject = ObjectSpace.getRemoteObject(connection, CacheNodeObjectID, DataCacheNodeDistribution.class);
				  RemoteObject remoteObject = (RemoteObject)cacheServerObject;
				  //five seconds timeout
//				  remoteObject.setResponseTimeout(5000);
				  remoteObject.setNonBlocking(true);
				  remoteObject.setTransmitReturnValue(false);
				  
				  //some advanced sample usage
//				  assertEquals(0f, test.other());
//              byte responseID = remoteObject.getLastResponseID();
//              assertEquals(other, remoteObject.waitForResponse(responseID));
//
//              // Non-blocking call that errors out
//              remoteObject.setTransmitReturnValue(false);
//              test.asplode();
//              assertEquals(remoteObject.waitForLastResponse().getClass(), UnsupportedOperationException.class);
//
//              // Call will time out if non-blocking isn't working properly
//              remoteObject.setTransmitExceptions(false);
//              test.moo("Mooooooooo", 3000);
//
//              // Test sending a reference to a remote object.
//              MessageWithTestObject m = new MessageWithTestObject();
//              m.number = 678;
//              m.text = "sometext";
//              m.testObject = ObjectSpace.getRemoteObject(connection, (short)id, TestObject.class);
//              connection.sendTCP(m);
				  Log.d("Data Cache Server", "Call reportSensorDataChange");
				  cacheServerObject.reportSensorDataChange(localSensorID, localSensorData);
				  
			  }
			  
		  }.run();
	  }

	  
	  
	  public Hashtable RMIGetSensorData(  InetAddress host, int tcpPort, String sensorID, long Timestamp, long maxTimeDifference, int CacheNodeObjectID ) throws IOException{
		final String localSensorID = sensorID;
		final long localTimestamp = Timestamp;
		final long localMaxTimeDifference = maxTimeDifference;
		
		  //Connect to data cache server using TCP
		try{
		  	 client = new Client();
		  	 localhost = host;
		  	 localtcpPort = tcpPort;
			 register(client.getKryo());
			 
//			 ObjectSpace clientObjectSpace = new ObjectSpace(client);
//			 final AppNodeDistributionImpl clientObject = 
			 
			 startEndPoint(client);
			 
			 final DataCacheNodeDistribution cacheServerObject = ObjectSpace.getRemoteObject(client, CacheNodeObjectID, DataCacheNodeDistribution.class);
			 
			 client.addListener(
					 new Listener() {
						 public void connected(final Connection connection){
							 //access to data cache node's subscribeSensorDataEvent
							// subscribeSensorDataEvent(connection, localCacheNodeObjectID, sensorIDLocal );
						 }
						 
						 //two way communication
//						 
//						 public void received(Connection connection, Object object){
//							 if (!(object instanceof MessageWithTestObject)) return;
//                       MessageWithTestObject m = (MessageWithTestObject)object;
//                       System.out.println(clientTestObject.value);
//                       System.out.println(((TestObjectImpl)m.testObject).value);
//                       assertEquals(1234f, m.testObject.other());
//                       stopEndPoints(2000);
//
//						 }
					 }
					 
					 );
			 
			 try {
					client.connect(10000, localhost, localtcpPort);
					  RemoteObject remoteObject = (RemoteObject)cacheServerObject;
					  //five seconds timeout
					  remoteObject.setResponseTimeout(5000);
					  Hashtable result ;
					// Server communication after connection can go here, or in Listener#connected().
					result = cacheServerObject.getSensorData( localSensorID, localTimestamp, localMaxTimeDifference);
				
					return result;
				} catch (Exception ex) {
					Log.d("DataCacheNodeServer", "getSensorData " + ex.toString());
					return null;
				}
			 finally{
					Thread.sleep(10000);
			 		client.stop();
			 		 client.close();
			 }
			 
			 
		
		}
		catch (Exception ex){
			Log.d("Data Cache Server", "RMIGetSensorData error: " + ex.toString());
			return null;
		}
			 
	  }

	
//		ArrayList getSensorNodeList();
	  public ArrayList RMIGetSensorNodeList( InetAddress host, int tcpPort, int CacheNodeObjectID ) throws IOException{
		
		  	
		  //Connect to data cache server using TCP
		try{
		  	 client = new Client();
		  	 localhost = host;
		  	 localtcpPort = tcpPort;
			 register(client.getKryo());
			 
//			 ObjectSpace clientObjectSpace = new ObjectSpace(client);
//			 final AppNodeDistributionImpl clientObject = 
			 
			 startEndPoint(client);
			 
			 final DataCacheNodeDistribution cacheServerObject = ObjectSpace.getRemoteObject(client, CacheNodeObjectID, DataCacheNodeDistribution.class);
			 
			 client.addListener(
					 new Listener() {
						 public void connected(final Connection connection){
							 //access to data cache node's subscribeSensorDataEvent
							// subscribeSensorDataEvent(connection, localCacheNodeObjectID, sensorIDLocal );
						 }
						 
						 //two way communication
//						 
//						 public void received(Connection connection, Object object){
//							 if (!(object instanceof MessageWithTestObject)) return;
//                       MessageWithTestObject m = (MessageWithTestObject)object;
//                       System.out.println(clientTestObject.value);
//                       System.out.println(((TestObjectImpl)m.testObject).value);
//                       assertEquals(1234f, m.testObject.other());
//                       stopEndPoints(2000);
//
//						 }
					 }
					 
					 );
			 
	
			 try {
				 client.connect(30000, localhost, localtcpPort);
				  RemoteObject remoteObject = (RemoteObject)cacheServerObject;
				  //five seconds timeout
				  remoteObject.setResponseTimeout(30000);
				// Server communication after connection can go here, or in Listener#connected().
				  ArrayList result = cacheServerObject.getSensorNodeListSimple();
				 
				  return result;
				} catch (Exception ex) {
					Log.d("DataCacheNodeServer", "RMIGetSensorNodeList " + ex.toString());
					return null;
				}
			 finally{
					Thread.sleep(10000);
			 		client.stop();
			 		 client.close();
			 }
			 	
		}
		catch (Exception ex){
			Log.d("Data Cache Server", "RMIGetSensorNodeList error " + ex.toString());
			return null;
		}
			 
	  }
	  
	  
//		ArrayList getSensorList(String sensorNodeID);
	  public ArrayList RMIGetSensorList( InetAddress host, int tcpPort, int CacheNodeObjectID, String sensorNodeID ) throws IOException{
			
		final String localSensorID = sensorNodeID;  	
		  //Connect to data cache server using TCP
		try{
		  	 client = new Client();
		  	 localhost = host;
		  	 localtcpPort = tcpPort;
			 register(client.getKryo());
			 
//			 ObjectSpace clientObjectSpace = new ObjectSpace(client);
//			 final AppNodeDistributionImpl clientObject = 
			 
			 startEndPoint(client);
			 
			 final DataCacheNodeDistribution cacheServerObject = ObjectSpace.getRemoteObject(client, CacheNodeObjectID, DataCacheNodeDistribution.class);
			 
			 client.addListener(
					 new Listener() {
						 public void connected(final Connection connection){
							 //access to data cache node's subscribeSensorDataEvent
							// subscribeSensorDataEvent(connection, localCacheNodeObjectID, sensorIDLocal );
						 }
						 
						 //two way communication
//						 
//						 public void received(Connection connection, Object object){
//							 if (!(object instanceof MessageWithTestObject)) return;
//                       MessageWithTestObject m = (MessageWithTestObject)object;
//                       System.out.println(clientTestObject.value);
//                       System.out.println(((TestObjectImpl)m.testObject).value);
//                       assertEquals(1234f, m.testObject.other());
//                       stopEndPoints(2000);
//
//						 }
					 }
					 
					 );
			 

			 try {
				 client.connect(30000, localhost, localtcpPort);
				  RemoteObject remoteObject = (RemoteObject)cacheServerObject;
				  //five seconds timeout
				  remoteObject.setResponseTimeout(30000);
				// Server communication after connection can go here, or in Listener#connected().
				ArrayList result = cacheServerObject.getSensorList(localSensorID);
				return result;
				} catch (Exception ex) {
					Log.d("DataCacheNodeServer", "getSensorList " + ex.toString());
					return null;
				}
			 finally{
					Thread.sleep(10000);
			 		client.stop();
			 		 client.close();
			 }
			 	
		}
		catch (Exception ex){
			Log.d("DataCacheNodeServer", "RMIGetSensorList errror: " + ex.toString());
			return null;
		}
			 
	  }	  
	  
	  
	  //App node needs to start his first
	public void startDataCacheNodeServer(int tcpPort, int CacheNodeObjectID) throws IOException {
	try{
		Server server = new Server();
		register(server.getKryo());
		startEndPoint(server);
		server.bind(tcpPort);
		
		final ObjectSpace serverObjectSpace = new ObjectSpace();
		final DataCacheNodeDistributionImpl cacheNodeImpl = new DataCacheNodeDistributionImpl();
		serverObjectSpace.register(CacheNodeObjectID, cacheNodeImpl);
		
		server.addListener( 
				new Listener() {
					public void connected( final Connection connection ){
						serverObjectSpace.addConnection(connection);
						//can invoke call back here
					}
					
					//can received remote object sent over TCP
//					  public void received (Connection connection, Object object) {
//                          if (!(object instanceof MessageWithTestObject)) return;
//                          MessageWithTestObject m = (MessageWithTestObject)object;
//                          System.out.println(serverTestObject.value);
//                          System.out.println(((TestObjectImpl)m.testObject).value);
//                          assertEquals(4321f, m.testObject.other());
//                          stopEndPoints(2000);
//                  }
					
				
				}
				
		);
		//waitForThreads();
	}
	catch (Exception ex ){
		Log.d("DataCacheNodeServer", "Server start error: " + ex.toString());
	}
	finally{
		//waitForThreads();
	}
		
	}
	
	//both client and server need to do it
    public void register (Kryo kryo) {
        // kryo.register(AppNodeDistribution.class);
    	 kryo.register(Client.class);
         kryo.register(DataCacheNodeDistribution.class);
         kryo.register(BraceNode.class);
         kryo.register(java.net.Inet4Address.class);
         kryo.register(byte[].class);
       //  kryo.register(SensorNodeDistribution.class);
         kryo.register(SensorMetaInfo.class);
         kryo.register(Connection.class);
         kryo.register(ArrayList.class);
         kryo.register(StackTraceElement.class);
         kryo.register(StackTraceElement[].class);
         kryo.register(UnsupportedOperationException.class);
         kryo.register(Hashtable.class);
         ObjectSpace.registerClasses(kryo);
 }
	
}
