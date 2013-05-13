package deferredmethods.syncInstrument.agent;

import java.util.ListIterator;

import javax.xml.bind.util.ValidationEventCollector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InjectHelper implements Opcodes {

	public static byte[] transformSynchronized(byte[] classfileBuffer) {
		ClassReader classReader = new ClassReader(classfileBuffer);
		ClassNode classNode = new ClassNode(Opcodes.ASM4);
		classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

		for (MethodNode methodNode : classNode.methods) {
			if ((methodNode.access & ACC_SYNCHRONIZED) != 0) {
				methodNode.access &= (~ACC_SYNCHRONIZED);

				int classStatic = methodNode.access & ACC_STATIC;
				int valTop = methodNode.localVariables.size() + 1;

				InsnList enterMethod = new InsnList();
				if (classStatic == 0) {
					enterMethod.add(new VarInsnNode(ALOAD, 0));
				} else {
					enterMethod.add(new LdcInsnNode(Type
							.getType(classNode.name)));
				}
				enterMethod.add(new InsnNode(DUP));
				enterMethod.add(new VarInsnNode(ASTORE, valTop));
				enterMethod.add(new InsnNode(MONITORENTER));

				InsnList exitMethod = new InsnList();
				exitMethod.add(new VarInsnNode(ALOAD, valTop));
				exitMethod.add(new InsnNode(MONITOREXIT));

				LabelNode start = new LabelNode();
				methodNode.instructions.insert(start);
				methodNode.instructions.insert(enterMethod);

				ListIterator<AbstractInsnNode> it = methodNode.instructions
						.iterator();
				/* change sychronized methods -> sychronized(this) */
				while (it.hasNext()) {
					AbstractInsnNode instr = it.next();
					int opcode = instr.getOpcode();
					if (opcode >= IRETURN && opcode <= RETURN) {
						methodNode.instructions.insertBefore(instr, exitMethod);
					}
				}

				LabelNode end = new LabelNode();
				LabelNode handler = new LabelNode();
				methodNode.instructions.add(end);
				methodNode.instructions.add(handler);

				InsnList exceptionHandler = new InsnList();
				exitMethod.add(new VarInsnNode(ALOAD, valTop));
				exitMethod.add(new InsnNode(MONITOREXIT));
				exceptionHandler.add(new InsnNode(ATHROW));

				methodNode.instructions.add(exceptionHandler);

				methodNode.tryCatchBlocks.add(new TryCatchBlockNode(start, end,
						handler, null));

			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classNode.accept(cw);
		return cw.toByteArray();
	}

	public static byte[] transformMonitor(byte[] classfileBuffer) {
		ClassReader classReader = new ClassReader(classfileBuffer);
		ClassNode classNode = new ClassNode(Opcodes.ASM4);
		classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

		for (MethodNode methodNode : classNode.methods) {
			int classStatic = methodNode.access & ACC_STATIC;
//			int valTop = methodNode.localVariables.size() + 1;

			ListIterator<AbstractInsnNode> it = methodNode.instructions
					.iterator();
			/* Add processCurrentBuffer when use monitorenter */
			while (it.hasNext()) {
				AbstractInsnNode instr = it.next();
				if (instr.getOpcode() == MONITORENTER) {
					methodNode.instructions.insertBefore(instr,
							buildTryMonitorEnter(classNode));
					it.next();
					methodNode.instructions.remove(instr);
					it.previous();
				}
				if (instr.getOpcode() == MONITOREXIT) {
					methodNode.instructions.insertBefore(instr,
							buildTryMonitorExit(classNode));
					it.next();
					methodNode.instructions.remove(instr);
					it.previous();
				}
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classNode.accept(cw);
		return cw.toByteArray();
	}

	private static InsnList buildTryMonitorEnter(ClassNode classNode) {
		LabelNode jumpTarget = new LabelNode();
		LabelNode endTarget = new LabelNode();

		InsnList buildMonitorEnter = new InsnList();
//		buildMonitorEnter.add(new VarInsnNode(ASTORE, valTop));
		buildMonitorEnter.add(new InsnNode(DUP));
		// buildMonitorEnter
		// .add(new LdcInsnNode(Type.getType("Lsun/misc/Unsafe;")));
		// buildMonitorEnter.add(new LdcInsnNode("theUnsafe"));
		// buildMonitorEnter.add(new MethodInsnNode(INVOKEVIRTUAL,
		// "java/lang/Class", "getDeclaredField",
		// "(Ljava/lang/String;)Ljava/lang/reflect/Field;"));
		// buildMonitorEnter.add(new VarInsnNode(ASTORE, valTop + 1));
		// buildMonitorEnter.add(new VarInsnNode(ALOAD, valTop + 1));
		// buildMonitorEnter.add(new InsnNode(ICONST_1));
		// buildMonitorEnter.add(new MethodInsnNode(INVOKEVIRTUAL,
		// "java/lang/reflect/Field", "setAccessible", "(Z)V"));
		// buildMonitorEnter.add(new VarInsnNode(ALOAD, valTop + 1));
		// buildMonitorEnter.add(new InsnNode(ACONST_NULL));
		// buildMonitorEnter.add(new MethodInsnNode(INVOKEVIRTUAL,
		// "java/lang/reflect/Field", "get",
		// "(Ljava/lang/Object;)Ljava/lang/Object;"));
		// buildMonitorEnter.add(new TypeInsnNode(CHECKCAST,
		// "sun/misc/Unsafe"));
		//buildMonitorEnter.add(new FieldInsnNode(GETSTATIC,
		//		"deferredmethods/syncInstrument/agent/UnsafeObject", "unsafe",
		//		"Lsun/misc/Unsafe;"));
		//buildMonitorEnter.add(new VarInsnNode(ASTORE, valTop + 2));

		buildMonitorEnter.add(new FieldInsnNode(GETSTATIC,
				"deferredmethods/syncInstrument/agent/UnsafeObject", "unsafe",
				"Lsun/misc/Unsafe;"));
//		buildMonitorEnter.add(new VarInsnNode(ALOAD, valTop));
		buildMonitorEnter.add(new InsnNode(SWAP));
		buildMonitorEnter.add(new MethodInsnNode(INVOKEVIRTUAL,
				"sun/misc/Unsafe", "tryMonitorEnter", "(Ljava/lang/Object;)Z"));
		buildMonitorEnter.add(new JumpInsnNode(IFNE, jumpTarget));

		buildMonitorEnter.add(new MethodInsnNode(INVOKESTATIC,
				"deferredmethods/syncInstrument/agent/CallProcess", "callProcess", "()V"));
//		buildMonitorEnter.add(buildCallProcess(valTop + 2));

		buildMonitorEnter.add(new FieldInsnNode(GETSTATIC,
				"deferredmethods/syncInstrument/agent/UnsafeObject", "unsafe",
				"Lsun/misc/Unsafe;"));
//		buildMonitorEnter.add(new VarInsnNode(ALOAD, valTop));
		buildMonitorEnter.add(new InsnNode(SWAP));
		buildMonitorEnter.add(new MethodInsnNode(INVOKEVIRTUAL,
				"sun/misc/Unsafe", "monitorEnter", "(Ljava/lang/Object;)V"));
		buildMonitorEnter.add(new JumpInsnNode(GOTO, endTarget));
		buildMonitorEnter.add(jumpTarget);
		buildMonitorEnter.add(new InsnNode(POP));
		buildMonitorEnter.add(endTarget);
		
		return buildMonitorEnter;
	}

	private static InsnList buildTryMonitorExit(ClassNode classNode) {
		InsnList buildMonitorExit = new InsnList();
//		buildMonitorExit.add(new VarInsnNode(ASTORE, valTop + 2));
		buildMonitorExit.add(new FieldInsnNode(GETSTATIC,
				"deferredmethods/syncInstrument/agent/UnsafeObject", "unsafe",
				"Lsun/misc/Unsafe;"));
		buildMonitorExit.add(new InsnNode(SWAP));
//		buildMonitorExit.add(new VarInsnNode(ALOAD, valTop + 2));
		buildMonitorExit.add(new MethodInsnNode(INVOKEVIRTUAL,
				"sun/misc/Unsafe", "monitorExit", "(Ljava/lang/Object;)V"));
		return buildMonitorExit;
	}

//	private static InsnList buildCallProcess(int valtop) {
//		InsnList buildCallProcess = new InsnList();
//		LabelNode outsideJudge = new LabelNode();
//		LabelNode loopBody = new LabelNode();
//		LabelNode insideJudge = new LabelNode();
//		LabelNode popPoint = new LabelNode();
//
//		buildCallProcess.add(new InsnNode(ICONST_0));
//		buildCallProcess.add(new VarInsnNode(ISTORE, valtop));
//		buildCallProcess.add(new JumpInsnNode(GOTO, outsideJudge));
//		buildCallProcess.add(loopBody);
//
//		buildCallProcess.add(new MethodInsnNode(INVOKESTATIC,
//				"java/lang/Thread", "currentThread", "()Ljava/lang/Thread;"));
//
//		buildCallProcess.add(new FieldInsnNode(GETFIELD, "java/lang/Thread",
//				"threadLocalBuffer", "[Ljava/lang/Object;"));
//		buildCallProcess.add(new VarInsnNode(ILOAD, valtop));
//		buildCallProcess.add(new InsnNode(AALOAD));
//		buildCallProcess.add(new InsnNode(DUP));
//
//		buildCallProcess.add(new JumpInsnNode(IFNULL, insideJudge));
//		buildCallProcess.add(new TypeInsnNode(CHECKCAST,
//				"deferredmethods/Buffer"));
//		buildCallProcess.add(new MethodInsnNode(INVOKEINTERFACE,
//				"deferredmethods/Buffer", "getEnv",
//				"()Ldeferredmethods/DeferredEnv;"));
//		buildCallProcess.add(new MethodInsnNode(INVOKEINTERFACE,
//				"deferredmethods/DeferredEnv", "processCurrentBuffer", "()V"));
//		buildCallProcess.add(new JumpInsnNode(GOTO, popPoint));
//
//		// TODO:add null check
//		buildCallProcess.add(insideJudge);
//		buildCallProcess.add(new InsnNode(POP));
//		buildCallProcess.add(popPoint);
//		buildCallProcess.add(new IincInsnNode(valtop, 1));
//		buildCallProcess.add(outsideJudge);
//
//		buildCallProcess.add(new VarInsnNode(ILOAD, valtop));
//		buildCallProcess.add(new MethodInsnNode(INVOKESTATIC,
//				"java/lang/Thread", "currentThread", "()Ljava/lang/Thread;"));
//		buildCallProcess.add(new FieldInsnNode(GETFIELD, "java/lang/Thread",
//				"threadLocalBuffer", "[Ljava/lang/Object;"));
//		buildCallProcess.add(new InsnNode(ARRAYLENGTH));
//		buildCallProcess.add(new JumpInsnNode(IF_ICMPLT, loopBody));
//
//		return buildCallProcess;
//	}
}
