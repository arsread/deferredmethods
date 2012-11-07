package test.danilo.workers.test1;

import java.util.HashMap;

import deferredmethods.ProcessingHooks;

public class CoalescedFibonacciCalc implements FibonacciCalcIntf, ProcessingHooks {

    private Result futureResult;
    private HashMap<Integer, Integer> valueRepetitions;

    public CoalescedFibonacciCalc(Result futureResult) {
        this.futureResult = futureResult;
    }

    @Override
    public void calcAndSum(int value) {
        Integer numRepetitions = valueRepetitions.get(value);
        numRepetitions = (numRepetitions == null) ? 1 : numRepetitions + 1;
        valueRepetitions.put(value, numRepetitions);
    }

    @Override
    public void beforeProcessing() {
        valueRepetitions = new HashMap<Integer, Integer>();
    }

    @Override
    public void afterProcessing() {
        int result = 0;
        for (Integer value : valueRepetitions.keySet()) {
            result += valueRepetitions.get(value) * Fibonacci.calc(value);
        }
        futureResult.set(result + futureResult.get());
    }

}
