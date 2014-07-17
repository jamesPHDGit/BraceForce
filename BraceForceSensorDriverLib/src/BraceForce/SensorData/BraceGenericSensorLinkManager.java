
package BraceForce.SensorData;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import BraceForce.Drivers.Driver;
import BraceForce.Drivers.ParameterMissingException;
import BraceForce.Drivers.SensorDataPacket;
import BraceForce.Drivers.SensorDataResponse;
import BraceForce.SensorLink.BraceForceSensorLinkManager;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.SensorEventModel;
import BraceForce.SensorLink.SensorNotFoundException;
import android.content.Context;
import android.util.Log;


public class BraceGenericSensorLinkManager extends SensorEventModel implements BraceForceSensorLinkManager {
	// logging
	protected static final String LOGTAG = "SensorImpl";

	protected String sensorId;
	protected boolean usingContentProvider;
	protected ChannelManager commChannelManager;
	protected Driver sensorDriverCom;
	protected Queue<SensorDataPacket> buffer;
	protected int clientCounter;

	protected byte [] remainingBytes;
	private Context mContext;

	public BraceGenericSensorLinkManager(){}

	public BraceGenericSensorLinkManager(String sensorID, Driver driverCom, ChannelManager channelMgr, Context context) {
		this.sensorId = sensorID;
		this.sensorDriverCom = driverCom;
		this.commChannelManager = channelMgr;

		this.mContext = context;

		this.usingContentProvider = false;
		clientCounter = 0;
		this.buffer = new ConcurrentLinkedQueue<SensorDataPacket>();
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#connect(boolean)
	 */
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
		commChannelManager.startSensorDataAcquisition(sensorId,
				sensorDriverCom.startCmd());
		clientCounter++;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.opendatakit.sensors.ODKSensorInterface#stopSensor()
	 */
	@Override
	public boolean stopSensor() {
		--clientCounter;
//		Log.d(LOGTAG,"stopping sensor. conn counter: " + clientCounter);
		
		if(clientCounter == 0) {
			commChannelManager.stopSensorDataAcquisition(sensorId,
					sensorDriverCom.stopCmd());

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
		if (buffer != null) {
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
		try {
			commChannelManager.sensorWrite(sensorId,
					sensorDriverCom.getSensorConfigCmd(setting, params));
		} catch (ParameterMissingException pmx) {
			pmx.printStackTrace();
			throw pmx;
		}
		
	}

	@Override
	public List<Hashtable> getSensorDatainCollection(long maxNumReadings) {
		// TODO Auto-generated method stub
		ArrayList<SensorDataPacket> rawData = new ArrayList<SensorDataPacket>();
		rawData.addAll(buffer);
		buffer.clear();
		SensorDataResponse response = sensorDriverCom.getSensorData(maxNumReadings, rawData, remainingBytes);
		return response.getSensorDataInCollection();
	}

	@Override
	public void sendDataToSensor(Hashtable dataToEncode) {
		// TODO Auto-generated method stub
		byte[] encodedSensorData = sensorDriverCom.sendDataToSensor(dataToEncode);
		if(encodedSensorData != null) 
			commChannelManager.sensorWrite(sensorId, encodedSensorData);
	}		
}
