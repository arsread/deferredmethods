package deferredmethods.bytecodegeneration.extendlock;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class ExtendReentrantLock {

    private static final String LOCK_BIN_DIR = "./bin-lock/";

    public static void main(String[] args) throws Exception {
    	String[] classArray = {"java/util/concurrent/locks/ReentrantLock",
    			"java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock",
    			"java/util/concurrent/locks/ReentrantReadWriteLock$WriteLock"};
    	
//        String lockClassName = "java/util/concurrent/locks/ReentrantReadWriteLock$ReadLock";
        
    	for (String lockClassName:classArray){
        InputStream tis = ClassLoader.getSystemResourceAsStream(lockClassName+".class");

        ClassReader cr = new ClassReader(tis);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new ClassVisitor(Opcodes.V1_6, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ("lock".equals(name)) {
                	mv=cv.visitMethod(access, "rename_lock", desc, signature, exceptions);
                }

                return mv;
            }
            
            @Override
			public void visitEnd(){
            	MethodVisitor mv=super.visitMethod(Opcodes.ACC_PUBLIC, "lock", "()V", null, null);
            	mv.visitCode();
             	mv.visitVarInsn(Opcodes.ALOAD, 0);
            	mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/locks/ReentrantLock", "tryLock", "()Z");
            	Label l0 = new Label();
            	mv.visitJumpInsn(Opcodes.IFNE, l0);
                
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitVarInsn(Opcodes.ISTORE, 1);
                Label l1 = new Label();
                mv.visitJumpInsn(Opcodes.GOTO, l1);
                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
                mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/Thread", "threadLocalBuffer", "[Ljava/lang/Object;");
                mv.visitVarInsn(Opcodes.ILOAD, 1);
                mv.visitInsn(Opcodes.AALOAD);
                mv.visitInsn(Opcodes.DUP);
                Label l3 = new Label();
                mv.visitJumpInsn(Opcodes.IFNULL, l3);
        		mv.visitTypeInsn(Opcodes.CHECKCAST, "deferredmethods/Buffer");
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "deferredmethods/Buffer", "getEnv", "()Ldeferredmethods/DeferredEnv;");
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "deferredmethods/DeferredEnv", "processCurrentBuffer", "()V");
                mv.visitLabel(l3);
                mv.visitIincInsn(1, 1);
                mv.visitLabel(l1);
                
                mv.visitVarInsn(Opcodes.ILOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
                mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/Thread", "threadLocalBuffer", "[Ljava/lang/Object;");
                mv.visitInsn(Opcodes.ARRAYLENGTH);
                mv.visitJumpInsn(Opcodes.IF_ICMPLT, l2);
                mv.visitVarInsn(Opcodes.ILOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
                mv.visitFieldInsn(Opcodes.GETFIELD, "java/lang/Thread", "threadLocalBuffer", "[Ljava/lang/Object;");
                mv.visitInsn(Opcodes.ARRAYLENGTH);
                mv.visitJumpInsn(Opcodes.IF_ICMPLT, l2);
                
            	mv.visitVarInsn(Opcodes.ALOAD, 0);
            	mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/locks/ReentrantLock", "rename_lock", "()V");
            	mv.visitLabel(l0);
            	mv.visitInsn(Opcodes.POP);
            	mv.visitInsn(Opcodes.RETURN);
            	mv.visitMaxs(0, 0);
            	mv.visitEnd();
            	
            	cv.visitEnd();
            }

        };

        cr.accept(cv, 0);

        // output Thread code into special thread bin directory
        write(LOCK_BIN_DIR + lockClassName +".class", cw.toByteArray());
    	}
    }

    private static void write(String outputFile, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(data);
        fos.close();
    }
}
