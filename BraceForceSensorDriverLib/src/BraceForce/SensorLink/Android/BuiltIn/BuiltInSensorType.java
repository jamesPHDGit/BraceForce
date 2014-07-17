package BraceForce.SensorLink.Android.BuiltIn;


import BraceForce.Drivers.Android.BuiltInDevices.AbstractBuiltinDriver;
import BraceForce.Drivers.Android.BuiltInDevices.AccelerometerDriver;
import BraceForce.Drivers.Android.BuiltInDevices.GravityDriver;
import BraceForce.Drivers.Android.BuiltInDevices.GyroscopeDriver;
import BraceForce.Drivers.Android.BuiltInDevices.LightDriver;
import BraceForce.Drivers.Android.BuiltInDevices.LinearAccelerationDriver;
import BraceForce.Drivers.Android.BuiltInDevices.MagneticFieldDriver;
import BraceForce.Drivers.Android.BuiltInDevices.OrientationDriver;
import BraceForce.Drivers.Android.BuiltInDevices.PressureDriver;
import BraceForce.Drivers.Android.BuiltInDevices.ProximityDriver;
import BraceForce.Drivers.Android.BuiltInDevices.RotationVectorDriver;
import BraceForce.Drivers.Android.BuiltInDevices.TemperatureDriver;

import android.hardware.Sensor;


/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public enum BuiltInSensorType {
	ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, AccelerometerDriver.class),
	GRAVITY(Sensor.TYPE_GRAVITY, GravityDriver.class),
	GYROSCOPE(Sensor.TYPE_GYROSCOPE, GyroscopeDriver.class),
	LIGHT(Sensor.TYPE_LIGHT, LightDriver.class),
	LINEAR_ACCELERATION(Sensor.TYPE_LINEAR_ACCELERATION, LinearAccelerationDriver.class),
	MAGNETIC_FIELD(Sensor.TYPE_MAGNETIC_FIELD, MagneticFieldDriver.class),
	ORIENTATION(Sensor.TYPE_ORIENTATION, OrientationDriver.class),
	PRESSURE(Sensor.TYPE_PRESSURE, PressureDriver.class),
	PROXIMITY(Sensor.TYPE_PROXIMITY, ProximityDriver.class),
	ROTATION_VECTOR(Sensor.TYPE_ROTATION_VECTOR, RotationVectorDriver.class),
	TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE, TemperatureDriver.class);
	
	private final int type;
	private final Class<? extends AbstractBuiltinDriver> driverClass;
	
	private BuiltInSensorType(int type, Class<? extends AbstractBuiltinDriver> driverClass) {
		this.type = type;
		this.driverClass = driverClass;
	}
	
	public final int getType() {
		return type;
	}
	
	public Class<? extends AbstractBuiltinDriver> getDriverClass() {
		return driverClass;
	}
	
	public static BuiltInSensorType convertToBuiltInSensor(int type) {
		for(BuiltInSensorType sensorType : BuiltInSensorType.values()) {
			if(sensorType.type == type) {
				return sensorType;
			}
		}
		return null;
	}
	

}

