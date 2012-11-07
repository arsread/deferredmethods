package test.deferredmethods.bytecodegeneration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.Counter;
import test.CrazyInterface;
import test.Task;
import test.TaskImplWithMultipleConstructors;
import deferredmethods.bytecodegeneration.Argument;
import deferredmethods.bytecodegeneration.DeferredMethodsReader;
import deferredmethods.bytecodegeneration.Method;

public class DeferredInterfaceReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMethodList() throws Exception {
        // set up
        DeferredMethodsReader reader = new DeferredMethodsReader(CrazyInterface.class, CrazyInterface.class);

        // exercise
        List<Method> methodList = reader.getMethodList();

        // assert
        /*
         * public void m1(String s1, String s2, String s3, Object[] o1); //
         * public String m2(String s1, String s2, Object[] o1, Object[] o2); //
         * public void m3(int a, int[][] c);
         */
        assertEquals(3, methodList.size());
        assertEquals("m1", methodList.get(0).getName());
        assertEquals("m2", methodList.get(1).getName());
        assertEquals("m3", methodList.get(2).getName());
    }

    @Test
    public void testGetArgListFromMethodList() throws Exception {
        // set up
        DeferredMethodsReader reader = new DeferredMethodsReader(CrazyInterface.class, CrazyInterface.class);

        // exercise
        List<Method> methodList = reader.getMethodList();
        Argument[] argsMethod1 = methodList.get(0).getArgs();
        Argument[] argsMethod2 = methodList.get(1).getArgs();
        Argument[] argsMethod3 = methodList.get(2).getArgs();

        // assert
        /*
         * public void m1(String s1, String s2, String s3, Object[] o1); //
         * public String m2(String s1, String s2, Object[] o1, Object[] o2); //
         * public void m3(int a, int[][] c);
         */
        assertEquals("java_lang_String1", argsMethod1[0].getName());
        assertEquals("java_lang_String2", argsMethod1[1].getName());
        assertEquals("java_lang_String3", argsMethod1[2].getName());
        assertEquals("java_lang_Object_array1", argsMethod1[3].getName());

        assertEquals("java_lang_String1", argsMethod2[0].getName());
        assertEquals("java_lang_String2", argsMethod2[1].getName());
        assertEquals("java_lang_Object_array1", argsMethod2[2].getName());
        assertEquals("java_lang_Object_array2", argsMethod2[3].getName());

        assertEquals("int1", argsMethod3[0].getName());
        assertEquals("int_array_array1", argsMethod3[1].getName());
    }

    @Test
    public void testArgumentList() throws Exception {
        // set up
        DeferredMethodsReader reader = new DeferredMethodsReader(CrazyInterface.class, CrazyInterface.class);

        // exercise
        List<Argument> argList = reader.getArgList();
        List<String> names = new ArrayList<String>();
        for (Argument argument : argList) {
            names.add(argument.getName());
        }
        String[] expectedNames = { "java_lang_String1", "java_lang_String2", "java_lang_String3",
                "java_lang_Object_array1", "java_lang_Object_array2", "int1", "int_array_array1", };

        // assert
        /*
         * public void m1(String s1, String s2, String s3, Object[] o1); //
         * public String m2(String s1, String s2, Object[] o1, Object[] o2); //
         * public void m3(int a, int[][] c);
         */
        assertEquals(7, argList.size());
        assertEquals(Arrays.asList(expectedNames), names);
    }

    @Test
    public void testValidationWithValidArgs() throws Exception {
        // set up
        DeferredMethodsReader reader = new DeferredMethodsReader(Task.class, TaskImplWithMultipleConstructors.class);

        // exercise
        reader.validate(new Counter());
        reader.validate(new Counter(), new String());

        // should not throw exception
    }

    @Test(expected = NoSuchMethodException.class)
    public void testValidationWithInvalidArgs() throws Exception {
        // set up
        DeferredMethodsReader reader = new DeferredMethodsReader(Task.class, TaskImplWithMultipleConstructors.class);

        // exercise
        reader.validate();
    }
}
