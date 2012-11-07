package test;

public class TaskImplWithSingleton implements Task {

    private static Counter counter = CounterSingletonWrapper.getCounter();
    
    @Override
    public void doSomething() {
        counter.increment();
        for (int i = 0; i < 100; i++) {
            Math.pow(i, i);
        }
    }
    
}
