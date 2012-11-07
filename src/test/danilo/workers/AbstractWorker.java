package test.danilo.workers;

import java.util.concurrent.CyclicBarrier;

public abstract class AbstractWorker extends Thread {
    protected final CyclicBarrier barrier;
    protected final int size;

    public AbstractWorker(CyclicBarrier barrier, int size, int id) {
        super("Worker" + id);
        this.barrier = barrier;
        this.size = size;
    }

    public abstract void run();
}
