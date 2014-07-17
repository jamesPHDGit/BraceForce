package braceForce.Drivers.Android;

import BraceForce.Drivers.SensorDataPacket;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Ported from ODK from University of Washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class AndroidSensorDataPacket implements Parcelable, SensorDataPacket { 

	// common sensor data		
	private long time;
	private byte[] payload;
	//not sure why still uses it
	private int sizeOfSeries;
	
	public AndroidSensorDataPacket(byte[] buff, long time) {
		this.payload = buff;
		this.time = time;
		this.sizeOfSeries = -1;
		if(buff == null) {
			buff = new byte[0];
		}

	}

	public AndroidSensorDataPacket(byte[] buff, long time, int numOfReadingsInSeries) {
		this(buff, time);
		this.sizeOfSeries = numOfReadingsInSeries;
	}	
	
	public long getTime() {
		return time;
	}

	public byte[] getPayload() {
		return payload;
	}

	public int getSizeOfSeries() {
		return sizeOfSeries;
	}


	public static Parcelable.Creator<AndroidSensorDataPacket> getCreator() {
		return CREATOR;
	}

	public AndroidSensorDataPacket(Parcel p) {		
		time = p.readLong();
		sizeOfSeries = p.readInt();
		payload = p.createByteArray();
//		Log.d("SDP", "read payload of length: " + payload.length);
	}
	
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeLong(time);
		dest.writeInt(sizeOfSeries);
		dest.writeByteArray(payload);
	}
	
	public int describeContents() {
		return 0;
	}
	
	
	public static final Parcelable.Creator<AndroidSensorDataPacket> CREATOR = new
			Parcelable.Creator<AndroidSensorDataPacket>() {
		public AndroidSensorDataPacket createFromParcel(Parcel in) {
			return new AndroidSensorDataPacket(in);
		}

		public AndroidSensorDataPacket[] newArray(int size) {
			return new AndroidSensorDataPacket[size];
		}
	};
	
//	/**
//	 * Parses a string for data and sets packet. Return true if parsing
//	 * successful, false otherwise.
//	 * @param tokens parsed string tokens
//	 * @return true if correct token count
//	 */
//	public boolean parseTokens(StringTokenizer tokens) {
//		if(tokens.countTokens() > 0) {
//			time = Long.valueOf(tokens.nextToken());
//			return true;
//		}
//		return false;
//	}
//	
//	/**
//	 * Creates a data array for storing packets
//	 * @param array size
//	 * @return new array
//	 */
//	public Long[][] arrayCreate(int size) {
//		return new Long[1][size];
//	}
//	
//	/**
//	 * Adds this sample to an array at the index.
//	 * @param index location to store data
//	 * @param values array to store data into
//	 */
//	public void arrayAdd(int index, long[][] values) {
//		values[0][index] = time;
//	}
//	
//	/**
//	 * Stores the array in a bundle object.
//	 * @param b bundle to store data
//	 * @param values array to read values from
//	 */
//	public void arrayToBundle(Bundle b, long[][] values) {
//		b.putLongArray(ServiceConstants.SENSOR_DATA_TIME, values[0]);
//	}
//	
//	/**
//	 * Write packet to a file
//	 * @param writer file to append data into
//	 */
//	public void fileAppend(BufferedWriter writer) {
//		try {
//			writer.write(Long.toString(time) + " ");
//		} catch (IOException e) { }
//	}
//	
//	//add sensor specific data in the bundle. return true if bundle modified, false otherwise
//	//base class just returns false
//	public boolean fillBundle(Bundle b) {
//		return false;
//	}		
}
