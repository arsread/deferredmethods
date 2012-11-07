package test;

public class TaskWithMultipleParamsImpl implements TaskWithMultipleParams {

    private static Counter counter;
    
    @Override
    public void doSomething(Integer i, Integer j) {
        counter.add(i + j);
    }

    @Override
    public void setCounter(Counter c) {
        counter = c;
    }

}
