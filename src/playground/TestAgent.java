package playground;

import java.io.IOException;

import test.Counter;
import test.CounterSingletonWrapper;
import test.Task;
import test.TaskImpl;
import test.TaskUser;
import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.Processor;
import deferredmethods.proc.SynchronousProc;



public class TestAgent {
    public static void main(String[] args) throws IOException {
        final Counter counter = CounterSingletonWrapper.getCounter();
        
        
        Processor proc = new SynchronousProc();
//        final GeneratedDeferredEnv6 deferredEnv = new GeneratedDeferredEnv6(proc, 100, counter);
//      final DeferredEnv deferredEnv = DeferredExecution.blah(proc, 100, counter);
        final DeferredEnv deferredEnv = DeferredExecution.createDeferredEnv(
                Task.class, 
                TaskImpl.class, 
                proc, counter);
        Task deferredTask = (Task) deferredEnv;

        final TaskUser userOfDeferredTask = new TaskUser();
        userOfDeferredTask.setTask(deferredTask);
        
//        long t = System.nanoTime();
        for (int c = 0; c < 1002; c++) {
//            userOfDeferredTask.usingTask();
        }
//        deferredEnv.processCurrentBuffer();
//        try {
//            proc.shutdown();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        System.out.println(counter.getValue());
//        System.out.println(System.nanoTime() - t);

        new Thread("another") {
            @Override
            public void run() {
                userOfDeferredTask.usingTask();
            }
        }.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println(counter.getValue());
            }
        });
    }

}
