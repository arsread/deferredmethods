package playground.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ChangeThread implements ClassFileTransformer {
    // transformer interface implementation
    public byte[] transform(ClassLoader loader, String cname, Class<?> clazz, ProtectionDomain domain, byte[] bytes)
            throws IllegalClassFormatException {
        System.out.println("Processing class " + cname);
        return null;
    }

    // Required method for instrumentation agent.
    public static void premain(String arglist, Instrumentation inst) {
        inst.addTransformer(new ChangeThread());
    }
}
