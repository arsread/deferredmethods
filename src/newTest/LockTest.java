package newTest;

import java.util.concurrent.locks.ReentrantLock;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.ShadowThreadProcWithHooks;

public class LockTest {
	public static void main(String[] args) {
		final DeferredEnv<LockTestInt> def = DeferredExecution
				.createDeferredEnv(LockTestInt.class, LockTestImpl.class,
						new ShadowThreadProcWithHooks(), 1000);
		final LockTestInt LockTest = def.getProxy();
		final ReentrantLock lock = new ReentrantLock();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				lock.lock();
				LockTest.afterLock(Thread.currentThread().getName());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				LockTest.releaseLock(Thread.currentThread().getName());
				lock.unlock();
			}

		}).start();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				LockTest.beforeLock(Thread.currentThread().getName());
				lock.lock();
				LockTest.afterLock(Thread.currentThread().getName());
				lock.unlock();
			}

		}).start();
	}

}
