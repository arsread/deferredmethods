package deferredmethods.syncInstrument.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class Agent {
	public static void premain(String agentArgs, Instrumentation instrumentation) {
		ClassFileTransformer transformer = new SyncTransformer();
		instrumentation.addTransformer(transformer);
	}
}
