package test;

public class TaskWithInstanceVar implements Task {

    private Counter counter;
    
    public TaskWithInstanceVar(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void doSomething() {
        counter.increment();
    }

}
