package deferredmethods;

public interface Buffer extends Runnable{
	public Thread handInThread(); //getProducerThread
	public int getBufferId();
	public DeferredEnv getEnv();
}
