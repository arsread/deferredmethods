package test;

public class TaskImplWithMultipleConstructors implements Task {

    private Counter counter;
    
    public TaskImplWithMultipleConstructors(Counter counter) {
        this.counter = counter;
    }
    
    public TaskImplWithMultipleConstructors(Counter counter, String s) {
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
