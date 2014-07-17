package BraceForce.Network.Android;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.util.Log;
import braceForce.distribution.BraceNode;
import braceForce.distribution.SensorMetaInfo;
import braceForce.distribution.SensorNodeDistribution;
import braceForce.distribution.SensorNodeDistributionImpl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

//Contains non-standard RMI client and server call for data cache node  

public class SensorNodeServer extends KyronetThread {

	 private  Client client;
	 private InetAddress localhost;
	 private int localtcpPort;
	 private Context localContext;

//		ArrayList getListOfSensors();
//		void subScribeSensorEvent(String sensorID, String dataCacheStationID);
//		Hashtable getSensorData(String sensorID, long dateTime, long timeDifference);
	
		//client call to establish TCP connection to remote data cache node and 
		//establish link between server and client
	  public void RMISubscribeSensorDataEvent( InetAddress host, int dataCacheNodePort, int tcpPort, String sensorID, String dataCacheStationID, int sensorNodeObjectID ) throws IOException{
		  	final  String sensorIDLocal = sensorID;
		  	final int localSensorNodeObjectID = sensorNodeObjectID;
		  	final String localDataCacheStationID = dataCacheStationID;
		  //	final InetAddress localHost = host;
		  	final int localTcpPort = tcpPort;
		  	final int LocaldatacacheNodePort = dataCacheNodePort;
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
							 subscribeSensorDataEvent( localDataCacheStationID, LocaldatacacheNodePort ,connection, localSensorNodeObjectID, sensorIDLocal, localDataCacheStationID );
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
			 
			 		client.connect(10000, localhost, localtcpPort);
			 		Thread.sleep(10000);
			 		client.stop();
			 		 client.close();
		}
		catch (Exception ex){
			Log.d("Sensor Node Server", "RMISubscribeSensorDataEvent error: " + ex.toString());
		}
			 
	  }
	  
	  //once connection to app node is established, make concrete RMI client call
	  static public void subscribeSensorDataEvent( final String hostAddress, final int hostPort,  final Connection connection, final int SensorNodeObjectID, final String  sensorID, final String dataCacheStationID){
		  new Thread(){
			  
			  public void run() {
				  SensorNodeDistribution sensorServerObject = ObjectSpace.getRemoteObject(connection, SensorNodeObjectID, SensorNodeDistribution.class);
				  RemoteObject remoteObject = (RemoteObject)sensorServerObject;
				  //five seconds timeout
//				  remoteObject.setResponseTimeout(5000);
				  remoteObject.setNonBlocking(true);
				  remoteObject.setTransmitReturnValue(false);
				  
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
				  Log.d("Sensor Node Server", "Call reportSensorDataChange");
				  
				  //host address and tcp port are available
				  //but not through connection
				  //as Sensor server requires the registration
				  //of Data Cache server
				  //the connection.getRemoteAddressTCP() is getting
				  //sensor server's information
				    
				//	String hostAddress = connection.getAddress().getHostAddress();
					String hostName =  hostAddress;
					//int hostPort = connection.getRemoteAddressTCP().getPort();
				  sensorServerObject.subScribeSensorEvent( sensorID, dataCacheStationID, hostAddress, hostName, hostPort );
				  
			  }
			  
		  }.run();
	  }

	  
		  
	  //Hashtable getSensorData(String sensorID, long dateTime, long timeDifference);
	  public Hashtable RMIGetSensorData(  InetAddress host, int tcpPort, String sensorID, long Timestamp, long maxTimeDifference, int SensorNodeObjectID ) throws IOException{
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
			 
			 final SensorNodeDistribution sensorServerObject = ObjectSpace.getRemoteObject(client, SensorNodeObjectID, SensorNodeDistribution.class);
			 
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
					  RemoteObject remoteObject = (RemoteObject)sensorServerObject;
					  //five seconds timeout
					  remoteObject.setResponseTimeout(30000);
					  Hashtable result ;
					// Server communication after connection can go here, or in Listener#connected().
					result = sensorServerObject.getSensorData( localSensorID, localTimestamp, localMaxTimeDifference);
					return result;
				} catch (Exception ex) {
					Log.d("SensorNodeServer", "getSensorData " + ex.toString());
					return null;
				}
			 finally{
					Thread.sleep(10000);
			 		client.stop();
			 		 client.close();
			 }
			 	
		}
		catch (Exception ex){
			Log.d("Sensor Node Server", "TCP connection error " + ex.toString());
			return null;
		}
			 
	  }

	
	  
