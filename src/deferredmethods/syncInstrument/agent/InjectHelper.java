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

		// System.out.println("Version:"+classNode.version);
		if ((classNode.version >= V1_5) && (classNode.version != V1_1)) {
			for (MethodNode methodNode : classNode.methods) {

				if ((methodNode.access & ACC_SYNCHRONIZED) != 0) {
					methodNode.access &= ~ACC_SYNCHRONIZED;

					int classStatic = (methodNode.access & ACC_STATIC);
					int valTop = methodNode.localVariables.size() + 1;

					InsnList enterMethod = new InsnList();

					if (classStatic == 0) {
						enterMethod.add(new VarInsnNode(ALOAD, 0));
					} else {

						enterMethod.add(new LdcInsnNode(Type
								.getObjectType(classNode.name)));
					}

					enterMethod.add(new InsnNode(MONITORENTER));

					InsnList exitMethod = new InsnList();
					if (classStatic == 0) {
						exitMethod.add(new VarInsnNode(ALOAD, 0));
					} else {
						exitMethod.add(new LdcInsnNode(Type
								.getObjectType(classNode.name)));
					}
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

							if (classStatic == 0) {
								methodNode.instructions.insertBefore(instr,
										new VarInsnNode(ALOAD, 0));
							} else {
								methodNode.instructions
										.insertBefore(
												instr,
												new LdcInsnNode(
														Type.getObjectType(classNode.name)));
							}
							methodNode.instructions.insertBefore(instr,
									new InsnNode(MONITOREXIT));
						}
					}

					LabelNode end = new LabelNode();
					LabelNode handler = new LabelNode();
					methodNode.instructions.add(end);
					methodNode.instructions.add(handler);

					InsnList exceptionHandler = new InsnList();
					if (classStatic == 0) {
						exceptionHandler.add(new VarInsnNode(ALOAD, 0));
					} else {
						exceptionHandler.add(new LdcInsnNode(Type
								.getObjectType(classNode.name)));
					}
					exceptionHandler.add(new InsnNode(MONITOREXIT));
					exceptionHandler.add(new InsnNode(ATHROW));

					methodNode.instructions.add(exceptionHandler);

					methodNode.tryCatchBlocks.add(new TryCatchBlockNode(start,
							end, handler, null));

				}
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

		// System.out.println("inside transformer 2!!!!!...");
		if ((classNode.version >= V1_5) && (classNode.version != V1_1)) {
			for (MethodNode methodNode : classNode.methods) {
				int classStatic = methodNode.access & ACC_STATIC;

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
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classNode.accept(cw);
		return cw.toByteArray();
	}

	public static byte[] transformWait(byte[] classfileBuffer) {
		ClassReader classReader = new ClassReader(classfileBuffer);
		ClassNode classNode = new ClassNode(Opcodes.ASM4);
		classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

		// System.out.println("inside transformer 2!!!!!...");
		if ((classNode.version >= V1_5) && (classNode.version != V1_1)) {
			for (MethodNode methodNode : classNode.methods) {
				int classStatic = methodNode.access & ACC_STATIC;

				ListIterator<AbstractInsnNode> it = methodNode.instructions
						.iterator();
				/* Add processCurrentBuffer when use monitorenter */
				while (it.hasNext()) {
					AbstractInsnNode instr = it.next();
					if (instr.getOpcode() == INVOKEVIRTUAL) {
						MethodInsnNode node = (MethodInsnNode) instr;
						if (node.name.equals("wait") && node.owner.equals("java/lang/Object")){
							AbstractInsnNode call=new MethodInsnNode(INVOKESTATIC,
								"deferredmethods/syncInstrument/agent/CallProcess",
								"callProcess", "()V");
							methodNode.instructions.insertBefore(instr,
									call);
						}
					}
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
		buildMonitorEnter.add(new InsnNode(DUP));

		buildMonitorEnter.add(new FieldInsnNode(GETSTATIC,
				"deferredmethods/syncInstrument/agent/UnsafeObject", "unsafe",
				"Lsun/misc/Unsafe;"));
		buildMonitorEnter.add(new InsnNode(SWAP));
		buildMonitorEnter.add(new MethodInsnNode(INVOKEVIRTUAL,
				"sun/misc/Unsafe", "tryMonitorEnter", "(Ljava/lang/Object;)Z"));
		buildMonitorEnter.add(new JumpInsnNode(IFNE, jumpTarget));

		buildMonitorEnter.add(new MethodInsnNode(INVOKESTATIC,
				"deferredmethods/syncInstrument/agent/CallProcess",
				"callProcess", "()V"));

		buildMonitorEnter.add(new FieldInsnNode(GETSTATIC,
				"deferredmethods/syncInstrument/agent/UnsafeObject", "unsafe",
				"Lsun/misc/Unsafe;"));
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
		buildMonitorExit.add(new FieldInsnNode(GETSTATIC,
				"deferredmethods/syncInstrument/agent/UnsafeObject", "unsafe",
				"Lsun/misc/Unsafe;"));
		buildMonitorExit.add(new InsnNode(SWAP));
		buildMonitorExit.add(new MethodInsnNode(INVOKEVIRTUAL,
				"sun/misc/Unsafe", "monitorExit", "(Ljava/lang/Object;)V"));
		return buildMonitorExit;
	}

}
