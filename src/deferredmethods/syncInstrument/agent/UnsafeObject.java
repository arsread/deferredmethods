package deferredmethods.syncInstrument.agent;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeObject {
	public static Unsafe unsafe;
	
	static{
		Field f = null;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		f.setAccessible(true);
		try {
			unsafe = (Unsafe) f.get(null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
