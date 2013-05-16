package deferredmethods.syncInstrument.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.security.KeyStore.Builder;

public class SyncTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		// System.out.println("Trying to instrument:"+className);
		if (className.startsWith("deferredmethods")
				|| className.startsWith("sun") || className.startsWith("java")
				|| className.startsWith("org/objectweb/asm") 
//				|| className.startsWith("org/apache/xerces/impl/XMLEntityManager")
//				|| className.startsWith("org/apache/xerces/impl/dv/DTDDVFactory")
				|| loader == null) {

			// System.out.println("skipping class : " + className);
			return classfileBuffer;
		}
		// System.out.println("trying inject..."+className);

		byte[] tmp = InjectHelper.transformSynchronized(classfileBuffer);

		// System.out.println("successfully inject..."+className);

		// try {
		// System.out.println("writing...");
		// FileOutputStream os = new
		// FileOutputStream("bin/"+className+"_instr1.class");
		// os.write(tmp);
		// os.close();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("first step...");

		tmp = InjectHelper.transformMonitor(tmp);
//		 byte[] tmp = InjectHelper.transformMonitor(classfileBuffer);

//		if (className.startsWith("org/apache/xerces/impl/XMLEntityManager")) {
//			System.out.println("I found!!!!!!----->" + className);
//			try {
//				System.out.println("writing...");
//				FileOutputStream os = new FileOutputStream("hehe.class");
//				os.write(tmp);
//				os.close();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		// System.out.println("Complete to instrument:"+className);

		return tmp;
//		return classfileBuffer;
	}

	// private void write(String className, byte[] transform) throws Exception {
	//
	// }

}
