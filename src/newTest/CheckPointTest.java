package newTest;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.ProcessingCheckPoint;
import deferredmethods.proc.ThreadPoolProc;

public class CheckPointTest {
	public static void main(String[] args) {
		DeferredEnv<CPTestInterfce> def = DeferredExecution.createDeferredEnv(
				CPTestInterfce.class, CPTestImpl.class, new ThreadPoolProc(),
				1000);
		final CPTestInterfce CPTest = def.getProxy();

		for (int i = 0; i < 30; i++)
			CPTest.foo1();
		CPTest.foo2end();
		final ProcessingCheckPoint pcp1 = def.createCheckPoint();
		System.out.println("CheckPoint1 Created!");
		// if (!pcp1.isProcessed()) {
		// System.out.println("CheckPoint1 not processed.Waiting...");
		// }
		// pcp1.awaitProcessed();
		// System.out.println("CheckPoint1 statue:"+pcp1.isProcessed());
		//
		// CPTest.foo2();
		// CPTest.foo2end();
		// ProcessingCheckPoint pcp2 = def.createCheckPoint();
		// System.out.println("CheckPoint2 Created!");
		//
		// for (int i = 0; i < 5; i++) {
		// CPTest.foo3(pcp2);
		// }
		for (int i = 0; i < 3; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < 5; i++) {
						CPTest.foo3(Thread.currentThread().getId(),pcp1);
					}
				}

			}).start();
		}
	}

}
