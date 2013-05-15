package deferredmethods;


public class Ret<T> {
	private T val;
	private boolean isDone=false;
	
	public Ret(){
	}
	
	public Ret(T val){
		set(val);
	}
	
	public void mov(Ret<T> ret){
		try {
			set(ret.get(null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized T get(DeferredEnv<?> env) throws InterruptedException {
		if (env != null) env.processCurrentBuffer();
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
