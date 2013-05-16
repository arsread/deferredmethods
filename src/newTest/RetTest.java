package newTest;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.Ret;
import deferredmethods.proc.AdaptiveProc;

public class RetTest {
	public static DeferredEnv<RetTestIntf> env = DeferredExecution
			.createDeferredEnv(RetTestIntf.class, RetTestImpl.class,
					new AdaptiveProc(), 15);
	public static RetTestIntf ret = env.getProxy();

	public static void main(String[] args) {
	
		for (int cnt=0;cnt<1000;cnt++){
			ret.nothing();
		}
		Ret<Long> val=null;
		for (int cnt=0;cnt<20;cnt++){
			val = ret.calc(cnt, 100);
		}
		ret.print(val, env);
		for (int cnt=0;cnt<1000;cnt++){
			ret.nothing();
		}
		try {
			System.out.println(val.get(env));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
