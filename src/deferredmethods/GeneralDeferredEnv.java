package deferredmethods;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import deferredmethods.proc.Processor;

/**
 * Abstract implementation of {@link DeferredEnv}
 * 
 * @author igormoreno
 * @see DeferredEnv
 */
public abstract class GeneralDeferredEnv<T extends Deferred> implements
		DeferredEnv<T> {

	private final int id;
	private volatile int bufferCapacity;
	protected final Processor proc;

	private HashMap<Long, PriorityQueue<Integer>> bufferHeap;
	private HashMap<Long, LinkedList<GeneralProcessingCheckPoint>> cpList;
	private HashMap<Long, Integer> envCouter; 

	private volatile PriorityQueue<Integer> bufferHeapbk;
	protected LinkedList<GeneralProcessingCheckPoint> cpListbk;
	private volatile int envCouterbk;

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
		
		this.bufferHeap = new HashMap<Long, PriorityQueue<Integer>>();
		this.cpList = new HashMap<Long, LinkedList<GeneralProcessingCheckPoint>>();
		this.envCouter = new HashMap<Long, Integer>();
		
		this.bufferHeapbk = new PriorityQueue<Integer>();
		this.cpListbk = new LinkedList<GeneralProcessingCheckPoint>();
		this.envCouterbk = 0;
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
	public synchronized void comfirmBuffer(final int bufferID,
			final long threadID) {
		if (bufferID == envCouterbk + 1) {
			envCouterbk++;
			while (!bufferHeapbk.isEmpty()
					&& bufferHeapbk.peek() == envCouterbk + 1) {
				envCouterbk++;
				bufferHeapbk.remove();
			}
			updateCheckPoints();
		} else {
			bufferHeapbk.add(bufferID);
		}
	}

	private void updateCheckPoints() {
		while (!cpListbk.isEmpty()
				&& cpListbk.peek().getBufferID() <= envCouterbk) {
			cpListbk.peek().setProcessed();
			cpListbk.remove();
		}
	}
	
	private PriorityQueue<Integer> getBufferHeap(long threadID){
		if (!bufferHeap.containsKey(threadID)){
			bufferHeap.put(threadID, new PriorityQueue<Integer>());
		}
		return bufferHeap.get(threadID);
	}
	
	private LinkedList<GeneralProcessingCheckPoint> getCPList(long threadID){
		if (!cpList.containsKey(threadID)){
			cpList.put(threadID, new LinkedList<GeneralProcessingCheckPoint>());
		}
		return cpList.get(threadID);
	}
	
	private int getEnvCouter(long threadID){
		if (!envCouter.containsKey(threadID)){
			envCouter.put(threadID, 0);
		}
		return envCouter.get(threadID);
	}
	
	private void increseEnvCouter(long threadID){
		int couter = envCouter.get(threadID);
		envCouter.put(threadID, couter+1);
	}
}
