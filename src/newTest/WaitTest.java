package newTest;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.proc.AdaptiveProc;

public class WaitTest{
	
	public static DeferredEnv<WaitTestIntf> env = DeferredExecution
			.createDeferredEnv(WaitTestIntf.class, WaitTestImpl.class,
					new AdaptiveProc(), 15);
	public static WaitTestIntf ret = env.getProxy();
	public static final WaitTest wt = new WaitTest();

	public static void main(String[] args){
		for (int i=0;i<10;i++) {
			ret.before();
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized(wt){
					wt.notifyAll();
				}
			}
		}).start();	
		
		try {
			synchronized(wt){
				wt.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i=0;i<10;i++) {
			ret.after();
		}

	}
}
