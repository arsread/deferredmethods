package test;

public class CounterTL {
    
    private final ThreadLocal<Integer> counterTL = 
        new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return new Integer(0);
        }
    };
    
    public void increment() {
        Integer old = counterTL.get();
        counterTL.set(old+1);
    }
    
    public void add(int i) {
        Integer old = counterTL.get();
        counterTL.set(old+i);
    }
    
    public int getValue() {
        return counterTL.get();
    }
    
    public String toString() {
        return Integer.toString(counterTL.get());
    }
}
