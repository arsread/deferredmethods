package deferredmethods;

import java.util.HashMap;
import java.util.IdentityHashMap;
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

	private final IdentityHashMap<Thread, PriorityQueue<Integer>> bufferHeap;
	private final IdentityHashMap<Thread, LinkedList<GeneralProcessingCheckPoint>> cpList;
	private final IdentityHashMap<Thread, Integer> envCouter;

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

		this.bufferHeap = new IdentityHashMap<Thread, PriorityQueue<Integer>>();
		this.cpList = new IdentityHashMap<Thread, LinkedList<GeneralProcessingCheckPoint>>();
		this.envCouter = new IdentityHashMap<Thread, Integer>();

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
			final Thread thread) {
		if (bufferID == getEnvCouter(thread) + 1) {
			increseEnvCouter(thread);
			while (!getBufferHeap(thread).isEmpty()
					&& getBufferHeap(thread).peek() == getEnvCouter(thread) + 1) {
				increseEnvCouter(thread);
				getBufferHeap(thread).remove();
			}
			updateCheckPoints(thread);
		} else {
			getBufferHeap(thread).add(bufferID);
		}
	}

	private void updateCheckPoints(Thread thread) {
		synchronized (cpList) {
			LinkedList<GeneralProcessingCheckPoint> tmpCPList = getCPList(thread);
			while (!tmpCPList.isEmpty()
					&& tmpCPList.peek().getBufferID() <= getEnvCouter(thread)) {
				tmpCPList.peek().setProcessed();
				tmpCPList.remove();
			}
		}
	}

	private PriorityQueue<Integer> getBufferHeap(Thread thread) {
		if (!bufferHeap.containsKey(thread)) {
			bufferHeap.put(thread, new PriorityQueue<Integer>());
		}
		return bufferHeap.get(thread);
	}

	private LinkedList<GeneralProcessingCheckPoint> getCPList(Thread thread) {
		if (!cpList.containsKey(thread)) {
			cpList.put(thread, new LinkedList<GeneralProcessingCheckPoint>());
		}
		return cpList.get(thread);
	}

	protected void addCPList(Thread thread, GeneralProcessingCheckPoint cp) {
		synchronized (cpList) {
			getCPList(thread).add(cp);
		}
	}

	private int getEnvCouter(Thread thread) {
		if (!envCouter.containsKey(thread)) {
			envCouter.put(thread, 0);
		}
		return envCouter.get(thread);
	}

	private void increseEnvCouter(Thread thread) {
		int couter = envCouter.get(thread);
		envCouter.put(thread, couter + 1);
	}
}
