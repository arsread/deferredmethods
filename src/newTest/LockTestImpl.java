package newTest;

public class LockTestImpl implements LockTestInter {

	@Override
	public void increase(SharedObj obj) {
		obj.increaseVal();
	}

	@Override
	public void decrease(SharedObj obj) {
		obj.decreaseVal();
	}

	@Override
	public void monitor(SharedObj obj) {
		if (obj.readVal()>100 || obj.readVal()<100){
			System.out.println("reset..");
			obj.setVal(0);
		}
	}


}
