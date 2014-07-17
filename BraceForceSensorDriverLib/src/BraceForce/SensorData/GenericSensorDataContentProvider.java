package BraceForce.SensorData;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericSensorDataContentProvider implements SensorDataContentProvider {

	private ConcurrentLinkedQueue<Hashtable> sensorDataDepo = new ConcurrentLinkedQueue<Hashtable>();
	private int queueLimit = 100;
	private AtomicInteger queue_count = new AtomicInteger();
	
	
	@Override
	public List<Hashtable> getSensorData(int numberOfReading) {
		// TODO Auto-generated method stub
		List<Hashtable> returnResults = new ArrayList<Hashtable>();
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
	public void addSensorData(Hashtable sensordata) {
		// TODO Auto-generated method stub
		if ( queue_count.incrementAndGet() >= queueLimit ) {
			sensorDataDepo.clear();
			queue_count.set(1);
		}
		
		sensorDataDepo.add(sensordata);
		
	}

	@Override
	public void addSensorData(List<Hashtable> sensordataList) {
		// TODO Auto-generated method stub
		if ( queue_count.addAndGet(sensordataList.size()) >= queueLimit ) {
			sensorDataDepo.clear();
			queue_count.set(0);
		}
		for (Hashtable ht : sensordataList){
			queue_count.incrementAndGet();
			sensorDataDepo.add(ht);
		}
	}

}
