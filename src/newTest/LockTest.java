package newTest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.ShadowThreadProcWithHooks;

public class LockTest {
	public static void main(String[] args) {
		final DeferredEnv<LockTestInt> def = DeferredExecution
				.createDeferredEnv(LockTestInt.class, LockTestImpl.class,
						new ShadowThreadProcWithHooks(), 1000);
		final LockTestInt lockTest = def.getProxy();
		final ReentrantLock lock = new ReentrantLock();
		final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		final ReadLock rl = rwl.readLock();
		final WriteLock wl = rwl.writeLock();
		
		
	}
		
}
