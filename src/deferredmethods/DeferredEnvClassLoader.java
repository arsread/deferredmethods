package deferredmethods;

import java.util.Map;

/**
 * Class loader used to load classes who's bytecode is in raw byte array format.
 * The classes to be loaded are passed to the constructor as a Map from class
 * name to an array of bytes containing the class bytecode.
 * 
 * @author igormoreno
 * 
 */
public class DeferredEnvClassLoader extends ClassLoader {

    private final Map<String, byte[]> mapClassnameToBytecode;

    /**
     * Creates an instance of the class loader that is able to load the classes
     * in the mapping.
     * 
     * @param mapClassnameToBytecode
     *            Mapping from class name to an array of bytes containing the
     *            class bytecode.
     */
    public DeferredEnvClassLoader(Map<String, byte[]> mapClassnameToBytecode) {
        super();
        this.mapClassnameToBytecode = mapClassnameToBytecode;
    }

    /**
     * Creates an instance of the class loader that is able to load the classes
     * in the mapping.
     * 
     * @param mapClassnameToBytecode
     *            Mapping from class name to an array of bytes containing the
     *            class bytecode.
     * @param classLoader
     *            Class loader used as parent.
     */
    public DeferredEnvClassLoader(Map<String, byte[]> mapClassnameToBytecode, ClassLoader classLoader) {
        super(classLoader);
        this.mapClassnameToBytecode = mapClassnameToBytecode;
    }

    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (mapClassnameToBytecode.containsKey(className)) {
            try {
                return loadFromBytecode(className, mapClassnameToBytecode.get(className));
            } catch (Exception e) {
                throw new ClassNotFoundException(e.getMessage(), e);
            }
        } else {
            return super.findClass(className);
        }
    }

    /**
     * Load one of the classes given in the constructor.
     * 
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> loadFromMap(String className) throws ClassNotFoundException {
        if (mapClassnameToBytecode.containsKey(className)) {
            return loadFromBytecode(className, mapClassnameToBytecode.get(className));
        } else {
            throw new ClassNotFoundException(className + " not found!");
        }
    }

    private Class<?> loadFromBytecode(String className, byte[] bytecode) {
        return defineClass(className, bytecode, 0, bytecode.length);
    }

}
