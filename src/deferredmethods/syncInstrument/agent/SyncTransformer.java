package deferredmethods.syncInstrument.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SyncTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		System.out.println("Trying to instrument:"+className);
        if (className.startsWith("deferredmethods")
                || className.startsWith("sun")
                || className.startsWith("java")
                || className.startsWith("org/objectweb/asm")
                || loader == null
                ) {

//        	System.out.println("skipping class : " + className);
            return classfileBuffer;
        }
        
//        System.out.println("inside transformer...");
        
        byte[] tmp = InjectHelper.transformSynchronized(classfileBuffer);
        
//        try {
//        	System.out.println("writing...");
//    		FileOutputStream os = new FileOutputStream("bin/"+className+"_instr1.class");
//    		os.write(tmp);
//    		os.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println("first step...");
        
        tmp = InjectHelper.transformMonitor(tmp);
        
//        try {
//        	System.out.println("writing...");
//    		FileOutputStream os = new FileOutputStream("bin/"+className+"_instr2.class");
//    		os.write(tmp);
//    		os.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        System.out.println("Complete to instrument:"+className);
        
		return tmp;
	}

//	private void write(String className, byte[] transform) throws Exception {
//		
//	}

}
