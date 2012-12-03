package deferredmethods;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import deferredmethods.bytecodegeneration.BufferGenerator;
import deferredmethods.bytecodegeneration.DeferredEnvGenerator;
import deferredmethods.bytecodegeneration.DeferredMethodsReader;
import deferredmethods.bytecodegeneration.ThreadLocalBufferGenerator;
import deferredmethods.proc.Processor;

/**
 * Class with static methods used to create deferred environments.
 * 
 * @author igormoreno
 * 
 */
public class DeferredExecution {

//    private static final int DEFAULT_BUFFER_SIZE = 100;
    private static final AtomicInteger id = new AtomicInteger(0);

    // TODO use weak references
    private static ConcurrentLinkedQueue<GeneralDeferredEnv<?>> allDeferredEnvs = new ConcurrentLinkedQueue<GeneralDeferredEnv<?>>();

//    /**
//     * Creates an instance of the deferred environment.
//     * 
//     * @param userDefIntf
//     *            Class that implements the methods to be deferred.
//     * @param userDefImpl
//     *            Interface defining the methods to be deferred.
//     * @param proc
//     *            Processor used to process the buffers. That is to execute the
//     *            deferred methods.
//     * @param args
//     *            Arguments passed to the constructor of the class that
//     *            implements the methods to be deferred (userDefIntf).
//     * @return An Instance of the deferred environment.
//     * @throws IOException
//     */
//    public static <T extends Deferred> DeferredEnv<T> createDeferredEnv(Class<T> userDefIntf,
//            Class<? extends T> userDefImpl, Processor proc, Object... args) throws IllegalArgumentException {
//        return createDeferredEnv(userDefIntf, userDefImpl, proc, DEFAULT_BUFFER_SIZE,
//                ClassLoader.getSystemClassLoader(), args);
//    }
//
//    /**
//     * Creates an instance of the deferred environment.
//     * 
//     * @param userDefIntf
//     *            Class that implements the methods to be deferred.
//     * @param userDefImpl
//     *            Interface defining the methods to be deferred.
//     * @param proc
//     *            Processor used to process the buffers. That is to execute the
//     *            deferred methods.
//     * @param classLoader
//     *            Class loader used to load the dynamically generated classes.
//     * @param args
//     *            Arguments passed to the constructor of the class that
//     *            implements the methods to be deferred (userDefIntf).
//     * @return An Instance of the deferred environment.
//     * @throws IOException
//     */
//    private static <T extends Deferred> DeferredEnv<T> createDeferredEnv(Class<T> userDefIntf,
//            Class<? extends T> userDefImpl, Processor proc, ClassLoader classLoader, Object... args)
//            throws IllegalArgumentException {
//        return createDeferredEnv(userDefIntf, userDefImpl, proc, DEFAULT_BUFFER_SIZE, classLoader, args);
//    }

    /**
     * Creates an instance of the deferred environment.
     * 
     * @param userDefIntf
     *            Class that implements the methods to be deferred.
     * @param userDefImpl
     *            Interface defining the methods to be deferred.
     * @param proc
     *            Processor used to process the buffers. That is to execute the
     *            deferred methods.
     * @param bufferSize
     *            Initial buffer capacity.
     * @param args
     *            Arguments passed to the constructor of the class that
     *            implements the methods to be deferred (userDefIntf).
     * @return An Instance of the deferred environment.
     * @throws IOException
     */
    public static <T extends Deferred> DeferredEnv<T> createDeferredEnv(Class<T> userDefIntf,
            Class<? extends T> userDefImpl, Processor proc, int bufferSize, Object... args)
            throws IllegalArgumentException {
        return createDeferredEnv(userDefIntf, userDefImpl, proc, bufferSize, ClassLoader.getSystemClassLoader(), args);
    }

