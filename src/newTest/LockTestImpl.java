package newTest;

import deferredmethods.ProcessingCheckPoint;

public class LockTestImpl implements LockTestInt {

	@Override
	public void beforeLock(String str) {
		System.out.println(str +":I'm trying to get the lock!");
	}

	@Override
	public void afterLock(String str) {
		System.out.println(str+":I've got the lock!");
	}

	@Override
	public void releaseLock(String str) {
		System.out.println(str+":I'm gonna release the lock!");
	}

}
