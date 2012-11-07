package playground.tobegenerated;

import test.Counter;
import test.Task;
import deferredmethods.GeneralDeferredEnv;
import deferredmethods.proc.Processor;

public class GeneratedDeferredEnv6 extends GeneralDeferredEnv<Task> implements Task {

    private final GeneratedThreadLocalBuffer6 bufferTL;
        
//    private GeneratedBuffer2 buffer;
//    private final Counter c;
//    private final String s;
    
    public GeneratedDeferredEnv6(Processor proc, final int bufferCapacity, int id, final Counter c) {
//    public GeneratedDeferredEnv6(Processor proc, final int bufferCapacity) {
        super(proc, bufferCapacity, id);
//        this.c = c;
//        this.s = s;
//        buffer = new GeneratedBuffer2(bufferCapacity, c, s);

//        bufferTL = new ThreadLocal<GeneratedBuffer2>() {
//            protected GeneratedBuffer2 initialValue() {
//                return new GeneratedBuffer2(getBufferCapacity(), c, test());
//            }
//        };
        
        bufferTL = new GeneratedThreadLocalBuffer6(this, c);
    }
    
    @Override
    public void processCurrentBuffer() {
        proc.process(bufferTL.get());
//        buffer = new GeneratedBuffer2(getBufferCapacity(), c, s);
        bufferTL.remove();
    }

    @Override
//    public void doSomething(int i, byte b, long l, float f, double d, short s, char c, boolean bool) {
    public void doSomething() {
//        if(buffer.appendDoSomething(i, b, l, f, d, s, c, bool) == false) {
        if(bufferTL.get().appendDoSomething() == false) {
            processCurrentBuffer();
        }
    }

    @Override
    public Task getProxy() {
        return this;
    }

//    @Override
//    public void setCounter(Counter counter) {
//        if(bufferTL.get().appendSetCounter(counter) == false) {
//            processCurrentBuffer();
//        }
//    }

//    public GeneratedBuffer6 getBuffer() {
//        return bufferTL.get();
//    }
}