    /**
     * Creates an instance of the deferred environment.
     * 
     * @param userDefIntf
     *            Class that implements the methods to be deferred.
     * @param userDefImpl
     *            Interface defining the methods to be deferred.
     * @param proc
     *            Processor used to process the buffers. That is to execute the
     *            deferred methods.
     * @param bufferSize
     *            Initial buffer capacity.
     * @param classLoader
     *            Class loader used to load the dynamically generated classes.
     * @param args
     *            Arguments passed to the constructor of the class that
     *            implements the methods to be deferred (userDefIntf).
     * @return An Instance of the deferred environment.
     * @throws IOException
     */
    private static <T extends Deferred> DeferredEnv<T> createDeferredEnv(Class<T> userDefIntf,
            Class<? extends T> userDefImpl, Processor proc, int bufferSize, ClassLoader classLoader, Object... args)
            throws IllegalArgumentException {

        DeferredMethodsReader interfaceReader;
        try {
            interfaceReader = new DeferredMethodsReader(userDefIntf, userDefImpl);
            interfaceReader.validate(args);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.toString());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.toString());
        }

        int id = getId();

        DeferredEnvGenerator deferredEnvGenerator = new DeferredEnvGenerator(id);
        ThreadLocalBufferGenerator threadLocalBufferGenerator = new ThreadLocalBufferGenerator(id);
        BufferGenerator bufferGenerator = new BufferGenerator(id);

        byte[] deferredEnvBytecode = deferredEnvGenerator.generateBytecode(bufferGenerator, threadLocalBufferGenerator,
                interfaceReader, args);
        byte[] threadLocalBufferBytecode = threadLocalBufferGenerator.generateBytecode(bufferGenerator,
                deferredEnvGenerator, args);
        byte[] bufferBytecode = bufferGenerator.generateBytecode(interfaceReader, deferredEnvGenerator, args);
        // TODO delete this
        // BytecodeHelper.saveClassToFile(bufferBytecode,
        // "GeneratedBuffer.class");
        // if (id == 2) {
        // BytecodeHelper.saveClassToFile(deferredEnvBytecode,
        // "GeneratedDeferredEnv.class");
        // }
        // BytecodeHelper.saveClassToFile(threadLocalBufferBytecode,
        // "GeneratedThreadLocalBuffer.class");

        Map<String, byte[]> mapClassnameToBytecode = new HashMap<String, byte[]>();
        mapClassnameToBytecode.put(deferredEnvGenerator.getClassNameWithDots(), deferredEnvBytecode);
        mapClassnameToBytecode.put(threadLocalBufferGenerator.getClassNameWithDots(), threadLocalBufferBytecode);
        mapClassnameToBytecode.put(bufferGenerator.getClassNameWithDots(), bufferBytecode);

        DeferredEnvClassLoader defEnvClassLoader = new DeferredEnvClassLoader(mapClassnameToBytecode, classLoader);
        Class<GeneralDeferredEnv<T>> generatedDeferredEnvClass = null;
        try {
            generatedDeferredEnvClass = (Class<GeneralDeferredEnv<T>>) defEnvClassLoader.loadFromMap(deferredEnvGenerator
                    .getClassNameWithDots());
        } catch (ClassNotFoundException e) {
            throw new InternalError(e.toString());
        }

        GeneralDeferredEnv<T> deferredEnv = null;
        try {
            deferredEnv = newDeferredEnv(generatedDeferredEnvClass, proc, bufferSize, args);
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString());
        } catch (IllegalArgumentException e) {
            throw new InternalError(e.toString());
        } catch (InstantiationException e) {
            throw new InternalError(e.toString());
        } catch (IllegalAccessException e) {
            throw new InternalError(e.toString());
        } catch (InvocationTargetException e) {
            throw new InternalError(e.toString());
        }

        // cast necessary so we dont need to put getId in the interface
        allDeferredEnvs.add(deferredEnv);

        return deferredEnv;
    }

    private static <T extends Deferred> GeneralDeferredEnv<T> newDeferredEnv(Class<GeneralDeferredEnv<T>> deferredEnvClass,
            Processor proc, int bufferSize, Object... args) throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        // processor and bufferSize
        int numOfExtraArgs = 2;

        Class<?>[] classes = new Class<?>[args.length + numOfExtraArgs];
        classes[0] = Processor.class;
        classes[1] = int.class;
        for (int i = 0; i < args.length; i++) {
            classes[i + numOfExtraArgs] = args[i].getClass();
        }

        Object[] fullArgs = new Object[args.length + numOfExtraArgs];
        fullArgs[0] = proc;
        fullArgs[1] = bufferSize;
        for (int i = 0; i < args.length; i++) {
            fullArgs[i + numOfExtraArgs] = args[i];
        }

        Constructor<GeneralDeferredEnv<T>> c = deferredEnvClass.getConstructor(classes);
        GeneralDeferredEnv<T> deferredEnv = c.newInstance(fullArgs);
        return deferredEnv;
    }

    /**
     * Force immediate processing of all currently unprocessed buffers created
     * for the current thread among all existing deferred environments.
     */
    public static void processRemainingBuffers() {
        Object[] threadLocal = Thread.currentThread().threadLocalBuffer;
        for (GeneralDeferredEnv<?> deferredEnv : allDeferredEnvs) {
            if(threadLocal[deferredEnv.getId()] != null) {
                deferredEnv.processCurrentBuffer();
            }
        }
    }

    public static void stopProcessors() {
    	for(GeneralDeferredEnv<?> deferredEnv : allDeferredEnvs) {
    		deferredEnv.proc.stop();
    	}
    }

    private static int getId() {
        return id.getAndIncrement();
    }

	public static void stopWorkerThreads() {
	       for (GeneralDeferredEnv<?> deferredEnv : allDeferredEnvs) {
	    	   deferredEnv.proc.producerThreadDied(Thread.currentThread());
            }
	}

	public static void setEndFlags() {
		Thread thread = Thread.currentThread();
	   	for(GeneralDeferredEnv<?> deferredEnv : allDeferredEnvs) {
    		deferredEnv.setEnd(thread);
    	}
	}

}
