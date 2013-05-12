package newTest;

import junit.framework.Test;

import org.omg.CORBA.PRIVATE_MEMBER;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.ShadowThreadProcWithHooks;

public class SyncTest2 {
	public static void main(String[] argsd) {
		final DeferredEnv<SyncInter> def = DeferredExecution.createDeferredEnv(
				SyncInter.class, SyncImpl.class, new ShadowThreadProcWithHooks(
						10, 1000), 1000);
		final SyncInter test = def.getProxy();
		final Object lock = new Object();
		final SyncTest2 sync2 = new SyncTest2();

		new Thread() {
			public int num = 0;

			@Override
			public void run() {
				for (int n = 0; n < 3; n++) {
					callsync(test, num, 1);
					num++;
				}
			}
		}.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread() {
			public int num = 0;

			@Override
			public void run() {
				for (int n = 0; n < 3; n++) {
					for (int i = 0; i < 3; i++) {
						test.test2(num);
						num++;
					}
					callsync(test, num, 2);
					num++;
				}
			}
		}.start();

	}

	static private void callsync(SyncInter test, int num, int call) {
		synchronized (SyncTest2.class) {
			if (call == 1){
				test.test1sync(num);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (call == 2){
				test.test2sync(num);
			}
		}
	}

}
