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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import deferredmethods.Buffer;

public class ShadowThreadProc implements Processor {
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static final int DEFAULT_QUEUE_CAPACITY = DEFAULT_THREAD_POOL_SIZE * 10;
    private static final boolean DEBUG = Boolean.getBoolean("dm.debug");
    
    private static class WorkerThread extends Thread {
        private static final AtomicInteger counter = new AtomicInteger();
        private static final Method bypass; 

        static {
            Method found = null;
            try {
                Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("ch.usi.dag.disl.dynamicbypass.DynamicBypass");
                found = clazz.getMethod("activate", new Class[0]);
            } catch(Throwable t) { /* exception handler intentionally left empty */ }
            bypass = found;
        }

        private final BlockingQueue<Runnable> queue;

        public WorkerThread(BlockingQueue<Runnable> queue) {
            super("TP_WORKER" + counter.incrementAndGet());
            setDaemon(true);
            this.queue = queue;
        }

        public void run() {
            if(bypass != null) {
                try { bypass.invoke(null, new Object[0]); } catch (Throwable t) { t.printStackTrace(); }
            }

            while(true) {
                try {
                    Runnable task = queue.take();
                    task.run();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
//    private CyclicBarrier barrier;
//    private SynchPill synchPill;
	private final IdentityHashMap<Thread, BlockingQueue<Runnable>> threadQueueMap;
    private final int queueCapacity;
    private final List<WorkerThread> workerList;
//    private final BlockingQueue<Runnable> workQueue;
//    private final WorkerThread[] workers;
//    public static volatile ArrayList<WorkerThread> workers;

    public ShadowThreadProc() {
        this(DEFAULT_THREAD_POOL_SIZE, DEFAULT_QUEUE_CAPACITY);
    }

    public ShadowThreadProc(int numThreads, int queueCapacity) {
        if(DEBUG) System.out.println("[ThreadPoolProc] Pool size: " + numThreads + "; Queue capacity: " + queueCapacity);

        isRunning = new AtomicBoolean(true);

//        barrier = new CyclicBarrier(1);
//
//        synchPill = new SynchPill(barrier);
        
        this.queueCapacity = queueCapacity;
        
        workerList = new LinkedList<WorkerThread>();

//        workQueue = new ArrayBlockingQueue<Runnable>(queueCapacity);

        threadQueueMap = new IdentityHashMap<Thread, BlockingQueue<Runnable>>();
        registerNewThread(null);

//        workers = new ArrayList<WorkerThread>();
//        for(int i = 0; i < numThreads; i++) {
//            workers[i] = new WorkerThread(workQueue);
//            workers[i].start();
//        }
    }

    @Override
    public void process(Buffer buffer) {
        if(isRunning.get()) { //some buffers might be processed after the status is set to !isRunning
        	Thread thread = buffer.handInThread();
        	BlockingQueue<Runnable> workQueue = getQueue(thread);
            forceSubmission(workQueue, buffer);
        }
    }

	@Override
    public void start() {
        if(isRunning.compareAndSet(false, true)) {
            if(DEBUG) System.out.println("Starting...");
//            barrierAwait(barrier);
            if(DEBUG) System.out.println("...started!");
        }
    }

    @Override
    public void stop() {
        if(isRunning.compareAndSet(true, false)) {
            if(DEBUG) System.out.println("Stopping...");
            Iterator<Entry<Thread, BlockingQueue<Runnable>>> itr = threadQueueMap.entrySet().iterator();
            CyclicBarrier barrier = new CyclicBarrier(workerList.size()+1);
            SynchPill synchPill = new SynchPill(barrier);
            while(itr.hasNext()) {
            	Entry<Thread, BlockingQueue<Runnable>> entry = itr.next();
                forceSubmission(entry.getValue(), synchPill);
            }
            barrierAwait(barrier);
            if(DEBUG) System.out.println("...stopped!");
        }
    }

    private static void barrierAwait(CyclicBarrier barrier) {
        try { barrier.await(); } catch (Throwable t) { t.printStackTrace(); }
    }

    private static void forceSubmission(BlockingQueue<Runnable> queue, Runnable task) {
        try { queue.put(task); } catch(InterruptedException e) { e.printStackTrace(); }
    }
    

    //WARNING: this method is not synchronized
    private BlockingQueue<Runnable> registerNewThread(Thread thread) {
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(queueCapacity);
		try {
			WorkerThread worker = new WorkerThread(queue);
    		worker.start();
    		workerList.add(worker);
    		threadQueueMap.put(thread, queue);
		} catch(NullPointerException e) {
			queue = threadQueueMap.get(null);
		}
		return queue;
    }

    private BlockingQueue<Runnable> getQueue(Thread thread) {
    	BlockingQueue<Runnable> queue;
    	synchronized (threadQueueMap) {
    		if ((queue = threadQueueMap.get(thread)) == null) {
    			queue = registerNewThread(thread);
    		}
    		return queue;
    	}
	}

	@Override
	public void producerThreadDied(Thread t) {
		// TODO Auto-generated method stub
		
	}
}
