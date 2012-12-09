package newTest;

import deferredmethods.Deferred;

public interface LockTestInt extends Deferred {

	public void beforeGetLock(String str);
	public void afterGetLock(String strj);
	public void beforeReleaseLock(String str);
	public void afterReleaseLock(String str);
	public void readLock(String str, SharedObj obj);
	public void writeLock(String str, SharedObj obj);
}
