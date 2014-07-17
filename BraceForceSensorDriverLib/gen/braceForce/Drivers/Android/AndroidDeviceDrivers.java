/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/james/ResearchProjects/BraceForceSensorDriverLib/src/braceForce/Drivers/Android/AndroidDeviceDrivers.aidl
 */
package braceForce.Drivers.Android;
public interface AndroidDeviceDrivers extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements braceForce.Drivers.Android.AndroidDeviceDrivers
{
private static final java.lang.String DESCRIPTOR = "braceForce.Drivers.Android.AndroidDeviceDrivers";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an braceForce.Drivers.Android.AndroidDeviceDrivers interface,
 * generating a proxy if needed.
 */
public static braceForce.Drivers.Android.AndroidDeviceDrivers asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof braceForce.Drivers.Android.AndroidDeviceDrivers))) {
return ((braceForce.Drivers.Android.AndroidDeviceDrivers)iin);
}
return new braceForce.Drivers.Android.AndroidDeviceDrivers.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_configureCmd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
byte[] _result = this.configureCmd(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_getSensorDataCmd:
{
data.enforceInterface(DESCRIPTOR);
byte[] _result = this.getSensorDataCmd();
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_getSensorData:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.util.List<braceForce.Drivers.Android.AndroidSensorDataPacket> _arg1;
_arg1 = data.createTypedArrayList(braceForce.Drivers.Android.AndroidSensorDataPacket.CREATOR);
byte[] _arg2;
_arg2 = data.createByteArray();
braceForce.Drivers.Android.AndroidSensorDataParseResponse _result = this.getSensorData(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_encodeDataToSendToSensor:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
byte[] _result = this.encodeDataToSendToSensor(_arg0);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_startCmd:
{
data.enforceInterface(DESCRIPTOR);
byte[] _result = this.startCmd();
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_stopCmd:
{
data.enforceInterface(DESCRIPTOR);
byte[] _result = this.stopCmd();
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_getDriverParameters:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<braceForce.Drivers.Android.AndroidSensorParameter> _result = this.getDriverParameters();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements braceForce.Drivers.Android.AndroidDeviceDrivers
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/*
	 * Get the sensor-specific command buffer to configure parameters. 
	 * return 0-length buffer if sensor does not have a start command
	 * @param configData Data for configuration.
	 */
@Override public byte[] configureCmd(java.lang.String setting, android.os.Bundle configInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(setting);
if ((configInfo!=null)) {
_data.writeInt(1);
configInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_configureCmd, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	 * Some sensors (e.g. WaterUse sensor) return data only when queried
	 * This method returns the command buffer needed to retrieve data from the sensor
	 * return 0-length buffer if a query command is not needed for the sensor
	 */
@Override public byte[] getSensorDataCmd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSensorDataCmd, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public braceForce.Drivers.Android.AndroidSensorDataParseResponse getSensorData(long maxNumReadings, java.util.List<braceForce.Drivers.Android.AndroidSensorDataPacket> rawSensorData, byte[] remainingData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
braceForce.Drivers.Android.AndroidSensorDataParseResponse _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(maxNumReadings);
_data.writeTypedList(rawSensorData);
_data.writeByteArray(remainingData);
mRemote.transact(Stub.TRANSACTION_getSensorData, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = braceForce.Drivers.Android.AndroidSensorDataParseResponse.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public byte[] encodeDataToSendToSensor(android.os.Bundle dataToFormat) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dataToFormat!=null)) {
_data.writeInt(1);
dataToFormat.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_encodeDataToSendToSensor, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	 * Get the sensor-specific start command. 
	 * return 0-length byte if sensor doesnot have a start command
	 */
@Override public byte[] startCmd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startCmd, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	 * Get the sensor-specific stop command. 
	 * return 0-length byte if sensor doesnot have a start command
	 */
@Override public byte[] stopCmd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopCmd, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	 * Get the list of driver parameters 
	 */
@Override public java.util.List<braceForce.Drivers.Android.AndroidSensorParameter> getDriverParameters() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<braceForce.Drivers.Android.AndroidSensorParameter> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDriverParameters, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(braceForce.Drivers.Android.AndroidSensorParameter.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_configureCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getSensorDataCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getSensorData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_encodeDataToSendToSensor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_stopCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getDriverParameters = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
/*
	 * Get the sensor-specific command buffer to configure parameters. 
	 * return 0-length buffer if sensor does not have a start command
	 * @param configData Data for configuration.
	 */
public byte[] configureCmd(java.lang.String setting, android.os.Bundle configInfo) throws android.os.RemoteException;
/*
	 * Some sensors (e.g. WaterUse sensor) return data only when queried
	 * This method returns the command buffer needed to retrieve data from the sensor
	 * return 0-length buffer if a query command is not needed for the sensor
	 */
public byte[] getSensorDataCmd() throws android.os.RemoteException;
public braceForce.Drivers.Android.AndroidSensorDataParseResponse getSensorData(long maxNumReadings, java.util.List<braceForce.Drivers.Android.AndroidSensorDataPacket> rawSensorData, byte[] remainingData) throws android.os.RemoteException;
public byte[] encodeDataToSendToSensor(android.os.Bundle dataToFormat) throws android.os.RemoteException;
/*
	 * Get the sensor-specific start command. 
	 * return 0-length byte if sensor doesnot have a start command
	 */
public byte[] startCmd() throws android.os.RemoteException;
/*
	 * Get the sensor-specific stop command. 
	 * return 0-length byte if sensor doesnot have a start command
	 */
public byte[] stopCmd() throws android.os.RemoteException;
/*
	 * Get the list of driver parameters 
	 */
public java.util.List<braceForce.Drivers.Android.AndroidSensorParameter> getDriverParameters() throws android.os.RemoteException;
}
