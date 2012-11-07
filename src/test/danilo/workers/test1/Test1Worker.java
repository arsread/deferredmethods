package test.danilo.workers.test1;

import java.util.concurrent.CyclicBarrier;

import test.danilo.workers.AbstractWorker;
import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.SynchronousProc;

public class Test1Worker extends AbstractWorker {

    public Test1Worker(CyclicBarrier barrier, int size, int id) {
        super(barrier, size, id);
    }

    public void run() {
        try {
//            Random rand = new Random();
            long sum = 0;

            Result result = new Result(); 
//            FibonacciCalcIntf fibonacciCalc = new NormalFibonacciCalc(result);
            
            DeferredEnv<FibonacciCalcIntf> defEnv = DeferredExecution.createDeferredEnv(
            		FibonacciCalcIntf.class, 
            		CoalescedFibonacciCalc.class, 
            		new SynchronousProc(),
            		1000,
            		result);
            FibonacciCalcIntf fibonacciCalc = defEnv.getProxy();

            barrier.await();
            for(int i = 0; i < size; i++) {
//                int val = (rand.nextBoolean()) ? 29 : 30;
                int val = 30;
                fibonacciCalc.calcAndSum(val);
//                sum += Fibonacci.calc(val);
            }
            defEnv.processCurrentBuffer();
            sum = result.get();
            barrier.await();

            System.out.println(getName() + "\t" + sum);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
