package playground;

import java.util.concurrent.CyclicBarrier;

public class TestAgent2 {

    public static void onThreadDeath() {
        System.out.println("die");
        System.out.println("Thread [" + Thread.currentThread().getName() + "] dying...");
//        System.out.println(numTL.get());
    }

//    private static final ThreadLocal<Integer> numTL = new ThreadLocal<Integer>() {
//        @Override
//        protected Integer initialValue() {
//            return new Integer(10);
//        }
//    };

    public static void main(String[] args) {
//        Class<?> agent = deferredmethods.Agent.class;
//        System.out.println("ok");
        
//        class MyThread extends Thread {
//            @Override
//            public void run() {
//                numTL.get();
//                numTL.set(1);
//                System.out.println(getName() + ": " + numTL.get());
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    // TODO Auto-generated catch block
////                    e.printStackTrace();
////                }
//            }
//        };
//        
//        System.out.println("start");
//        
//        int numthreads = 4;
//        Thread[] threads = new Thread[numthreads];
//        for (int i = 0; i < numthreads; i++) {
//            threads[i] = new MyThread();
//            threads[i].setName("mythread-" + i);
//            threads[i].start();
//        }

        int numthreads = 4;
//        final CyclicBarrier barrier = new CyclicBarrier(numthreads + 1);

        for (int i = 0; i < numthreads; i++) {
            new Thread("Thread" + i) {
                public void run() {
                    System.out.println(getName());
//                    try { barrier.await(); } catch(Exception e) { e.printStackTrace(); }; 
                }
            }.start();
        }

        //try { barrier.await(); } catch(Exception e) { e.printStackTrace(); };
    }
}
