package deferredmethods;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import deferredmethods.proc.Processor;


/**
 * Abstract implementation of {@link DeferredEnv}
 * 
 * @author igormoreno
 * @see DeferredEnv
 */
public abstract class GeneralDeferredEnv<T extends Deferred> implements DeferredEnv<T> {
	private final int DEFAULT_BUFFERHEAP_NUM = 100;
	
    private final int id;

    private volatile PriorityQueue<Integer> bufferHeap;
    protected ConcurrentLinkedQueue<GeneralProcessingCheckPoint> cpList;
    private volatile int bufferCapacity;
    private volatile int envCouter;
    protected final Processor proc;

    /**
     * Minimum requirements for a deferred environment.
     * 
     * @param proc
     *            Processor used to execute the deferred methods.
     * @param bufferCapacity
     *            Initial buffer capacity.
     * @param id
     *            Unique identifier for each deferred environment
     */
    public GeneralDeferredEnv(Processor proc, int bufferCapacity, int id) {
        this.id = id;
        this.proc = proc;
        this.bufferCapacity = bufferCapacity;
        this.bufferHeap = new PriorityQueue<Integer>();
        this.cpList = new ConcurrentLinkedQueue<GeneralProcessingCheckPoint>();
        this.envCouter = 0;
    }

    @Override
    public void setBufferCapacity(int size) {
        bufferCapacity = size;
    }

    @Override
    public int getBufferCapacity() {
        return bufferCapacity;
    }

    public int getId() {
        return id;
    }
    
    @Override
    public synchronized void comfirmBuffer(final int bufferID){
    	if (bufferID == envCouter +1 ) {
    		envCouter++;
    		while (!bufferHeap.isEmpty() && bufferHeap.peek()  == envCouter+1){
    			envCouter++;
    			bufferHeap.remove();
    		}
    		updateCheckPoints();
    	} else {
    		bufferHeap.add(bufferID);
    	}
    }
    
    private void updateCheckPoints(){
    	while ( !cpList.isEmpty() && cpList.peek().getBufferID() <= envCouter ){
    		cpList.peek().setProcessed();
    		cpList.remove();
    	}
    }

}
