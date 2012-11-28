package deferredmethods.proc;

import deferredmethods.Buffer;

public class SynchronousProc implements Processor {
    private volatile boolean isRunning = false;

    public SynchronousProc() {
        start();
    }

    @Override
    public void process(Buffer buffer) {
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
	public void producerThreadDied(Thread t) {
		// Just useful for ShadowThread Proc
	}

}
