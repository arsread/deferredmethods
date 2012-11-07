package test;

public class TaskImpl implements Task {

    private Counter counter;
    
    public TaskImpl(Counter counter) {
        this.counter = counter;
    }
    @Override
    public void doSomething() {
        counter.increment();
        for (int i = 0; i < 100; i++) {
            Math.pow(i, i);
        }
    }
    
}
