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

	private final HashMap<Long, PriorityQueue<Integer>> bufferHeap;
	private final HashMap<Long, LinkedList<GeneralProcessingCheckPoint>> cpList;
	private final HashMap<Long, Integer> envCouter;

	// private volatile PriorityQueue<Integer> bufferHeapbk;
	// protected LinkedList<GeneralProcessingCheckPoint> cpListbk;
	// private volatile int envCouterbk;

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

		// this.bufferHeapbk = new PriorityQueue<Integer>();
		// this.cpListbk = new LinkedList<GeneralProcessingCheckPoint>();
		// this.envCouterbk = 0;
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
		if (bufferID == getEnvCouter(threadID) + 1) {
			increseEnvCouter(threadID);
			while (!getBufferHeap(threadID).isEmpty()
					&& getBufferHeap(threadID).peek() == getEnvCouter(threadID) + 1) {
				increseEnvCouter(threadID);
				getBufferHeap(threadID).remove();
			}
			updateCheckPoints(threadID);
		} else {
			getBufferHeap(threadID).add(bufferID);
		}
	}

	private void updateCheckPoints(long threadID) {
		synchronized (cpList) {
			LinkedList<GeneralProcessingCheckPoint> tmpCPList = getCPList(threadID);
			while (!tmpCPList.isEmpty()
					&& tmpCPList.peek().getBufferID() <= getEnvCouter(threadID)) {
				tmpCPList.peek().setProcessed();
				tmpCPList.remove();
			}
		}
	}

	private PriorityQueue<Integer> getBufferHeap(long threadID) {
		if (!bufferHeap.containsKey(threadID)) {
			bufferHeap.put(threadID, new PriorityQueue<Integer>());
		}
		return bufferHeap.get(threadID);
	}

	private LinkedList<GeneralProcessingCheckPoint> getCPList(long threadID) {
		if (!cpList.containsKey(threadID)) {
			cpList.put(threadID, new LinkedList<GeneralProcessingCheckPoint>());
		}
		return cpList.get(threadID);
	}

	protected void addCPList(long threadID, GeneralProcessingCheckPoint cp) {
		synchronized (cpList) {
			getCPList(threadID).add(cp);
		}
	}

	private int getEnvCouter(long threadID) {
		if (!envCouter.containsKey(threadID)) {
			envCouter.put(threadID, 0);
		}
		return envCouter.get(threadID);
	}

	private void increseEnvCouter(long threadID) {
		int couter = envCouter.get(threadID);
		envCouter.put(threadID, couter + 1);
	}
}
