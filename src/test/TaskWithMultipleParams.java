package test;

import deferredmethods.Deferred;

public interface TaskWithMultipleParams extends Deferred {
    public void doSomething(Integer i, Integer j);
    public void setCounter(Counter counter);
}
