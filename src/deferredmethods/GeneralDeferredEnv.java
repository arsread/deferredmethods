package deferredmethods;

import java.util.IdentityHashMap;
import java.util.LinkedList;

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
	public void comfirmBuffer(final int bufferID, final Thread thread) {
		ThreadCheckPointItems threadItems = getThreadItems(thread);
		// PriorityQueue<Integer> bufferHeap = threadItems.getBufferHeap();
		// LinkedList<GeneralProcessingCheckPoint> cpList = threadItems
		// .getCPList();
		//
		// synchronized (threadItems) {
		// int envCouter = threadItems.getEnvCouter();
		//
		// if (bufferID == envCouter + 1) {
		// envCouter++;
		// while (!bufferHeap.isEmpty()
		// && bufferHeap.peek() == envCouter + 1) {
		// envCouter++;
		// bufferHeap.remove();
		// }
		// threadItems.setEnvCouter(envCouter);
		// updateCheckPoints(cpList, envCouter);
		// } else {
		// bufferHeap.add(bufferID);
		// }
		// }

		threadItems.comfirmBuffer(bufferID);
		checkMapItems(thread);
	}

	public void checkMapItems(final Thread thread) {
		ThreadCheckPointItems threadItems = returnIfExist(thread);
		if (threadItems != null && threadItems.canBeRemoved()) {
			synchronized (threadItemsMap) {
				threadItemsMap.remove(thread);
			}
		}
	}

	public void setEnd(Thread thread) {
		ThreadCheckPointItems threadItems = returnIfExist(thread);
		if (threadItems != null) {
			threadItems.setEnd();
			checkMapItems(thread);
		}
	}

	private ThreadCheckPointItems getThreadItems(Thread thread) {
		synchronized (threadItemsMap) {
			if (!threadItemsMap.containsKey(thread)) {
				threadItemsMap.put(thread, new ThreadCheckPointItems());
			}
			return threadItemsMap.get(thread);
		}
	}

	private ThreadCheckPointItems returnIfExist(Thread thread) {
		synchronized (threadItemsMap) {
			if (threadItemsMap.containsKey(thread)) {
				return threadItemsMap.get(thread);
			}
		}
		return null;
	}

	// used by the generated Env
	protected void addCPList(Thread thread, GeneralProcessingCheckPoint cp) {
		LinkedList<GeneralProcessingCheckPoint> cpList = getThreadItems(thread)
				.getCPList();
		synchronized (cpList) {
			cpList.add(cp);
		}
	}
	
	// used by the generated Env, to ensure the Max Buffer
	protected void handInBuffer(Buffer buffer){
			ThreadCheckPointItems threadItems = getThreadItems(buffer.handInThread());
			threadItems.handInBuffer(buffer.getBufferId());
	}
}
