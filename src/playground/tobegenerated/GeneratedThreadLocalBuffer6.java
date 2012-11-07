package playground.tobegenerated;

import deferredmethods.ThreadLocalBuffer;
import test.Counter;

//public class GeneratedThreadLocalBuffer6 extends ThreadLocal<GeneratedBuffer6> {
////    private final String s;
//    private final Counter c;
//    private final GeneratedDeferredEnv6 generatedDeferredEnv2;
//    public GeneratedThreadLocalBuffer6(GeneratedDeferredEnv6 generatedDeferredEnv2, Counter c) {
//        this.generatedDeferredEnv2 = generatedDeferredEnv2; 
//        this.c = c;
////        this.s = s;
//    }
//    protected GeneratedBuffer6 initialValue() {
////        return new GeneratedBufferByHand(generatedDeferredEnv2.getBufferCapacity(), c);
//        return new GeneratedBuffer6(generatedDeferredEnv2.getBufferCapacity(), c);
//    }
//}

public class GeneratedThreadLocalBuffer6 extends ThreadLocalBuffer<GeneratedBuffer6> {
    // private final String s;
    private final Counter c;
    private final GeneratedDeferredEnv6 generatedDeferredEnv2;

    public GeneratedThreadLocalBuffer6(GeneratedDeferredEnv6 generatedDeferredEnv2, Counter c) {
        super(generatedDeferredEnv2);
        this.generatedDeferredEnv2 = generatedDeferredEnv2;
        this.c = c;
        // this.s = s;
    }

    protected GeneratedBuffer6 initialValue() {
        // return new
        // GeneratedBufferByHand(generatedDeferredEnv2.getBufferCapacity(), c);
        return new GeneratedBuffer6(generatedDeferredEnv2.getBufferCapacity(), c);
    }
}
