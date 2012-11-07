package test;

public class CounterSingletonWrapper {

    private static volatile Counter theOne;
    
    private CounterSingletonWrapper() { }
    
    public static Counter getCounter() {
        if(theOne == null) {
            synchronized (CounterSingletonWrapper.class) {
                if(theOne == null) 
                    theOne = new Counter();
            }
        }
        return theOne;
    }
}
