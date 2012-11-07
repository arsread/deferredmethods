package test.danilo.workers.test1;

import deferredmethods.Deferred;

public interface FibonacciCalcIntf extends Deferred {
    public void calcAndSum(int value);
}
