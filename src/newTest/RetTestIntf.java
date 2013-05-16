package newTest;

import deferredmethods.Deferred;
import deferredmethods.DeferredEnv;
import deferredmethods.Ret;

public interface RetTestIntf extends Deferred{ 
	Ret<Long> calc(long sum, long adder);
	void nothing();
	void print(Ret<Long> n, DeferredEnv<?> env);
	Ret<Integer> getPlusOne(int i);
}
