package newTest;

import java.util.concurrent.locks.ReentrantLock;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.SynchronousProc;

public class LockTest {
	public static void main(String[] args) {
		final DeferredEnv<LockTestInt> def = DeferredExecution
				.createDeferredEnv(LockTestInt.class, LockTestImpl.class,
						new SynchronousProc(), 1000);
		final LockTestInt lockTest = def.getProxy();
		final ReentrantLock lock = new ReentrantLock();
//		final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
//		final ReadLock rl = rwl.readLock();
//		final WriteLock wl = rwl.writeLock();
		final SharedObj obj = new SharedObj();
		final SharedObj obj2 = new SharedObj();

		for (int i = 0; i < 1; i++) {
			new Thread("ReadLockThread-" + i) {
				@Override
				public void run() {
//					int count = -1;
					while (true) {
//						lockTest.beforeGetLock(getName());
						try{
							lock.lock();
//						lockTest.afterGetLock(getName());
//						if (count != obj.readVal()){ 
//							lockTest.readLock(getName(), obj);
//							count = obj.readVal();
//						}
//						lockTest.beforeReleaseLock(getName());
						lockTest.readLock(getName(), obj);
						}finally{
							lock.unlock();
						}
//						lockTest.afterReleaseLock(getName());
					}
				}
			}.start();
		}

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		for (int i = 0; i < 1; i++) {
			new Thread("WriteLockThread-" + i) {
				@Override
				public void run() {
					while (true) {
//						lockTest.beforeGetLock(getName());
//						System.out.println(getName()+"try to lock!");
						try{
							lock.lock();
//						lockTest.afterGetLock(getName());
						lockTest.writeLock(getName(), obj);
//						lockTest.beforeReleaseLock(getName());
						} finally{
							lock.unlock();
						}
//						lockTest.afterReleaseLock(getName());
					}
				}
			}.start();
		}
		
	}

}
