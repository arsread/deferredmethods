package test.danilo.workers.tl;

import java.util.concurrent.CyclicBarrier;

import test.danilo.ArrayAccessor;
import test.danilo.workers.AbstractWorker;

public class ArrayWorker extends AbstractWorker {
    public ArrayWorker(CyclicBarrier barrier, int size, int id) {
        super(barrier, size, id);
    }

    public void run() { 
        try {
//            int arrays = 10;
//            myCounters = new MyCounter[arrays];
//            for(int i = 0; i < arrays; i++) {
//                myCounters[i] = new MyCounter();
//            }

            ArrayAccessor aa = new ArrayAccessor();

            barrier.await();
            for(int i = 0; i < size; i++) {
                aa.getMyCounter(5).randomAdd();
            }
            barrier.await();
    
            System.out.println(getName() + " " + aa.getMyCounter(5).get());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
