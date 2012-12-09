package newTest;

public class LockTestImpl implements LockTestInt {

	@Override
	public void beforeGetLock(String str) {
		System.out.println(str + ":I'm trying to get the lock!");
	}

	@Override
	public void afterGetLock(String str) {
		System.out.println(str + ":I've got the lock!");
	}

	@Override
	public void beforeReleaseLock(String str) {
		System.out.println(str + ":I'm trying to release the lock!");
	}

	@Override
	public void afterReleaseLock(String str) {
		System.out.println(str + ":I've released the lock!");
	}

	@Override
	public void readLock(String str, SharedObj obj) {
//		System.out.println(str + ":obj value -->" + obj.readVal());
//		obj.readVal();
	}

	@Override
	public void writeLock(String str, SharedObj obj) {
		System.out.println(str + ":increasing value...");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		for (int n=0;n<100000;n++){
//			System.out.println("haha"); ;	
//		}

//		obj.increaseVal();
		
		System.out.println(str + ":increased!");
	}

}
