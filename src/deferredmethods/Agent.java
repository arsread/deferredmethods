package deferredmethods;

import java.lang.reflect.Method;

import newTest.Example;

import deferredmethods.proc.ThreadPoolProc;

/**
 * Class to be called by the native agent on each thread's death in order to
 * process the remaining unprocessed buffer associated with the thread that is about to die.
 * 
 * @author igormoreno
 * 
 */
public class Agent {

    private static final Method activateBypass;
    private static final Method deactivateBypass;

    static {
        Method found1 = null, found2 = null;

        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("ch.usi.dag.disl.dynamicbypass.DynamicBypass");
            found1 = clazz.getMethod("activate", new Class[0]);
            found2 = clazz.getMethod("deactivate", new Class[0]);
        } catch(Throwable t) { /* exception handler intentionally left empty */ }

        activateBypass = found1;
        deactivateBypass = found2;
    }

    /**
     * To be called on thread's death so it can process its remaining buffers.
     */
    public static void onThreadDeath() {
        if(activateBypass != null) {
            try { activateBypass.invoke(null, new Object[0]); } catch (Throwable t) { t.printStackTrace(); }
        }

        DeferredExecution.processRemainingBuffers();

        if(deactivateBypass != null) {
            try { deactivateBypass.invoke(null, new Object[0]); } catch (Throwable t) { t.printStackTrace(); }
        }
    }

    /**
     * To be called on thread's death so it can process its remaining buffers.
     */
    public static void onVMDeath() {
//    	System.out.println("I am dying!!!!!!");
//    	for(Thread t : ThreadPoolProc.workers) {
//    		System.out.println(t.getName() + "\t" + t.isAlive());
//    	}
    	DeferredExecution.stopProcessors();
    	System.out.println("FINAL COUNTER VALUE: " + Example.counter.get());
    }
}
