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
		System.out.println("main thread's buffer ends!!");
	}

	@Override
	public void foo3(long threadID,ProcessingCheckPoint pcp) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("I'm thread:"+threadID+"\tCheckPoint 1 statue:"+pcp.isProcessed());
	}

	@Override
	public void fooBuffParaTest(ProcessingCheckPoint pcp, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fooBuffParaTest2(int i, int i2) {
		// TODO Auto-generated method stub
		
	}

}
