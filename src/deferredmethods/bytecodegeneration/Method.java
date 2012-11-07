package deferredmethods.bytecodegeneration;

import java.util.Arrays;

import org.objectweb.asm.Type;

public class Method {
    private final String name;
    private final String appendName;
    private final String desc;
    private final String appendDesc;
    private final String signature;
    private final String[] exceptions;
    private final Type[] types;
    private final Argument[] args;

    public Method(String name, String desc, String signature, String[] exceptions, Argument[] args) {
        this.name = name;
        this.appendName = "append" + name.substring(0, 1).toUpperCase() + name.substring(1);
        this.desc = desc;
        this.appendDesc = desc.substring(0, desc.length() - 1) + "Z";
        this.signature = signature;
        this.exceptions = exceptions;
        this.types = Type.getArgumentTypes(desc);
        this.args = args;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public String getAppendName() {
        return appendName;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    public String getAppendDesc() {
        return appendDesc;
    }

    public Type[] getTypes() {
        return types;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @return the exceptions
     */
    public String[] getExceptions() {
        return exceptions;
    }

    public Argument[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return String
                .format("Method [name=%s, appendName=%s, desc=%s, appendDesc=%s, signature=%s, exceptions=%s, types=%s, args=%s]",
                        name, appendName, desc, appendDesc, signature, Arrays.toString(exceptions),
                        Arrays.toString(types), Arrays.toString(args));
    }

}