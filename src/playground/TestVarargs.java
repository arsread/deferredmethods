package playground;

import java.lang.reflect.Constructor;

public class TestVarargs {
    public static void main(String[] args) throws Exception {
        test("hoi", 1);
    }
    
    private static void test(String s, Object... args) throws Exception {

        Class<?>[] classes = new Class<?>[args.length+1];
        classes[0] = String.class;
        for (int i = 0; i < args.length; i++) {
            classes[i+1] = args[i].getClass();
        }
        Object[] fullArgs = new Object[args.length+1];
        fullArgs[0] = s;
        for (int i = 0; i < args.length; i++) {
            fullArgs[i+1] = args[i];
        }
        Constructor<?> c = Stuff.class.getConstructor(classes);
        Stuff stuff = (Stuff) c.newInstance(fullArgs);
    }
}

class Stuff {
    public Stuff() {
        System.out.println("nothing");
    }

    public Stuff(String str) {
        System.out.println(str);
    }

    public Stuff(Integer i) {
        System.out.println(i);
    }

    public Stuff(String s, Integer i) {
        System.out.println(s + " plus " + i);
    }
}