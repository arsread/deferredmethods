package deferredmethods.proc;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import deferredmethods.Buffer;

public class ThreadPoolProc implements Processor {
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
    private final CyclicBarrier barrier;
    private final SynchPill synchPill;
    private final BlockingQueue<Runnable> workQueue;
//    private final WorkerThread[] workers;
    public volatile WorkerThread[] workers;

    public ThreadPoolProc() {
        this(DEFAULT_THREAD_POOL_SIZE, DEFAULT_QUEUE_CAPACITY);
    }

    public ThreadPoolProc(int numThreads, int queueCapacity) {
        if(DEBUG) System.out.println("[ThreadPoolProc] Pool size: " + numThreads + "; Queue capacity: " + queueCapacity);

        isRunning = new AtomicBoolean(true);

        barrier = new CyclicBarrier(numThreads + 1);

        synchPill = new SynchPill(barrier);

        workQueue = new ArrayBlockingQueue<Runnable>(queueCapacity);

        workers = new WorkerThread[numThreads];
        for(int i = 0; i < numThreads; i++) {
            workers[i] = new WorkerThread(workQueue);
            workers[i].start();
        }
    }

    @Override
    public void process(Buffer buffer) {
        if(isRunning.get()) { //some buffers might be processed after the status is set to !isRunning
            forceSubmission(workQueue, buffer);
        }
    }

    @Override
    public void start() {
        if(isRunning.compareAndSet(false, true)) {
            if(DEBUG) System.out.println("Starting...");
            barrierAwait(barrier);
            if(DEBUG) System.out.println("...started!");
        }
    }

    @Override
    public void stop() {
        if(isRunning.compareAndSet(true, false)) {
            if(DEBUG) System.out.println("Stopping...");
            for(int i = 0; i < workers.length; i++) {
                forceSubmission(workQueue, synchPill);
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

	@Override
	public void producerThreadDied(Thread t) {
		// Just useful for ShadowThread Proc
	}

}
