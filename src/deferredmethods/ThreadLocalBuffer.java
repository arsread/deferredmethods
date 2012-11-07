package deferredmethods;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This implementation directly accesses an array of Object inserted in the
 * java.lang.Thread class accessed with
 * <code>Thread.currentThread().threadLocalBuffer</code>. The semantics is
 * similar to the one of java.lang.ThreadLocal except that each buffer is unique
 * per Thread and per DeferredEnv id.
 * 
 * @author igormoreno
 * 
 * @param <T>
 */
public abstract class ThreadLocalBuffer<T> {

    private final int deferredEnvId;
    protected final AtomicInteger bufferID;

    public ThreadLocalBuffer(GeneralDeferredEnv<?> deferredEnv) {
        this.deferredEnvId = deferredEnv.getId();
        this.bufferID = new AtomicInteger(1);
    }

//    public T get() {
//        Object[] threadLocalBuffer = Thread.currentThread().threadLocalBuffer;
//        Object buffer = null;
//        if (threadLocalBuffer.length > deferredEnvId) {
//            buffer = threadLocalBuffer[deferredEnvId];
//        }
//
//        if (buffer == null) {
//            threadLocalBuffer = ensureCapacity(threadLocalBuffer, deferredEnvId + 1);
//            buffer = initialValue();
//            threadLocalBuffer[deferredEnvId] = buffer;
//            Thread.currentThread().threadLocalBuffer = threadLocalBuffer;
//        }
//
//        return (T) buffer;
//    }

    public T get() {
        Object[] threadLocalBuffer = Thread.currentThread().threadLocalBuffer;

        Object buffer = null;
        try {
            buffer = threadLocalBuffer[deferredEnvId];
        } catch(ArrayIndexOutOfBoundsException e) { }

        if (buffer == null) {
            threadLocalBuffer = ensureCapacity(threadLocalBuffer, deferredEnvId + 1);
            buffer = initialValue();
            threadLocalBuffer[deferredEnvId] = buffer;
            Thread.currentThread().threadLocalBuffer = threadLocalBuffer;
        }

        return (T) buffer;
    }

    private Object[] ensureCapacity(Object[] array, int minCapacity) {
        int oldCapacity = array.length;
        if (oldCapacity < minCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (minCapacity > newCapacity) newCapacity = minCapacity;

            array = Arrays.copyOf(array, newCapacity);
        }
        
        return array;
    }

    public void remove() {
        Thread.currentThread().threadLocalBuffer[deferredEnvId] = null;
    }

    protected abstract T initialValue();
}