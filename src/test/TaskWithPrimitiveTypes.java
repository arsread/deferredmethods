package test;

import deferredmethods.Deferred;

public interface TaskWithPrimitiveTypes extends Deferred {
    public void doSomething(int i, byte b, long l, float f, double d, short s, char c, boolean bool);
    public void setCounter(MultipleCounter counter);
}
