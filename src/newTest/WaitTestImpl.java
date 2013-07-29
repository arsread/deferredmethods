package newTest;

public class WaitTestImpl implements WaitTestIntf{

	@Override
	public void before() {
		System.out.println("DM before wait!");
	}

	@Override
	public void after() {
		System.out.println("DM after wait!");
	}

}
