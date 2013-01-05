package newTest;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.ShadowThreadProcWithHooks;

public class LockTest {
	public static void main(String[] args) {
		DeferredEnv<LockTestInter> def = DeferredExecution.createDeferredEnv(
				LockTestInter.class, LockTestImpl.class,
				new ShadowThreadProcWithHooks(), 1000);
		final LockTestInter test = def.getProxy();
		final SharedObj obj = new SharedObj();

		final ReentrantLock lock = new ReentrantLock();

		for (int i = 0; i < 10; i++) {
			new Thread("Increase-" + i) {
				@Override
				public void run() {
					Random ran = new Random();
					while (true) {
						if (ran.nextBoolean()) {
							lock.lock();
							test.increase(obj);
							lock.unlock();
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}.start();
		}

		for (int i = 0; i < 10; i++) {
			new Thread("Decrease-" + i) {
				@Override
				public void run() {
					Random ran = new Random();
					while (true) {
						if (ran.nextBoolean()) {
							lock.lock();
							test.decrease(obj);
							lock.unlock();
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}.start();
		}

		for (int i = 0; i < 1; i++) {
			new Thread("Monitor-" + i) {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						lock.lock();
						if (obj.readVal()>100 || obj.readVal()<-100){
							System.out.println("reset..");
							obj.setVal(0);
						}
						System.out.println("current value:"+obj.readVal());
						lock.unlock();
					}
				}
			}.start();
		}

	}
}
