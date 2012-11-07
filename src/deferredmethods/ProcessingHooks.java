package deferredmethods;

/**
 * If the class that implements the methods that will be deferred implements
 * that interface it can receive callbacks before and after processing the
 * deferred method invocations. The main use case of this interface is to
 * support the implementation of custom strategies for coalescing deferred
 * method invocations. Instead of accessing shared data structures each time
 * that a deferred method is invoked, one can define some instance fields to
 * record the invocations in thread-local data structures.
 * 
 * @author igormoreno
 * 
 */
public interface ProcessingHooks {

    /**
     * Use this method to initialize thread-local fields before they can be used
     * in the deferred methods.
     */
    public void beforeProcessing();

    /**
     * Use this method to integrate results stored in thread-local fields into
     * shared data structures
     */
    public void afterProcessing();
}
