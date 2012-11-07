package newTest;

import deferredmethods.ProcessingCheckPoint;

public class CPTestImpl implements CPTestInterfce {

	@Override
	public void foo1() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void foo2() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void foo2end() {
		System.out.println("foo2 ends!!");
	}

	@Override
	public void foo3(ProcessingCheckPoint pcp2) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("I'm foo3!CheckPoint 2 statue:"+pcp2.isProcessed());
	}

}
