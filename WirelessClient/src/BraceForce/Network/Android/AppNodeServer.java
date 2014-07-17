package BraceForce.Network.Android;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Hashtable;

import BraceForce.Distribution.AppNodeDistribution;
import BraceForce.Distribution.AppNodeDistributionImpl;

import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class AppNodeServer extends KyronetThread{

	  public void connectToServerUsingTCP(InetAddress host, int tcpPort, String sensorID, int appNodeObjectID, Hashtable sensorData) throws IOException{
		  	final  String sensorIDLocal = sensorID;
		  	final int appNodeObjectIDLocal = appNodeObjectID;
		  	final Hashtable sensorDataLocal = sensorData;
		  //Connect to server using TCP
		try{
		  	Client client = new Client();
			 register(client.getKryo());
			 
//			 ObjectSpace clientObjectSpace = new ObjectSpace(client);
//			 final AppNodeDistributionImpl clientObject = 
			 
			 startEndPoint(client);
			 client.addListener(
					 new Listener() {
						 public void connected(final Connection connection){
							 //access to Application node's reportSensorDataChange
							 reportSensorDataChangeToAppNode(connection, appNodeObjectIDLocal, sensorIDLocal, sensorDataLocal);
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
			 
			 		client.connect(10000, host, tcpPort);
			 	
		}
		catch (Exception ex){
			Log.d("TCP Client", "TCP connection error " + ex.toString());
		}
			 
	  }
	  
	  static public void reportSensorDataChangeToAppNode(final Connection connection, final int appNodeObjectID, final String  sensorID, final Hashtable sensorData){
		  new Thread(){
			  
			  public void run() {
				  AppNodeDistribution appServerObject = ObjectSpace.getRemoteObject(connection, appNodeObjectID, AppNodeDistribution.class);
				  RemoteObject remoteObject = (RemoteObject)appServerObject;
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
				  Log.d("TCP Client", "Call reportSensorDataChange");
				  appServerObject.reportSensorDataChange(sensorID, sensorData);
				  
			  }
			  
		  }.run();
	  }
	
	public void startAppServer(int tcpPort, int appNodeObjectID) throws IOException {
	try{
		Server server = new Server();
		register(server.getKryo());
		startEndPoint(server);
		server.bind(tcpPort);
		
		final ObjectSpace serverObjectSpace = new ObjectSpace();
		final AppNodeDistributionImpl appNodeImpl = new AppNodeDistributionImpl();
		serverObjectSpace.register(appNodeObjectID, appNodeImpl);
		
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
	}
	catch (Exception ex ){
		Log.d("TCP Server", "Server start error: " + ex.toString());
	}
	finally{
		waitForThreads();
	}
		
	}
	
    public void register (Kryo kryo) {
         kryo.register(AppNodeDistribution.class);
         kryo.register(StackTraceElement.class);
         kryo.register(StackTraceElement[].class);
         kryo.register(UnsupportedOperationException.class);
         kryo.register(Hashtable.class);
         ObjectSpace.registerClasses(kryo);
 }
	
}
