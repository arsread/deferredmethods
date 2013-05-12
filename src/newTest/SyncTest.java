package newTest;

import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;

import deferredmethods.Deferred;
import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.AdaptiveProc;

public class SyncTest {
	public static void main(String[] argsd) {
		final DeferredEnv<SyncInter> def = DeferredExecution.createDeferredEnv(
				SyncInter.class, SyncImpl.class, new AdaptiveProc(10, 1000),
				1000);
		final SyncInter test = def.getProxy();
		final Object lock = new Object();

		new Thread() {
			public int num = 0;

			@Override
			public void run() {
//				while (true) {
					synchronized (lock) {
						for (int i = 0; i < 10; i++) {
							test.test1(num);
							// System.out.println("test1:"+num);
							num++;
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					for (int i = 0; i < 10; i++) {
						test.test1(num);
						// System.out.println("test1:"+num);
						num++;
					}
//				}
			}
		}.start();

		new Thread() {
			public int num = 0;

			@Override
			public void run() {
//				while (true) {
					for (int i = 0; i < 10; i++) {
						test.test2(num);
						// System.out.println("test2:"+num);
						num++;
					}
					synchronized (lock) {
						for (int i = 0; i < 10; i++) {
							test.test2(num);
							// System.out.println("test2:"+num);
							num++;
						}
					}
//				}
			}
		}.start();

	}

}
