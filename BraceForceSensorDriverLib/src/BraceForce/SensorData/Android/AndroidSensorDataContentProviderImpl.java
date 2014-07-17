package BraceForce.SensorData.Android;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Bundle;

public class AndroidSensorDataContentProviderImpl implements
		AndroidSensorDataContentProvider {

	
	private ConcurrentLinkedQueue<Bundle> sensorDataDepo = new ConcurrentLinkedQueue<Bundle>();
	private int queueLimit = 100;
	private AtomicInteger queue_count = new AtomicInteger();
	
	@Override
	public List<Bundle> getSensorDataReading(int numberOfReading) {
		// TODO Auto-generated method stub
		List<Bundle> returnResults = new ArrayList<Bundle>();
		try 
		{
			for ( int intCounter = 0; intCounter < numberOfReading; intCounter++) {
				returnResults.add( sensorDataDepo.poll() );
			}
			return returnResults;
		}
		catch ( Exception ex ){
			return returnResults;
		}
		
	}

	@Override
	public void addSensorData(Bundle sensordata) {
		// TODO Auto-generated method stub
		if ( queue_count.getAndIncrement() >= queueLimit ) {
			sensorDataDepo.clear();
			queue_count.set(1);
		}
		
		sensorDataDepo.add(sensordata);
		
	}

	
	@Override
	public List<Hashtable> getSensorData(int numberOfReading) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSensorData(Hashtable sensordata) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSensorData(List<Hashtable> sensordataList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSensorDataList(List<Bundle> sensorDataList) {
		// TODO Auto-generated method stub
		if ( queue_count.addAndGet(sensorDataList.size()) >= queueLimit ) {
			sensorDataDepo.clear();
			queue_count.set(0);
		}
		for (Bundle bd : sensorDataList){
			queue_count.incrementAndGet();
			sensorDataDepo.add(bd);
		}
		
	}

	@Override
	public List<Bundle> getSensorDataReading() {
		// TODO Auto-generated method stub
		List<Bundle> returnResults = new ArrayList<Bundle>();
		try 
		{
			int queueSize = sensorDataDepo.size();
			for ( int intCounter = 0; intCounter < queueSize; intCounter++) {
				returnResults.add( sensorDataDepo.poll() );
			}
			return returnResults;
		}
		catch ( Exception ex ){
			return returnResults;
		}
				
	}



}
