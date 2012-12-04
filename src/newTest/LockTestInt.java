package newTest;

import deferredmethods.Deferred;
import deferredmethods.ProcessingCheckPoint;

public interface LockTestInt extends Deferred {

	void beforeLock(String str);

	void afterLock(String str);

	void releaseLock(String str);

}
