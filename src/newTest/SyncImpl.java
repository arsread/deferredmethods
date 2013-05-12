package newTest;

public class SyncImpl implements SyncInter {

	@Override
	public void test1(int num) {
		System.out.println("[TEST1]\t\t"+Thread.currentThread().getName()+":Processing Method\t"+num);
	}

	@Override
	public void test2(int num) {
		System.out.println("[TEST2]\t\t"+Thread.currentThread().getName()+":Processing Method\t"+num);
	}

	@Override
	public void test3(int num) {
		System.out.println("[TEST3]\t\t"+Thread.currentThread().getName()+":Processing Method\t"+num);
	}

	@Override
	public void test1sync(int num) {
		System.out.println("[TEST1][Sync]\t"+Thread.currentThread().getName()+":Processing Method\t"+num);

	}

	@Override
	public void test2sync(int num) {
		System.out.println("[TEST2][Sync]\t"+Thread.currentThread().getName()+":Processing Method\t"+num);
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
