package deferredmethods.proc;

import deferredmethods.ExtendedRunnable;

public class SynchronousProc implements Processor {
    private volatile boolean isRunning = false;

    public SynchronousProc() {
        start();
    }

    @Override
    public void process(ExtendedRunnable buffer) {
        if(isRunning) {
            buffer.run();
        }
    }

    @Override
    public void start() {
        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

	@Override
	public void ensureQueue(Thread thread) {
		// TODO Auto-generated method stub
		
	}

}
