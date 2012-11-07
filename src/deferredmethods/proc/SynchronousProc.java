package deferredmethods.proc;

public class SynchronousProc implements Processor {
    private volatile boolean isRunning = false;

    public SynchronousProc() {
        start();
    }

    @Override
    public void process(Runnable buffer) {
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

}
