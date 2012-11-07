package test.danilo.workers.tl;

import java.util.concurrent.CyclicBarrier;

import test.danilo.workers.AbstractWorker;

public class FieldWorker extends AbstractWorker {
    public FieldWorker(CyclicBarrier barrier, int size, int id) {
        super(barrier, size, id);
    }

    public void run() { 
//        try {
//            myCounter = new MyCounter();
//    
//            barrier.await();
//            for(int i = 0; i < size; i++) {
//                Thread.currentThread().myCounter.randomAdd();
//            }
//            barrier.await();
//    
//            System.out.println(getName() + " " + myCounter.get());
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }
}
