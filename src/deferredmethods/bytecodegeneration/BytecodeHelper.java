package deferredmethods.bytecodegeneration;

import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BytecodeHelper {
    public static void saveClassToFile(byte[] b, String className) throws IOException {
        FileOutputStream fos = new FileOutputStream(className);
        fos.write(b);
        fos.close();
    }

    public static int getTypeOpcode(Type type) {
        switch (type.getSort()) {
        case Type.BYTE:
            return Opcodes.T_BYTE;
        case Type.BOOLEAN:
            return Opcodes.T_BOOLEAN;
        case Type.CHAR:
            return Opcodes.T_CHAR;
        case Type.DOUBLE:
            return Opcodes.T_DOUBLE;
        case Type.FLOAT:
            return Opcodes.T_FLOAT;
        case Type.INT:
            return Opcodes.T_INT;
        case Type.LONG:
            return Opcodes.T_LONG;
        case Type.SHORT:
            return Opcodes.T_SHORT;
        default:
            return -1;
        }
    }
    
    public static String getDescriptor(Object[] objs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length; i++) {
            Class<?> c = objs[i].getClass();
            sb.append(Type.getDescriptor(c));
        }
        return sb.toString();
    }
}
