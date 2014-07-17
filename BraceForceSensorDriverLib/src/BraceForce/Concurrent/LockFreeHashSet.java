package BraceForce.Concurrent;
import java.util.concurrent.atomic.AtomicInteger;


public class LockFreeHashSet<T> {

	protected BucketList<T>[] bucket;
	protected AtomicInteger bucketSize;
	protected AtomicInteger setSize;
	protected final int THRESHOLD = 4; //density for each bucket is no more than 4
	
	public LockFreeHashSet(int capacity){
		bucket = (BucketList<T>[])new BucketList[capacity];
		bucket[0] = new BucketList<T>();
		bucketSize = new AtomicInteger(2);
		setSize = new AtomicInteger(0);
		
	}
	
	public boolean add(T x){
		
		//int hashKey = Math.abs(BucketList.hashCode(x));
		int myBucket = x.hashCode() % bucketSize.get();
		BucketList<T> b = getBucketList(myBucket);
		if ( !b.add(x))
			return false;
		
		int setSizeNow = setSize.getAndIncrement();
		int bucketSizeNow = bucketSize.get();
		if ( (double)setSizeNow/(double)bucketSizeNow > THRESHOLD && bucketSize.get() < (bucket.length/2) )
			bucketSize.compareAndSet(bucketSizeNow, 2 * bucketSizeNow );
		return true;
		
	}
	
	public boolean remove(T x){
		//int hashKey = Math.abs(BucketList.hashCode(x));
		int myBucket = x.hashCode() % bucketSize.get();
		BucketList<T> b = getBucketList(myBucket);
		if ( !b.remove(x))
			return false;
		
		setSize.getAndDecrement();
		return true;
	}
	
	public boolean contains(T x){
		
		//int hashKey = Math.abs(BucketList.hashCode(x));
		int myBucket = x.hashCode() % bucketSize.get();
		BucketList<T> b = getBucketList(myBucket);
		return b.contains(x);
	}

	private BucketList<T> getBucketList(int myBucket) {
		// TODO Auto-generated method stub
		if ( bucket[myBucket] == null ){
			initializeBucket(myBucket);
		}
		return bucket[myBucket];
	}

	private void initializeBucket(int myBucket) {
		// TODO Auto-generated method stub
		int parent = getParent(myBucket);
		if ( bucket[parent] == null ){
			initializeBucket ( parent );
		}
		BucketList<T> b = bucket[parent].getSentinel(myBucket);
		if ( b!= null ){
			bucket[myBucket] = b;
		}
	}

	private int getParent(int myBucket) {
		// TODO Auto-generated method stub
		int parent = bucketSize.get();
		do{
			parent = parent >> 1;
		} while ( parent > myBucket );
		
		parent = myBucket - parent;
		return parent;
		
	}
	
	
	
	
}
