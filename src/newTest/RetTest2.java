package newTest;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.Ret;
import deferredmethods.proc.AdaptiveProc;
import deferredmethods.proc.ShadowThreadProcWithHooks;
import deferredmethods.proc.SynchronousProc;
import deferredmethods.proc.ThreadPoolProc;

public class RetTest2 {
	public static final int nThreads = 30;
	public static final int arraySize = 20;
	public static final int bufferSize = 15;
	public static DeferredEnv<RetTestIntf> env = DeferredExecution
			.createDeferredEnv(RetTestIntf.class, RetTestImpl.class,
					new ThreadPoolProc(), bufferSize);
	public static RetTestIntf ret = env.getProxy();
	public static CyclicBarrier barrier = new CyclicBarrier(nThreads);

	public static void main(String[] args) {
		InnerThread[] threads = new InnerThread[nThreads];
		Ret<Integer>[][] retArray = new Ret[nThreads][];
		for (int i = 0; i < nThreads; i++) {
			retArray[i] = new Ret[arraySize];
		}

		for (int i = 0; i < nThreads; i++) {
			threads[i] = new InnerThread(env, ret, retArray[i],
					retArray[(i + 1) % nThreads]);
		}

		for (InnerThread thread : threads) {
			thread.start();
		}
	}

	private static class InnerThread extends Thread {
		private DeferredEnv<?> env;
		private RetTestIntf ret;
		private Ret<?>[] retA;
		private Ret<?>[] retB;
		private static AtomicInteger id = new AtomicInteger(0);

		public InnerThread(DeferredEnv<?> env, RetTestIntf ret, Ret<?>[] retA,
				Ret<?>[] retB) {
			this.setName("Test Thread " + id.incrementAndGet());
			this.env = env;
			this.ret = ret;
			this.retA = retA;
			this.retB = retB;
			for (int i = 0; i < retA.length; i++) {
				retA[i] = ret.getPlusOne(i);
			}
		}

		@Override
		public void run() {
			for (int i = 0; i < retB.length; i++) {
				try {
					System.out.println("[" + Thread.currentThread().getName()
							+ "][" + i + "]" + retB[i].get(env));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
