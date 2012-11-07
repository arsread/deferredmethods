package test;

import deferredmethods.Deferred;

public interface TaskWithArrayTypes extends Deferred {
    public void doSomething(int[] i, int[][] j, Integer[] k, Integer[][] l);
    public void setCounter(Counter counter);
}
