package BraceForce.Concurrent;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class BucketList<T> implements Set<T> {
	 static final int WORD_SIZE = 24;
	 static final int LO_MASK = 0x00000001;
	 static final int HI_MASK = 0x00800000;
	 static final int MASK = 0x00FFFFFF;
	 private Node<T> head;
	
	 public BucketList(Node<T> header){
		 head = header;
		// head.next = new AtomicMarkableReference(new Node<T>(Integer.MAX_VALUE),false);
	 }
	 
	 public BucketList(){
		 head = new Node<T>(0);
		 head.next = new AtomicMarkableReference(new Node<T>(Integer.MAX_VALUE),false);
	 }
	 
	 private static int reverseBetterThanJavaInteger(int key){
	    int loMask = LO_MASK;
        int hiMask = HI_MASK;
        int result = 0;
        for(int i = 0; i < WORD_SIZE; i++) {
            if((key & loMask) != 0) { // bit set
                result |= hiMask;
            }
            loMask <<= 1;
            hiMask >>>= 1; // fill with 0 from left
        }
        return result;
	 }
	 
	 public int makeOrderinaryKey(T x){
		 int code = x.hashCode() & MASK;  //take 3 lowest bytes
		 return reverseBetterThanJavaInteger(code | HI_MASK);
	 }
	 
	 private static int makeSentinelKey(int key) {
		 return reverseBetterThanJavaInteger( key & MASK );
	 }
	 
	 public static int hashCode(Object x){
		 return x.hashCode() & MASK;
	 }
	 
	 private class Node<T> {
	   	 T item;
	   	 int key;
	   	 AtomicMarkableReference<Node<T>> next;
	   	 
	   	 public Node(){
	   		
	   	 }
	   	 
	   	 public Node(int defaultKey){
	   		 key = defaultKey;
	   		 next = new AtomicMarkableReference<Node<T>>(null, false);
	   		
	   	 }
	   	 
	   	 public Node(int key, T item){
	   		 this.item = item;
	   		 this.key = key;
	   		 next = new AtomicMarkableReference<Node<T>>(null, false);
	   	 }
  }

   public Window find(Node<T> head, int key){
   	Node<T> pred = null, curr = null, succ = null;
   	boolean[] marked = {false};
   	boolean snip;
   	retry:
   		while (true) {
	    		//Restart loop
	    		pred = head;
	    		curr = pred.next.getReference();
	    		while (true) {
	    			//traverse the linked list
	    			if (  curr.next == null )
	    			{
	    				//end of the list
	    				return new Window(pred,curr);
	    			}
	    			succ = curr.next.get(marked);
	    			
	    			while (marked[0]){
	    				snip = pred.next.compareAndSet(curr, succ, false, false);
	    				if (!snip) continue retry;
	    				curr = succ;
	    				if ( curr.next == null )
	    				{
	    					break;
	    				}
	    				succ = curr.next.get(marked);
	    			}
	    			if ( curr.key >= key )
	    			   return new Window(pred, curr);
	    			pred = curr;
	    			curr = succ;
	    		}
	    	}
   }
	 
	@Override
	public boolean add(T item) {
		int key =  makeOrderinaryKey(item);
		while ( true ){
			Window window = find( head, key );
			Node<T> pred = window.pred, curr = window.curr;
			if ( curr.key == key ){
				return false;
			}
			else {
				Node<T> node = new Node<T>(key,item);
				node.next = new AtomicMarkableReference(curr, false);
				if ( pred.next.compareAndSet(curr, node, false, false)) {
					return true;
					
				}
			}
		}
	}

	@Override
	public boolean remove(T item) {
		int key = makeOrderinaryKey(item);
		boolean snip;
		while ( true ){
			Window window = find(head,key);
			Node<T> pred = window.pred, curr = window.curr;
			if ( curr.key != key ){
				return false;
			} else{
				Node<T> succ = curr.next.getReference();
				snip = curr.next.attemptMark(succ, true);
				if ( !snip ){
					continue;
				}
				//try to delete, failure does not care. The other node delete and add can handle it
				pred.next.compareAndSet(curr, succ, false, false); 
				return true;
			}
		}
	}

	@Override
	public boolean contains(T item) {
		// TODO Auto-generated method stub
		int key = makeOrderinaryKey(item);
		Window window = find(head, key);
		Node pred = window.pred;
		Node curr = window.curr;
		return (curr.key == key);
	}

	public int count(){
		boolean[] marked = {false};
		int nodeCount = 0;
		Node<T> curr = head;
		while ( curr!=null )
		{
			if ( curr.next != null )
			{
				curr = curr.next.get(marked);
				if ( !marked[0])
					nodeCount++;
			}
			else 
				curr = null;
		}
		//remove last sentinel node
		return nodeCount-1;
	}
	
	public BucketList<T> getSentinel(int index){
		int key = makeSentinelKey(index);
		boolean splice;
		while ( true ){
			Window window = find(head, key);
			Node pred = window.pred;
			Node curr = window.curr;
			if ( curr.key == key ){
				return new BucketList<T>();
			}
			else{
				Node node = new Node(key);
				node.next.set(pred.next.getReference(), false);
				splice = pred.next.compareAndSet(curr, node, false, false);
				if ( splice ){
					return new BucketList<T>(node);
				}
				else {
					continue;
				}
			}
		}
	}
	
	
	 //inner class Window
	class Window {
		public Node<T> pred, curr;
		Window(Node<T> myPred, Node<T> myCurr){
			pred = myPred; curr = myCurr;
		}
	}
	

	
}
