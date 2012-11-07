package test;

public class DefMethodsImpl implements DefMethods {
    
    private static final Counter counterTS = CounterSingletonWrapper.getCounter();

    @Override
    public void profCall(String caller, String callee) {
        counterTS.increment();
    }

    @Override
    public void profAlloc(String mid, Object allocObj) {
        counterTS.increment();
    }

}
