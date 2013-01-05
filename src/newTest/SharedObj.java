package newTest;

public class SharedObj {
	private int val =  0;
	public synchronized int readVal(){
		return val;
	}
	
	public synchronized void increaseVal(){
		val++;
	}
	
	public synchronized void decreaseVal(){
		val--;
	}
	
	public synchronized void setVal(int val){
		this.val = val;
	}
}
