package test;

public class TaskWithArrayTypesImpl implements TaskWithArrayTypes {

    private static Counter counter;
    
    @Override
    public void doSomething(int[] i, int[][] j, Integer[] k, Integer[][] l) {
        counter.add(i[0]);
        counter.add(j[0][0]);
        counter.add(k[0]);
        counter.add(l[0][0]);
    }

    @Override
    public void setCounter(Counter c) {
        counter = c;
    }

}
