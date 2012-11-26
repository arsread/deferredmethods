package deferredmethods.bytecodegeneration;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ThreadLocalBufferGenerator extends ClassGenerator {

    private static final String THREAD_LOCAL_BUFFER_SUPERCLASS = "deferredmethods/ThreadLocalBuffer";

    private static final String DEFAULT_THREAD_LOCAL_BUFFER_CLASSNAME = "deferredmethods/GeneratedThreadLocalBuffer";

    private BufferGenerator bufferGenerator;
    private DeferredEnvGenerator deferredEnvGenerator;
    private Object[] args;

    public ThreadLocalBufferGenerator(int id) {
        super(DEFAULT_THREAD_LOCAL_BUFFER_CLASSNAME + id);
    }

    public byte[] generateBytecode(BufferGenerator bufferGenerator, DeferredEnvGenerator deferredEnvGenerator,
            Object... args) {
        this.bufferGenerator = bufferGenerator;
        this.deferredEnvGenerator = deferredEnvGenerator;
        this.args = args;

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        String signature = "L" + THREAD_LOCAL_BUFFER_SUPERCLASS + "<" + bufferGenerator.getClassDescriptor() + ">;";
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, getClassName(), signature,
                THREAD_LOCAL_BUFFER_SUPERCLASS, null);

        threadLocalBufferFields(cw);

        threadLocalBufferConstructor(cw);

        threadLocalBufferInitialValue(cw);

        cw.visitEnd();

        return cw.toByteArray();
    }

    private void threadLocalBufferFields(ClassWriter cw) {
        FieldVisitor fv;

        fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "generatedDeferredEnv",
                deferredEnvGenerator.getClassDescriptor(), null, null);
        fv.visitEnd();

        for (int i = 0; i < args.length; i++) {
            fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "arg" + i,
                    Type.getDescriptor(args[i].getClass()), null, null);
            fv.visitEnd();
        }
    }

    private void threadLocalBufferInitialValue(ClassWriter cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(Opcodes.ACC_PROTECTED, "initialValue", "()" + bufferGenerator.getClassDescriptor(), null,
                null);

        mv.visitTypeInsn(Opcodes.NEW, bufferGenerator.getClassName());
        mv.visitInsn(Opcodes.DUP);
        
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(), "getBufferID", "(J)I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "generatedDeferredEnv",
                deferredEnvGenerator.getClassDescriptor());
        for (int i = 0; i < args.length; i++) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "arg" + i, Type.getDescriptor(args[i].getClass()));
        }
        String descriptor = "(I" + deferredEnvGenerator.getClassDescriptor() + BytecodeHelper.getDescriptor(args) + ")V";
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, bufferGenerator.getClassName(), "<init>", descriptor);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PROTECTED + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "initialValue",
                "()Ljava/lang/Object;", null, null);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(), "initialValue",
                "()" + bufferGenerator.getClassDescriptor());
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void threadLocalBufferConstructor(ClassWriter cw) {
        MethodVisitor mv;

        String descriptor = "(" + deferredEnvGenerator.getClassDescriptor() + BytecodeHelper.getDescriptor(args) + ")V";
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", descriptor, null, null);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, THREAD_LOCAL_BUFFER_SUPERCLASS, "<init>", "(Ldeferredmethods/GeneralDeferredEnv;)V");

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "generatedDeferredEnv",
                deferredEnvGenerator.getClassDescriptor());

        for (int i = 0; i < args.length; i++) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, i + 2);
            mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "arg" + i, Type.getDescriptor(args[i].getClass()));
        }

        mv.visitInsn(Opcodes.RETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
