package test.danilo.workers.test1;

public class Fibonacci {
    public static int calc(int val) {
        if(val < 0) throw new NumberFormatException();
        if(val <= 1) return val;
        return calc(val - 1) + calc(val - 2);
    }
}
