package braceForce.Drivers.Android;


import BraceForce.Drivers.SensorParameter;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Ported from ODK from University of Washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public final class AndroidSensorParameter implements Parcelable, SensorParameter{
	public enum Type {
		BOOLEAN,
		BOOLEANARRAY,
		BYTE,
		BYTEARRAY,
		DOUBLE,
		DOUBLEARRAY,
		FLOAT,
		FLOATARRAY,
		INTEGER,
		INTEGERARRAY,
		INTEGERARRAYLIST,
		LONG,
		LONGARRAY,
		PARCELABLE,
		PARCELABLEARRAY,
		PARCELABLEARRAYLIST,
		SERIALIZABLE,
		STRING,
		STRINGARRAY,
		STRINGARRAYLIST,
		VOID;
	}
	
	
	public enum Purpose {
		DATA,
		CONFIG,
		TIME,
		ACTION;
	}
	
	private final String keyName;
	
	private final Type valueType;
	
	private final String valueDescription;
	
	public AndroidSensorParameter(String keyName, Type valueType, Purpose valuePurpose, String valueDescription) {
		this.keyName = keyName;
		this.valueType = valueType;
		this.valueDescription = valueDescription;
	}

	public AndroidSensorParameter(Parcel p) {
		keyName = p.readString();
		valueType = Type.valueOf(p.readString());
		valueDescription = p.readString();
	}
	
	public String getKeyName() {
		return keyName;
	}

	public Type getValueType() {
		return valueType;
	}

	public String getValueDescription() {
		return valueDescription;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(keyName);
		dest.writeString(valueType.name());
		dest.writeString(valueDescription);
	}
	
	public static final Parcelable.Creator<AndroidSensorParameter> CREATOR = new Parcelable.Creator<AndroidSensorParameter>() {
		public AndroidSensorParameter createFromParcel(Parcel in) {
			return new AndroidSensorParameter(in);
		}

		public AndroidSensorParameter[] newArray(int size) {
			return new AndroidSensorParameter[size];
		}
	};
}
