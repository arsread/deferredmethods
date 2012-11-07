package test.danilo;

import java.util.concurrent.CyclicBarrier;

import test.danilo.workers.test1.Test1Worker;

public class Harness {
    private final int size;
    private final int threads;
    private final int maxIter = 5;

    private final CyclicBarrier barrier;

    public Harness(int size, int threads) {
        this.size = size;
        this.threads = threads;

        barrier = new CyclicBarrier(threads + 1, new Runnable() {
            int iter;
            long time;

            public void run() {
                if(time == 0) {
                    time = System.currentTimeMillis();
                } else {
                    if(++iter > maxIter) {
                        iter = 1;
                    }

                    System.err.println(iter + ") " + (System.currentTimeMillis() - time));
                    time = 0;
                }
            }
        });
    }

    public void test() {
        try {
            for(int i = 0; i < maxIter; i++) {
                for(int j = 0; j < threads; j++) {
//                    new ThreadLocalWorker(barrier, size, j).start();
//                    new FieldWorker(barrier, size, j).start();
//                    new ArrayWorker(barrier, size, j).start();
                    new Test1Worker(barrier, size, j).start();
                }

                barrier.await();
                barrier.await();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int size = 0;
        int threads = 0;

        try {
            size = Integer.parseInt(args[0]);
            threads = Integer.parseInt(args[1]);
        } catch(Exception e) {
            System.err.println("Usage:");
            System.err.println("java Harness <size> <threads>");
            System.exit(-1);
        }

        new Harness(size, threads).test();
    }
}
