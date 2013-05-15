package newTest;

import deferredmethods.Deferred;
import deferredmethods.Ret;

public interface SimpleTestInter extends Deferred {
	public void noRet(int i);
	Ret<Integer> ret();
}
