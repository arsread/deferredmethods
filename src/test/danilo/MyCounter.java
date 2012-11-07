package test.danilo;

import java.util.Random;

public class MyCounter {
    private long counter;
    private final Random rand = new Random();

    public long get() {
        return counter;
    }

    public long addAndGet(long add) {
        counter += add;
        return counter;
    }

    public long incrementAndGet() {
        return ++counter;
    }

    public void randomAdd() {
        counter += rand.nextInt(100);
    }
}
