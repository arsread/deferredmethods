package newTest;


public class LockTestImpl implements LockTestInt {

	@Override
	public void callObj(SyncObj obj) {
		obj.call();
	}


}
