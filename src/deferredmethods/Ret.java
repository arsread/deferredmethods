package deferredmethods;


public class Ret<T> {
	private T val;
	private boolean isDone=false;
	
	public void mov(Ret<T> ret){
		try {
			set(ret.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized T get() throws InterruptedException {
		while (!isDone){
			wait();
		}
		return val;
	}
	
	public synchronized void set(T val){
		this.val = val;
		isDone = true;
		notifyAll();
	}

	public boolean isDone() {
		return isDone;
	}
}
