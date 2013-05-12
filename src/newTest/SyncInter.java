package newTest;

import deferredmethods.Deferred;

public interface SyncInter extends Deferred {
	public void test1(int num);
	public void test1sync(int num);
	public void test2(int num);
	public void test2sync(int num);
	public void test3(int num);
}
