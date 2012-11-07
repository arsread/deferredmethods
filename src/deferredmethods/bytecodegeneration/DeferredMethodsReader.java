package deferredmethods.bytecodegeneration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import deferredmethods.ProcessingHooks;

public class DeferredMethodsReader {

    private final List<Method> methodList;
    private final List<Argument> argList;
    private final String intfInternalName;
    private final String implInternalName;
    private final String intfDescriptor;
    private boolean hasProcessingHooks;
    private final List<String> implConstructorDescs;

    public DeferredMethodsReader(Class<?> userDefIntf, Class<?> userDefImpl) throws IOException {
        this.intfInternalName = userDefIntf.getName().replace('.', '/');
        this.implInternalName = userDefImpl.getName().replace('.', '/');
        this.intfDescriptor = "L" + intfInternalName + ";";

        this.methodList = new ArrayList<Method>();
        this.argList = new ArrayList<Argument>();
        this.hasProcessingHooks = false;

        this.implConstructorDescs = new ArrayList<String>();

        parseIntf(userDefIntf);
        parseImpl(userDefImpl);
    }

    public String getIntfInternalName() {
        return intfInternalName;
    }

    public String getIntfDescriptor() {
        return intfDescriptor;
    }

    public String getImplInternalName() {
        return implInternalName;
    }

    public List<Argument> getArgList() {
        return argList;
    }

    public List<Method> getMethodList() {
        return methodList;
    }

    public boolean hasProcessingHooks() {
        return hasProcessingHooks;
    }

    private static Argument buildArgFromType(Type t, int numOcurrences) {
        String argName = t.getClassName().replace('.', '_').replace("[]", "_array") + numOcurrences;
        return new Argument(argName, t);
    }

    private void parseIntf(Class<?> userDefIntf) throws IOException {
        final Map<Type, Integer> maxNumOccurrences = new HashMap<Type, Integer>();

        ClassVisitor cv = new ClassVisitor(Opcodes.V1_6) {
            // TODO refactor!!
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                Type[] types = Type.getArgumentTypes(desc);
                Argument[] args = new Argument[types.length];
                Map<Type, Integer> numOccurPerMethod = new HashMap<Type, Integer>();
                for (int i = 0; i < types.length; i++) {
                    Type t = types[i];
                    Integer numOccurrences = numOccurPerMethod.get(t);
                    Integer maxOccurrences = maxNumOccurrences.get(t);
                    if (numOccurrences == null) {
                        numOccurPerMethod.put(t, 1);
                        args[i] = buildArgFromType(t, 1);
                        if (maxOccurrences == null) {
                            maxNumOccurrences.put(t, 1);
                            argList.add(args[i]);
                        }
                    } else {
                        Integer plusOne = numOccurrences + 1;
                        numOccurPerMethod.put(t, plusOne);
                        args[i] = buildArgFromType(t, plusOne);
                        if (plusOne > maxOccurrences) {
                            maxNumOccurrences.put(t, plusOne);
                            argList.add(args[i]);
                        }
                    }
                }

                methodList.add(new Method(name, desc, signature, exceptions, args));

                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };

        ClassReader cr = new ClassReader(userDefIntf.getCanonicalName());
        cr.accept(cv, 0);
    }

    private void parseImpl(Class<?> userDefImpl) throws IOException {

        ClassVisitor cv = new ClassVisitor(Opcodes.V1_6) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName,
                    String[] interfaces) {

                for (String intf : interfaces) {
                    String processingHooksInternalName = ProcessingHooks.class.getCanonicalName().replace('.', '/');
                    if (intf.equals(processingHooksInternalName)) {
                        hasProcessingHooks = true;
                    }
                }

                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                if (name.equals("<init>")) {
                    implConstructorDescs.add(desc);
                }

                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };

        ClassReader cr = new ClassReader(userDefImpl.getCanonicalName());
        cr.accept(cv, 0);
    }

    public void validate(Object... args) throws NoSuchMethodException {
        // looking for this constructor
        String desiredConstructor = "(" + BytecodeHelper.getDescriptor(args) + ")V";
        for (String desc : implConstructorDescs) {
            if (desiredConstructor.equals(desc)) {
                /*
                 * found matching constructor. passed validation.
                 */
                return;
            }
        }
        throw new NoSuchMethodException("Constructor with arguments " + args + " not found");
    }
}