//		ArrayList getListOfSensors();
	  public ArrayList RMIGetSensorList( InetAddress host, int tcpPort, int sensorNodeObjectID) throws IOException{
			
	
		  //Connect to data cache server using TCP
		try{
		  	 client = new Client();
		  	 localhost = host;
		  	 localtcpPort = tcpPort;
			 register(client.getKryo());
			 
//			 ObjectSpace clientObjectSpace = new ObjectSpace(client);
//			 final AppNodeDistributionImpl clientObject = 
			 
			 startEndPoint(client);
			 
			 final SensorNodeDistribution sensorServerObject = ObjectSpace.getRemoteObject(client, sensorNodeObjectID, SensorNodeDistribution.class);
			 
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
					  RemoteObject remoteObject = (RemoteObject)sensorServerObject;
					  //five seconds timeout
					  remoteObject.setResponseTimeout(30000);
					  ArrayList result ;
					// Server communication after connection can go here, or in Listener#connected().
					result = sensorServerObject.getListOfSensors();
					return result;
				} catch (Exception ex) {
					Log.d("SensorNodeServer", "getSensorList " + ex.toString());
					return null;
				}
			 finally{
				 client.stop();
				 client.close();
			 }
		
			 	
		}
		catch (Exception ex){
			Log.d("Sensor Node Server", "TCP connection error " + ex.toString());
			return null;
		}
			 
	  }	  
	  
	  //suppress sensor node
		//client call to establish TCP connection to remote data cache node and 
		//establish link between server and client
	  public void RMISuppressSensorData(InetAddress host, int tcpPort, String sensorID, boolean suppressionMode, int sensorNodeObjectID ) throws IOException{
		  	final  String sensorIDLocal = sensorID;
		  	final int localSensorNodeObjectID = sensorNodeObjectID;
		  	final boolean sensorSuppressionModeLocal = suppressionMode;
		  	
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
							 suppressSensorNodeData(connection, localSensorNodeObjectID, sensorIDLocal, sensorSuppressionModeLocal );
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
			Log.d("Sensor Node Server", "RMISuppressSensorData error: " + ex.toString());
		}
			 
	  }
	  
	  //once connection to app node is established, make concrete RMI client call
	  static public void suppressSensorNodeData(final Connection connection, final int SensorNodeObjectID, final String  sensorID, final boolean sensorSuppressionMode){
		  new Thread(){
			  
			  public void run() {
				  SensorNodeDistribution sensorServerObject = ObjectSpace.getRemoteObject(connection, SensorNodeObjectID, SensorNodeDistribution.class);
				  RemoteObject remoteObject = (RemoteObject)sensorServerObject;
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
//    
//              test.moo("Mooooooooo", 3000);
//
//              // Test sending a reference to a remote object.
//              MessageWithTestObject m = new MessageWithTestObject();
//              m.number = 678;
//              m.text = "sometext";
//              m.testObject = ObjectSpace.getRemoteObject(connection, (short)id, TestObject.class);
//              connection.sendTCP(m);
				  Log.d("Sensor Node Server", "Call suppressSensorNodeData");
				  sensorServerObject.suppressSensorEvent(sensorID, sensorSuppressionMode);
				  
			  }
			  
		  }.run();
	  }

	  
	  //App node needs to start his first
	public void startSensorNodeServer(int tcpPort, int SensorNodeObjectID) throws IOException {
	try{
		Server server = new Server();
		register(server.getKryo());
		startEndPoint(server);
		server.bind(tcpPort);
		
		final ObjectSpace serverObjectSpace = new ObjectSpace();
		final SensorNodeDistributionImpl sensorNodeImpl = new SensorNodeDistributionImpl();
		serverObjectSpace.register(SensorNodeObjectID, sensorNodeImpl);
		
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
		Log.d("Sensor Node Server", "Server start error: " + ex.toString());
	}
	finally{
		//waitForThreads();
		//AndroidSensorDataManagerSingleton.cleanup();
		
	}
		
	}
	
	//both client and server need to do it
    public void register (Kryo kryo) {
       //  kryo.register(AppNodeDistribution.class);
      //   kryo.register(DataCacheNodeDistribution.class);
    	//
   
    	 kryo.register(Client.class);
    	 kryo.register(SensorNodeDistribution.class);
    	 kryo.register(BraceNode.class);
    	 kryo.register(java.net.Inet4Address.class);
    	 kryo.register(byte[].class);
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
