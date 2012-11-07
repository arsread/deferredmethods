package deferredmethods.bytecodegeneration.extendthread;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import deferredmethods.Constants;

public final class ExtendThread {

    private static final String THREAD_BIN_DIR = "./bin-thread/";

    public static void main(String[] args) throws Exception {

        String threadClassName = "java/lang/Thread.class";

        // get thread class as resource
        InputStream tis = ClassLoader.getSystemResourceAsStream(threadClassName);

        // parse Thread in ASM
        ClassReader cr = new ClassReader(tis);
        ClassWriter cw = new ClassWriter(0);

        ClassVisitor cv = new ClassVisitor(Opcodes.V1_6, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ("<init>".equals(name)) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.BIPUSH, Constants.DEFAULT_NUM_THREADLOCALBUFFERS);
                    mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
                    mv.visitFieldInsn(Opcodes.PUTFIELD, "java/lang/Thread", "threadLocalBuffer", "[Ljava/lang/Object;");
                }

                return mv;
            }

            @Override
            public void visit(int version, int access, String name, String signature, String superName,
                    String[] interfaces) {
                super.visit(version, access, name, signature, superName, interfaces);
                super.visitField(Opcodes.ACC_PUBLIC, "threadLocalBuffer", "[Ljava/lang/Object;", null, null);
            }
        };

        cr.accept(cv, 0);

        // output Thread code into special thread bin directory
        write(THREAD_BIN_DIR + threadClassName, cw.toByteArray());
    }

    private static void write(String outputFile, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(data);
        fos.close();
    }
}
