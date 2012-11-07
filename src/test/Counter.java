package test;

public class Counter {
    private int counter = 0;
    public synchronized void increment() {
        counter++;
    }
    public synchronized void add(int i) {
        counter += i;
    }
    public synchronized int getValue() {
        return counter;
    }
    public synchronized void integrate(CounterTL counterTL) {
        counter += counterTL.getValue();
    }
    public String toString() {
        return Integer.toString(counter);
    }
}
