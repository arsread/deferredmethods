package deferredmethods.proc;

import deferredmethods.Buffer;

/**
 * A processor is responsible for actually executing the deferred methods.
 * Different implementation will determine different processing strategies.
 * 
 * @author igormoreno
 * 
 */
public interface Processor {
    
    /**
     * Receives a buffer for execution
     * 
     * @param buffer Buffer to be executed
     */
    void process(Buffer buffer);
    void start();
    void stop();
    void producerThreadDied(Thread t);
}
