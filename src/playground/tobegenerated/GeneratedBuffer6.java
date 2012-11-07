 package playground.tobegenerated;

import test.Counter;
import test.TaskWithCoalescingImpl;

public class GeneratedBuffer6 extends TaskWithCoalescingImpl implements Runnable {

    private final int[] deferredMethodID;
    private int currentPos;
    
    private static final int DO_SOMETHING = 1;
    private static final int SET_COUNTER = 2;
    
//    private final int[] arg1;
//    private final byte[] arg2;
//    private final long[] arg3;
//    private final float[] arg4;
//    private final double[] arg5;
//    private final short[] arg6;
//    private final char[] arg7;
//    private final boolean[] arg8;
    private final Counter[] argC;
    
    public GeneratedBuffer6(int bufferCapacity, Counter c) {
        super(c);
        
        // TODO check buffer capacity
        deferredMethodID = new int[bufferCapacity];
        currentPos = 0;
//        arg1 = new int[bufferCapacity];
//        arg2 = new byte[bufferCapacity];
//        arg3 = new long[bufferCapacity];
//        arg4 = new float[bufferCapacity];
//        arg5 = new double[bufferCapacity];
//        arg6 = new short[bufferCapacity];
//        arg7 = new char[bufferCapacity];
//        arg8 = new boolean[bufferCapacity];
        argC = new Counter[bufferCapacity];
    }
    
    public int currentPos() {
        return currentPos;
    }

//    public boolean appendDoSomething(int i, byte b, long l, float f, double d, short s, char c, boolean bool) {
    public boolean appendDoSomething() {
        // TODO delete check
        if(currentPos < deferredMethodID.length) {
            deferredMethodID[currentPos] = DO_SOMETHING;
//            arg1[currentPos] = i;
//            arg2[currentPos] = b;
//            arg3[currentPos] = l;
//            arg4[currentPos] = f;
//            arg5[currentPos] = d;
//            arg6[currentPos] = s;
//            arg7[currentPos] = c;
//            arg8[currentPos] = bool;
            currentPos++;
        }
        
        return currentPos < deferredMethodID.length;
    }
    
    public boolean appendSetCounter(Counter counter) {
        if(currentPos < deferredMethodID.length) {
            deferredMethodID[currentPos] = SET_COUNTER;
            argC[currentPos] = counter;
            currentPos++;
        }
        
        return currentPos < deferredMethodID.length;
    }
    
    @Override
    public void run() {
        beforeProcessing();
        // TODO create a default
        for (int i = 0; i < currentPos; i++) {
            switch (deferredMethodID[i]) {
            case DO_SOMETHING:
                doSomething();
                break;
//            case SET_COUNTER:
//                setCounter(argC[i]);
////                test(argA[i], argB[i]);
//                break;
            }
        }
        afterProcessing();
    }

}
