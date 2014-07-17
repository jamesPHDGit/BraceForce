package braceForce.Drivers.Android;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import BraceForce.Drivers.SensorDataResponse;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Ported from ODK from University of Washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public final class AndroidSensorDataParseResponse implements Parcelable, SensorDataResponse {

	private final byte[] remainingData;
	
	private final List<Bundle> sensorData;
	
	public AndroidSensorDataParseResponse(List<Bundle> sensorData, byte[] remainingData) {
		this.sensorData = sensorData;
		this.remainingData = remainingData;
	}
	
	public AndroidSensorDataParseResponse(Parcel p) {
		sensorData = new ArrayList<Bundle>();
		p.readList(sensorData, null);
		remainingData = p.createByteArray();
//		Log.d("SDP", "read payload of length: " + payload.length);
	}

	
	
	public byte[] getRemainingData() {
		return remainingData;
	}

	public List<Bundle> getSensorData() {
		return sensorData;
	}
	
	public List<Hashtable> getSensorDataInCollection() {
		return null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(sensorData);
		dest.writeByteArray(remainingData);
	}

	public static final Parcelable.Creator<AndroidSensorDataParseResponse> CREATOR = new Parcelable.Creator<AndroidSensorDataParseResponse>() {
		public AndroidSensorDataParseResponse createFromParcel(Parcel in) {
			return new AndroidSensorDataParseResponse(in);
		}

		public AndroidSensorDataParseResponse[] newArray(int size) {
			return new AndroidSensorDataParseResponse[size];
		}
	};

}
