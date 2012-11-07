package test.danilo.workers.tl;

import java.util.concurrent.CyclicBarrier;

import test.danilo.MyCounter;
import test.danilo.workers.AbstractWorker;

public class ThreadLocalWorker extends AbstractWorker {
    private final ThreadLocal<MyCounter> myCounter = new ThreadLocal<MyCounter>() {
        public MyCounter initialValue() {
            return new MyCounter();
        }
    };

    public ThreadLocalWorker(CyclicBarrier barrier, int size, int id) {
        super(barrier, size, id);
    }

    public void run() {
        try {
            myCounter.get();

            barrier.await();
            for(int i = 0; i < size; i++) {
                myCounter.get().randomAdd();
            }
            barrier.await();

            System.out.println(getName() + " " + myCounter.get().get());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
