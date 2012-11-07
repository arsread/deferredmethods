package playground;
import test.Counter;




Counter c1 = new Counter();
Analysis a1 = new Analysis(c1);

Counter c2 = new Counter();
Analysis a2 = new Analysis(c2);

new GUI(c1, c2).start();
Proxy

-------------------------------------------------

public class Wrapper {
    private static LinkedList<Counter> counters;

    public static Counter getNextCounter() {
        Counter newCounter = new Counter();
        counters.add(newCounter);
        return newCounter;
    }

    public static LinkedList<Counter> getCounters() {
        return counters;
    }
}


de1 = createDeferredEnv(AnalysisImpl.class, ...);
de2 = createDeferredEnv(AnalysisImpl.class, ...);

public class AnalysisImpl {
    private static final Counter counter = Wrapper.getNextCounter();

    //deferred methods
}


LinkedList<Counter> counters = Wrapper.getCounters();

//WHAT ABOUT THE MATCHING?


-------------------------------------------------

de1 = createDeferredEnv(Analysis.class, ..., c1);
de2 = createDeferredEnv(Analysis.class, ..., c2);

new GUI(c1, c2).start();

public class AnalysisImpl {
    private final Counter counter;
    
    public AnalysisImpl(Counter counter) {
        this.counter = counter;
    }

    //deferred methods
}
