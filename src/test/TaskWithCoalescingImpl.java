package test;

import deferredmethods.ProcessingHooks;

public class TaskWithCoalescingImpl implements Task, ProcessingHooks {

    private CounterTL counterTL;
    private Counter counterTS;
    
    public TaskWithCoalescingImpl(Counter c) {
        counterTS = c;
    }

    @Override
    public void beforeProcessing() {
        counterTL = new CounterTL();
    }

    @Override
    public void afterProcessing() {
        counterTS.integrate(counterTL);
    }

    @Override
    public void doSomething() {
        counterTL.increment();
        for (int i = 0; i < 100; i++) {
            Math.pow(i, i);
        }
    }
    
}
