package BraceForce.SensorData.Android;

import BraceForce.SensorData.SensorStateMachine;
import BraceForce.SensorData.SensorStatus;
import BraceForce.SensorData.SensorWorkStatus;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Ported from ODK from University of Washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */

public class AndroidSensorStateMachine implements Parcelable, SensorStateMachine {

	//TODO: should these be public or not?
	public SensorStatus status;
	public SensorWorkStatus workStatus;

	public AndroidSensorStateMachine() {
		status = SensorStatus.DISCONNECTED;
		workStatus = SensorWorkStatus.NOT_BUSY;
	}

	public AndroidSensorStateMachine(Parcel in) {
		status = SensorStatus.valueOf(in.readString());
		workStatus = SensorWorkStatus.valueOf(in.readString());
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(status.name());
		dest.writeString(workStatus.name());
	}

	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<AndroidSensorStateMachine> CREATOR = new Parcelable.Creator<AndroidSensorStateMachine>() {
		public AndroidSensorStateMachine createFromParcel(Parcel in) {
			return new AndroidSensorStateMachine(in);
		}

		public AndroidSensorStateMachine[] newArray(int size) {
			return new AndroidSensorStateMachine[size];
		}
	};

	@Override
	public SensorStatus returnSensorStatus() {
		// TODO Auto-generated method stub
		return status;
	}

	@Override
	public SensorWorkStatus returnSensorWorkStatus() {
		// TODO Auto-generated method stub
		return workStatus;
	}
}