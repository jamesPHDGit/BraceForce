package BraceForce.SensorData;

import BraceForce.Drivers.SensorDataPacket;
import android.content.Context;

public class SensorDataChangeEvent {
	
	private String sensorID;
	
	public String getSensorID(){
		return sensorID;
	}
	
	SensorDataPacket newDataPacket;
	
	public SensorDataPacket getSensorDataPacket(){
		return newDataPacket;
	}
	
    public SensorDataChangeEvent( String sensorID, SensorDataPacket newDataPacket){
		this.sensorID = sensorID;
		this.newDataPacket = newDataPacket;
	}
    
	
    public SensorDataChangeEvent( String sensorID, SensorDataPacket newDataPacket, Context context){
		this.sensorID = sensorID;
		this.newDataPacket = newDataPacket;
		setBraceForceContext(context);
	}
    
    public Context getBraceForceContext() {
		return braceForceContext;
	}

	public void setBraceForceContext(Context braceForceContext) {
		this.braceForceContext = braceForceContext;
	}

	//attach Context for BraceForce library for Android
    private Context braceForceContext;
    
    

}
