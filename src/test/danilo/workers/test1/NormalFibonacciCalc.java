package test.danilo.workers.test1;

public class NormalFibonacciCalc implements FibonacciCalcIntf {

    private Result futureResult;

    public NormalFibonacciCalc(Result futureResult) {
        this.futureResult = futureResult;
    }

    @Override
    public void calcAndSum(int value) {
        int result = futureResult.get() + Fibonacci.calc(value); 
        futureResult.set(result);
    }

}
