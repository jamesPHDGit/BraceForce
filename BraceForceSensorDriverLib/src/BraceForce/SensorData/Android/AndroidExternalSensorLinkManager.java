package BraceForce.SensorData.Android;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import braceForce.Drivers.Android.AndroidDriverCommunicator;
import braceForce.Drivers.Android.AndroidSensorDataPacket;
import braceForce.Drivers.Android.AndroidSensorDataParseResponse;
import braceForce.Drivers.Android.AndroidSensorParameter;

import BraceForce.Drivers.ParameterMissingException;
import BraceForce.Drivers.SensorDataPacket;
import BraceForce.SensorData.ChannelManager;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.SensorEventModel;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public class AndroidExternalSensorLinkManager extends SensorEventModel implements AndroidSensorLinkManager {

	// logging
	protected static final String LOGTAG = "SensorImpl";

	protected String sensorId;
	protected boolean usingContentProvider;
	protected ChannelManager commChannelManager;
	protected Queue<SensorDataPacket> buffer;
	protected int clientCounter;
	
	protected byte [] remainingBytes;

	private ConcurrentLinkedQueue<AndroidSensorDataPacket> bufferAndroid;
	private Context mContext;
	
	protected AndroidDriverCommunicator sensorDriverCom;
	
	public AndroidExternalSensorLinkManager(String sensorID, AndroidDriverCommunicator driverCom, ChannelManager channelMgr, Context context) {
		this.sensorId = sensorID;
		this.sensorDriverCom = driverCom;
		this.commChannelManager = channelMgr;
		this.mContext = context;

		this.usingContentProvider = false;
		clientCounter = 0;
		this.bufferAndroid = new ConcurrentLinkedQueue<AndroidSensorDataPacket>();
	}
	
	public void configure(String setting, Bundle params) throws ParameterMissingException {
		try {
			commChannelManager.sensorWrite(sensorId,
					sensorDriverCom.configureCmd(setting, params));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public List<Bundle> getSensorData(long maxNumReadings) {
		ArrayList<AndroidSensorDataPacket> rawData = new ArrayList<AndroidSensorDataPacket>();
		rawData.addAll(bufferAndroid);
		
		AndroidSensorDataParseResponse response;
		
		try {
			response = sensorDriverCom.getSensorData(maxNumReadings, rawData, remainingBytes);
			if ( response == null ) return null;
			//only when proxy driver returns valid answer, clear the history data
			bufferAndroid.clear();
			remainingBytes = response.getRemainingData();
			return response.getSensorData();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void addSensorDataPacket(AndroidSensorDataPacket packet) {
		bufferAndroid.add(packet);
		notifyListeners(getSensorID(), packet, mContext);
	}

	public void sendDataToSensor(Bundle dataToEncode) {
		byte[] encodedSensorData;
		try {
			encodedSensorData = sensorDriverCom.encodeDataToSendToSensor(dataToEncode);
			if(encodedSensorData != null) 
				commChannelManager.sensorWrite(sensorId, encodedSensorData);		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean usingContentProvider() {
		return usingContentProvider;
	}

	@Override
	public void connect()
			throws SensorNotFoundException {
		try {
			commChannelManager.sensorConnect(sensorId);			
		} catch (SensorNotFoundException snfe) {
			snfe.printStackTrace();
			throw snfe;
		}
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#getCommunicationChannelType()
	 */
	@Override
	public CommunicationChannelType getCommunicationChannelType() {
		return commChannelManager.getCommChannelType();
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#disconnect()
	 */
	@Override
	public void disconnect() throws SensorNotFoundException {
		try {
			commChannelManager.sensorDisconnect(sensorId);
		} catch (SensorNotFoundException snfe) {
			snfe.printStackTrace();
			throw snfe;
		}
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#configure(java.lang.String, android.os.Bundle)
	 */

	
	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#startSensor()
	 */
	@Override
	public boolean startSensor() {
		try {
			commChannelManager.startSensorDataAcquisition(sensorId,
					sensorDriverCom.startCmd());
			clientCounter++;
			return true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#stopSensor()
	 */
	@Override
	public boolean stopSensor() {
		--clientCounter;
//		Log.d(LOGTAG,"stopping sensor. conn counter: " + clientCounter);
		
		if(clientCounter == 0) {
			try {
				commChannelManager.stopSensorDataAcquisition(sensorId,
						sensorDriverCom.stopCmd());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				commChannelManager.sensorDisconnect(sensorId);
			}
			catch(SensorNotFoundException snfe) {
				snfe.printStackTrace();
			}
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#getSensorID()
	 */
	@Override
	public String getSensorID() {
		return sensorId;
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#addSensorDataPacket(org.opendatakit.sensors.SensorDataPacket)
	 */
	@Override
	public void addSensorDataPacket(SensorDataPacket packet) {
		buffer.add(packet);
		notifyListeners(getSensorID(), packet, mContext);
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#dataBufferReset()
	 */
	@Override
	public void dataBufferReset() {
		Log.v(LOGTAG, "dataBufferReset: clearing buffer for sensor ");
		if (bufferAndroid != null) {
			bufferAndroid = new ConcurrentLinkedQueue<AndroidSensorDataPacket>();
		}
		
		//Backward compatibility
		if ( buffer != null ){
			buffer = new ConcurrentLinkedQueue<SensorDataPacket>();
		}
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#usingContentProvider()
	 */

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#shutdown()
	 */
	@Override
	public void shutdown() throws SensorNotFoundException {
		try {
			this.disconnect();
		}
		finally {
			sensorDriverCom.shutdown();
		}
	}

	@Override
	public void configure(String setting, Hashtable params)
			throws ParameterMissingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Hashtable> getSensorDatainCollection(long maxNumReadings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendDataToSensor(Hashtable dataToEncode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBuiltin() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AndroidSensorParameter> returnDriverParameters() {
		// TODO Auto-generated method stub
		try {
			return sensorDriverCom.getDriverParameters();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	

}
