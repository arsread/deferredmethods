package deferredmethods.bytecodegeneration;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BufferGenerator extends ClassGenerator {

	private static final String DEFAULT_BUFFER_CLASSNAME = "deferredmethods/GeneratedBuffer";

	private DeferredMethodsReader deferredMethodsReader;
	private DeferredEnvGenerator deferredEnvGenerator;
	private Object[] args;

	public BufferGenerator(int id) {
		super(DEFAULT_BUFFER_CLASSNAME + id);
	}

	public byte[] generateBytecode(DeferredMethodsReader interfaceReader,
			DeferredEnvGenerator deferredEnvGenerator, Object... args) {
		this.deferredMethodsReader = interfaceReader;
		this.args = args;
		this.deferredEnvGenerator = deferredEnvGenerator;

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		// class definition
		String superClassName = interfaceReader.getImplInternalName();
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				getClassName(), null, superClassName,
				new String[] { "deferredmethods/Buffer" });

		bufferFields(cw);

		bufferConstructor(cw);

		bufferAppendMethods(cw);

		bufferRunMethod(cw);

		handInThreadMethod(cw);

		getBufferIdMethod(cw);

		getEnvMethod(cw);

		cw.visitEnd();

		return cw.toByteArray();
	}

	private void bufferRunMethod(ClassWriter cw) {
		List<Method> deferrableMethods = deferredMethodsReader.getMethodList();

		MethodVisitor mv;
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "run", "()V", null, null);

		if (deferredMethodsReader.hasProcessingHooks()) {
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(),
					"beforeProcessing", "()V");
		}

		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitVarInsn(Opcodes.ISTORE, 1);

		Label l2 = new Label();
		mv.visitJumpInsn(Opcodes.GOTO, l2);
		Label l3 = new Label();
		mv.visitLabel(l3);

		mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { Opcodes.INTEGER }, 0,
				null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "deferredMethodID",
				"[I");
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitInsn(Opcodes.IALOAD);

		Label[] switchLabels = new Label[deferrableMethods.size()];
		for (int i = 0; i < deferrableMethods.size(); i++) {
			Label l = new Label();
			switchLabels[i] = l;
		}
		Label switchEnd = new Label();
		mv.visitTableSwitchInsn(1, switchLabels.length, switchEnd, switchLabels);

		for (int i = 0; i < deferrableMethods.size(); i++) {
			Method method = deferrableMethods.get(i);

			mv.visitLabel(switchLabels[i]);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(Opcodes.ALOAD, 0);

			Argument[] args = deferrableMethods.get(i).getArgs();
			for (Argument arg : args) {
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
						arg.getName(), "[" + arg.getType().getDescriptor());
				mv.visitVarInsn(Opcodes.ILOAD, 1);
				mv.visitInsn(arg.getType().getOpcode(Opcodes.IALOAD));
			}

			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(),
					method.getName(), method.getDesc());
			
			//Store the return Ret<T> to the array
			if (method.hasRet()) {
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
						"storedRet", "[Ldeferredmethods/Ret;");
				mv.visitVarInsn(Opcodes.ILOAD, 1);
				mv.visitInsn(Opcodes.AALOAD);
				mv.visitInsn(Opcodes.SWAP);

				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
						"deferredmethods/Ret", "mov",
						"(Ldeferredmethods/Ret;)V");

			}

			if (i < deferrableMethods.size() - 1)
				mv.visitJumpInsn(Opcodes.GOTO, switchEnd);
		}

		mv.visitLabel(switchEnd);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitIincInsn(1, 1);

		mv.visitLabel(l2);

		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		// mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
		// "deferredMethodID", "[I");
		// mv.visitInsn(Opcodes.ARRAYLENGTH);
		// mv.visitJumpInsn(Opcodes.IF_ICMPLT, l3);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "currentPos", "I");
		mv.visitJumpInsn(Opcodes.IF_ICMPLT, l3);

		if (deferredMethodsReader.hasProcessingHooks()) {
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(),
					"afterProcessing", "()V");
		}

		// inform the Env about the buffer's being processed
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "generatedEnv",
				deferredEnvGenerator.getClassDescriptor());
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "bufferID", "I");
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "thread",
				"Ljava/lang/Thread;");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				deferredEnvGenerator.getClassName(), "comfirmBuffer",
				"(ILjava/lang/Thread;)V");

		mv.visitInsn(Opcodes.RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void bufferAppendMethods(ClassWriter cw) {
		MethodVisitor mv;

		List<Method> deferrableMethods = deferredMethodsReader.getMethodList();
		int idx = 0;
		for (Method method : deferrableMethods) {
			idx++;
			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, method.getAppendName(),
					method.getAppendDesc(), null, null);

			int currentPosLVI = 1;
			for (Argument arg : method.getArgs()) {
				currentPosLVI += arg.getType().getSize();
			}
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "currentPos",
					"I");
			mv.visitVarInsn(Opcodes.ISTORE, currentPosLVI);

			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
					"deferredMethodID", "[I");
			mv.visitVarInsn(Opcodes.ILOAD, currentPosLVI);
			mv.visitIntInsn(Opcodes.BIPUSH, idx);
			mv.visitInsn(Opcodes.IASTORE);

			int currStackPos = 1;
			for (Argument arg : method.getArgs()) {
				Type type = arg.getType();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
						arg.getName(), "[" + type.getDescriptor());
				mv.visitVarInsn(Opcodes.ILOAD, currentPosLVI);
				mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), currStackPos);
				mv.visitInsn(type.getOpcode(Opcodes.IASTORE));
				currStackPos += type.getSize();
			}

			if (method.hasRet()) {
				mv.visitTypeInsn(Opcodes.NEW, "deferredmethods/Ret");
				mv.visitInsn(Opcodes.DUP);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
						"deferredmethods/Ret", "<init>", "()V");
				mv.visitInsn(Opcodes.DUP);

				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
						"storedRet", "[Ldeferredmethods/Ret;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitVarInsn(Opcodes.ILOAD, currentPosLVI);
				mv.visitInsn(Opcodes.SWAP);

				mv.visitInsn(Opcodes.AASTORE);

			}

			mv.visitIincInsn(currentPosLVI, 1);

			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ILOAD, currentPosLVI);
			mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "currentPos",
					"I");
			
			mv.visitVarInsn(Opcodes.ILOAD, currentPosLVI);
			
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
					"deferredMethodID", "[I");
			mv.visitInsn(Opcodes.ARRAYLENGTH);

			Label l5 = new Label();
			mv.visitInsn(Opcodes.SWAP);
			mv.visitJumpInsn(Opcodes.IF_ICMPGT, l5);
			
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(), "getEnv",
					"()Ldeferredmethods/DeferredEnv;");
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
					"deferredmethods/DeferredEnv", "processCurrentBuffer",
					"()V");
			
			mv.visitLabel(l5);

			 mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			if (method.hasRet()) {
				mv.visitInsn(Opcodes.ARETURN);
			} else {
				mv.visitInsn(Opcodes.RETURN);
			}

			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}

	private void bufferConstructor(ClassWriter cw) {
		MethodVisitor mv;
		String descriptor = "(I" + deferredEnvGenerator.getClassDescriptor()
				+ BytecodeHelper.getDescriptor(args) + ")V";
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", descriptor, null,
				null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		for (int i = 0; i < args.length; i++) {
			mv.visitVarInsn(Opcodes.ALOAD, i + 3);
		}
		String superClassName = deferredMethodsReader.getImplInternalName();
		descriptor = "(" + BytecodeHelper.getDescriptor(args) + ")V";
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "<init>",
				descriptor);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "bufferID", "I");

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "generatedEnv",
				deferredEnvGenerator.getClassDescriptor());

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				deferredEnvGenerator.getClassName(), "getBufferCapacity", "()I");
		mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
		mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "deferredMethodID",
				"[I");

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "currentPos", "I");

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread",
				"currentThread", "()Ljava/lang/Thread;");
		mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "thread",
				"Ljava/lang/Thread;");

		List<Argument> allArgs = deferredMethodsReader.getArgList();
		for (Argument arg : allArgs) {
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
					deferredEnvGenerator.getClassName(), "getBufferCapacity",
					"()I");
			if (arg.getType().getSort() == Type.OBJECT
					|| arg.getType().getSort() == Type.ARRAY) {
				mv.visitTypeInsn(Opcodes.ANEWARRAY, arg.getType()
						.getInternalName());
			} else {
				mv.visitIntInsn(Opcodes.NEWARRAY,
						BytecodeHelper.getTypeOpcode(arg.getType()));
			}
			mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), arg.getName(),
					"[" + arg.getType().getDescriptor());
		}

		// initialize the new Ret Array
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				deferredEnvGenerator.getClassName(), "getBufferCapacity", "()I");
		mv.visitTypeInsn(Opcodes.ANEWARRAY, "deferredmethods/Ret");
		mv.visitFieldInsn(Opcodes.PUTFIELD, getClassName(), "storedRet",
				"[Ldeferredmethods/Ret;");

		mv.visitInsn(Opcodes.RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void bufferFields(ClassWriter cw) {
		FieldVisitor fv;

		fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
				"deferredMethodID", "[I", null, null);
		fv.visitEnd();

		fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, "bufferID",
				"I", null, null);
		fv.visitEnd();

		fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
				"generatedEnv", deferredEnvGenerator.getClassDescriptor(),
				null, null);
		fv.visitEnd();

		fv = cw.visitField(Opcodes.ACC_PUBLIC, "currentPos", "I", null, null);
		fv.visitEnd();

		fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "thread",
				"Ljava/lang/Thread;", null, null);
		fv.visitEnd();

		// the Ret Array
		fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
				"storedRet", "[Ldeferredmethods/Ret;", null, null);
		fv.visitEnd();

		List<Method> deferrableMethods = deferredMethodsReader.getMethodList();
		for (int i = 0; i < deferrableMethods.size(); i++) {
			String methodId = "ID_" + deferrableMethods.get(i).getName();
			fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL
					+ Opcodes.ACC_STATIC, methodId, "I", null, i + 1);
			fv.visitEnd();
		}

		List<Argument> allArgs = deferredMethodsReader.getArgList();
		for (Argument arg : allArgs) {
			fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
					arg.getName(), "[" + arg.getType().getDescriptor(), null,
					null);
			fv.visitEnd();
		}
	}

	// Method handInThread()
	private void handInThreadMethod(ClassWriter cw) {
		MethodVisitor mv;
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "handInThread",
				"()Ljava/lang/Thread;", null, null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "thread",
				"Ljava/lang/Thread;");
		mv.visitInsn(Opcodes.ARETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void getBufferIdMethod(ClassWriter cw) {
		MethodVisitor mv;
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getBufferId", "()I", null,
				null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "bufferID", "I");
		mv.visitInsn(Opcodes.IRETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void getEnvMethod(ClassWriter cw) {
		MethodVisitor mv;
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getEnv",
				"()Ldeferredmethods/DeferredEnv;", null, null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, getClassName(), "generatedEnv",
				deferredEnvGenerator.getClassDescriptor());
		mv.visitTypeInsn(Opcodes.CHECKCAST, "deferredmethods/DeferredEnv");
		mv.visitInsn(Opcodes.ARETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
