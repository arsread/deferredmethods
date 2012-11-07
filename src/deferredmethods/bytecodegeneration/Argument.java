package deferredmethods.bytecodegeneration;

import org.objectweb.asm.Type;

public class Argument {

    private final String name;
    private final Type type;

    public Argument(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("Argument [name=%s, type=%s]", name, type);
    }

}
