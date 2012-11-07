package playground;

import java.io.IOException;

import test.Counter;
import test.CounterSingletonWrapper;
import test.Task;
import test.TaskImpl;
import test.TaskUser;
import test.TaskWithCoalescingImpl;
import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.ThreadPoolProc;
import deferredmethods.proc.Processor;
import deferredmethods.proc.SynchronousProc;



public class Main {
    public static void main(String[] args) throws IOException {
//        Counter counter = new Counter();
        
        Processor proc = new SynchronousProc();
//        Processor proc = new SynchronousProc();
//        DeferredEnv deferredEnv = new GeneratedDeferredEnv2(proc, 10000, counter, "blah");
        DeferredEnv deferredEnv = DeferredExecution.createDeferredEnv(
                Task.class, 
                TaskImpl.class, 
                proc);
//                counter, 
//                "blah"); 
        Task deferredTask = (Task) deferredEnv;

//        deferredTask.setCounter(counter);

        TaskUser userOfDeferredTask = new TaskUser();
        userOfDeferredTask.setTask(deferredTask);
        
        long t = System.nanoTime();
        for (int c = 0; c < 1002; c++) {
            userOfDeferredTask.usingTask();
        }
//        deferredEnv.processCurrentBuffer();
//        try {
//            proc.shutdown();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        System.out.println(counter.getValue());
        System.out.println(CounterSingletonWrapper.getCounter().getValue());
        System.out.println(System.nanoTime() - t);

//        Task nonDeferredTask = new TaskImpl();
//        nonDeferredTask.setCounter(counter);
//        
//        TaskUser userOfNonDeferredTask = new TaskUser();
//        userOfNonDeferredTask.setTask(nonDeferredTask);
//        
//        t = System.nanoTime();
//        for (int c = 0; c < 1000; c++) {
//            userOfNonDeferredTask.usingTask();
//        }
//        System.out.println(counter.getValue());
//        System.out.println(System.nanoTime() - t);

    }

}
