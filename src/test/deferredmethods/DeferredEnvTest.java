//package test.deferredmethods;
//
//import static org.junit.Assert.assertEquals;
//
//import java.lang.reflect.Constructor;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import test.Counter;
//import test.DefMethods;
//import test.DefMethodsImpl;
//import test.MultipleCounter;
//import test.Task;
//import test.TaskImpl;
//import test.TaskImplWithSingleton;
//import test.TaskUser;
//import test.TaskWithArrayTypes;
//import test.TaskWithArrayTypesImpl;
//import test.TaskWithCoalescingImpl;
//import test.TaskWithInstanceVar;
//import test.TaskWithMultipleParams;
//import test.TaskWithMultipleParamsImpl;
//import test.TaskWithPrimitiveTypes;
//import test.TaskWithPrimitiveTypesImpl;
//
//import deferredmethods.DeferredEnv;
//import deferredmethods.DeferredEnvClassLoader;
//import deferredmethods.DeferredExecution;
//import deferredmethods.bytecodegeneration.BufferGenerator;
//import deferredmethods.bytecodegeneration.DeferredMethodsReader;
//import deferredmethods.proc.AdaptiveProc;
//import deferredmethods.proc.Processor;
//import deferredmethods.proc.SynchronousProc;
//
//public class DeferredEnvTest {
//
//    @Before
//    public void setUp() throws Exception {
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testDeferredEnvWithNoParam() throws Exception {
//        // set up
//        Counter counter = new Counter();
//        Processor proc = new AdaptiveProc();
//        DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class, TaskImpl.class,
//                proc, counter);
//        Task task = deferredEnv.getProxy();
//
//        // exercise
//        for (int i = 0; i < 999; i++) {
//            task.doSomething();
//        }
//
//        deferredEnv.processCurrentBuffer();
//        proc.stop();
//        // assert
//        assertEquals(999, counter.getValue());
//        // assertEquals(1000, CounterSingletonWrapper.getCounter().getValue());
//    }
//
//    @Test
//    public void testProcessEmptyBuffer() throws Exception {
//        // set up
//        DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class, TaskImplWithSingleton.class,
//                new SynchronousProc());
//
//        // exercise
//        deferredEnv.processCurrentBuffer();
//
//        // assert
//        // it shouldn't break
//    }
//
//    @Test
//    public void testDeferredEnvWithInstanceVar() throws Exception {
//        // set up
//        Counter counter = new Counter();
//        DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class, TaskWithInstanceVar.class,
//                new SynchronousProc(), counter);
//        Task task = deferredEnv.getProxy();
//
//        // exercise
//        // task.setCounter(counter);
//        for (int i = 0; i < 100; i++) {
//            task.doSomething();
//        }
//        deferredEnv.processCurrentBuffer();
//
//        // assert
//        assertEquals(100, counter.getValue());
//    }
//
//    @Test
//    public void testDeferredEnvWithMultipleParams() throws Exception {
//        // set up
//        DeferredEnv<TaskWithMultipleParams> deferredEnv = DeferredExecution.createDeferredEnv(
//                TaskWithMultipleParams.class, TaskWithMultipleParamsImpl.class, new SynchronousProc());
//        TaskWithMultipleParams task = deferredEnv.getProxy();
//        Counter counter = new Counter();
//
//        // exercise
//        task.setCounter(counter);
//        for (int i = 0; i < 1000; i++) {
//            task.doSomething(1, new Integer(1));
//        }
//        deferredEnv.processCurrentBuffer();
//
//        // assert
//        assertEquals(2000, counter.getValue());
//    }
//
//    @Test
//    public void testDeferredEnvWithPrimitiveTypes() throws Exception {
//        // set up
//        DeferredEnv<TaskWithPrimitiveTypes> deferredEnv = DeferredExecution.createDeferredEnv(
//                TaskWithPrimitiveTypes.class, TaskWithPrimitiveTypesImpl.class, new SynchronousProc());
//        TaskWithPrimitiveTypes task = deferredEnv.getProxy();
//        MultipleCounter counter = new MultipleCounter();
//        int i = 1;
//        byte b = 1;
//        long l = 1;
//        float f = 1;
//        double d = 1;
//        short s = 1;
//        char c = 1;
//        boolean bool = true;
//
//        // exercise
//        task.setCounter(counter);
//        for (int j = 0; j < 1000; j++) {
//            task.doSomething(i, b, l, f, d, s, c, bool);
//        }
//        deferredEnv.processCurrentBuffer();
//
//        // assert
//        assertEquals(1000, counter.getNumint());
//        assertEquals(1000, counter.getNumboolean());
//        assertEquals(1000, counter.getNumbyte());
//        assertEquals(1000, counter.getNumchar());
//        assertEquals(1000, counter.getNumdouble());
//        assertEquals(1000, counter.getNumfloat());
//        assertEquals(1000, counter.getNumlong());
//        assertEquals(1000, counter.getNumshort());
//    }
//
//    @Test
//    public void testDeferredEnvWithArrayTypes() throws Exception {
//        // set up
//        DeferredEnv<TaskWithArrayTypes> deferredEnv = DeferredExecution.createDeferredEnv(TaskWithArrayTypes.class,
//                TaskWithArrayTypesImpl.class, new SynchronousProc());
//        TaskWithArrayTypes task = deferredEnv.getProxy();
//        Counter counter = new Counter();
//        int[] i = { 1 };
//        int[][] j = { { 1 } };
//        Integer[] k = { 1 };
//        Integer[][] l = { { 1 } };
//
//        // exercise
//        task.setCounter(counter);
//        for (int c = 0; c < 1000; c++) {
//            task.doSomething(i, j, k, l);
//        }
//        deferredEnv.processCurrentBuffer();
//
//        // assert
//        assertEquals(4000, counter.getValue());
//    }
//
//    @Test
//    public void testDeferredEnvComparingWithNonDeferred() throws Exception {
//        // set up
//        Counter counterForDeferredTask = new Counter();
//        DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class, TaskImpl.class,
//                new SynchronousProc(), counterForDeferredTask);
//        Task deferredTask = deferredEnv.getProxy();
//
//        Counter counterForNonDeferredTask = new Counter();
//        Task nonDeferredTask = new TaskImpl(counterForNonDeferredTask);
//
//        TaskUser userOfDeferredTask = new TaskUser();
//        userOfDeferredTask.setTask(deferredTask);
//
//        TaskUser userOfNonDeferredTask = new TaskUser();
//        userOfNonDeferredTask.setTask(nonDeferredTask);
//
//        // exercise
//        for (int c = 0; c < 1001; c++) {
//            userOfDeferredTask.usingTask();
//            userOfNonDeferredTask.usingTask();
//        }
//        deferredEnv.processCurrentBuffer();
//
//        // assert
//        assertEquals(counterForNonDeferredTask.getValue(), counterForDeferredTask.getValue());
//    }
//
//    @Test
//    public void testDeferredEnvForArrayOptimization() throws Exception {
//        // set up
//        Class<?> userDefIntf = DefMethods.class;
//        Class<?> userDefImpl = DefMethodsImpl.class;
//
//        DeferredMethodsReader interfaceReader = new DeferredMethodsReader(userDefIntf, userDefImpl);
//
//        BufferGenerator bufferGenerator = new BufferGenerator(0);
//
//        byte[] bufferBytecode = bufferGenerator.generateBytecode(interfaceReader);
//
//        Map<String, byte[]> mapClassnameToBytecode = new HashMap<String, byte[]>();
//        mapClassnameToBytecode.put(bufferGenerator.getClassNameWithDots(), bufferBytecode);
//
//        DeferredEnvClassLoader classLoader = new DeferredEnvClassLoader(mapClassnameToBytecode);
//        Class<?> generatedBufferClass = classLoader.loadFromMap(bufferGenerator.getClassNameWithDots());
//
//        // exercise
//        Constructor<?> c = generatedBufferClass.getConstructor(int.class);
//        DefMethods buffer = (DefMethods) c.newInstance(1);
//
//        // System.out.println(buffer.getClass());
//        // System.out.println(Arrays.asList(buffer.getClass().getDeclaredFields()).toString().replace(',',
//        // '\n'));
//
//        // assert
//        /*
//         * 1 (deferredMethodID) +1 (currentPos) +2 (method ids) +3 (optimized
//         * arrays)
//         */
//        assertEquals(7, buffer.getClass().getDeclaredFields().length);
//    }
//
//    @Test
//    public void testDeferredEnvWithCoalescing() throws Exception {
//        // set up
//        Counter counter = new Counter();
//        Counter counter2 = new Counter();
//        final DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class,
//                TaskWithCoalescingImpl.class, new SynchronousProc(), counter);
//        final Task deferredTask = deferredEnv.getProxy();
//        final DeferredEnv<Task> deferredEnv2 = DeferredExecution.createDeferredEnv(Task.class,
//                TaskWithCoalescingImpl.class, new SynchronousProc(), counter2);
//        final Task deferredTask2 = deferredEnv2.getProxy();
//
//        final int iterations = 1001;
//        final int iterations2 = 2001;
//        class MyThread extends Thread {
//            @Override
//            public void run() {
//                TaskUser userOfDeferredTask = new TaskUser();
//                userOfDeferredTask.setTask(deferredTask);
//                TaskUser userOfDeferredTask2 = new TaskUser();
//                userOfDeferredTask2.setTask(deferredTask2);
//
//                for (int c = 0; c < iterations; c++) {
//                    userOfDeferredTask.usingTask();
//                }
//                for (int c = 0; c < iterations2; c++) {
//                    userOfDeferredTask2.usingTask();
//                }
//                // deferredEnv.processCurrentBuffer();
//                // deferredEnv2.processCurrentBuffer();
//            }
//        }
//        ;
//
//        // exercise
//        int numthreads = 4;
//        Thread[] threads = new Thread[numthreads];
//        for (int i = 0; i < numthreads; i++) {
//            threads[i] = new MyThread();
//            threads[i].start();
//        }
//        for (int i = 0; i < numthreads; i++) {
//            threads[i].join();
//        }
//
//        // assert
//        assertEquals(numthreads * iterations, counter.getValue());
//        assertEquals(numthreads * iterations2, counter2.getValue());
//    }
//
//    @Test
//    public void testDeferredEnvWithVariableBufferSize() throws Exception {
//        // set up
//        int bufferSize = 100;
//        Counter counter = new Counter();
//        DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class, TaskWithInstanceVar.class,
//                new SynchronousProc(), bufferSize, counter);
//        Task task = deferredEnv.getProxy();
//
//        // exercise
//        task.doSomething();
//        deferredEnv.setBufferCapacity(10);
//        for (int i = 0; i < 99; i++) {
//            task.doSomething();
//        }
//        assertEquals(100, counter.getValue());
//
//        for (int i = 0; i < 10; i++) {
//            task.doSomething();
//        }
//
//        // assert
//        assertEquals(110, counter.getValue());
//    }
//
//    @Test
//    public void testDeferredEnvWithClassLoader() throws Exception {
//        // set up
//        Counter counter = new Counter();
//        DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class, TaskWithInstanceVar.class,
//                new SynchronousProc(), ClassLoader.getSystemClassLoader(), counter);
//        Task task = deferredEnv.getProxy();
//
//        // exercise
//        for (int i = 0; i < 1000; i++) {
//            task.doSomething();
//        }
//
//        // assert
//        assertEquals(1000, counter.getValue());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testDeferredEnvWithWrongArgs() throws Exception {
//        // set up
//        // Counter counter = new Counter();
//        DeferredEnv<Task> deferredEnv = DeferredExecution.createDeferredEnv(Task.class, TaskWithInstanceVar.class,
//                new SynchronousProc());
//
//        // expected exception
//    }
//
//    @Test
//    public void testDeferredEnvForMaximumNumOfEnv() throws Exception {
//        // set up
//        Counter counter = new Counter();
//
//        // exercise
//        for (int i = 0; i < 1000; i++) {
//            DeferredExecution.createDeferredEnv(Task.class, TaskWithInstanceVar.class, new SynchronousProc(), counter);
//        }
//
//        // assert
//    }
//}
