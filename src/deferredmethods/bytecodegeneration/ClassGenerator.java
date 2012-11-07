package deferredmethods.bytecodegeneration;

public abstract class ClassGenerator {

    private final String className;
    private final String classNameWithDots;
    private final String classDescriptor;

    public ClassGenerator(String className) {
        this.className = className;
        this.classNameWithDots = className.replace('/', '.');
        this.classDescriptor = "L" + className + ";";
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getClassDescriptor() {
        return this.classDescriptor;
    }
    
    public String getClassNameWithDots() {
        // TODO find better name
        return this.classNameWithDots;
    }
    
    // TODO try to do this
//    abstract public byte[] generateBytecode();
    
}
