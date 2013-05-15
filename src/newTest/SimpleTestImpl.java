package newTest;

import deferredmethods.Ret;

public class SimpleTestImpl implements SimpleTestInter {

	@Override
	public void noRet(int x) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("no return method!Procedure:"+x);
	}

	@Override
	public Ret<Integer> ret() {
		System.out.println("return method!");
		return new Ret<Integer>(20);
	}

}
