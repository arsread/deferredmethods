package newTest;

import java.util.concurrent.atomic.AtomicInteger;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.GeneralProcessingCheckPoint;
import deferredmethods.ProcessingCheckPoint;
import deferredmethods.proc.SynchronousProc;
import deferredmethods.proc.ThreadPoolProc;

public class Example {
	public static final AtomicInteger counter = new AtomicInteger();

	static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
        	public void run() {
        		System.out.println("I AM THE SHUTDOWN HOOK!! BUAHAHAHA!!");
        	}
        });
	}

	void go() {
		DeferredEnv<FooInterface> defEnv = DeferredExecution.createDeferredEnv(
				FooInterface.class, 
				FooImpl.class, 
				new ThreadPoolProc(),
				1000,
				counter);
		FooInterface foo = defEnv.getProxy();

		for(int i = 0; i < 1000000; i++) {
			foo.foo1();
		}
//		foo.foo1printend();
		ProcessingCheckPoint pcp1 = defEnv.createCheckPoint();

		for(int i = 0; i < 1000000; i++) {
			foo.foo2();
		}
//		foo.foo2printend();

		ProcessingCheckPoint pcp2 = defEnv.createCheckPoint();

//		int count = 0;
//		while(!pcp1.isProcessed()) {
//			count++;
//			System.out.println("Waiting..."+count);
//			try { Thread.sleep(1000); } catch(InterruptedException e) { }
//		}
//
//		System.out.println("Checkpoint1 ends!");

		for(int i = 0; i < 3000; i++) {
			foo.foo3();
		}
//		foo.foo3printend();
//		ProcessingCheckPoint pcp3 = defEnv.createCheckPoint();
//
//		pcp2.awaitProcessed();
//		System.out.println("Checkpoint2 ends!");	

		System.out.println("Main ends!");	
		System.out.println("COUNTER: " + counter.get());
	}

	public static void main(String[] args) {
		new Example().go();
	}
}
