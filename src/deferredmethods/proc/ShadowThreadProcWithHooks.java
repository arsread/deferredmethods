package deferredmethods.proc;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import deferredmethods.Buffer;
import deferredmethods.DeferredExecution;

public class ShadowThreadProcWithHooks implements Processor {
	private static final int DEFAULT_THREAD_POOL_SIZE = 4;
	private static final int DEFAULT_QUEUE_CAPACITY = DEFAULT_THREAD_POOL_SIZE * 10;
	private static final boolean DEBUG = Boolean.getBoolean("dm.debug");

	private static class WorkerThread extends Thread {
		private static final AtomicInteger counter = new AtomicInteger();
		private static final Method activateBypass;
		private static final Method deactivateBypass;

		private boolean isRunning = false;

		static {
			Method found1 = null, found2 = null;
			try {
				Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(
						"ch.usi.dag.disl.dynamicbypass.DynamicBypass");
				found1 = clazz.getMethod("activate", new Class[0]);
				found2 = clazz.getMethod("deactivate", new Class[0]);
			} catch (Throwable t) { /*
									 * exception handler intentionally left
									 * empty
									 */
			}
			activateBypass = found1;
			deactivateBypass = found2;
		}

		private final BlockingQueue<Runnable> queue;

		public WorkerThread(BlockingQueue<Runnable> queue) {
			super("TP_WORKER" + counter.incrementAndGet());
			this.queue = queue;
			this.isRunning = true;
		}

		public void run() {
			if (activateBypass != null) {
				try {
					activateBypass.invoke(null, new Object[0]);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}

			while (!queue.isEmpty() || isRunning) {
				try {
					Runnable task;
					task = queue.poll(1, TimeUnit.SECONDS);
					if (task!=null) task.run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// if(deactivateBypass != null) {
			// try { deactivateBypass.invoke(null, new Object[0]); } catch
			// (Throwable t) { t.printStackTrace(); }
			// }
		}

		public BlockingQueue<Runnable> getQueue() {
			return queue;
		}

		public synchronized void terminate() {
			isRunning = false;
		}
	}

	 private static class SynchPill implements Runnable {
	 private final CyclicBarrier barrier;
	
	 public SynchPill(CyclicBarrier barrier) {
	 this.barrier = barrier;
	 }
	
	 public void run() {
	 barrierAwait(barrier); // to synch with the sender
	 barrierAwait(barrier); // to actually wait
	 }
	
	 }

	private final AtomicBoolean isRunning;
	private final IdentityHashMap<Thread, WorkerThread> threadWorkerMap;
	private final int queueCapacity;
//	private final List<WorkerThread> workerList;

	public ShadowThreadProcWithHooks() {
		this(DEFAULT_THREAD_POOL_SIZE, DEFAULT_QUEUE_CAPACITY);
	}

	public ShadowThreadProcWithHooks(int numThreads, int queueCapacity) {
		if (DEBUG)
			System.out.println("[ThreadPoolProc] Pool size: " + numThreads
					+ "; Queue capacity: " + queueCapacity);

		isRunning = new AtomicBoolean(true);
		this.queueCapacity = queueCapacity;

//		workerList = new LinkedList<WorkerThread>();

		threadWorkerMap = new IdentityHashMap<Thread, WorkerThread>();
		
		BlockingQueue<Runnable> nullQueue = new ArrayBlockingQueue<Runnable>(
				queueCapacity);
		WorkerThread worker = new WorkerThread(nullQueue);
		worker.setDaemon(true);
		worker.start();
//		workerList.add(worker);
		threadWorkerMap.put(null, worker);

	}

	@Override
	public void process(Buffer buffer) {
		if (isRunning.get()) { // some buffers might be processed after the
								// status is set to !isRunning
			Thread thread = buffer.handInThread();
			BlockingQueue<Runnable> workQueue = getQueue(thread);
			forceSubmission(workQueue, buffer);
		}
	}

	@Override
	public void start() {
		if (isRunning.compareAndSet(false, true)) {
			if (DEBUG)
				System.out.println("Starting...");
			// barrierAwait(barrier);
			if (DEBUG)
				System.out.println("...started!");
		}
	}

	@Override
	public void stop() {
		if (isRunning.compareAndSet(true, false)) {
			if (DEBUG)
				System.out.println("Stopping...");
			// Iterator<Entry<Thread, WorkerThread>> itr =
			// threadWorkerMap.entrySet().iterator();
			// CyclicBarrier barrier = new CyclicBarrier(2);
			// SynchPill synchPill = new SynchPill(barrier);
			// while(itr.hasNext()) {
			// Entry<Thread, WorkerThread> entry = itr.next();
			// forceSubmission(entry.getValue().getQueue(), synchPill);
			// }
			// barrierAwait(barrier);
			WorkerThread worker = threadWorkerMap.remove(null);
			if (worker != null) {
				CyclicBarrier barrier = new CyclicBarrier(2);
				 SynchPill synchPill = new SynchPill(barrier);
//				workerList.remove(worker);// but I cannot do it immediately
				forceSubmission(worker.getQueue(), synchPill);
				barrierAwait(barrier);
			}
				if (DEBUG)
					System.out.println("...stopped!");
			}
	}

	private static void barrierAwait(CyclicBarrier barrier) {
		try {
			barrier.await();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static void forceSubmission(BlockingQueue<Runnable> queue,
			Runnable task) {
		try {
			queue.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// WARNING: this method is not synchronized
	private BlockingQueue<Runnable> registerNewThread(Thread thread) {
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(
				queueCapacity);
		try {
			WorkerThread worker = new WorkerThread(queue);
			worker.start();
//			workerList.add(worker);
			threadWorkerMap.put(thread, worker);
		} catch (NullPointerException e) {
			queue = threadWorkerMap.get(null).getQueue();
		}
		return queue;
	}

	private BlockingQueue<Runnable> getQueue(Thread thread) {
		BlockingQueue<Runnable> queue;
		synchronized (threadWorkerMap) {
			WorkerThread worker = threadWorkerMap.get(thread);
			if (worker == null) {
				queue = registerNewThread(thread);
			} else {
				queue = worker.getQueue();
			}
			return queue;
		}
	}

	@Override
	public void producerThreadDied(Thread thread) {
		synchronized (threadWorkerMap) {
			WorkerThread worker = threadWorkerMap.remove(thread);
			if (worker != null) {
//				workerList.remove(worker);// but I cannot do it immediately
				worker.terminate();
			}
		}
	}
}
