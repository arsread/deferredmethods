package deferredmethods;

import deferredmethods.Buffer;

public class CallProcess {
	public static void callProcess() {
		for (Object obj : Thread.currentThread().threadLocalBuffer) {
			if (obj != null) {
				Buffer buf = (Buffer) obj;
				buf.getEnv().processCurrentBuffer();
			}
		}
	}

}
