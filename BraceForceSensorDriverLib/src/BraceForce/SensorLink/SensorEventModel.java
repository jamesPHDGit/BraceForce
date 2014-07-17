package BraceForce.SensorLink;

import java.util.ArrayList;
import java.util.List;

import BraceForce.Drivers.SensorDataPacket;
import BraceForce.SensorData.SensorDataChangeEvent;
import BraceForce.SensorData.SensorDataChangeListener;
import android.content.Context;
import android.util.Log;

public class SensorEventModel {

	protected List<SensorDataChangeListener> listeners = new ArrayList<SensorDataChangeListener>();
	
	public void addRealtimeChangeListener( SensorDataChangeListener newListener ){
       //only allow one listener per sensor
		//later can introduce ID to uniquely identify each listern
		//if ( listeners.size() == 0 )
		listeners.add( newListener );
	}
	
	protected void notifyListeners(String sensorID, SensorDataPacket value, Context context){
		Log.d("Sensor node", "notify event listener");
		//try{
		try{
		for ( SensorDataChangeListener sdcl : listeners ) {
			sdcl.onDataChanged(  new SensorDataChangeEvent(sensorID, value, context) );
		}
		}
		catch(Exception ex){
			
		}
	}
	
	
}
