package newTest;

import deferredmethods.Deferred;

public interface LockTestInter extends Deferred {

	void increase(SharedObj obj);

	void decrease(SharedObj obj);

	void monitor(SharedObj obj);

}
