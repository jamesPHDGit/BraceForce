package braceForce.distribution;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import BraceForce.Network.Android.AppNodeServer;
import BraceForce.Network.Android.SensorNodeServer;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;

public class DataCacheNodeDistributionImpl implements DataCacheNodeDistribution {

	private String nodeStationID;
	private int sensorNodeObjectRMIID =38;
	private int appNodeObjectRMIID = 42;
	private int nodeTCPPort = 56554;

	//call from application node
	//establish subscription
	@Override
	public void subscribeSensorDataEvent( String sensorID, int appNodePort, String appNodeAddress) {
		// TODO Auto-generated method stub
		String appNodeID = appNodeAddress;
		
		
		try {
			InetAddress remoteAddress = InetAddress.getByName(appNodeAddress);
			
			DataCacheNodeAppD2DManager.addNewAppNode(remoteAddress,
					appNodeAddress,  appNodeAddress , 
					  Integer.toString(appNodePort), null);
			
			DataCacheNodeAppD2DManager.subscribeForSensorEvent(appNodeID, sensorID);
			//subscribe for this sensor
			//first to find out which sensor node this sensor is attached to
			String sensorNodeID = DataCacheNodeSensorD2DManager.findSensorNodeAttached(sensorID);
			//find out InetAddress and TCP for this sensor node
			BraceNode sensorNodeInfo = DataCacheNodeSensorD2DManager.findSensorNodeInfo(sensorNodeID);
					
			//subscribe to the sensor station
			SensorNodeServer sensorServer = new SensorNodeServer();
			
			sensorServer.RMISubscribeSensorDataEvent( sensorNodeInfo.getNodeAddress(), getNodeStationTCPPort(), Integer.parseInt(sensorNodeInfo.getTcpPort()), sensorID, getNodeStationID(), getSensorNodeObjectRMIID());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("DataCacheNodeDistribution Error: " + e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("DataCacheNodeDistribution Error: " + e.toString());
		}
	}

	//find which sensor node this sensor belongs to
	//and retrieves the sensor data in real time
	@Override
	public Hashtable getSensorData(String sensorID, long Timestamp,
			long maxTimeDifference) {
		// TODO Auto-generated method stub
		//subscribe for this sensor
		//first to find out which sensor node this sensor is attached to
		String sensorNodeID = DataCacheNodeSensorD2DManager.findSensorNodeAttached(sensorID);
		//find out InetAddress and TCP for this sensor node
		BraceNode sensorNodeInfo = DataCacheNodeSensorD2DManager.findSensorNodeInfo(sensorNodeID);
				
		//subscribe to the sensor station
		SensorNodeServer sensorServer = new SensorNodeServer();
		
		try {
			return sensorServer.RMIGetSensorData( sensorNodeInfo.getNodeAddress(), Integer.parseInt(sensorNodeInfo.getTcpPort()), sensorID, Timestamp, maxTimeDifference, getSensorNodeObjectRMIID());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("DataCacheNodeDistribution Error: " + e.toString());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("DataCacheNodeDistribution Error: " + e.toString());
			return null;
		}
	}

	//find which sensor node this sensor belongs to
	//and retrieves the sensor data in real time
	//consults with internal lock-free queue to find list of sensor nodes
	@Override
	public ArrayList getSensorNodeList() {
		// TODO Auto-generated method stub
		return (ArrayList) DataCacheNodeSensorD2DManager.getActiveSensorNodes();
	}
	
	
	//For a given sensorNode, find out list of sensors
	@Override
	public ArrayList getSensorList(String sensorNodeID) {
		return DataCacheNodeSensorD2DManager.getSensorList(sensorNodeID);
	}

	//Call back from sensor node
	@Override
	public void reportSensorDataChange(String sensorID, Hashtable sensorData) {
	
		//register the data change for spatial data correlation
		DataCacheNodeSensorD2DManager.storeTemporalReading(sensorID, sensorData);
		
		Log.d("DataNodeService", "get sensor data report back from " + sensorID);
				
	   try {
			String appNodeID = DataCacheNodeAppD2DManager.findSubscriber(sensorID);
		
			//find out InetAddress and TCP for this app node
			BraceNode appNodeInfo = DataCacheNodeAppD2DManager.findAppNode(appNodeID);
					
			//report the data to app node
			AppNodeServer appServer = new AppNodeServer();
			
		
		
		   appServer.RMIReportSensorDataChange( appNodeInfo.getNodeAddress(), Integer.parseInt(appNodeInfo.getTcpPort()),
					sensorID, getAppNodeObjectRMIID(),  sensorData);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("DataCacheNodeDistribution Error: " + e.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("DataCacheNodeDistribution Error: " + e.toString());
		}
	}

	public int getNodeStationTCPPort() {
		return nodeTCPPort;
	}
	
	public void setNodeStationTCPPort(int tcpPort) {
		nodeTCPPort = tcpPort;
	}
	
	public String getNodeStationID() {
		return nodeStationID;
	}

	public void setNodeStationID(String nodeStationID) {
		this.nodeStationID = nodeStationID;
	}

	public int getSensorNodeObjectRMIID() {
		return sensorNodeObjectRMIID;
	}

	public void setSensorNodeObjectRMIID(int sensorNodeObjectRMIID) {
		this.sensorNodeObjectRMIID = sensorNodeObjectRMIID;
	}

	public int getAppNodeObjectRMIID() {
		return appNodeObjectRMIID;
	}

	public void setAppNodeObjectRMIID(int appNodeObjectRMIID) {
		this.appNodeObjectRMIID = appNodeObjectRMIID;
	}


		//suppress sensor report
		public void suppressSensorDataReport(String sensorID, boolean suppressMode) {
			// TODO Auto-generated method stub
			//subscribe for this sensor
			//first to find out which sensor node this sensor is attached to
			String sensorNodeID = DataCacheNodeSensorD2DManager.findSensorNodeAttached(sensorID);
			//find out InetAddress and TCP for this sensor node
			BraceNode sensorNodeInfo = DataCacheNodeSensorD2DManager.findSensorNodeInfo(sensorNodeID);
					
			//subscribe to the sensor station
			SensorNodeServer sensorServer = new SensorNodeServer();
			
			try {
				sensorServer.RMISuppressSensorData(sensorNodeInfo.getNodeAddress(), Integer.parseInt(sensorNodeInfo.getTcpPort()), sensorID, suppressMode, getSensorNodeObjectRMIID());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				System.out.println("DataCacheNodeDistribution Error: " + e.toString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("DataCacheNodeDistribution Error: " + e.toString());
				
			}
		}

		@Override
		public ArrayList getSensorNodeListSimple() {
			// TODO Auto-generated method stub
			List<BraceNode> nodeLists = DataCacheNodeSensorD2DManager.getActiveSensorNodes();
			ArrayList<String> simpleNodeArray = new ArrayList<String>();
			for (BraceNode braceNode : nodeLists){
				simpleNodeArray.add(braceNode.getNodeID());
			}
			return simpleNodeArray;
		}

		
	


}
