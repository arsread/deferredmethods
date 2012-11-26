package deferredmethods;

/**
 * Interface defining management of buffer size and processing of deferred
 * methods. It is parameterized with the type (T) of the interface defining the
 * methods to be deferred.
 * 
 * @author igormoreno
 * 
 */
public interface DeferredEnv<T extends Deferred> {

    /**
     * Sets the buffer capacity (in number of entries). Changing the buffer
     * capacity influences subsequent buffer allocations; it does not change the
     * capacity of the current buffer.
     * 
     * @param size
     *            Buffer capacity (in number of entries)
     */
    public void setBufferCapacity(int size);

    /**
     * Queries the capacity of the next buffer to be allocated.
     * 
     * @return Buffer capacity
     */
    public int getBufferCapacity();

    /**
     * Force immediate processing of the current thread's buffer, independently
     * of its filling state; afterwards, a new buffer is created.
     */
    public void processCurrentBuffer();

    /**
     * Returns an instance of a class that implements the interface T that
     * defines the methods that should be deferred. Calls to methods of this
     * class will be deferred. It acts as a dynamic proxy to the real
     * implementation of T.
     */
    public T getProxy();
    
    public ProcessingCheckPoint createCheckPoint();
    
	public void comfirmBuffer(int bufferID, long threadID);
}
