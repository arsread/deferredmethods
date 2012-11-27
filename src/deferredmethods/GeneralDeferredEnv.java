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

	private final IdentityHashMap<Thread, ThreadCheckPointItems> threadItemsMap;

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
		
		this.threadItemsMap = new IdentityHashMap<Thread, ThreadCheckPointItems>();
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
		ThreadCheckPointItems threadItems = getThreadItems(thread);
		PriorityQueue<Integer> bufferHeap = threadItems.getBufferHeap();
		LinkedList<GeneralProcessingCheckPoint> cpList = threadItems.getCPList();
		int envCouter = threadItems.getEnvCouter(); 

		if (bufferID == envCouter + 1) {
			envCouter++;
			while (!bufferHeap.isEmpty()
					&& bufferHeap.peek() == envCouter + 1) {
				envCouter++;
				bufferHeap.remove();
			}
			threadItems.setEnvCouter(envCouter);
			updateCheckPoints(cpList, envCouter);
		} else {
			bufferHeap.add(bufferID);
		}
	}
	
	private void updateCheckPoints(LinkedList<GeneralProcessingCheckPoint> cpList, int envCouter) {
		synchronized (cpList) {
			while (!cpList.isEmpty()
					&& cpList.peek().getBufferID() <= envCouter) {
				cpList.peek().setProcessed();
				cpList.remove();
			}
		}
	}
	
	private ThreadCheckPointItems getThreadItems(Thread thread){
		synchronized (threadItemsMap){
			if (!threadItemsMap.containsKey(thread)) {
				threadItemsMap.put(thread, new ThreadCheckPointItems());
			}
			return threadItemsMap.get(thread);
		}
	}

	protected void addCPList(Thread thread, GeneralProcessingCheckPoint cp) {
		LinkedList<GeneralProcessingCheckPoint> cpList=getThreadItems(thread).getCPList();
		synchronized (cpList) {
			cpList.add(cp);
		}
	}
}
