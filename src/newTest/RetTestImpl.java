package newTest;

import deferredmethods.DeferredEnv;
import deferredmethods.Ret;

public class RetTestImpl implements RetTestIntf {
	public static int cnt=0;

	@Override
	public Ret<Long> calc(long sum, long adder) {
		sum += adder;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Ret<Long>(sum);
	}

	@Override
	public void nothing() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cnt++;
	}

	@Override
	public void print(Ret<Long> n, DeferredEnv<?> env) {
		try {
			System.out.println("Print Val:"+n.get(env));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Ret<Integer> getPlusOne(int i) {
		return (new Ret<Integer>(i+1));
	}
	
}
