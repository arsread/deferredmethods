package newTest;

import deferredmethods.Ret;

public class SimpleTestImpl implements SimpleTestInter {

	@Override
	public void noRet() {
		System.out.println("no return method!");
	}

	@Override
	public Ret<Integer> ret() {
		System.out.println("return method!");
		Ret<Integer> x=new Ret<Integer>();
		x.set(20);
		return x;
	}

}
