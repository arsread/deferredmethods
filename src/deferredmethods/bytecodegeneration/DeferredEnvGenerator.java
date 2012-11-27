package deferredmethods.bytecodegeneration;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class DeferredEnvGenerator extends ClassGenerator {

	private static final String DEFAULT_DEFERRED_ENV_CLASSNAME = "deferredmethods/GeneratedDeferredEnv";

	private BufferGenerator bufferGenerator;
	private ThreadLocalBufferGenerator threadLocalBufferGenerator;
	private DeferredMethodsReader deferredMethodsReader;
	private Object[] args;
	private int id;

	public DeferredEnvGenerator(int id) {
		super(DEFAULT_DEFERRED_ENV_CLASSNAME + id);
		this.id = id;
	}

	public byte[] generateBytecode(BufferGenerator bufferGenerator,
			ThreadLocalBufferGenerator threadLocalBufferGenerator,
			DeferredMethodsReader interfaceReader, Object... args) {
		this.bufferGenerator = bufferGenerator;
		this.threadLocalBufferGenerator = threadLocalBufferGenerator;
		this.deferredMethodsReader = interfaceReader;
		this.args = args;

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		FieldVisitor fv;

		// class definition
		String[] interfaces = new String[] { interfaceReader
				.getIntfInternalName() };
		String superClass = "deferredmethods/GeneralDeferredEnv";
		String signature = "L" + superClass + "<"
				+ interfaceReader.getIntfDescriptor() + ">;"
				+ interfaceReader.getIntfDescriptor();
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				getClassName(), signature, superClass, interfaces);

		// fields
		String descriptor = threadLocalBufferGenerator.getClassDescriptor();
		fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "bufferTL",
				descriptor, null, null);
		fv.visitEnd();

		deferredEnvConstructor(cw);

		deferredEnvProcessCurrentBuffer(cw);

		deferredEnvDeferredMethods(cw);

		deferredEnvGetProxy(cw);

		deferredEnvCreateCheckPoint(cw);

		cw.visitEnd();

		return cw.toByteArray();
	}

	private void deferredEnvProcessCurrentBuffer(final ClassWriter cw) {
		MethodVisitor mv;
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "processCurrentBuffer", "()V",
				null, null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "proc",
				"Ldeferredmethods/proc/Processor;");
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "bufferTL",
				threadLocalBufferGenerator.getClassDescriptor());
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "proc",
				"Ldeferredmethods/proc/Processor;");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				threadLocalBufferGenerator.getClassName(), "get",
				"(Ldeferredmethods/proc/Processor;)Ljava/lang/Object;");
		mv.visitTypeInsn(Opcodes.CHECKCAST, "deferredmethods/ExtendedRunnable");
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
				"deferredmethods/proc/Processor", "process",
				"(Ldeferredmethods/ExtendedRunnable;)V");

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "bufferTL",
				threadLocalBufferGenerator.getClassDescriptor());
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				threadLocalBufferGenerator.getClassName(), "remove", "()V");

		mv.visitInsn(Opcodes.RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void deferredEnvDeferredMethods(final ClassWriter cw) {
		MethodVisitor mv;
		// TODO use automatic constant creation
		for (Method deferrableMethod : deferredMethodsReader.getMethodList()) {
			String name = deferrableMethod.getName();
			String desc = deferrableMethod.getDesc();
			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, name, desc,
					deferrableMethod.getSignature(),
					deferrableMethod.getExceptions());
			String newName = deferrableMethod.getAppendName();
			String newDesc = deferrableMethod.getAppendDesc();

			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "bufferTL",
					threadLocalBufferGenerator.getClassDescriptor());
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "proc",
					"Ldeferredmethods/proc/Processor;");
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
					threadLocalBufferGenerator.getClassName(), "get",
					"(Ldeferredmethods/proc/Processor;)Ljava/lang/Object;");
			mv.visitTypeInsn(Opcodes.CHECKCAST, bufferGenerator.getClassName());

			Type[] types = deferrableMethod.getTypes();
			int currStackPos = 1;
			for (int j = 0; j < types.length; j++) {
				currStackPos += (j == 0 ? 0 : types[j - 1].getSize());
				mv.visitVarInsn(types[j].getOpcode(Opcodes.ILOAD), currStackPos);
			}

			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
					bufferGenerator.getClassName(), newName, newDesc);
			Label l1 = new Label();
			mv.visitJumpInsn(Opcodes.IFNE, l1);

			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(),
					"processCurrentBuffer", "()V");
			mv.visitLabel(l1);

			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(Opcodes.RETURN);

			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}

	private void deferredEnvConstructor(final ClassWriter cw) {
		MethodVisitor mv;

		String descriptor = "(Ldeferredmethods/proc/Processor;I"
				+ BytecodeHelper.getDescriptor(args) + ")V";
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", descriptor, null,
				null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ILOAD, 2);
		mv.visitIntInsn(Opcodes.BIPUSH, this.id);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
				"deferredmethods/GeneralDeferredEnv", "<init>",
				"(Ldeferredmethods/proc/Processor;II)V");

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitTypeInsn(Opcodes.NEW, threadLocalBufferGenerator.getClassName());
		mv.visitInsn(Opcodes.DUP);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		for (int i = 0; i < args.length; i++) {
			mv.visitVarInsn(Opcodes.ALOAD, i + 3);
		}
		descriptor = "(" + getClassDescriptor()
				+ BytecodeHelper.getDescriptor(args) + ")V";
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
				threadLocalBufferGenerator.getClassName(), "<init>", descriptor);
		mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "bufferTL",
				threadLocalBufferGenerator.getClassDescriptor());

		mv.visitInsn(Opcodes.RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void deferredEnvGetProxy(final ClassWriter cw) {
		MethodVisitor mv;

		String desc = "()" + deferredMethodsReader.getIntfDescriptor();

		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getProxy", desc, null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE
				+ Opcodes.ACC_SYNTHETIC, "getProxy",
				"()Ldeferredmethods/Deferred;", null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(), "getProxy",
				desc);
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	// Method createCheckPoint()
	private void deferredEnvCreateCheckPoint(final ClassWriter cw) {
		MethodVisitor mv;

		String desc = "()Ldeferredmethods/ProcessingCheckPoint;";
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "createCheckPoint", desc, null,
				null);

		// get bufferid and use it to create a CheckPoint
		mv.visitTypeInsn(Opcodes.NEW,
				"deferredmethods/GeneralProcessingCheckPoint");
		mv.visitInsn(Opcodes.DUP);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "bufferTL",
				threadLocalBufferGenerator.getClassDescriptor());
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "proc",
				"Ldeferredmethods/proc/Processor;");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				threadLocalBufferGenerator.getClassName(), "get",
				"(Ldeferredmethods/proc/Processor;)Ljava/lang/Object;");
		mv.visitTypeInsn(Opcodes.CHECKCAST, bufferGenerator.getClassName());
		mv.visitFieldInsn(Opcodes.GETFIELD, bufferGenerator.getClassName(),
				"bufferID", "I");
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
				"deferredmethods/GeneralProcessingCheckPoint", "<init>", "(I)V");
		mv.visitVarInsn(Opcodes.ASTORE, 1);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				getClassName(), "addCPList",
				"(Ljava/lang/Thread;Ldeferredmethods/GeneralProcessingCheckPoint;)V");
//		mv.visitInsn(Opcodes.POP);

		// Processing current buffer
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(),
				"processCurrentBuffer", "()V");

		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitInsn(Opcodes.ARETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

}
