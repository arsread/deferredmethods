package newTest;

public class WaitTest{
	synchronized public void test() throws InterruptedException{
		wait();
		foo();
	}
	
	public void foo(){
		System.out.println();
	}

	public static void main(String[] args){
	}
}
